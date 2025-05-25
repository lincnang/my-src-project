package com.add.BigHot;

import com.add.L1Config;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NoSell;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.ArrayList;

public class L1BigHotble {
    private static BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot();
    ;
    private static L1BigHotble instance;

    public static L1BigHotble getInstance() {
        if (instance == null) {
            instance = new L1BigHotble();
        }
        return instance;
    }

    private static ArrayList<L1ItemInstance> sellList(L1PcInstance pc) {
        ArrayList<L1ItemInstance> BigHots = new ArrayList<L1ItemInstance>();
        L1ItemInstance[] BigHotItems = pc.getInventory().findBigHot();
        if (BigHotItems.length <= 0) {
            return BigHots;
        }
        for (L1ItemInstance gItem : BigHotItems) {
            int BigHotId = gItem.getGamNo();
            L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(BigHotId);
            if (BigHotInfo != null) {
                String A = BigHotInfo.get_number();
                String B = gItem.getStarNpcId();
                int ch = 0;
                for (int a = 0; a < A.split(",").length; a++) {
                    String[] pk = B.split(",");
                    if (("," + A).indexOf("," + pk[a] + ",") >= 0) {
                        ch++;
                    }
                }
                if (ch >= 3) {
                    BigHots.add(gItem);
                }
            }
        }
        return BigHots;
    }

    public void selltickets(L1NpcInstance npc, L1PcInstance pc) {
        ArrayList<L1ItemInstance> list = sellList(pc);
        if (list.size() <= 0) {
            pc.sendPackets(new S_NoSell(npc));
        } else {
            pc.getBigHotSplist().set_copySellBigHot(list);
            pc.sendPackets(new S_ShopBuyListBigHot(npc.getId(), list));
        }
    }

    public void LookMoney(L1PcInstance pc) {
        int price = 0;
        int yuanbao = _BigHot.get_yuanbao();
        if (yuanbao == 0) {
            price += L1Config._2164;
        } else {
            price += _BigHot.get_yuanbao();
        }
        L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(_BigHot.get_BigHotId() - 1);
        if (BigHotInfo != null) {
            if (BigHotInfo.get_count() == 0) {
                if (BigHotInfo.get_money1() < L1Config._2166) {
                    price += BigHotInfo.get_money1();
                } else {
                    price += L1Config._2165;
                }
            } else {
                price += L1Config._2165;
            }
            if (BigHotInfo.get_count1() == 0) {
                price += BigHotInfo.get_money2();
            }
            if (BigHotInfo.get_count2() == 0) {
                price += BigHotInfo.get_money3();
            }
        } else {
            price += L1Config._2165;
        }
        int money = price * 7 / 10;
        pc.sendPackets(new S_SystemMessage("目前頭獎累積的彩金為(" + money + ")。"));
    }

    public void LookMoney1(L1PcInstance pc) {
        int price = 0;
        int yuanbao = _BigHot.get_yuanbao();
        if (yuanbao == 0) {
            price += L1Config._2164;
        } else {
            price += _BigHot.get_yuanbao();
        }
        L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(_BigHot.get_BigHotId() - 1);
        if (BigHotInfo != null) {
            if (BigHotInfo.get_count() == 0) {
                if (BigHotInfo.get_money1() < L1Config._2166) {
                    price += BigHotInfo.get_money1();
                } else {
                    price += L1Config._2165;
                }
            } else {
                price += L1Config._2165;
            }
            if (BigHotInfo.get_count1() == 0) {
                price += BigHotInfo.get_money2();
            }
            if (BigHotInfo.get_count2() == 0) {
                price += BigHotInfo.get_money3();
            }
        } else {
            price += L1Config._2165;
        }
        int money = price * 2 / 10;
        pc.sendPackets(new S_SystemMessage("目前壹獎累積的彩金為(" + money + ")。"));
    }

    public void LookMoney2(L1PcInstance pc) {
        int price = 0;
        int yuanbao = _BigHot.get_yuanbao();
        if (yuanbao == 0) {
            price += L1Config._2164;
        } else {
            price += _BigHot.get_yuanbao();
        }
        L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(_BigHot.get_BigHotId() - 1);
        if (BigHotInfo != null) {
            if (BigHotInfo.get_count() == 0) {
                if (BigHotInfo.get_money1() < L1Config._2166) {
                    price += BigHotInfo.get_money1();
                } else {
                    price += L1Config._2165;
                }
            } else {
                price += L1Config._2165;
            }
            if (BigHotInfo.get_count1() == 0) {
                price += BigHotInfo.get_money2();
            }
            if (BigHotInfo.get_count2() == 0) {
                price += BigHotInfo.get_money3();
            }
        } else {
            price += L1Config._2165;
        }
        int money = price / 10;
        pc.sendPackets(new S_SystemMessage("目前貳獎累積的彩金為(" + money + ")。"));
    }
}
