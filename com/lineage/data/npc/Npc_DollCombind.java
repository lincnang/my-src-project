package com.lineage.data.npc;

import com.add.Tsai.dollBookCmd;
import com.add.Tsai.dollTable;
import com.lineage.config.ConfigDoll;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.serverpackets.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 娃娃卡合成系統
 * 技術老爹製作
 */
public class Npc_DollCombind extends NpcExecutor {
    private final int[] doll1 = ConfigDoll.DOLL_LIST_1;
    private final int[] doll2 = ConfigDoll.DOLL_LIST_2;
    private final int[] doll3 = ConfigDoll.DOLL_LIST_3;
    private final int[] doll4 = ConfigDoll.DOLL_LIST_4;
    private final int[] doll5 = ConfigDoll.DOLL_LIST_5;

    // 建議所有娃娃指令都加 doll_ 前綴
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        if ("doolset2".equalsIgnoreCase(cmd)) {
            // 呼叫 dollBookCmd 的 DOLLAllSet (直接調用現有功能)
            dollBookCmd.get().DOLLAllSet(pc); // 你要import com.add.Tsai.dollBookCmd
            return true;
        }
        if ("Book_D01".equalsIgnoreCase(cmd)) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                stringBuilder.append(dollTable.get().getDollHaveGroup(pc, i + 1)).append(",");
                stringBuilder.append(dollTable.get().getDollGroupSize(i + 1)).append(",");
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D01", clientStrAry));
            return true;
        }
        // 首頁
        if ("dollcombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind1"));
            return true;
        }
        // 分頁刷新
        if ("doll_A1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_B1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_C1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_D1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }

        // 合成機率調整
        if ("doll_A11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() < ConfigDoll.ZUIDAJILV2) {
                pc.setDollrun2(pc.getDollrun2() + ConfigDoll.ZENGJIAJILV2);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_A12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() > ConfigDoll.ZUIXIAOJILV2) {
                pc.setDollrun2(pc.getDollrun2() - ConfigDoll.ZENGJIAJILV2);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_B11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() < ConfigDoll.ZUIDAJILV3) {
                pc.setDollrun3(pc.getDollrun3() + ConfigDoll.ZENGJIAJILV3);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_B12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() > ConfigDoll.ZUIXIAOJILV3) {
                pc.setDollrun3(pc.getDollrun3() - ConfigDoll.ZENGJIAJILV3);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_C11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() < ConfigDoll.ZUIDAJILV4) {
                pc.setDollrun4(pc.getDollrun4() + ConfigDoll.ZENGJIAJILV4);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_C12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() > ConfigDoll.ZUIXIAOJILV4) {
                pc.setDollrun4(pc.getDollrun4() - ConfigDoll.ZENGJIAJILV4);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_D11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() < ConfigDoll.ZUIDAJILV5) {
                pc.setDollrun5(pc.getDollrun5() + ConfigDoll.ZENGJIAJILV5);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }
        if ("doll_D12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() > ConfigDoll.ZUIXIAOJILV5) {
                pc.setDollrun5(pc.getDollrun5() - ConfigDoll.ZENGJIAJILV5);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }

        // 合成主流程
        if (cmd.equalsIgnoreCase("doll_A") || cmd.equalsIgnoreCase("doll_A_total") ||
                cmd.equalsIgnoreCase("doll_B") || cmd.equalsIgnoreCase("doll_B_total") ||
                cmd.equalsIgnoreCase("doll_C") || cmd.equalsIgnoreCase("doll_C_total") ||
                cmd.equalsIgnoreCase("doll_D") || cmd.equalsIgnoreCase("doll_D_total")) {

            new Npc_DollCombind().action(pc, null, cmd, 0);

            // 合成完刷新頁面
            String[] data = new String[3];
            if (cmd.toLowerCase().startsWith("doll_a")) {
                data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            } else if (cmd.toLowerCase().startsWith("doll_b")) {
                data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            } else if (cmd.toLowerCase().startsWith("doll_c")) {
                data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            } else if (cmd.toLowerCase().startsWith("doll_d")) {
                data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            }
            return true;
        }
        return false;
    }

    public static NpcExecutor get() {
        return new Npc_DollCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind1"));
    }

    // 主要合成邏輯
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        // ========== 主合成流程 ==========
        boolean enough = false;
        ArrayList<ItemConsume> consumeList = new ArrayList<>();
        int chance = 0;
        int needcount = 0;
        int[] olddolls = null;
        int newdoll = 0;
        final String[] data = new String[3];

        if (cmd.equalsIgnoreCase("doll_A") || cmd.equalsIgnoreCase("doll_A_total")) {
            chance = ConfigDoll.CONSUME2 + pc.getDollrun2();
            needcount = ConfigDoll.SHULIANG2 + pc.getDollCount();
            olddolls = doll1;
            newdoll = doll2[ThreadLocalRandom.current().nextInt(doll2.length)];
        } else if (cmd.equalsIgnoreCase("doll_B") || cmd.equalsIgnoreCase("doll_B_total")) {
            chance = ConfigDoll.CONSUME3 + pc.getDollrun3();
            needcount = ConfigDoll.SHULIANG3 + pc.getDollCount2();
            olddolls = doll2;
            newdoll = doll3[ThreadLocalRandom.current().nextInt(doll3.length)];
        } else if (cmd.equalsIgnoreCase("doll_C") || cmd.equalsIgnoreCase("doll_C_total")) {
            chance = ConfigDoll.CONSUME4 + pc.getDollrun4();
            needcount = ConfigDoll.SHULIANG4 + pc.getDollCount3();
            olddolls = doll3;
            newdoll = doll4[ThreadLocalRandom.current().nextInt(doll4.length)];
        } else if (cmd.equalsIgnoreCase("doll_D") || cmd.equalsIgnoreCase("doll_D_total")) {
            chance = ConfigDoll.CONSUME5 + pc.getDollrun5();
            needcount = ConfigDoll.SHULIANG5 + pc.getDollCount4();
            olddolls = doll4;
            newdoll = doll5[ThreadLocalRandom.current().nextInt(doll5.length)];
        } else {
            return;
        }

        // 檢查娃娃有沒有在外面
        for (Object babyObject : pc.getPetList().values().toArray()) {
            if (babyObject instanceof L1DollInstance || babyObject instanceof L1DollInstance2) {
                if (!pc.getPetList().isEmpty()) {
                    pc.sendPackets(new S_SystemMessage("請先收回娃娃"));
                    return;
                }
            }
        }

        boolean isPlayAnimation = !cmd.toLowerCase().contains("_total");

        // 堆疊數量判斷與消耗紀錄
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
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        // 判斷並消耗材料，做合成
        if (enough) {
            boolean consumed = true;
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
                    final com.lineage.server.templates.L1DollHeCheng card1 =
                            com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
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
                    final com.lineage.server.templates.L1DollHeCheng card1 =
                            com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
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

    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;

        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }
}
