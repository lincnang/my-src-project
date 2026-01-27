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
 * 守護星盤 - 絲莉安 指令處理
 */
public class SilianAstrologyCmd {

    private static final Log _log = LogFactory.getLog(SilianAstrologyCmd.class);

    // 玩家已啟用技能紀錄（絲莉安專用）
    private static final Map<Integer, Integer> _SILIAN_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _SILIAN_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _SILIAN_LAST_BTN = new ConcurrentHashMap<>();

    // 偏移避免與其他星盤共用 quest key
    private static final int QUEST_KEY_OFFSET = 2000;
    private int qk(int id) { return QUEST_KEY_OFFSET + id; }

    private static SilianAstrologyCmd _instance;

    public static SilianAstrologyCmd get() {
        if (_instance == null) {
            synchronized (SilianAstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new SilianAstrologyCmd();
                }
            }
        }
        return _instance;
    }

    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _SILIAN_SKILLS.remove(playerId);
        _SILIAN_SKILL_NAMES.remove(playerId);
        _SILIAN_LAST_BTN.remove(playerId);
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
        if (!"astr3".equals(cmd) && pc.getAstrologyPlateType() != 2) {
            return false;
        }
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟絲莉安星盤功能）
            if ("astr3".equals(cmd)) {
                pc.setAstrologyPlateType(2);
                updateUI(pc, "t_silian");
                return true;
            }

            // 點擊星盤節點
            if (cmd.startsWith("tsi_")) {
                int id = Integer.parseInt(cmd.substring(4));
                SilianAstrologyData data = SilianAstrologyTable.get().getData(id);
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
                    quest = new AstrologyQuest(pc.getId(), qk(id), data.get_cards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), qk(id), data.get_cards());
                }
                // 檢查需求道具（依資料庫設定）
                if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                    int needItemId = Integer.parseInt(data.getNeedItemId());
                    int needItemNum = Integer.parseInt(data.getNeedItemCount());
                    if (!pc.getInventory().checkItem(needItemId, needItemNum)) {
                        pc.sendPackets(new S_SystemMessage("需求道具不足"));
                        updateUI(pc, "t_silian");
                        return true;
                    }
                }
                // 已完成
                if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())) {
                    if (data.getSkillId() > 0) {
                        // 僅切換技能節點效果：移除上一個技能節點，不清除一般加成
                        Integer prevBtn = _SILIAN_LAST_BTN.get(pc.getId());
                        if (prevBtn != null && prevBtn != id) {
                            SilianAstrologyData prevData = SilianAstrologyTable.get().getData(prevBtn);
                            if (prevData != null) {
                                SilianAstrologyTable.effectBuff(pc, prevData, -1);
                            }
                        }
                        SilianAstrologyTable.effectBuff(pc, data, 1);
                        _SILIAN_LAST_BTN.put(pc.getId(), id);
                        _SILIAN_SKILLS.put(pc.getId(), data.getSkillId());
                        _SILIAN_SKILL_NAMES.put(pc.getId(), data.getNote());
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + "已開啟！", 1));
                    } else {
                        // 非技能節點：直接套用一次性的屬性加成並記錄，避免重複
                        pc.addAstrologyPower(data, id);
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                    }
                    updateUI(pc, "t_silian");
                    return true;
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
        if (pc.getAstrologyPlateType() != 2) {
            return false;
        }
        try {
            final String[] msg = "".split(",");
            int astrologyType = pc.getAstrologyType();
            SilianAstrologyData data = SilianAstrologyTable.get().getData(astrologyType);
            if (data == null) {
                return false; // 不是絲莉安的節點，讓其他星盤處理
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
            updateUI(pc, "t_silian");
            pc.sendPackets(new S_PacketBoxGree(1));
            return true;
        } catch (Exception e) {
            _log.error("絲莉安星盤 abu 指令處理錯誤", e);
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
        SilianAstrologyData prev = SilianAstrologyTable.get().getData(prevType);
        if (prev != null && !AstrologyHistoryTable.get().isUnlocked(pc.getId(), prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號（依資料庫規則，可依實際調整）
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
            Integer[] orders = SilianAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                SilianAstrologyData data = SilianAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.getQuestId())
                        ? data.getCompleteGfxId()
                        : data.getIncompleteGfxId();
                builder.append(img).append(",");
            }
            String skillName = _SILIAN_SKILL_NAMES.getOrDefault(pc.getId(), "");
            builder.append(skillName);
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("SilianAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取得玩家當前啟用的絲莉安技能節點按鈕編號
     */
    public Integer getSilianSkillActive(final L1PcInstance pc) {
        if (pc == null) return null;
        return _SILIAN_LAST_BTN.get(pc.getId());
    }
}
