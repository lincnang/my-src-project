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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 魔法卡合成NPC主控類
 * 主要處理各階段技能卡的合成機率調整、材料判斷、消耗與結果回饋
 * 技術老爹 製作
 */
public class Npc_MagicCombind extends NpcExecutor {
    private final Random _random = new Random();
    // 五個階段卡片ID陣列，全部來自ConfigMagic設定
    private final int[] Magic1 = ConfigMagic.Magic_LIST_1;
    private final int[] Magic2 = ConfigMagic.Magic_LIST_2;
    private final int[] Magic3 = ConfigMagic.Magic_LIST_3;
    private final int[] Magic4 = ConfigMagic.Magic_LIST_4;
    private final int[] Magic5 = ConfigMagic.Magic_LIST_5;

    public static NpcExecutor get() {
        return new Npc_MagicCombind();
    }

    public int type() {
        return 3; // 代表特殊NPC功能型態
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        // 初始對話，顯示magicchang1頁面
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        // ======== 機率調整相關指令處理 ========
        if (cmd.equalsIgnoreCase("A1")) {
            // 顯示二階合成當前機率
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A11")) {
            // 增加二階合成機率（累積次數，提升合成率）
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() >= ConfigMagic.ZUIDAJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階段技能卡最高機率" + ConfigMagic.ZUIDAJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount(pc.getMagicCount() + 1);
            pc.setMagicrun2(pc.getMagicrun2() + ConfigMagic.ZENGJIAJILV2);
            data[0] = String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A12")) {
            // 降低二階合成機率（扣除次數，降低合成率）
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() <= ConfigMagic.ZUIXIAOJILV2) {
                pc.sendPackets(new S_SystemMessage("合成二階技能卡最低機率" + ConfigMagic.ZUIXIAOJILV2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount(pc.getMagicCount() - 1);
            pc.setMagicrun2(pc.getMagicrun2() - ConfigMagic.ZENGJIAJILV2);
            data[0] = String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang2", data));
            return;
        }
        // 其餘B1~D12與A段相同邏輯，僅階段/參數不同
        if (cmd.equalsIgnoreCase("B1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang3", data));
            return;
        }
        if (cmd.equalsIgnoreCase("B11")) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() >= ConfigMagic.ZUIDAJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階段技能卡最高機率" + ConfigMagic.ZUIDAJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount2(pc.getMagicCount2() + 1);
            pc.setMagicrun3(pc.getMagicrun3() + ConfigMagic.ZENGJIAJILV3);
            data[0] = String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang3", data));
            return;
        }
        if (cmd.equalsIgnoreCase("B12")) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() <= ConfigMagic.ZUIXIAOJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階技能卡最低機率" + ConfigMagic.ZUIXIAOJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount2(pc.getMagicCount2() - 1);
            pc.setMagicrun3(pc.getMagicrun3() - ConfigMagic.ZENGJIAJILV3);
            data[0] = String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang3", data));
            return;
        }
        if (cmd.equalsIgnoreCase("C1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang4", data));
            return;
        }
        if (cmd.equalsIgnoreCase("C11")) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() >= ConfigMagic.ZUIDAJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階段技能卡最高機率" + ConfigMagic.ZUIDAJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount3(pc.getMagicCount3() + 1);
            pc.setMagicrun4(pc.getMagicrun4() + ConfigMagic.ZENGJIAJILV4);
            data[0] = String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang4", data));
            return;
        }
        if (cmd.equalsIgnoreCase("C12")) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() <= ConfigMagic.ZUIXIAOJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階技能卡最低機率" + ConfigMagic.ZUIXIAOJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount3(pc.getMagicCount3() - 1);
            pc.setMagicrun4(pc.getMagicrun4() - ConfigMagic.ZENGJIAJILV4);
            data[0] = String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang4", data));
            return;
        }
        if (cmd.equalsIgnoreCase("D1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang5", data));
            return;
        }
        if (cmd.equalsIgnoreCase("D11")) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() >= ConfigMagic.ZUIDAJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階段技能卡最高機率" + ConfigMagic.ZUIDAJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount4(pc.getMagicCount4() + 1);
            pc.setMagicrun5(pc.getMagicrun5() + ConfigMagic.ZENGJIAJILV5);
            data[0] = String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang5", data));
            return;
        }
        if (cmd.equalsIgnoreCase("D12")) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() <= ConfigMagic.ZUIXIAOJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階技能卡最低機率" + ConfigMagic.ZUIXIAOJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setMagicCount4(pc.getMagicCount4() - 1);
            pc.setMagicrun5(pc.getMagicrun5() - ConfigMagic.ZENGJIAJILV5);
            data[0] = String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang5", data));
            return;
        }

        // ======= 合成主邏輯區塊 =======
        class ItemConsume {
            L1ItemInstance itemInstance;
            int count;
            ItemConsume(L1ItemInstance itemInstance, int count) {
                this.itemInstance = itemInstance;
                this.count = count;
            }
        }

        boolean enough = false; // 材料是否足夠
        ArrayList<ItemConsume> consumeList = new ArrayList<>();
        int chance = 0;      // 最終合成機率
        int needcount = 0;   // 需要消耗幾張卡片
        int[] oldMagics = null; // 本階段允許作為素材的舊卡ID清單
        int newMagic = 0;    // 最後給予的新卡
        final String[] data = new String[3];

        // 根據指令決定本次合成屬於哪一階段
        switch (cmd) {
            case "A": // 一階合成，消耗Magic1產出Magic2
                chance = ConfigMagic.CONSUME2 + pc.getMagicrun2();
                needcount = ConfigMagic.SHULIANG2 + pc.getMagicCount();
                oldMagics = Magic1;
                newMagic = Magic2[ThreadLocalRandom.current().nextInt(Magic2.length)];
                break;
            case "B": // 二階合成，消耗Magic2產出Magic3
                chance = ConfigMagic.CONSUME3 + pc.getMagicrun3();
                needcount = ConfigMagic.SHULIANG3 + pc.getMagicCount2();
                oldMagics = Magic2;
                newMagic = Magic3[ThreadLocalRandom.current().nextInt(Magic3.length)];
                break;
            case "C": // 三階合成，消耗Magic3產出Magic4
                chance = ConfigMagic.CONSUME4 + pc.getMagicrun4();
                needcount = ConfigMagic.SHULIANG4 + pc.getMagicCount3();
                oldMagics = Magic3;
                newMagic = Magic4[ThreadLocalRandom.current().nextInt(Magic4.length)];
                break;
            case "D": // 四階合成，消耗Magic4產出Magic5
                chance = ConfigMagic.CONSUME5 + pc.getMagicrun5();
                needcount = ConfigMagic.SHULIANG5 + pc.getMagicCount4();
                oldMagics = Magic4;
                newMagic = Magic5[ThreadLocalRandom.current().nextInt(Magic5.length)];
                break;
            default:
                return;
        }

        // 從玩家背包逐一統計素材卡片數量（支援堆疊）
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
                        if (totalCount >= needcount) {
                            break outer;
                        }
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        // 材料足夠才進入合成
        if (enough) {
            for (ItemConsume ic : consumeList) {
                pc.getInventory().removeItem(ic.itemInstance, ic.count);
            }

            // 合成機率判斷
            try {
                // 每次合成後重置該階段所有累積參數
                pc.setMagicCount(0);
                pc.setMagicCount2(0);
                pc.setMagicCount3(0);
                pc.setMagicCount4(0);
                pc.setMagicrun2(0);
                pc.setMagicrun3(0);
                pc.setMagicrun4(0);
                pc.setMagicrun5(0);

                if (ThreadLocalRandom.current().nextInt(100) < chance) { // 合成成功
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
                    pc.sendPacketsX8(new S_Sound(20468));
                    pc.sendPackets(new S_PacketBoxGree(15));
                    pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
                } else { // 合成失敗，返還一張隨機素材卡
                    pc.sendPackets(new S_SystemMessage("合成技能卡失敗了。"));
                    pc.sendPackets(new S_PacketBoxGree(16));
                    int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
                    final L1ItemInstance item = ItemTable.get().createItem(itemId);
                    final L1MagicHeCheng card1 = MagicHeChengTable.getInstance().getTemplate(itemId);
                    if (card1 != null) {
                        // 這裡可以擴充額外失敗效果
                    }
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPacketsX8(new S_Sound(20468));

                    pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            // 材料不足提示
            pc.sendPackets(new S_SystemMessage("你的技能卡不足【" + needcount + "】個"));
        }
    }
}
