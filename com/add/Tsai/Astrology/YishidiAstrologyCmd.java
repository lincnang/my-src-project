package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 依詩蒂 指令處理（流程比照 Grit/Silian）
 */
public class YishidiAstrologyCmd {

    private static final Log _log = LogFactory.getLog(YishidiAstrologyCmd.class);

    private static final Map<Integer, Integer> _YI_LAST_BTN = new ConcurrentHashMap<>();

    private static YishidiAstrologyCmd _instance;
    public static YishidiAstrologyCmd get() {
        if (_instance == null) {
            _instance = new YishidiAstrologyCmd();
        }
        return _instance;
    }

    /** 取得玩家目前啟用中的依詩蒂技能節點按鈕（若沒有回傳 null） */
    public Integer getYishidiLastBtn(final L1PcInstance pc) {
        if (pc == null) return null;
        return _YI_LAST_BTN.get(pc.getId());
    }

    /**
     * 點選技能節點當下的特效與範圍傷害（以玩家為中心）
     * AoE基準 = level*10 * (技能範圍傷害%/100)
     */
    private void castSelectAoE(final L1PcInstance pc, final YishidiAstrologyData data) {
        if (pc == null || data == null) return;
        int range = Math.max(0, data.getSkillRange());
        if (range <= 0) return;
        int pct = Math.max(0, Math.min(100, data.getSkillRangeDamagePercent()));
        int base = Math.max(1, pc.getLevel() * 10);
        int areaDamage = Math.max(1, (int) Math.floor(base * (pct / 100.0)));
        if (data.getSkillProcGfxId() > 0) {
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), data.getSkillProcGfxId()));
        }
        for (L1Object o : World.get().getVisibleObjects(pc, range)) {
            if (o == null) continue;
            if (o instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) o;
                if (tgpc == pc || tgpc.isDead()) continue;
                tgpc.receiveDamage(pc, areaDamage, false, true);
            } else if (o instanceof L1NpcInstance) {
                L1NpcInstance tgnpc = (L1NpcInstance) o;
                if (tgnpc.isDead()) continue;
                tgnpc.receiveDamage(pc, areaDamage);
            }
        }
    }

    /**
     * 指令入口
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 指令入口命名：沿用 astr5 作為依詩蒂入口（絲莉安=astr3, 格立特=astr4）
        if (!"astr5".equals(cmd) && pc.getAstrologyPlateType() != 4) return false;
        try {
            final String[] msg = "".split(",");
            if ("astr5".equals(cmd)) {
                pc.setAstrologyPlateType(4);
                updateUI(pc, "t_Yishidi");
                return true;
            }

            // 點擊依詩蒂節點：支援 tyi_#（本專用）與 tgr_#（沿用格立特HTML模板）
            if (cmd.startsWith("tyi_") || cmd.startsWith("tgr_")) {
                int id = Integer.parseInt(cmd.substring(4));
                YishidiAstrologyData data = YishidiAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("依詩蒂星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), id);
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), id, data.getCards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), id, data.getCards());
                }

                // 已完成：技能節點立即套用、非技能節點登入已套用
                if (pc.getQuest().isEnd(data.getQuestId())) {
                    if (data.getSkillId() > 0) {
                        YishidiAstrologyTable.effectBuff(pc, data, 1);
                        _YI_LAST_BTN.put(pc.getId(), id);
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + " 已開啟！", 1));
                    } else {
                        _YI_LAST_BTN.put(pc.getId(), id);
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖。"));
                    }
                    updateUI(pc, "t_Yishidi");
                    return true;
                }

                // 未完成 → 進入抽卡頁
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true;
            }

            // 抽卡流程 abu*
            if (cmd.startsWith("abu")) {
                int astrologyType = pc.getAstrologyType();
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), astrologyType);
                if (quest == null) return true;
                if (!pc.getInventory().checkItem(11618, 1)) {
                    pc.sendPackets(new S_SystemMessage("缺少守護石，無法啟用！"));
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                    return true;
                }
                pc.getInventory().consumeItem(11618, 1);
                int rnd = java.util.concurrent.ThreadLocalRandom.current().nextInt(100) + 1;
                if (quest.getNum() == 1) rnd = 100;
                if (rnd < 85) {
                    pc.sendPackets(new S_SystemMessage("開啟守護星，失敗"));
                    AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, quest.getNum() - 1);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + (quest.getNum() - 1), msg));
                    return true;
                }
                YishidiAstrologyData data = YishidiAstrologyTable.get().getData(astrologyType);
                if (data != null) {
                    boolean unlockSuccess = false;
                    if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                        int needItemId = 0;
                        int needItemNum = 0;
                        try { needItemId = Integer.parseInt(data.getNeedItemId()); } catch (Exception ignore) {}
                        try { needItemNum = Integer.parseInt(data.getNeedItemCount()); } catch (Exception ignore) {}
                        if (needItemId > 0 && needItemNum > 0 && pc.getInventory().checkItem(needItemId, needItemNum)) {
                            pc.getInventory().consumeItem(needItemId, needItemNum);
                            unlockSuccess = true;
                        } else {
                            pc.sendPackets(new S_SystemMessage("道具不足"));
                        }
                    } else {
                        unlockSuccess = true;
                    }
                    if (unlockSuccess) {
                        pc.getQuest().set_step(data.getQuestId(), 255);
                        AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, 1);
                        AstrologyQuestReading.get().delQuest(pc.getId(), astrologyType);
                        if (data.getSkillId() == 0) {
                            YishidiAstrologyTable.effectBuff(pc, data, 1);
                            pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                        }
                    }
                    updateUI(pc, "t_Yishidi");
                    pc.sendPackets(new S_PacketBoxGree(1));
                }
                return true;
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            Integer[] orders = YishidiAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                YishidiAstrologyData data = YishidiAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = pc.getQuest().isEnd(data.getQuestId())
                        ? data.getCompleteGfxId()
                        : data.getIncompleteGfxId();
                builder.append(img).append(",");
            }
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("YishidiAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 判斷前置星盤是否解鎖
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        // 取消資料庫前置設計，仿照 Grit：僅使用核心 calcPrevType 規則
        int prevType = calcPrevType(key);
        if (prevType < 0) return false;
        YishidiAstrologyData prev = YishidiAstrologyTable.get().getData(prevType);
        if (prev != null && !pc.getQuest().isEnd(prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號（當資料庫未設定前置編號時的預設規則）
     * 可根據你的星盤設計調整
     */
    private int calcPrevType(int key) {
        switch (key) {
            // 第一層（中心）
            case 0: return -1;
            case 1: return 0;
            case 3: return 1;
            case 7: return 3;
            case 6: case 11: case 8: return 7;
            case 2: return 6;
            case 5: return 2;
            case 10: return 5;
            case 13: return 10;
            case 16: return 13;
            case 19: return 16;
            case 22: return 19;
            case 27: return 22;
            case 23: return 27;

            case 14: return 11;
            case 17: return 14;
            case 20: return 17;
            case 24: return 17;
            case 28: return 24;
            case 30: return 28;
            case 31: return 30;

            case 4: return 8;
            case 9: return 4;
            case 12: return 9;
            case 15: return 12;
            case 18: return 15;
            case 21: return 18;
            case 26: return 21;
            case 29: return 26;
            case 25: return 29;

            default: return -1; // 未定義的節點
        }
    }
}


