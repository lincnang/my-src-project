package com.lineage.data.npc;

import com.lineage.config.ConfigMagic;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MagicHeChengTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1MagicHeCheng;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 魔法卡合成NPC主控類
 * 指令全部改為 magic_ 開頭，跟娃娃/聖物統一
 * 技術老爹 製作
 */
public class Npc_MagicCombind extends NpcExecutor {
    private final int[] Magic1 = ConfigMagic.Magic_LIST_1;
    private final int[] Magic2 = ConfigMagic.Magic_LIST_2;
    private final int[] Magic3 = ConfigMagic.Magic_LIST_3;
    private final int[] Magic4 = ConfigMagic.Magic_LIST_4;
    private final int[] Magic5 = ConfigMagic.Magic_LIST_5;

    public static NpcExecutor get() {
        return new Npc_MagicCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang1"));
    }

    /** 靜態指令入口，建議一律走這裡！ */
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        // 首頁
        if ("magiccombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang1"));
            return true;
        }
        if ("magicchang1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang1"));
            return true;
        }
        // 分頁刷新
        if ("magic_A1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_B1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_C1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_D1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        // 機率調整
        if ("magic_A11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() < ConfigMagic.ZUIDAJILV2)
                pc.setMagicrun2(pc.getMagicrun2() + ConfigMagic.ZENGJIAJILV2);
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_A12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() > ConfigMagic.ZUIXIAOJILV2)
                pc.setMagicrun2(pc.getMagicrun2() - ConfigMagic.ZENGJIAJILV2);
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_B11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() < ConfigMagic.ZUIDAJILV3)
                pc.setMagicrun3(pc.getMagicrun3() + ConfigMagic.ZENGJIAJILV3);
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_B12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() > ConfigMagic.ZUIXIAOJILV3)
                pc.setMagicrun3(pc.getMagicrun3() - ConfigMagic.ZENGJIAJILV3);
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_C11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() < ConfigMagic.ZUIDAJILV4)
                pc.setMagicrun4(pc.getMagicrun4() + ConfigMagic.ZENGJIAJILV4);
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_C12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() > ConfigMagic.ZUIXIAOJILV4)
                pc.setMagicrun4(pc.getMagicrun4() - ConfigMagic.ZENGJIAJILV4);
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_D11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() < ConfigMagic.ZUIDAJILV5)
                pc.setMagicrun5(pc.getMagicrun5() + ConfigMagic.ZENGJIAJILV5);
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        if ("magic_D12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() > ConfigMagic.ZUIXIAOJILV5)
                pc.setMagicrun5(pc.getMagicrun5() - ConfigMagic.ZENGJIAJILV5);
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        // 合成主流程
        if (cmd.equalsIgnoreCase("magic_A") || cmd.equalsIgnoreCase("magic_A_total")
                || cmd.equalsIgnoreCase("magic_B") || cmd.equalsIgnoreCase("magic_B_total")
                || cmd.equalsIgnoreCase("magic_C") || cmd.equalsIgnoreCase("magic_C_total")
                || cmd.equalsIgnoreCase("magic_D") || cmd.equalsIgnoreCase("magic_D_total")) {
            new Npc_MagicCombind().action(pc, null, cmd, 0);
            return true;
        }
        return false;
    }

    /** 合成主邏輯，支援 magic_A, magic_B, magic_C, magic_D 指令 */
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldMagics = null;
        int newMagic = 0;
        int chance = 0;
        int needcount = 0;
        switch (cmd) {
            case "magic_A":
            case "magic_A_total":
                chance = ConfigMagic.CONSUME2 + pc.getMagicrun2();
                needcount = ConfigMagic.SHULIANG2 + pc.getMagicCount();
                oldMagics = Magic1;
                newMagic = Magic2[ThreadLocalRandom.current().nextInt(Magic2.length)];
                break;
            case "magic_B":
            case "magic_B_total":
                chance = ConfigMagic.CONSUME3 + pc.getMagicrun3();
                needcount = ConfigMagic.SHULIANG3 + pc.getMagicCount2();
                oldMagics = Magic2;
                newMagic = Magic3[ThreadLocalRandom.current().nextInt(Magic3.length)];
                break;
            case "magic_C":
            case "magic_C_total":
                chance = ConfigMagic.CONSUME4 + pc.getMagicrun4();
                needcount = ConfigMagic.SHULIANG4 + pc.getMagicCount3();
                oldMagics = Magic3;
                newMagic = Magic4[ThreadLocalRandom.current().nextInt(Magic4.length)];
                break;
            case "magic_D":
            case "magic_D_total":
                chance = ConfigMagic.CONSUME5 + pc.getMagicrun5();
                needcount = ConfigMagic.SHULIANG5 + pc.getMagicCount4();
                oldMagics = Magic4;
                newMagic = Magic5[ThreadLocalRandom.current().nextInt(Magic5.length)];
                break;
            default:
                pc.sendPackets(new S_SystemMessage("指令錯誤!"));
                return;
        }

        // 堆疊扣除材料
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
        if (oldMagics != null) {
            int totalCount = 0;
            outer:
            for (int oldMagic : oldMagics) {
                L1ItemInstance[] cards = pc.getInventory().findItemsId(oldMagic);
                if (cards != null) {
                    for (L1ItemInstance card : cards) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) card.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(card, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        if (!enough) {
            pc.sendPackets(new S_SystemMessage("你的技能卡不足【" + needcount + "】個"));
            String htmlPage = "";
            String[] data = new String[3];

            // 根據cmd決定回到哪個頁面
            if (cmd.equalsIgnoreCase("A") || cmd.equalsIgnoreCase("A_total")) {
                htmlPage = "magicchang2";
                data[0] = String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2());
            } else if (cmd.equalsIgnoreCase("B") || cmd.equalsIgnoreCase("B_total")) {
                htmlPage = "magicchang3";
                data[0] = String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3());
            } else if (cmd.equalsIgnoreCase("C") || cmd.equalsIgnoreCase("C_total")) {
                htmlPage = "magicchang4";
                data[0] = String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4());
            } else if (cmd.equalsIgnoreCase("D") || cmd.equalsIgnoreCase("D_total")) {
                htmlPage = "magicchang5";
                data[0] = String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5());
            }

            if (!htmlPage.isEmpty())
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlPage, data));
            return;
        }




        // 材料足夠才進入合成
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 合成完後重置參數
        pc.setMagicCount(0);
        pc.setMagicCount2(0);
        pc.setMagicCount3(0);
        pc.setMagicCount4(0);
        pc.setMagicrun2(0);
        pc.setMagicrun3(0);
        pc.setMagicrun4(0);
        pc.setMagicrun5(0);

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            // 成功
            final L1ItemInstance item = ItemTable.get().createItem(newMagic);
            int itemId = item.getItem().getItemId();
            final StringBuilder stringBuilder = new StringBuilder();
            final L1MagicHeCheng card1 = MagicHeChengTable.getInstance().getTemplate(itemId);
            if (card1 != null) {
                stringBuilder.append(card1.getGfxid()).append(",");
                if (card1.getNot() != 0) {
                    World.get().broadcastPacketToAll(
                            new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了技能卡\\fN【" + item.getLogName() + "】"));
                }
            }
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20360));
            pc.sendPackets(new S_PacketBoxGree(15));
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
        } else {
            // 失敗返還隨機素材卡
            pc.sendPackets(new S_SystemMessage("合成技能卡失敗了。"));
            pc.sendPackets(new S_PacketBoxGree(16));
            int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20468));
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }
}
