package com.lineage.data.npc;

import com.add.Tsai.ACardTable;
import com.add.Tsai.CardBookCmd;
import com.lineage.config.Configpoly;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.polyHeChengTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1polyHeCheng;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Npc_PolyCombind extends NpcExecutor {
    // 供合成流程使用的簡單結構
    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;
        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }
    private final int[] poly1 = Configpoly.poly_LIST_1;
    private final int[] poly2 = Configpoly.poly_LIST_2;
    private final int[] poly3 = Configpoly.poly_LIST_3;
    private final int[] poly4 = Configpoly.poly_LIST_4;
    private final int[] poly5 = Configpoly.poly_LIST_5;

    public static Npc_PolyCombind get() {
        return new Npc_PolyCombind();
    }
    public int type() { return 3; }

    // NPC 點擊進入主畫面
    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind1"));
    }

    // ========================= 支援 CMD 直接進入入口 =========================
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        if ("cardset2".equalsIgnoreCase(cmd)) {
            // 呼叫 dollBookCmd 的 DOLLAllSet (直接調用現有功能)
            CardBookCmd.get().CardAllSet(pc); // 你要import com.add.Tsai.dollBookCmd
            return true;
        }

        if ("card_01".equalsIgnoreCase(cmd)) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(ACardTable.get().getCardHaveGroup(pc, i + 1)).append(",");
            stringBuilder.append(ACardTable.get().getCardGroupSize(i + 1)).append(",");
        }

        final String[] clientStrAry = stringBuilder.toString().split(",");
        pc.sendPackets(new S_NPCTalkReturn(pc, "card_01", clientStrAry));
            return true;
        }

        if ("polycombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind1"));
            return true;
        }
        // 分頁
        if ("poly_A1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind2", data));
            return true;
        }
        if ("poly_B1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind3", data));
            return true;
        }
        if ("poly_C1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind4", data));
            return true;
        }
        if ("poly_D1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind5", data));
            return true;
        }

        // 機率調整
        if ("poly_A11".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME2 + pc.getpolyrun2() < Configpoly.ZUIDAJILV2)
                pc.setpolyrun2(pc.getpolyrun2() + Configpoly.ZENGJIAJILV2);
            String[] data = { String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind2", data));
            return true;
        }
        if ("poly_A12".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME2 + pc.getpolyrun2() > Configpoly.ZUIXIAOJILV2)
                pc.setpolyrun2(pc.getpolyrun2() - Configpoly.ZENGJIAJILV2);
            String[] data = { String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind2", data));
            return true;
        }
        if ("poly_B11".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME3 + pc.getpolyrun3() < Configpoly.ZUIDAJILV3)
                pc.setpolyrun3(pc.getpolyrun3() + Configpoly.ZENGJIAJILV3);
            String[] data = { String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind3", data));
            return true;
        }
        if ("poly_B12".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME3 + pc.getpolyrun3() > Configpoly.ZUIXIAOJILV3)
                pc.setpolyrun3(pc.getpolyrun3() - Configpoly.ZENGJIAJILV3);
            String[] data = { String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind3", data));
            return true;
        }
        if ("poly_C11".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME4 + pc.getpolyrun4() < Configpoly.ZUIDAJILV4)
                pc.setpolyrun4(pc.getpolyrun4() + Configpoly.ZENGJIAJILV4);
            String[] data = { String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind4", data));
            return true;
        }
        if ("poly_C12".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME4 + pc.getpolyrun4() > Configpoly.ZUIXIAOJILV4)
                pc.setpolyrun4(pc.getpolyrun4() - Configpoly.ZENGJIAJILV4);
            String[] data = { String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind4", data));
            return true;
        }
        if ("poly_D11".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME5 + pc.getpolyrun5() < Configpoly.ZUIDAJILV5)
                pc.setpolyrun5(pc.getpolyrun5() + Configpoly.ZENGJIAJILV5);
            String[] data = { String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind5", data));
            return true;
        }
        if ("poly_D12".equalsIgnoreCase(cmd)) {
            if (Configpoly.CONSUME5 + pc.getpolyrun5() > Configpoly.ZUIXIAOJILV5)
                pc.setpolyrun5(pc.getpolyrun5() - Configpoly.ZENGJIAJILV5);
            String[] data = { String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polycombind5", data));
            return true;
        }

        // 合成主流程直接呼叫 action
        if (cmd.equalsIgnoreCase("poly_A") || cmd.equalsIgnoreCase("poly_A_total") ||
                cmd.equalsIgnoreCase("poly_B") || cmd.equalsIgnoreCase("poly_B_total") ||
                cmd.equalsIgnoreCase("poly_C") || cmd.equalsIgnoreCase("poly_C_total") ||
                cmd.equalsIgnoreCase("poly_D") || cmd.equalsIgnoreCase("poly_D_total")) {
            new Npc_PolyCombind().action(pc, null, cmd, 0);
            return true;
        }
        return false;
    }

    // ======================== 主合成流程 ========================
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldpolys = null;
        int newpoly = 0;
        int chance = 0;
        int needcount = 0;
        switch (cmd) {
            case "poly_A":
            case "poly_A_total":
                chance = Configpoly.CONSUME2 + pc.getpolyrun2();
                needcount = Configpoly.SHULIANG2 + pc.getpolyCount();
                oldpolys = poly1;
                newpoly = poly2[ThreadLocalRandom.current().nextInt(poly2.length)];
                break;
            case "poly_B":
            case "poly_B_total":
                chance = Configpoly.CONSUME3 + pc.getpolyrun3();
                needcount = Configpoly.SHULIANG3 + pc.getpolyCount2();
                oldpolys = poly2;
                newpoly = poly3[ThreadLocalRandom.current().nextInt(poly3.length)];
                break;
            case "poly_C":
            case "poly_C_total":
                chance = Configpoly.CONSUME4 + pc.getpolyrun4();
                needcount = Configpoly.SHULIANG4 + pc.getpolyCount3();
                oldpolys = poly3;
                newpoly = poly4[ThreadLocalRandom.current().nextInt(poly4.length)];
                break;
            case "poly_D":
            case "poly_D_total":
                chance = Configpoly.CONSUME5 + pc.getpolyrun5();
                needcount = Configpoly.SHULIANG5 + pc.getpolyCount4();
                oldpolys = poly4;
                newpoly = poly5[ThreadLocalRandom.current().nextInt(poly5.length)];
                break;
            default:
                pc.sendPackets(new S_SystemMessage("指令錯誤!"));
                return;
        }

        // 消耗卡片統計（使用類別頂端的 ItemConsume 結構）
        boolean enough = false;
        ArrayList<ItemConsume> consumeList = new ArrayList<>();
        if (oldpolys != null) {
            int totalCount = 0;
            outer:
            for (int oldpoly : oldpolys) {
                L1ItemInstance[] polys = pc.getInventory().findItemsId(oldpoly);
                if (polys != null) {
                    for (L1ItemInstance poly : polys) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) poly.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(poly, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        if (!enough) {
            pc.sendPackets(new S_SystemMessage("你的變身卡不足【" + needcount + "】個"));
            String htmlPage = "";
            String[] data = new String[3];

            // 判斷回到哪一個階段畫面
            if (cmd.equalsIgnoreCase("A") || cmd.equalsIgnoreCase("A_Total")) {
                htmlPage = "polycombind2";
                data[0] = String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2());
            } else if (cmd.equalsIgnoreCase("B") || cmd.equalsIgnoreCase("B_Total")) {
                htmlPage = "polycombind3";
                data[0] = String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3());
            } else if (cmd.equalsIgnoreCase("C") || cmd.equalsIgnoreCase("C_Total")) {
                htmlPage = "polycombind4";
                data[0] = String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4());
            } else if (cmd.equalsIgnoreCase("D") || cmd.equalsIgnoreCase("D_Total")) {
                htmlPage = "polycombind5";
                data[0] = String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5());
            }

            if (!htmlPage.isEmpty())
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlPage, data));
            return;
        }


        // 扣除材料
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 重置全部階段加成
        pc.setpolyCount(0); pc.setpolyCount2(0); pc.setpolyCount3(0); pc.setpolyCount4(0);
        pc.setpolyrun2(0); pc.setpolyrun3(0); pc.setpolyrun4(0); pc.setpolyrun5(0);

        boolean isPlayAnimation = !cmd.contains("_total");
        // 非阻塞：先送動畫，再延遲執行合成結果
        if (isPlayAnimation) {
            try { pc.sendHtmlCastGfx(new String[3]); } catch (Exception ignored) {}
            final int fNewpoly = newpoly;
            final int fChance = chance;
            final ArrayList<ItemConsume> fConsumeList = new ArrayList<>(consumeList);
            GeneralThreadPool.get().schedule(() -> doPolyCombine(pc, isPlayAnimation, fNewpoly, fChance, fConsumeList), 2100);
            return;
        }
        // 直接執行合成結果（無動畫/批量）
        doPolyCombine(pc, false, newpoly, chance, consumeList);
    }

    private void doPolyCombine(L1PcInstance pc, boolean isPlayAnimation, int newpoly, int chance, ArrayList<ItemConsume> consumeList) {
        // ===== 合成結果 =====
        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            // 成功
            final L1ItemInstance item = ItemTable.get().createItem(newpoly);
            if (item == null || item.getItem() == null) {
                pc.sendPackets(new S_SystemMessage("合成成功，但道具模板不存在（itemId=" + newpoly + "）。請聯絡管理員。"));
                return;
            }
            final int itemId = item.getItem().getItemId();
            final String newName = item.getLogName();
            final StringBuilder stringBuilder = new StringBuilder();
            // 安全組 msg（給成功 HTML 用）
            final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(itemId);
            if (card1 != null) {
                stringBuilder.append(card1.getGfxid()).append(",");
                if (card1.getNot() != 0) {
                    World.get().broadcastPacketToAll(
                            new S_SystemMessage("【公告】玩家 " + pc.getName() + " 成功合成了變身卡「" + newName + "」!")
                    );
                }
            }
            final String[] msg = stringBuilder.toString().split(",");
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20360));
            if (isPlayAnimation) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "wwhccg", msg));
            }
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + newName));
            pc.sendPackets(new S_PacketBoxGree(15));
        } else {
            // 失敗返還隨機卡
            pc.sendPackets(new S_SystemMessage("合成變身卡失敗了。"));
            pc.sendPackets(new S_PacketBoxGree(16));
            if (consumeList.isEmpty()) {
                return;
            }
            final ItemConsume ic = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size()));
            if (ic == null || ic.itemInstance == null) {
                return;
            }

            final int backId = ic.itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(backId);
            if (item == null || item.getItem() == null) {
                pc.sendPackets(new S_SystemMessage("返還物品模板不存在（itemId=" + backId + "）。"));
                return;
            }

            // 安全組 msg（給失敗 HTML 用）
            String[] msg;
            final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(item.getItem().getItemId());
            if (card1 != null) {
                String gfxStr = String.valueOf(card1.getGfxid());
                msg = (gfxStr != null && !gfxStr.isEmpty() && !"0".equals(gfxStr)) ? new String[]{ gfxStr } : new String[0];
            } else {
                msg = new String[0];
            }

            item.setIdentified(true);
            pc.getInventory().storeItem(item);

            pc.sendPacketsX8(new S_Sound(20468));
            if (isPlayAnimation) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "wwhcsb", msg));
            }
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }
}