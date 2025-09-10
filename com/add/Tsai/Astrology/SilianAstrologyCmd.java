package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 絲莉安 指令處理
 */
public class SilianAstrologyCmd {

    private static final Log _log = LogFactory.getLog(SilianAstrologyCmd.class);

    // 玩家已啟用技能紀錄（絲莉安專用）
    private static final Map<Integer, Integer> _SILIAN_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _SILIAN_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _SILIAN_LAST_BTN = new ConcurrentHashMap<>();

    private static SilianAstrologyCmd _instance;

    public static SilianAstrologyCmd get() {
        if (_instance == null) {
            _instance = new SilianAstrologyCmd();
        }
        return _instance;
    }

    /**
     * 指令入口（完整複製 AttonAstrologyCmd 流程，改為絲莉安命名與 UI）
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 僅當處於絲莉安星盤或開啟入口時處理
        if (!"astr3".equals(cmd) && pc.getAstrologyPlateType() != 2) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟絲莉安星盤）
            if ("astr3".equals(cmd)) {
                pc.setAstrologyPlateType(2);
                updateUI(pc, "t_silian");
                return true;
            }

            // 點擊絲莉安節點（直接嘗試解鎖或切換生效）
            if (cmd.startsWith("tsi_")) {
                int id = Integer.parseInt(cmd.substring(4));
                SilianAstrologyData data = SilianAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("絲莉安星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }

                // 前置檢查（若資料有設定）
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }

                // 建立/取得抽卡任務進度
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), id);
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), id, data.get_cards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), id, data.get_cards());
                }

                // 檢查守護石（若未持有，仍可用需求道具直解；與 Atton 一致）
                if (!pc.getInventory().checkItem(11618, 1)) {
                    if (data.getQuestId() > 0 && !pc.getQuest().isEnd(data.getNeedQuestId())) {
                        pc.sendPackets(new S_SystemMessage("前置任務未完成"));
                        updateUI(pc, "t_silian");
                        return true;
                    }
                }

                // 已完成 → 切換當前選擇的技能（僅發放對應啟動道具，不直接啟動效果）
                if (pc.getQuest().isEnd(data.getQuestId())) {
                    // 僅更新當前選擇（不在這裡套用效果 / HOT）
                    _SILIAN_LAST_BTN.put(pc.getId(), id);
                    if (data.getSkillId() > 0) {
                        _SILIAN_SKILLS.put(pc.getId(), data.getSkillId());
                        _SILIAN_SKILL_NAMES.put(pc.getId(), data.getNote());
                        // 切換技能節點：回收其他等級啟動道具，並發放當前等級對應啟動道具（若有設定）
                        try {
                            SilianAstrologyTable.get().revokeGrantItems(pc, data.getGrantItemId());
                        } catch (Exception ignored) {
                        }
                        if (data.getGrantItemId() > 0) {
                            pc.getInventory().storeItem(data.getGrantItemId(), 1);
                        }
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + "，請使用啟動道具啟動效果。", 1));
                    } else {
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖。"));
                    }
                    updateUI(pc, "t_silian");
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
                SilianAstrologyData data = SilianAstrologyTable.get().getData(astrologyType);
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
                        // 任務完成即給能力：非技能節點直接生效
                        if (data.getSkillId() == 0) {
                            // 絲莉安非技能節點：直接套用能力（由表驅動）
                            SilianAstrologyTable.effectBuff(pc, data, 1);
                            pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                        }
                    }
                    updateUI(pc, "t_silian");
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
     * 更新絲莉安主 UI
     */
    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            Integer[] orders = SilianAstrologyTable.get().getIndexArray();
            Arrays.sort(orders);
            for (Integer order : orders) {
                SilianAstrologyData data = SilianAstrologyTable.get().getData(order);
                if (data == null) continue;
                int img = pc.getQuest().isEnd(data.getQuestId())
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
     * 取得玩家當前選擇的絲莉安技能節點按鈕序號（已完成節點）。
     */
    public Integer getSilianSkillActive(final L1PcInstance pc) {
        if (pc == null) return null;
        return _SILIAN_LAST_BTN.get(pc.getId());
    }

    public static void startRegenStatic(final L1PcInstance pc, final SilianAstrologyData data) {
        final int hotTime = Math.max(1, data.getHotTime() > 0 ? data.getHotTime() : 5);
        final int totalHp = Math.max(0, data.getHpr()); // 總共回多少血量
        final int totalMp = Math.max(0, data.getMpr()); // 總共回多少魔量
        if (totalHp == 0 && totalMp == 0) return;

        // 均勻分配到每秒，最後一跳補齊餘數
        final int hpPerTick = (hotTime > 1) ? (totalHp / hotTime) : totalHp;
        final int hpLastTick = totalHp - hpPerTick * (hotTime - 1);
        final int mpPerTick = (hotTime > 1) ? (totalMp / hotTime) : totalMp;
        final int mpLastTick = totalMp - mpPerTick * (hotTime - 1);

        // 使用絲莉安專用時間戳，不與蓋亞共用
        long until = System.currentTimeMillis() + hotTime * 1000L;
        pc.setSilianRegenUntil(until);
        // 專用圖示（沿用相同 ICON ID，但不設置 GAIA 狀態）
        pc.sendPackets(new S_InventoryIcon(9700, true, 2783,  hotTime));

        Timer timer = new Timer();
        for (int i = 1; i <= hotTime; i++) {
            final int hpHeal = (i == hotTime) ? hpLastTick : hpPerTick;
            final int mpHeal = (i == hotTime) ? mpLastTick : mpPerTick;
            if (hpHeal == 0 && mpHeal == 0) continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() > pc.getSilianRegenUntil()) return;
                    if (hpHeal > 0 && pc.getCurrentHp() < pc.getMaxHp()) {
                        pc.setCurrentHp(Math.min(pc.getCurrentHp() + hpHeal, pc.getMaxHp()));
                    }
                    if (mpHeal > 0 && pc.getCurrentMp() < pc.getMaxMp()) {
                        pc.setCurrentMp(Math.min(pc.getCurrentMp() + mpHeal, pc.getMaxMp()));
                    }
                }
            }, i * 1000L);
        }
    }

    // 個別回收/發放邏輯已併入 Cmd 切換流程

    /**
     * 判斷前置星盤是否解鎖（邏輯比照 AttonAstrologyCmd）
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        SilianAstrologyData cur = SilianAstrologyTable.get().getData(key);
        int prevType = -1;
        if (cur != null && cur.getNeedQuestId() > 0) {
            prevType = cur.getNeedQuestId();
        } else {
            prevType = calcPrevType(key);
        }
        if (prevType < 0) return false;
        SilianAstrologyData prev = SilianAstrologyTable.get().getData(prevType);
        if (prev != null && !pc.getQuest().isEnd(prev.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + prev.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號（沿用 Atton 規則）
     */
    private int calcPrevType(int key) {
        switch (key) {
            case 0: return -1;
            case 3: return 0;
            case 5: case 2: return 3;
            case 7: return 5;
            case 8: case 11: return 7;
            case 9: return 8;
            case 12: return 9;
            case 15: return 12;
            case 17: return 15;
            case 19: return 17;

            case 14: return 11;
            case 16: return 14;
            case 18: return 16;
            case 20: return 18;
            case 21: return 20;
            case 22: return 21;

            case 1: return 2;
            case 4: return 1;
            case 6: return 4;
            case 10: return 6;
            case 13: return 10;
            default:
                if (key >= 10 && key <= 23) return key - 1;
        }
        return -1;
    }
}


