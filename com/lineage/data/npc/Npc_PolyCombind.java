package com.lineage.data.npc;

import com.lineage.config.Configpoly;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.polyHeChengTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1polyHeCheng;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 變身卡合成系統
 * 後續由 技術老爹 製作
 *
 * @author Administrator
 */
public class Npc_PolyCombind extends NpcExecutor {
    private final Random _random = new Random();
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
        //////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("A1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(Configpoly.CONSUME2 + pc.getpolyrun2());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind2", data));
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
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("B1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind3", data));
        }
        if (cmd.equalsIgnoreCase("B11")) {
            if (Configpoly.CONSUME3 + pc.getpolyrun3() >= Configpoly.ZUIDAJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階段變身卡最高機率" + Configpoly.ZUIDAJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount2(pc.getpolyCount2() + 1);
            pc.setpolyrun3(pc.getpolyrun3() + Configpoly.ZENGJIAJILV3);
            data[0] = String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind3", data));
        }
        if (cmd.equalsIgnoreCase("B12")) {
            if (Configpoly.CONSUME3 + pc.getpolyrun3() <= Configpoly.ZUIXIAOJILV3) {
                pc.sendPackets(new S_SystemMessage("合成三階段變身卡最低機率" + Configpoly.ZUIXIAOJILV3 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount2(pc.getpolyCount2() - 1);
            pc.setpolyrun3(pc.getpolyrun3() - Configpoly.ZENGJIAJILV3);
            data[0] = String.valueOf(Configpoly.CONSUME3 + pc.getpolyrun3());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind3", data));
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("C1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind4", data));
        }
        if (cmd.equalsIgnoreCase("C11")) {
            if (Configpoly.CONSUME4 + pc.getpolyrun4() >= Configpoly.ZUIDAJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階段變身卡最高機率" + Configpoly.ZUIDAJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount3(pc.getpolyCount3() + 1);
            pc.setpolyrun4(pc.getpolyrun4() + Configpoly.ZENGJIAJILV4);
            data[0] = String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind4", data));
        }
        if (cmd.equalsIgnoreCase("C12")) {
            if (Configpoly.CONSUME4 + pc.getpolyrun4() <= Configpoly.ZUIXIAOJILV4) {
                pc.sendPackets(new S_SystemMessage("合成四階段變身卡最低機率" + Configpoly.ZUIXIAOJILV4 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount3(pc.getpolyCount3() - 1);
            pc.setpolyrun4(pc.getpolyrun4() - Configpoly.ZENGJIAJILV4);
            data[0] = String.valueOf(Configpoly.CONSUME4 + pc.getpolyrun4());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind4", data));
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        if (cmd.equalsIgnoreCase("D1")) {
            String[] data = new String[3];
            data[0] = String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind5", data));
        }
        if (cmd.equalsIgnoreCase("D11")) {
            if (Configpoly.CONSUME5 + pc.getpolyrun5() >= Configpoly.ZUIDAJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階段變身卡最高機率" + Configpoly.ZUIDAJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount4(pc.getpolyCount4() + 1);
            pc.setpolyrun5(pc.getpolyrun5() + Configpoly.ZENGJIAJILV5);
            data[0] = String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind5", data));
        }
        if (cmd.equalsIgnoreCase("D12")) {
            if (Configpoly.CONSUME5 + pc.getpolyrun5() <= Configpoly.ZUIXIAOJILV5) {
                pc.sendPackets(new S_SystemMessage("合成五階段變身卡最低機率" + Configpoly.ZUIXIAOJILV5 + "%"));
                return;
            }
            String[] data = new String[3];
            pc.setpolyCount4(pc.getpolyCount4() - 1);
            pc.setpolyrun5(pc.getpolyrun5() - Configpoly.ZENGJIAJILV5);
            data[0] = String.valueOf(Configpoly.CONSUME5 + pc.getpolyrun5());
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "polycombind5", data));
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        boolean enough = false;// 材料是否足夠
        ArrayList<Integer> cousumepolys = new ArrayList<Integer>();// 預備消耗的變身卡道具ID列表
        int chance = 0;// 合成機率
        int needcount = 0;// 需要數量
        int[] oldpolys = null;// 上一階段的變身卡
        int newpoly = 0;// 給予的新變身卡
        final String[] data = new String[3];
        switch (cmd) {
            case "A":
            case "A_Total":
                chance = Configpoly.CONSUME2 + pc.getpolyrun2();
                needcount = Configpoly.SHULIANG2 + pc.getpolyCount();
                oldpolys = poly1;
                newpoly = poly2[ThreadLocalRandom.current().nextInt(poly2.length)];// 隨機2階段變身卡
                break;
            case "B":
            case "B_Total":
                chance = Configpoly.CONSUME3 + pc.getpolyrun3();
                needcount = Configpoly.SHULIANG3 + pc.getpolyCount2();
                oldpolys = poly2;
                newpoly = poly3[ThreadLocalRandom.current().nextInt(poly3.length)];// 隨機3階段變身卡
                break;
            case "C":
            case "C_Total":
                chance = Configpoly.CONSUME4 + pc.getpolyrun4();
                needcount = Configpoly.SHULIANG4 + pc.getpolyCount3();
                oldpolys = poly3;
                newpoly = poly4[ThreadLocalRandom.current().nextInt(poly4.length)];// 隨機4階段變身卡
                break;
            case "D":
            case "D_Total":
                chance = Configpoly.CONSUME5 + pc.getpolyrun5();
                needcount = Configpoly.SHULIANG5 + pc.getpolyCount4();
                oldpolys = poly4;
                newpoly = poly5[ThreadLocalRandom.current().nextInt(poly5.length)];// 隨機5階段變身卡fPoly2UserSet
                break;
        }

        boolean isPlayAnimation = true;
        if (cmd.contains("_Total"))
            isPlayAnimation = false;

        if (oldpolys != null) {
            for (int oldpoly : oldpolys) {
                L1ItemInstance[] polys = pc.getInventory().findItemsId(oldpoly);// 取回此變身卡陣列
                if (polys != null) {
                    for (L1ItemInstance poly : polys) {
                        int itemid = poly.getItemId();
                        cousumepolys.add(itemid);
                        if (cousumepolys.size() == needcount) {// 足夠數量 終止迴圈
                            break;
                        }
                    }
                }
                if (cousumepolys.size() == needcount) {// 足夠數量 終止迴圈
                    break;
                }
            }
        }
        if (cousumepolys.size() == needcount) {// 足夠數量
            enough = true;// 設定材料足夠
            if (pc.getInventory().consumeItemsIdArray(cousumepolys)) {// 消耗列表道具各1個
                try {
                    if (isPlayAnimation) {
                        pc.sendHtmlCastGfx(data);

                        TimeUnit.MILLISECONDS.sleep(2100);
                    }

                    pc.setpolyCount(0);
                    pc.setpolyCount2(0);
                    pc.setpolyCount3(0);
                    pc.setpolyCount4(0);
                    pc.setpolyrun2(0);
                    pc.setpolyrun3(0);
                    pc.setpolyrun4(0);
                    pc.setpolyrun5(0);
                    if (ThreadLocalRandom.current().nextInt(100) < chance) {// 合成成功
                        final L1ItemInstance item = ItemTable.get().createItem(newpoly);
                        int itemId = item.getItem().getItemId();
                        final StringBuilder stringBuilder = new StringBuilder();
                        final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(itemId);
                        //獲取列表id
                        if (card1 != null) {//列表ID空白不啟動
                            stringBuilder.append(card1.getGfxid()).append(",");
                            if (card1.getNot() != 0) {
                                World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了變身卡\\fN【" + item.getLogName() + "】"));
                            }
                        }
                        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
                        item.setIdentified(true);
                        pc.getInventory().storeItem(item);
                        pc.sendPacketsX8(new S_Sound(20360)); // 音效
                        if (isPlayAnimation)
                            pc.sendPackets(new S_NPCTalkReturn(pc, "wwhccg", msg));//對話當調用IMG
                        pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
                    } else {// 合成失敗
                        pc.sendPackets(new S_SystemMessage("合成變身卡失敗了。"));
                        final L1ItemInstance item = ItemTable.get().createItem(cousumepolys.get(ThreadLocalRandom.current().nextInt(cousumepolys.size())));
                        int itemId = item.getItem().getItemId();
                        final StringBuilder stringBuilder = new StringBuilder();
                        final L1polyHeCheng card1 = polyHeChengTable.getInstance().getTemplate(itemId);
                        //獲取列表id
                        if (card1 != null) {//列表ID空白不啟動
                            stringBuilder.append(card1.getGfxid()).append(",");
                        }
                        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
                        item.setIdentified(true);
                        pc.getInventory().storeItem(item);
                        pc.sendPacketsX8(new S_Sound(20468)); // 音效
                        if (isPlayAnimation)
                            pc.sendPackets(new S_NPCTalkReturn(pc, "wwhcsb", msg));//對話當調用IMG
                        pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                /*catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
            }
        }
        if (!enough) {// 材料不足
            pc.sendPackets(new S_SystemMessage("你的變身卡不足【" + needcount + "】個"));
        }
    }
}