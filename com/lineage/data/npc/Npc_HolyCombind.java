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
import java.util.concurrent.ThreadLocalRandom;

public class Npc_HolyCombind extends NpcExecutor {
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
        int newHoly = 0;
        int chance = 0;
        int needcount = 0;
        switch (cmd) {
            case "holy_A":
            case "holy_A_total":
                chance = ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2();
                needcount = ConfigHoly.HOLY_COUNT2 + pc.getHolyCount();
                oldHolys = holy1;
                newHoly = holy2[ThreadLocalRandom.current().nextInt(holy2.length)];
                break;
            case "holy_B":
            case "holy_B_total":
                chance = ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3();
                needcount = ConfigHoly.HOLY_COUNT3 + pc.getHolyCount2();
                oldHolys = holy2;
                newHoly = holy3[ThreadLocalRandom.current().nextInt(holy3.length)];
                break;
            case "holy_C":
            case "holy_C_total":
                chance = ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4();
                needcount = ConfigHoly.HOLY_COUNT4 + pc.getHolyCount3();
                oldHolys = holy3;
                newHoly = holy4[ThreadLocalRandom.current().nextInt(holy4.length)];
                break;
            case "holy_D":
            case "holy_D_total":
                chance = ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5();
                needcount = ConfigHoly.HOLY_COUNT5 + pc.getHolyCount4();
                oldHolys = holy4;
                newHoly = holy5[ThreadLocalRandom.current().nextInt(holy5.length)];
                break;
            default:
                pc.sendPackets(new S_SystemMessage("指令錯誤!"));
                return;
        }
        // 下面的材料判斷與合成流程照舊
        class ItemConsume {
            L1ItemInstance itemInstance;
            int count;

            ItemConsume(L1ItemInstance itemInstance, int count) {
                this.itemInstance = itemInstance;
                this.count = count;
            }
        }
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

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            final L1ItemInstance item = ItemTable.get().createItem(newHoly);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            World.get().broadcastPacketToAll(
                    new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了聖物卡\\fN【" + item.getLogName() + "】"));
            pc.sendPacketsX8(new S_Sound(20360));
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
            pc.sendPackets(new S_PacketBoxGree(15));
        } else {
            pc.sendPackets(new S_SystemMessage("合成聖物卡失敗了。"));
            int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20468));
            pc.sendPackets(new S_PacketBoxGree(16));
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }
}