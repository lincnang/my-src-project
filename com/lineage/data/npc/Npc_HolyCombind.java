package com.lineage.data.npc;

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

/**
 * 聖物卡合成系統（純合成邏輯，無資料表查詢）
 * @author 技術老爹
 */
public class Npc_HolyCombind extends NpcExecutor {
    // 五個階段聖物卡ID陣列，來自 ConfigHoly 設定檔
    private final int[] holy1 = ConfigHoly.holy_LIST_1;
    private final int[] holy2 = ConfigHoly.holy_LIST_2;
    private final int[] holy3 = ConfigHoly.holy_LIST_3;
    private final int[] holy4 = ConfigHoly.holy_LIST_4;
    private final int[] holy5 = ConfigHoly.holy_LIST_5;

    public static NpcExecutor get() {
        return new Npc_HolyCombind();
    }

    public int type() {
        return 3;
    }

    // NPC對話初始
    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind1")); // HTML請命名 holycombind1.html
    }

    // 處理玩家操作命令
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        // ======== 合成機率調整範例（以二階為例） ========
        if (cmd.equalsIgnoreCase("A1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A11")) {
            if (ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2() >= ConfigHoly.HOLY_MAX_RATE2) {
                pc.sendPackets(new S_SystemMessage("合成二階段聖物卡最高機率" + ConfigHoly.HOLY_MAX_RATE2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount(pc.getHolyCount() + 1);
            pc.setHolyRun2(pc.getHolyRun2() + ConfigHoly.HOLY_STEP_RATE2);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind2", data));
            return;
        }
        if (cmd.equalsIgnoreCase("A12")) {
            if (ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2() <= ConfigHoly.HOLY_MIN_RATE2) {
                pc.sendPackets(new S_SystemMessage("合成二階段聖物卡最低機率" + ConfigHoly.HOLY_MIN_RATE2 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount(pc.getHolyCount() - 1);
            pc.setHolyRun2(pc.getHolyRun2() - ConfigHoly.HOLY_STEP_RATE2);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind2", data));
            return;
        }
        // ======= 三階段合成（B區塊） =======

// 點選增加三階機率
        if (cmd.equalsIgnoreCase("B1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind3", data));
            return;
        }
// 增加三階機率按鈕
        if (cmd.equalsIgnoreCase("B11")) {
            if (ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3() >= ConfigHoly.HOLY_MAX_RATE3) {
                pc.sendPackets(new S_SystemMessage("合成三階段聖物卡最高機率" + ConfigHoly.HOLY_MAX_RATE3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount2(pc.getHolyCount2() + 1);
            pc.setHolyRun3(pc.getHolyRun3() + ConfigHoly.HOLY_STEP_RATE3);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind3", data));
            return;
        }
// 減少三階機率按鈕
        if (cmd.equalsIgnoreCase("B12")) {
            if (ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3() <= ConfigHoly.HOLY_MIN_RATE3) {
                pc.sendPackets(new S_SystemMessage("合成三階段聖物卡最低機率" + ConfigHoly.HOLY_MIN_RATE3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount2(pc.getHolyCount2() - 1);
            pc.setHolyRun3(pc.getHolyRun3() - ConfigHoly.HOLY_STEP_RATE3);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind3", data));
            return;
        }
        // ======= 四階段合成（C區塊） =======

// 點選增加四階機率
        if (cmd.equalsIgnoreCase("C1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind4", data));
            return;
        }
// 增加四階機率按鈕
        if (cmd.equalsIgnoreCase("C11")) {
            if (ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4() >= ConfigHoly.HOLY_MAX_RATE4) {
                pc.sendPackets(new S_SystemMessage("合成四階段聖物卡最高機率" + ConfigHoly.HOLY_MAX_RATE4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount3(pc.getHolyCount3() + 1);
            pc.setHolyRun4(pc.getHolyRun4() + ConfigHoly.HOLY_STEP_RATE4);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind4", data));
            return;
        }
// 減少四階機率按鈕
        if (cmd.equalsIgnoreCase("C12")) {
            if (ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4() <= ConfigHoly.HOLY_MIN_RATE4) {
                pc.sendPackets(new S_SystemMessage("合成四階段聖物卡最低機率" + ConfigHoly.HOLY_MIN_RATE4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount3(pc.getHolyCount3() - 1);
            pc.setHolyRun4(pc.getHolyRun4() - ConfigHoly.HOLY_STEP_RATE4);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind4", data));
            return;
        }
// ======= 五階段合成（D區塊） =======

// 點選增加五階機率
        if (cmd.equalsIgnoreCase("D1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind5", data));
            return;
        }
// 增加五階機率按鈕
        if (cmd.equalsIgnoreCase("D11")) {
            if (ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5() >= ConfigHoly.HOLY_MAX_RATE5) {
                pc.sendPackets(new S_SystemMessage("合成五階段聖物卡最高機率" + ConfigHoly.HOLY_MAX_RATE5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount4(pc.getHolyCount4() + 1);
            pc.setHolyRun5(pc.getHolyRun5() + ConfigHoly.HOLY_STEP_RATE5);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind5", data));
            return;
        }
// 減少五階機率按鈕
        if (cmd.equalsIgnoreCase("D12")) {
            if (ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5() <= ConfigHoly.HOLY_MIN_RATE5) {
                pc.sendPackets(new S_SystemMessage("合成五階段聖物卡最低機率" + ConfigHoly.HOLY_MIN_RATE5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setHolyCount4(pc.getHolyCount4() - 1);
            pc.setHolyRun5(pc.getHolyRun5() - ConfigHoly.HOLY_STEP_RATE5);
            data[0] = String.valueOf(ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "holycombind5", data));
            return;
        }


        // 其餘 B1~D12 可依照上方範例擴寫

        // ======= 合成主流程 =======
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
        int[] oldHolys = null;
        int newHoly = 0;

        // 指令->階段->合成需求（五階段皆可參照下方方式新增）
        switch (cmd) {
            case "A":
                chance = ConfigHoly.HOLY_CONSUME2 + pc.getHolyRun2();
                needcount = ConfigHoly.HOLY_COUNT2 + pc.getHolyCount();
                oldHolys = holy1;
                newHoly = holy2[ThreadLocalRandom.current().nextInt(holy2.length)];
                break;
            case "B":
                chance = ConfigHoly.HOLY_CONSUME3 + pc.getHolyRun3();
                needcount = ConfigHoly.HOLY_COUNT3 + pc.getHolyCount2();
                oldHolys = holy2;
                newHoly = holy3[ThreadLocalRandom.current().nextInt(holy3.length)];
                break;
            case "C":
                chance = ConfigHoly.HOLY_CONSUME4 + pc.getHolyRun4();
                needcount = ConfigHoly.HOLY_COUNT4 + pc.getHolyCount3();
                oldHolys = holy3;
                newHoly = holy4[ThreadLocalRandom.current().nextInt(holy4.length)];
                break;
            case "D":
                chance = ConfigHoly.HOLY_CONSUME5 + pc.getHolyRun5();
                needcount = ConfigHoly.HOLY_COUNT5 + pc.getHolyCount4();
                oldHolys = holy4;
                newHoly = holy5[ThreadLocalRandom.current().nextInt(holy5.length)];
                break;
            default:
                return;
        }

        // 堆疊扣除材料判斷
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
            pc.setHolyCount(0);
            pc.setHolyCount2(0);
            pc.setHolyCount3(0);
            pc.setHolyCount4(0);
            pc.setHolyRun2(0);
            pc.setHolyRun3(0);
            pc.setHolyRun4(0);
            pc.setHolyRun5(0);

            try {
                if (ThreadLocalRandom.current().nextInt(100) < chance) {
                    // 合成成功
                    final L1ItemInstance item = ItemTable.get().createItem(newHoly);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    // 廣播、音效、提示訊息
                    World.get().broadcastPacketToAll(
                            new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了聖物卡\\fN【" + item.getLogName() + "】"));
                    pc.sendPacketsX8(new S_Sound(20360));
                    pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
                    pc.sendPackets(new S_PacketBoxGree(15));
                } else {
                    // 合成失敗返還一張隨機素材卡
                    pc.sendPackets(new S_SystemMessage("合成聖物卡失敗了。"));
                    int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
                    final L1ItemInstance item = ItemTable.get().createItem(itemId);
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
            pc.sendPackets(new S_SystemMessage("你的聖物卡不足【" + needcount + "】個"));
        }
    }
}
