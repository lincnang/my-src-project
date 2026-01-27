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
 * 守護星盤 - 依詩蒂 指令處理
 */
public class YishidiAstrologyCmd {

    private static final Log _log = LogFactory.getLog(YishidiAstrologyCmd.class);

    // 玩家已啟用技能紀錄（依詩蒂專用）
    private static final Map<Integer, Integer> _YISHIDI_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _YISHIDI_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _YISHIDI_LAST_BTN = new ConcurrentHashMap<>();

    // 偏移避免與其他星盤共用 quest key
    private static final int QUEST_KEY_OFFSET = 4000;
    private int qk(int id) { return QUEST_KEY_OFFSET + id; }

    private static YishidiAstrologyCmd _instance;

    public static YishidiAstrologyCmd get() {
        if (_instance == null) {
            synchronized (YishidiAstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new YishidiAstrologyCmd();
                }
            }
        }
        return _instance;
    }

    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _YISHIDI_SKILLS.remove(playerId);
        _YISHIDI_SKILL_NAMES.remove(playerId);
        _YISHIDI_LAST_BTN.remove(playerId);
    }

    /**
     * 指令入口
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // abu 指令由所有星盤共用，需要特別處理
        if (cmd.startsWith("abu")) {
            return handleAbu(pc, cmd);
        }
        // 其他指令需要檢查 plateType
        if (!"astr5".equals(cmd) && pc.getAstrologyPlateType() != 4) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟依詩蒂星盤功能）
            if ("astr5".equals(cmd)) {
                pc.setAstrologyPlateType(4);
                updateUI(pc, "t_yishidi");
                return true;
            }

            // 點擊星盤節點
            if (cmd.startsWith("trs_")) {
                int id = Integer.parseInt(cmd.substring(4));
                YishidiAstrologyData data = YishidiAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }
                // 前置檢查統一由 checkEndAstrologyQuest 處理
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), qk(id));
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), qk(id), data.getCards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), qk(id), data.getCards());
                }
                // 檢查需求道具（依資料庫設定）
                if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                    int needItemId = Integer.parseInt(data.getNeedItemId());
                    int needItemNum = Integer.parseInt(data.getNeedItemCount());
                    if (!pc.getInventory().checkItem(needItemId, needItemNum)) {
                        pc.sendPackets(new S_SystemMessage("需求道具不足"));
                        updateUI(pc, "t_yishidi");
                        return true;
                    }
                }
                // 已完成
                if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())) {
                    if (data.getSkillId() > 0) {
                        // 僅切換技能節點效果：移除上一個技能節點，不清除一般加成
                        Integer prevBtn = _YISHIDI_LAST_BTN.get(pc.getId());
                        if (prevBtn != null && prevBtn != id) {
                            YishidiAstrologyData prevData = YishidiAstrologyTable.get().getData(prevBtn);
                            if (prevData != null) {
                                YishidiAstrologyTable.effectBuff(pc, prevData, -1);
                            }
                        }
                        YishidiAstrologyTable.effectBuff(pc, data, 1);
                        _YISHIDI_LAST_BTN.put(pc.getId(), id);
                        _YISHIDI_SKILLS.put(pc.getId(), data.getSkillId());
                        _YISHIDI_SKILL_NAMES.put(pc.getId(), data.getNote());
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + "已開啟！", 1));
                    } else {
                        // 非技能節點：直接套用一次性的屬性加成並記錄，避免重複
                        pc.addAstrologyPower(data, id);
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                    }
                    updateUI(pc, "t_yishidi");
                    return true;
                }
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true;
            }

            // 其他不認識指令
        } catch (final Exception e) {
            _log.error("依詩蒂星盤指令處理錯誤", e);
        }
        return false;
    }

    /**
     * 處理 abu 抽卡解鎖指令
     */
    private boolean handleAbu(final L1PcInstance pc, final String cmd) {
        // 檢查 plateType 是否正確
        if (pc.getAstrologyPlateType() != 4) {
            return false;
        }
        try {
            final String[] msg = "".split(",");
            int astrologyType = pc.getAstrologyType();
            YishidiAstrologyData data = YishidiAstrologyTable.get().getData(astrologyType);
            if (data == null) {
                return false; // 不是依詩蒂的節點，讓其他星盤處理
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
            updateUI(pc, "t_yishidi");
            pc.sendPackets(new S_PacketBoxGree(1));
            return true;
        } catch (Exception e) {
            _log.error("依詩蒂星盤 abu 指令處理錯誤", e);
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
        YishidiAstrologyData prev = YishidiAstrologyTable.get().getData(prevType);
        if (prev != null && !AstrologyHistoryTable.get().isUnlocked(pc.getId(), prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號（線性規則）
     */
    private int calcPrevType(int key) {
        if (key == 0) return -1;
        if (key > 0) return key - 1;
        return -1;
    }

    /**
     * 更新主UI
     */
    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            Integer[] orders = YishidiAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                YishidiAstrologyData data = YishidiAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())
                        ? data.getCompleteGfxId()
                        : data.getIncompleteGfxId();
                builder.append(img).append(",");
            }
            String skillName = _YISHIDI_SKILL_NAMES.getOrDefault(pc.getId(), "");
            builder.append(skillName);
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("YishidiAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取得玩家「最後點擊/選擇」的依詩蒂節點按鈕編號
     */
    public Integer getYishidiLastBtn(final L1PcInstance pc) {
        if (pc == null) return null;
        return _YISHIDI_LAST_BTN.get(pc.getId());
    }
}
