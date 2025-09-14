package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 格立特 指令處理（流程比照絲莉安）
 */
public class GritAstrologyCmd {

    private static final Log _log = LogFactory.getLog(GritAstrologyCmd.class);

    // 玩家已啟用技能紀錄（格立特專用）
    private static final Map<Integer, Integer> _GRIT_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _GRIT_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _GRIT_LAST_BTN = new ConcurrentHashMap<>();

    private static GritAstrologyCmd _instance;

    public static GritAstrologyCmd get() {
        if (_instance == null) {
            _instance = new GritAstrologyCmd();
        }
        return _instance;
    }

    /**
     * 指令入口（仿照 SilianAstrologyCmd 流程，改為格立特命名與 UI）
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 僅當處於格立特星盤或開啟入口時處理（絲莉安用 2，這裡沿用 3 做為 plate type）
        if (!"astr4".equals(cmd) && pc.getAstrologyPlateType() != 3) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟格立特星盤）
            if ("astr4".equals(cmd)) {
                pc.setAstrologyPlateType(3);
                updateUI(pc, "t_gretel");
                return true;
            }

            // 點擊格立特節點（直接嘗試解鎖或切換生效）
            if (cmd.startsWith("tgr_")) {
                int id = Integer.parseInt(cmd.substring(4));
                GritAstrologyData data = GritAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("格立特星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }

                // 前置檢查（若有需求可以在此加入自定規則；目前沿用線性順序規則）
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }

                // 建立/取得抽卡任務進度
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), id);
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), id, data.getCards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), id, data.getCards());
                }

                // 檢查守護石（未持有也允許用需求道具直解；參考絲莉安）
                if (!pc.getInventory().checkItem(11618, 1)) {
                    if (data.getQuestId() > 0 && !pc.getQuest().isEnd(data.getQuestId())) {
                        pc.sendPackets(new S_SystemMessage("前置任務未完成"));
                        updateUI(pc, "t_gretel");
                        return true;
                    }
                }

                // 已完成 → 切換當前選擇（技能節點：移除上一個並套用新的效果；非技能節點：永久生效）
                if (pc.getQuest().isEnd(data.getQuestId())) {
                    if (data.getSkillId() > 0) {
                        // 技能節點：移除上一個技能效果，套用新的技能效果
                        Integer prevBtn = _GRIT_LAST_BTN.get(pc.getId());
                        if (prevBtn != null && prevBtn != id) {
                            GritAstrologyData prevData = GritAstrologyTable.get().getData(prevBtn);
                            if (prevData != null && prevData.getSkillId() > 0) {
                                GritAstrologyTable.effectBuff(pc, prevData, -1);
                            }
                        }
                        GritAstrologyTable.effectBuff(pc, data, 1);
                        _GRIT_LAST_BTN.put(pc.getId(), id);
                        _GRIT_SKILLS.put(pc.getId(), data.getSkillId());
                        _GRIT_SKILL_NAMES.put(pc.getId(), data.getNote());
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + " 已開啟！", 1));
                    } else {
                        // 非技能節點：不需再套用（登入已套用），僅提示
                        _GRIT_LAST_BTN.put(pc.getId(), id);
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖。"));
                    }
                    updateUI(pc, "t_gretel");
                    return true;
                }

                // 未完成 → 進入抽卡頁（沿用 t_butN）
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true;
            }

            // 抽卡解鎖流程（沿用 abu 前綴）
            if (cmd.startsWith("abu")) {
                int astrologyType = pc.getAstrologyType();
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), astrologyType);
                if (quest == null) {
                    return true;
                }
                if (!pc.getInventory().checkItem(11618, 1)) {
                    pc.sendPackets(new S_SystemMessage("缺少守護石，無法啟用！"));
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                    return true;
                }
                pc.getInventory().consumeItem(11618, 1);
                int rnd = java.util.concurrent.ThreadLocalRandom.current().nextInt(100) + 1;
                if (quest.getNum() == 1) {
                    rnd = 100;
                }
                if (rnd < 85) {
                    pc.sendPackets(new S_SystemMessage("開啟守護星，失敗"));
                    AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, quest.getNum() - 1);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + (quest.getNum() - 1), msg));
                    return true;
                }
                GritAstrologyData data = GritAstrologyTable.get().getData(astrologyType);
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
                        // 任務完成即給能力：非技能節點直接生效（以資料表驅動）
                        if (data.getSkillId() == 0) {
                            GritAstrologyTable.effectBuff(pc, data, 1);
                            pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                        }
                    }
                    updateUI(pc, "t_gretel");
                    pc.sendPackets(new S_PacketBoxGree(1));
                }
                return true;
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 更新格立特主 UI
     */
    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            Integer[] orders = GritAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                GritAstrologyData data = GritAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = pc.getQuest().isEnd(data.getQuestId())
                        ? data.getCompleteGfxId()
                        : data.getIncompleteGfxId();
                builder.append(img).append(",");
            }
            String skillName = _GRIT_SKILL_NAMES.getOrDefault(pc.getId(), "");
            builder.append(skillName);
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("GritAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取得玩家目前啟用中的格立特技能節點按鈕（若沒有回傳 -1）
     */
    public int getGritLastBtn(final L1PcInstance pc) {
        if (pc == null) return -1;
        Integer v = _GRIT_LAST_BTN.get(pc.getId());
        return v == null ? -1 : v.intValue();
    }

    /**
     * 判斷前置星盤是否解鎖（沿用簡單線性規則；如需客製可改 SQL 加『前置編號』）
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        int prevType = calcPrevType(key);
        if (prevType < 0) return false;
        GritAstrologyData prev = GritAstrologyTable.get().getData(prevType);
        if (prev != null && !pc.getQuest().isEnd(prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    private int calcPrevType(int key) {
        switch (key) {
            case 0: return -1;
            case 1: return 0;
            case 3: return 1;
            case 7: return 3;
            case 6: case 8: return 7;
            case 4: return 8;
            case 9: return 4;
            case 12: return 9;
            case 15: return 12;
            case 18: return 15;
            case 21: return 18;
            case 24: return 21;
            case 27: return 24;
            case 30: return 27;

            case 2: return 6;
            case 5: return 2;
            case 10: return 5;
            case 13: return 10;
            case 16: return 13;
            case 19: return 16;
            case 22: return 19;
            case 25: return 22;
            case 28: return 25;

            case 11: return 7;
            case 14: return 11;
            case 17: return 14;
            case 20: return 17;
            case 23: return 20;
            case 26: return 23;
            case 29: return 26;

            default:
                if (key >= 10 && key <= 30) return key - 1;
        }
        return -1;
    }
}


