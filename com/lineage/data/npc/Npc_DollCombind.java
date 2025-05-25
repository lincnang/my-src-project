package com.lineage.data.npc;

import com.lineage.config.ConfigDoll;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.serverpackets.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 後續由 技術老爹 製作
 *
 * @author Administrator
 */
public class Npc_DollCombind extends NpcExecutor {
    private final Random _random = new Random();
    private final int[] doll1 = ConfigDoll.DOLL_LIST_1;
    private final int[] doll2 = ConfigDoll.DOLL_LIST_2;
    private final int[] doll3 = ConfigDoll.DOLL_LIST_3;
    private final int[] doll4 = ConfigDoll.DOLL_LIST_4;
    private final int[] doll5 = ConfigDoll.DOLL_LIST_5;

    public static NpcExecutor get() {
        return new Npc_DollCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind1"));
    }
    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;

        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        //////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("A1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A11")) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() >= ConfigDoll.ZUIDAJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階段娃娃最高機率" + ConfigDoll.ZUIDAJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount(pc.getDollCount() + 1);
            pc.setDollrun2(pc.getDollrun2() + ConfigDoll.ZENGJIAJILV2);
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A12")) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() <= ConfigDoll.ZUIXIAOJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階段娃娃最低機率" + ConfigDoll.ZUIXIAOJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount(pc.getDollCount() - 1);
            pc.setDollrun2(pc.getDollrun2() - ConfigDoll.ZENGJIAJILV2);
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind2", data));
            return;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("B1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind3", data));
            return;
        }
        if (cmd.equalsIgnoreCase("B11")) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() >= ConfigDoll.ZUIDAJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階段娃娃最高機率" + ConfigDoll.ZUIDAJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount2(pc.getDollCount2() + 1);
            pc.setDollrun3(pc.getDollrun3() + ConfigDoll.ZENGJIAJILV3);
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind3", data));
            return;
        }
        if (cmd.equalsIgnoreCase("B12")) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() <= ConfigDoll.ZUIXIAOJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階段娃娃最低機率" + ConfigDoll.ZUIXIAOJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount2(pc.getDollCount2() - 1);
            pc.setDollrun3(pc.getDollrun3() - ConfigDoll.ZENGJIAJILV3);
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind3", data));
            return;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("C1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind4", data));
            return;
        }
        if (cmd.equalsIgnoreCase("C11")) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() >= ConfigDoll.ZUIDAJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階段娃娃最高機率" + ConfigDoll.ZUIDAJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount3(pc.getDollCount3() + 1);
            pc.setDollrun4(pc.getDollrun4() + ConfigDoll.ZENGJIAJILV4);
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind4", data));
            return;
        }
        if (cmd.equalsIgnoreCase("C12")) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() <= ConfigDoll.ZUIXIAOJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階段娃娃最低機率" + ConfigDoll.ZUIXIAOJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount3(pc.getDollCount3() - 1);
            pc.setDollrun4(pc.getDollrun4() - ConfigDoll.ZENGJIAJILV4);
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind4", data));
            return;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("D1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind5", data));
            return;
        }
        if (cmd.equalsIgnoreCase("D11")) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() >= ConfigDoll.ZUIDAJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階段娃娃最高機率" + ConfigDoll.ZUIDAJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount4(pc.getDollCount4() + 1);
            pc.setDollrun5(pc.getDollrun5() + ConfigDoll.ZENGJIAJILV5);
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind5", data));
            return;
        }
        if (cmd.equalsIgnoreCase("D12")) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() <= ConfigDoll.ZUIXIAOJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階段娃娃最低機率" + ConfigDoll.ZUIXIAOJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setDollCount4(pc.getDollCount4() - 1);
            pc.setDollrun5(pc.getDollrun5() - ConfigDoll.ZENGJIAJILV5);
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind5", data));
            return;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
// ========== 主要合成邏輯：娃娃堆疊數量正確處理 ==========

// ======= 合成主邏輯（支援堆疊道具正確消耗與返還）=======

        boolean enough = false;// 材料是否足夠
        ArrayList<ItemConsume> consumeList = new ArrayList<>();// 要消耗的娃娃物品與數量
        int chance = 0;// 合成機率
        int needcount = 0;// 需要數量
        int[] olddolls = null;// 上一階段的娃娃
        int newdoll = 0;// 給予的新娃娃
        final String[] data = new String[3];

        switch (cmd) {
            case "A":
            case "A_Total":
                chance = ConfigDoll.CONSUME2 + pc.getDollrun2();
                needcount = ConfigDoll.SHULIANG2 + pc.getDollCount();
                olddolls = doll1;
                newdoll = doll2[ThreadLocalRandom.current().nextInt(doll2.length)];
                break;
            case "B":
            case "B_Total":
                chance = ConfigDoll.CONSUME3 + pc.getDollrun3();
                needcount = ConfigDoll.SHULIANG3 + pc.getDollCount2();
                olddolls = doll2;
                newdoll = doll3[ThreadLocalRandom.current().nextInt(doll3.length)];
                break;
            case "C":
            case "C_Total":
                chance = ConfigDoll.CONSUME4 + pc.getDollrun4();
                needcount = ConfigDoll.SHULIANG4 + pc.getDollCount3();
                olddolls = doll3;
                newdoll = doll4[ThreadLocalRandom.current().nextInt(doll4.length)];
                break;
            case "D":
            case "D_Total":
                chance = ConfigDoll.CONSUME5 + pc.getDollrun5();
                needcount = ConfigDoll.SHULIANG5 + pc.getDollCount4();
                olddolls = doll4;
                newdoll = doll5[ThreadLocalRandom.current().nextInt(doll5.length)];
                break;
                default:
                // 不是合成指令就直接結束
                return;
        }

// 檢查出場娃娃
        for (Object babyObject : pc.getPetList().values().toArray()) {
            if (babyObject instanceof L1DollInstance || babyObject instanceof L1DollInstance2) {
                if (!pc.getPetList().isEmpty()) {
                    pc.sendPackets(new S_SystemMessage("請先收回娃娃"));
                    return;
                }
            }
        }

        boolean isPlayAnimation = true;
        if (cmd.contains("_Total"))
            isPlayAnimation = false;

// ====== 堆疊數量判斷與消耗紀錄 ======
        if (olddolls != null) {
            int totalCount = 0;
            outer:
            for (int olddoll : olddolls) {
                L1ItemInstance[] dolls = pc.getInventory().findItemsId(olddoll);
                if (dolls != null) {
                    for (L1ItemInstance doll : dolls) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) doll.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(doll, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) {
                            break outer;
                        }
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        if (enough) {
            boolean consumed = true;
            // 扣除每個堆疊物品對應數量
            for (ItemConsume ic : consumeList) {
                if (!pc.getInventory().consumeItem(ic.itemInstance.getItemId(), ic.count)) {
                    consumed = false;
                    break;
                }
            }
            if (!consumed) {
                pc.sendPackets(new S_SystemMessage("消耗物品失敗，請聯絡管理員"));
                return;
            }

            try {
                if (isPlayAnimation) {
                    pc.sendHtmlCastGfx(data);
                    TimeUnit.MILLISECONDS.sleep(2100);
                }

                // 重置調整參數
                pc.setDollCount(0);
                pc.setDollCount2(0);
                pc.setDollCount3(0);
                pc.setDollCount4(0);
                pc.setDollrun2(0);
                pc.setDollrun3(0);
                pc.setDollrun4(0);
                pc.setDollrun5(0);

                if (ThreadLocalRandom.current().nextInt(100) < chance) { // 合成成功
                    final L1ItemInstance item = ItemTable.get().createItem(newdoll);
                    int itemId = item.getItem().getItemId();
                    final StringBuilder stringBuilder = new StringBuilder();
                    final com.lineage.server.templates.L1DollHeCheng card1 = com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
                    if (card1 != null) {
                        stringBuilder.append(card1.getGfxid()).append(",");
                        if (card1.getNot() != 0) {
                            com.lineage.server.world.World.get().broadcastPacketToAll(
                                    new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了娃娃\\fN【" + item.getLogName() + "】")
                            );
                        }
                    }
                    final String[] msg = stringBuilder.toString().split(",");
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPacketsX8(new S_Sound(20360));
                    if (isPlayAnimation)
                        pc.sendPackets(new S_NPCTalkReturn(pc, "wwhccg", msg));
                    pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
                    pc.sendPackets(new S_PacketBoxGree(15));
                } else { // 合成失敗返還一個消耗的道具
                    pc.sendPackets(new S_SystemMessage("合成娃娃失敗了。"));
                    pc.sendPackets(new S_PacketBoxGree(16));
                    int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
                    final L1ItemInstance item = ItemTable.get().createItem(itemId);
                    final StringBuilder stringBuilder = new StringBuilder();
                    final com.lineage.server.templates.L1DollHeCheng card1 = com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
                    if (card1 != null) {
                        stringBuilder.append(card1.getGfxid()).append(",");
                    }
                    final String[] msg = stringBuilder.toString().split(",");
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPacketsX8(new S_Sound(20468));
                    if (isPlayAnimation)
                        pc.sendPackets(new S_NPCTalkReturn(pc, "wwhcsb", msg));
                    pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            pc.sendPackets(new S_SystemMessage("你的娃娃不足【" + needcount + "】個"));
        }
    }
}

