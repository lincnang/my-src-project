package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 新守護星盤 AttonAstrologyCmd
 * 完全獨立於舊星盤邏輯的專用指令與資料存取
 *
 * @author hero
 */
public class AttonAstrologyCmd {

    private static final Log _log = LogFactory.getLog(AttonAstrologyCmd.class);

    // 玩家技能啟動紀錄
    private static final Map<Integer, Integer> _ASTROLOGY_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _ASTROLOGY_SKILLS2 = new ConcurrentHashMap<>();
    // 記錄每位玩家目前啟用中的「技能節點」按鈕編號（避免清空所有一般加成）
    private static final Map<Integer, Integer> _ATTON_LAST_BTN = new ConcurrentHashMap<>();

    // 為避免與其他星盤共用進度鍵，加入 Atton 專用偏移
    private static final int QUEST_KEY_OFFSET = 1000;
    private int qk(int id) { return QUEST_KEY_OFFSET + id; }


    private static AttonAstrologyCmd _instance;

    public static AttonAstrologyCmd get() {
        if (_instance == null) {
            synchronized (AttonAstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new AttonAstrologyCmd();
                }
            }
        }
        return _instance;
    }

    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _ASTROLOGY_SKILLS.remove(playerId);
        _ASTROLOGY_SKILLS2.remove(playerId);
        _ATTON_LAST_BTN.remove(playerId);
    }

    /**
     * 取得玩家已啟動技能編號
     */
    public int getAstrologySkillActive(L1PcInstance pc) {
        return _ASTROLOGY_SKILLS.getOrDefault(pc.getId(), -1);
    }

    /**
     * 指令入口
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // abu 指令由所有星盤共用，需要特別處理
        if (cmd.startsWith("abu")) {
            return handleAbu(pc, cmd);
        }

        if (!"astr2".equals(cmd) && pc.getAstrologyPlateType() != 1) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟阿頓星盤功能）
            if ("astr2".equals(cmd)) {
                pc.setAstrologyPlateType(1);
                updateUI(pc, "t_atton");
                return true;
            }

            // 點擊星盤節點
            if (cmd.startsWith("tat_")) {
                int id = Integer.parseInt(cmd.substring(4));
                AttonAstrologyData data = AttonAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }
                // 前置檢查統一由 checkEndAstrologyQuest 處理
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }

                // 修正：優先檢查是否已經解鎖，若已解鎖則直接切換技能或顯示訊息，不檢查/消耗道具
                if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())) {
                    if (data.getSkillId() > 0) {
                        // 僅切換技能節點效果：移除上一個技能節點，不清除一般加成
                        Integer prevBtn = _ATTON_LAST_BTN.get(pc.getId());
                        if (prevBtn != null && prevBtn != id) {
                            AttonAstrologyData prevData = AttonAstrologyTable.get().getData(prevBtn);
                            if (prevData != null) {
                                AttonAstrologyTable.effectBuff(pc, prevData, -1);
                            }
                        }
                        AttonAstrologyTable.effectBuff(pc, data, 1);
                        _ATTON_LAST_BTN.put(pc.getId(), id);
                        _ASTROLOGY_SKILLS.put(pc.getId(), data.getSkillId());
                        _ASTROLOGY_SKILLS2.put(pc.getId(), data.getNote());
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + "已開啟！", 1));
                    } else {
                        // 非技能節點：直接套用一次性的屬性加成並記錄，避免重複
                        pc.addAstrologyPower(data, id);
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                    }
                    updateUI(pc, "t_atton");
                    return true;
                }

                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), qk(id));
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), qk(id), data.get_cards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), qk(id), data.get_cards());
                }
                // 檢查需求道具（依資料庫設定）
                if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                    int needItemId = Integer.parseInt(data.getNeedItemId());
                    int needItemNum = Integer.parseInt(data.getNeedItemCount());
                    if (!pc.getInventory().checkItem(needItemId, needItemNum)) {
                        pc.sendPackets(new S_SystemMessage("需求道具不足"));
                        updateUI(pc, "t_atton");
                        return true;
                    }
                }
                
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true;
            }

            // 其他不認識指令
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 處理 abu 抽卡解鎖指令
     */
    private boolean handleAbu(final L1PcInstance pc, final String cmd) {
        // 檢查 plateType 是否正確
        if (pc.getAstrologyPlateType() != 1) {
            return false;
        }
        try {
            final String[] msg = "".split(",");
            int astrologyType = pc.getAstrologyType();
            AttonAstrologyData data = AttonAstrologyTable.get().getData(astrologyType);
            if (data == null) {
                return false; // 不是阿頓的節點，讓其他星盤處理
            }
            AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), qk(astrologyType));
            if (quest == null) {
                return false;
            }
            // 檢查並扣除需求道具（依資料庫設定，無論成功失敗都扣除）
            if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                int needItemId = Integer.parseInt(data.getNeedItemId());
                int needItemNum = Integer.parseInt(data.getNeedItemCount());
                if (!pc.getInventory().checkItem(needItemId, needItemNum)) {
                    pc.sendPackets(new S_SystemMessage("需求道具不足，無法啟用！"));
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                    return true;
                }
                pc.getInventory().consumeItem(needItemId, needItemNum);
            }
            int rnd = ThreadLocalRandom.current().nextInt(100) + 1;
            if (quest.getNum() == 1) {
                rnd = 100;
            }
            if (rnd < 85) {
                pc.sendPackets(new S_SystemMessage("開啟守護星，失敗"));
                AstrologyQuestReading.get().updateQuest(pc.getId(), qk(astrologyType), quest.getNum() - 1);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + (quest.getNum() - 1), msg));
                return true;
            }
            // 成功
            // pc.getQuest().set_step(data.getQuestId(), 255); // 改用 AstrologyHistoryTable
            AstrologyQuestReading.get().updateQuest(pc.getId(), qk(astrologyType), 1);
            AstrologyQuestReading.get().delQuest(pc.getId(), qk(astrologyType));
            // 新增解鎖紀錄
            AstrologyHistoryTable.get().add(pc.getId(), data.getQuestId());

            pc.sendPackets(new S_SystemMessage("恭喜您解鎖[" + data.getNote() + "]"));
            // 任務完成即給能力：非技能節點直接生效
            if (data.getSkillId() == 0) {
                pc.addAstrologyPower(data, astrologyType);
                pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
            }
            updateUI(pc, "t_atton");
            pc.sendPackets(new S_PacketBoxGree(1));
            return true;
        } catch (Exception e) {
            _log.error("阿頓星盤 abu 指令處理錯誤", e);
            return false;
        }
    }

    /**
     * 判斷前置星盤是否解鎖
     * 前置檢查統一由此處理，使用 calcPrevType 規則
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        int prevType = calcPrevType(key);
        if (prevType < 0) return false;
        AttonAstrologyData prev = AttonAstrologyTable.get().getData(prevType);
        if (prev != null && !AstrologyHistoryTable.get().isUnlocked(pc.getId(), prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號
     */
    private int calcPrevType(int key) {
        switch (key) {
            case 8: return 3;
            case 9: return 7;
            case 0: return -1;
            case 1: return 0;
            case 2: return 1;
            case 5: return 2;
            case 4: case 6: return 5;
            case 3: return 4;
            case 7: return 6;

            case 10:return 8;
            case 12:return 10;
            case 16:return 12;
            case 19:return 16;
            case 22:return 19;
            case 25:return 22;
            case 27:return 25;

            case 11:return 9;
            case 15:return 11;
            case 14: case 18: return 15;
            case 13:return 14;
            case 17:return 13;
            case 20:return 17;
            case 23:return 20;

            case 21:return 18;
            case 24:return 21;
            case 26:return 24;
            case 28:return 26;

            default:
                if (key >= 10 && key <= 28) return key - 1;
        }
        return -1;
    }

    /**
     * 更新主UI
     */
    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            Integer[] orders = AttonAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                AttonAstrologyData data = AttonAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())
                        ? data.getCompleteGfxId()
                        : data.getIncompleteGfxId(); // 尚未開啟/未完成 → 顯示未完成IMG
                builder.append(img).append(",");
            }
            String skillName = _ASTROLOGY_SKILLS2.getOrDefault(pc.getId(), "");
            builder.append(skillName);
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("AttonAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }
}
