package com.lineage.data.npc;

import com.add.Tsai.holyBookCmd;
import com.add.Tsai.holyTable;
import com.lineage.config.ConfigHoly;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.lineage.server.thread.GeneralThreadPool;

public class Npc_HolyCombind extends NpcExecutor {
    // 供合成流程使用的簡單結構
    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;
        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }

    private final int[] holy1 = ConfigHoly.holy_LIST_1;
    private final int[] holy2 = ConfigHoly.holy_LIST_2;
    private final int[] holy3 = ConfigHoly.holy_LIST_3;
    private final int[] holy4 = ConfigHoly.holy_LIST_4;
    private final int[] holy5 = ConfigHoly.holy_LIST_5;

    public static Npc_HolyCombind get() {
        return new Npc_HolyCombind();
    }

    /**
     * 統一 CMD 靜態入口（推薦全部用這個叫進去！）
     */
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        if ("holy_set".equalsIgnoreCase(cmd)) {
            holyBookCmd.get().HolyAllSet(pc);
            return true;

        }
        if ("holycombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind1"));
            return true;
        }
        if ("Holy_D01".equalsIgnoreCase(cmd)) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                stringBuilder.append(holyTable.get().getHolyHaveGroup(pc, i + 1)).append(",");
                stringBuilder.append(holyTable.get().getHolyGroupSize(i + 1)).append(",");
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            if (clientStrAry.length == 12) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D01", clientStrAry));
            } else {
                pc.sendPackets(new S_SystemMessage("Holy_D01資料異常：" + clientStrAry.length));
            }
            return true;
        }

        if ("holy_A1".equalsIgnoreCase(cmd)) {
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind2", data));
            return true;
        }
        if ("holy_B1".equalsIgnoreCase(cmd)) {
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind3", data));
            return true;
        }
        if ("holy_C1".equalsIgnoreCase(cmd)) {
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind4", data));
            return true;
        }
        if ("holy_D1".equalsIgnoreCase(cmd)) {
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind5", data));
            return true;
        }
        // 機率調整按鈕
        if ("holy_A11".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2() < ConfigHoly.HOLY_MAX_RATE2)
                pc.setHolyRun2(pc.getHolyRun2() + ConfigHoly.HOLY_STEP_RATE2);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind2", data));
            return true;
        }
        if ("holy_A12".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2() > ConfigHoly.HOLY_MIN_RATE2)
                pc.setHolyRun2(pc.getHolyRun2() - ConfigHoly.HOLY_STEP_RATE2);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind2", data));
            return true;
        }
        if ("holy_B11".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3() < ConfigHoly.HOLY_MAX_RATE3)
                pc.setHolyRun3(pc.getHolyRun3() + ConfigHoly.HOLY_STEP_RATE3);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind3", data));
            return true;
        }
        if ("holy_B12".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3() > ConfigHoly.HOLY_MIN_RATE3)
                pc.setHolyRun3(pc.getHolyRun3() - ConfigHoly.HOLY_STEP_RATE3);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind3", data));
            return true;
        }
        if ("holy_C11".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4() < ConfigHoly.HOLY_MAX_RATE4)
                pc.setHolyRun4(pc.getHolyRun4() + ConfigHoly.HOLY_STEP_RATE4);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind4", data));
            return true;
        }
        if ("holy_C12".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4() > ConfigHoly.HOLY_MIN_RATE4)
                pc.setHolyRun4(pc.getHolyRun4() - ConfigHoly.HOLY_STEP_RATE4);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind4", data));
            return true;
        }
        if ("holy_D11".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5() < ConfigHoly.HOLY_MAX_RATE5)
                pc.setHolyRun5(pc.getHolyRun5() + ConfigHoly.HOLY_STEP_RATE5);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind5", data));
            return true;
        }
        if ("holy_D12".equalsIgnoreCase(cmd)) {
            if (ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5() > ConfigHoly.HOLY_MIN_RATE5)
                pc.setHolyRun5(pc.getHolyRun5() - ConfigHoly.HOLY_STEP_RATE5);
            String[] data = {String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5()), "", ""};
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "holycombind5", data));
            return true;
        }
        // 合成主流程
        if (cmd.equalsIgnoreCase("holy_A") || cmd.equalsIgnoreCase("holy_A_total") ||
                cmd.equalsIgnoreCase("holy_B") || cmd.equalsIgnoreCase("holy_B_total") ||
                cmd.equalsIgnoreCase("holy_C") || cmd.equalsIgnoreCase("holy_C_total") ||
                cmd.equalsIgnoreCase("holy_D") || cmd.equalsIgnoreCase("holy_D_total")) {
            new Npc_HolyCombind().action(pc, null, cmd, 0);
            return true;
        }
        return false;
    }

    public int type() {
        return 3;
    }

    // NPC 對話進入主畫面
    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind1"));
    }

    // ======================== 主合成流程 ========================
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldHolys = null;
        int[] newHolys = null;
        int chance = 0;
        int needcount = 0;
        switch (cmd) {
            case "holy_A":
            case "holy_A_total":
                chance = ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2();
                needcount = ConfigHoly.HOLY_COUNT2 + pc.getHolyCount();
                oldHolys = holy1;
                newHolys = holy2;
                break;
            case "holy_B":
            case "holy_B_total":
                chance = ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3();
                needcount = ConfigHoly.HOLY_COUNT3 + pc.getHolyCount2();
                oldHolys = holy2;
                newHolys = holy3;
                break;
            case "holy_C":
            case "holy_C_total":
                chance = ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4();
                needcount = ConfigHoly.HOLY_COUNT4 + pc.getHolyCount3();
                oldHolys = holy3;
                newHolys = holy4;
                break;
            case "holy_D":
            case "holy_D_total":
                chance = ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5();
                needcount = ConfigHoly.HOLY_COUNT5 + pc.getHolyCount4();
                oldHolys = holy4;
                newHolys = holy5;
                break;
            default:
                pc.sendPackets(new S_SystemMessage("指令錯誤!"));
                return;
        }

        // 判斷是否為批量合成模式
        boolean isTotalMode = cmd.toLowerCase().contains("_total");

        // 批量合成模式：異步執行避免卡頓
        if (isTotalMode) {
            final int[] fOldHolys = oldHolys;
            final int[] fNewHolys = newHolys;
            final int fChance = chance;
            final int fNeedcount = needcount;
            final String fCmd = cmd;
            GeneralThreadPool.get().schedule(() -> doBatchCombine(pc, fCmd, fOldHolys, fNewHolys, fChance, fNeedcount), 100);
            return;
        }

        // ========== 單次合成模式（原有邏輯） ==========
        boolean enough = false;
        ArrayList<ItemConsume> consumeList = new ArrayList<>();
        if (oldHolys != null) {
            int totalCount = 0;
            outer:
            for (int oldHoly : oldHolys) {
                L1ItemInstance[] holys = pc.getInventory().findItemsId(oldHoly);
                if (holys != null) {
                    for (L1ItemInstance holy : holys) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) holy.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(holy, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        if (!enough) {
            pc.sendPackets(new S_SystemMessage("你的聖物卡不足【" + needcount + "】個"));
            return;
        }

        // 扣除材料
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 重置全部階段加成
        pc.setHolyCount(0);
        pc.setHolyCount2(0);
        pc.setHolyCount3(0);
        pc.setHolyCount4(0);
        pc.setHolyRun2(0);
        pc.setHolyRun3(0);
        pc.setHolyRun4(0);
        pc.setHolyRun5(0);

        // 執行單次合成
        int newHoly = newHolys[ThreadLocalRandom.current().nextInt(newHolys.length)];
        doSingleCombine(pc, newHoly, chance, consumeList);
    }

    // ======================== 單次合成結果處理 ========================
    private void doSingleCombine(L1PcInstance pc, int newHoly, int chance, ArrayList<ItemConsume> consumeList) {
        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            final L1ItemInstance item = ItemTable.get().createItem(newHoly);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            World.get().broadcastPacketToAllAsync(
                    new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了聖物卡\\fN【" + item.getLogName() + "】"));
            pc.sendPackets(new S_Sound(20360));
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
            pc.sendPackets(new S_PacketBoxGree(15));
        } else {
            pc.sendPackets(new S_SystemMessage("合成聖物卡失敗了。"));
            int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_Sound(20468));
            pc.sendPackets(new S_PacketBoxGree(16));
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }

    // ======================== 批量合成流程（一次性計算） ========================
    private void doBatchCombine(L1PcInstance pc, String cmd, int[] oldHolys, int[] newHolys, int chance, int needcount) {
        // ========== 步驟1：一次性收集所有材料 ==========
        final ArrayList<ItemConsume> consumeList = new ArrayList<>();
        int totalAvailable = 0;

        if (oldHolys != null) {
            for (int oldHoly : oldHolys) {
                L1ItemInstance[] holys = pc.getInventory().findItemsId(oldHoly);
                if (holys != null) {
                    for (L1ItemInstance holy : holys) {
                        int count = (int) holy.getCount();
                        consumeList.add(new ItemConsume(holy, count));
                        totalAvailable += count;
                    }
                }
            }
        }

        if (totalAvailable < needcount) {
            pc.sendPackets(new S_SystemMessage("你的聖物卡不足【" + needcount + "】個"));
            sendBackToPage(pc, cmd);
            return;
        }

        int maxCombine = totalAvailable / needcount;

        // ========== 步驟2：一次性扣除所有材料 ==========
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 重置全部階段加成
        pc.setHolyCount(0);
        pc.setHolyCount2(0);
        pc.setHolyCount3(0);
        pc.setHolyCount4(0);
        pc.setHolyRun2(0);
        pc.setHolyRun3(0);
        pc.setHolyRun4(0);
        pc.setHolyRun5(0);

        // ========== 步驟3：一次性計算結果（按成功率直接計算） ==========
        final Map<Integer, Integer> successItems = new HashMap<>();
        final Map<Integer, Integer> failReturnItems = new HashMap<>();

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < maxCombine; i++) {
            int newHoly = newHolys[ThreadLocalRandom.current().nextInt(newHolys.length)];
            if (ThreadLocalRandom.current().nextInt(100) < chance) {
                // 成功
                successCount++;
                successItems.merge(newHoly, 1, Integer::sum);
            } else {
                // 失敗 - 返還一階卡
                failCount++;
                int backId = oldHolys[ThreadLocalRandom.current().nextInt(oldHolys.length)];
                failReturnItems.merge(backId, 1, Integer::sum);
            }
        }

        // ========== 步驟4：一次性給予所有結果道具 ==========
        // 給予成功的道具
        for (Map.Entry<Integer, Integer> entry : successItems.entrySet()) {
            L1ItemInstance item = ItemTable.get().createItem(entry.getKey());
            if (item != null && item.getItem() != null) {
                item.setIdentified(true);
                if (entry.getValue() > 1) {
                    item.setCount(entry.getValue());
                }
                pc.getInventory().storeItem(item);
            }
        }

        // 給予失敗返還的道具
        for (Map.Entry<Integer, Integer> entry : failReturnItems.entrySet()) {
            L1ItemInstance item = ItemTable.get().createItem(entry.getKey());
            if (item != null && item.getItem() != null) {
                item.setIdentified(true);
                if (entry.getValue() > 1) {
                    item.setCount(entry.getValue());
                }
                pc.getInventory().storeItem(item);
            }
        }

        // ========== 步驟5：發送結果 ==========
        pc.sendPackets(new S_Sound(successCount > 0 ? 20360 : 20468));
        pc.sendPackets(new S_SystemMessage("批量合成完成！成功: " + successCount + " 次，失敗: " + failCount + " 次"));
        if (successCount > 0) {
            pc.sendPackets(new S_PacketBoxGree(15));
        } else {
            pc.sendPackets(new S_PacketBoxGree(16));
        }

        sendBackToPage(pc, cmd);
    }

    // 返回對應的合成頁面
    private void sendBackToPage(L1PcInstance pc, String cmd) {
        String htmlPage = "";
        String[] data = new String[3];

        if (cmd.toLowerCase().contains("holy_a")) {
            htmlPage = "holycombind2";
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME2);
        } else if (cmd.toLowerCase().contains("holy_b")) {
            htmlPage = "holycombind3";
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME3);
        } else if (cmd.toLowerCase().contains("holy_c")) {
            htmlPage = "holycombind4";
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME4);
        } else if (cmd.toLowerCase().contains("holy_d")) {
            htmlPage = "holycombind5";
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME5);
        }

        if (!htmlPage.isEmpty())
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlPage, data));
    }
}