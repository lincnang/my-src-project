package com.lineage.data.npc;

import com.lineage.config.Configpoly;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.polyHeChengTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1polyHeCheng;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 變身卡合成系統
 * 對齊技能卡合成系統的設計
 * 技術老爹 製作
 */
public class Npc_PolyCombind extends NpcExecutor {
    private final Random _random = new Random();
    // 五個階段卡片ID陣列
    private final int[] poly1 = Configpoly.poly_LIST_1;
    private final int[] poly2 = Configpoly.poly_LIST_2;
    private final int[] poly3 = Configpoly.poly_LIST_3;
    private final int[] poly4 = Configpoly.poly_LIST_4;
    private final int[] poly5 = Configpoly.poly_LIST_5;

    public static NpcExecutor get() {
        return new Npc_PolyCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        // ======== 機率調整區塊（與技能卡一致） ========
        if (cmd.equalsIgnoreCase("A1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A11")) {
            if (Configpoly.CONSUME2 + pc.getpolyrun2() >= Configpoly.ZUIDAJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階段變身卡最高機率" + Configpoly.ZUIDAJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount(pc.getpolyCount() + 1);
            pc.setpolyrun2(pc.getpolyrun2() + Configpoly.ZENGJIAJILV2);
            data[0] = String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A12")) {
            if (Configpoly.CONSUME2 + pc.getpolyrun2() <= Configpoly.ZUIXIAOJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階段變身卡最低機率" + Configpoly.ZUIXIAOJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount(pc.getpolyCount() - 1);
            pc.setpolyrun2(pc.getpolyrun2() - Configpoly.ZENGJIAJILV2);
            data[0] = String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind2", data));
            return;
        }
        // 其餘 B1~D12 相同（省略，與技能卡架構同步）

        // ======= 合成主邏輯 =======
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
        int chance = 0;
        int needcount = 0;
        int[] oldpolys = null;
        int newpoly = 0;

        // 指令->階段->合成需求
        switch (cmd) {
            case "A":
                chance = Configpoly.CONSUME2 + pc.getpolyrun2();
                needcount = Configpoly.SHULIANG2 + pc.getpolyCount();
                oldpolys = poly1;
                newpoly = poly2[ThreadLocalRandom.current().nextInt(poly2.length)];
                break;
            case "B":
                chance = Configpoly.CONSUME3 + pc.getpolyrun3();
                needcount = Configpoly.SHULIANG3 + pc.getpolyCount2();
                oldpolys = poly2;
                newpoly = poly3[ThreadLocalRandom.current().nextInt(poly3.length)];
                break;
            case "C":
                chance = Configpoly.CONSUME4 + pc.getpolyrun4();
                needcount = Configpoly.SHULIANG4 + pc.getpolyCount3();
                oldpolys = poly3;
                newpoly = poly4[ThreadLocalRandom.current().nextInt(poly4.length)];
                break;
            case "D":
                chance = Configpoly.CONSUME5 + pc.getpolyrun5();
                needcount = Configpoly.SHULIANG5 + pc.getpolyCount4();
                oldpolys = poly4;
                newpoly = poly5[ThreadLocalRandom.current().nextInt(poly5.length)];
                break;
            default:
                return;
        }

        // 堆疊扣除材料判斷
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
                        if (totalCount >= needcount) {
                            break outer;
                        }
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        // 材料充足才進行合成
        if (enough) {
            for (ItemConsume ic : consumeList) {
                pc.getInventory().removeItem(ic.itemInstance, ic.count);
            }
            // 重置所有階段合成機率參數
            pc.setpolyCount(0);
            pc.setpolyCount2(0);
            pc.setpolyCount3(0);
            pc.setpolyCount4(0);
            pc.setpolyrun2(0);
            pc.setpolyrun3(0);
            pc.setpolyrun4(0);
            pc.setpolyrun5(0);

            try {
                if (ThreadLocalRandom.current().nextInt(100) < chance) {
                    // 合成成功
                    final L1ItemInstance item = ItemTable.get().createItem(newpoly);
                    int itemId = item.getItem().getItemId();
                    final StringBuilder stringBuilder = new StringBuilder();
                    final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(itemId);
                    if (card1 != null) {
                        stringBuilder.append(card1.getGfxid()).append(",");
                        if (card1.getNot() != 0) {
                            World.get().broadcastPacketToAll(
                                    new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了變身卡\\fN【" + item.getLogName() + "】"));
                        }
                    }
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPacketsX8(new S_Sound(20360));
                    pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
                    pc.sendPackets(new S_PacketBoxGree(15));
                } else {
                    // 合成失敗返還一張隨機素材卡
                    pc.sendPackets(new S_SystemMessage("合成變身卡失敗了。"));
                    int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
                    final L1ItemInstance item = ItemTable.get().createItem(itemId);
                    final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(itemId);
                    if (card1 != null) {
                        // 這裡可以擴充失敗效果
                    }
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPacketsX8(new S_Sound(20468));
                    pc.sendPackets(new S_PacketBoxGree(16));
                    pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            pc.sendPackets(new S_SystemMessage("你的變身卡不足【" + needcount + "】個"));
        }
    }
}
