package com.add.Mobbling;

import com.eric.RandomMobTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NoSell;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L1Mobble {
    private static Logger _log = Logger.getLogger(L1Mobble.class.getName());
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();
    private static L1Mobble instance;
    private String _htmlid = null;
    private String[] _data = null;

    public static L1Mobble getInstance() {
        if (instance == null) {
            instance = new L1Mobble();
        }
        return instance;
    }

    private static ArrayList<L1ItemInstance> sellList(L1PcInstance pc) {
        ArrayList<L1ItemInstance> Mobs = new ArrayList<>();
        L1ItemInstance[] MobItems = pc.getInventory().findMob();
        if (MobItems.length <= 0) {
            return Mobs;
        }
        for (L1ItemInstance gItem : MobItems) {
            int MobId = gItem.getGamNo();
            L1Mobbling MobInfo = MobblingLock.create().getMobbling(MobId);
            if ((MobInfo == null) || (MobInfo.get_npcid() != gItem.getGamNpcId())) {
                continue;
            }
            Mobs.add(gItem);
        }
        return Mobs;
    }

    private static String[] status() {
        L1Mobbling[] list = MobblingLock.create().getMobblingList();
        L1NpcInstance npcMob1 = _Mob.get_npcMob1();
        L1NpcInstance npcMob2 = _Mob.get_npcMob2();
        L1NpcInstance npcMob3 = _Mob.get_npcMob3();
        L1NpcInstance npcMob4 = _Mob.get_npcMob4();
        L1NpcInstance npcMob5 = _Mob.get_npcMob5();
        L1NpcInstance npcMob6 = _Mob.get_npcMob6();
        L1NpcInstance npcMob7 = _Mob.get_npcMob7();
        L1NpcInstance npcMob8 = _Mob.get_npcMob8();
        L1NpcInstance npcMob9 = _Mob.get_npcMob9();
        L1NpcInstance npcMob10 = _Mob.get_npcMob10();
        int Aa1 = 0;
        int Aa2 = 0;
        int Aa3 = 0;
        int Aa4 = 0;
        int Aa5 = 0;
        int Aa6 = 0;
        int Aa7 = 0;
        int Aa8 = 0;
        int Aa9 = 0;
        int Aa10 = 0;
        int MobId = _Mob.get_MobId();
        for (L1Mobbling gItem : list) {
            if (MobId - 50 <= gItem.get_id()) {
                if (gItem.get_npcid() == npcMob1.getNpcId()) {
                    ++Aa1;
                }
                if (gItem.get_npcid() == npcMob2.getNpcId()) {
                    ++Aa2;
                }
                if (gItem.get_npcid() == npcMob3.getNpcId()) {
                    ++Aa3;
                }
                if (gItem.get_npcid() == npcMob4.getNpcId()) {
                    ++Aa4;
                }
                if (gItem.get_npcid() == npcMob5.getNpcId()) {
                    ++Aa5;
                }
                if (gItem.get_npcid() == npcMob6.getNpcId()) {
                    ++Aa6;
                }
                if (gItem.get_npcid() == npcMob7.getNpcId()) {
                    ++Aa7;
                }
                if (gItem.get_npcid() == npcMob8.getNpcId()) {
                    ++Aa8;
                }
                if (gItem.get_npcid() == npcMob9.getNpcId()) {
                    ++Aa9;
                }
                if (gItem.get_npcid() == npcMob10.getNpcId()) {
                    ++Aa10;
                }
            }
        }
        String Aac1 = "$370";
        String Aac2 = "$370";
        String Aac3 = "$370";
        String Aac4 = "$370";
        String Aac5 = "$370";
        String Aac6 = "$370";
        String Aac7 = "$370";
        String Aac8 = "$370";
        String Aac9 = "$370";
        String Aac10 = "$370";
        if ((Aa1 <= 50) && (Aa1 >= 35)) {
            Aac1 = "$368";
        } else if ((Aa1 < 35) && (Aa1 >= 10)) {
            Aac1 = "$369";
        }
        if ((Aa2 <= 50) && (Aa2 >= 35)) {
            Aac2 = "$368";
        } else if ((Aa2 < 35) && (Aa2 >= 10)) {
            Aac2 = "$369";
        }
        if ((Aa3 <= 50) && (Aa3 >= 35)) {
            Aac3 = "$368";
        } else if ((Aa3 < 35) && (Aa3 >= 10)) {
            Aac3 = "$369";
        }
        if ((Aa4 <= 50) && (Aa4 >= 35)) {
            Aac4 = "$368";
        } else if ((Aa4 < 35) && (Aa4 >= 10)) {
            Aac4 = "$369";
        }
        if ((Aa5 <= 50) && (Aa5 >= 35)) {
            Aac5 = "$368";
        } else if ((Aa5 < 35) && (Aa5 >= 10)) {
            Aac5 = "$369";
        }
        if ((Aa6 <= 50) && (Aa6 >= 35)) {
            Aac6 = "$368";
        } else if ((Aa6 < 35) && (Aa6 >= 10)) {
            Aac6 = "$369";
        }
        if ((Aa7 <= 50) && (Aa7 >= 35)) {
            Aac7 = "$368";
        } else if ((Aa7 < 35) && (Aa7 >= 10)) {
            Aac7 = "$369";
        }
        if ((Aa8 <= 50) && (Aa8 >= 35)) {
            Aac8 = "$368";
        } else if ((Aa8 < 35) && (Aa8 >= 10)) {
            Aac8 = "$369";
        }
        if ((Aa9 <= 50) && (Aa9 >= 35)) {
            Aac9 = "$368";
        } else if ((Aa9 < 35) && (Aa9 >= 10)) {
            Aac9 = "$369";
        }
        if ((Aa10 <= 50) && (Aa10 >= 35)) {
            Aac10 = "$368";
        } else if ((Aa10 < 35) && (Aa10 >= 10)) {
            Aac10 = "$369";
        }
        String Aao1 = String.valueOf(Aa1 / 50.0D * 100.0D);
        String Aao2 = String.valueOf(Aa2 / 50.0D * 100.0D);
        String Aao3 = String.valueOf(Aa3 / 50.0D * 100.0D);
        String Aao4 = String.valueOf(Aa4 / 50.0D * 100.0D);
        String Aao5 = String.valueOf(Aa5 / 50.0D * 100.0D);
        String Aao6 = String.valueOf(Aa6 / 50.0D * 100.0D);
        String Aao7 = String.valueOf(Aa7 / 50.0D * 100.0D);
        String Aao8 = String.valueOf(Aa8 / 50.0D * 100.0D);
        String Aao9 = String.valueOf(Aa9 / 50.0D * 100.0D);
        String Aao10 = String.valueOf(Aa10 / 50.0D * 100.0D);
        npcMob1 = _Mob.get_npcMob1();
        npcMob2 = _Mob.get_npcMob2();
        npcMob3 = _Mob.get_npcMob3();
        npcMob4 = _Mob.get_npcMob4();
        npcMob5 = _Mob.get_npcMob5();
        npcMob6 = _Mob.get_npcMob6();
        npcMob7 = _Mob.get_npcMob7();
        npcMob8 = _Mob.get_npcMob8();
        npcMob9 = _Mob.get_npcMob9();
        npcMob10 = _Mob.get_npcMob10();
        String[] info = {npcMob1.getNameId(), Aac1, Aao1, npcMob2.getNameId(), Aac2, Aao2, npcMob3.getNameId(), Aac3, Aao3, npcMob4.getNameId(), Aac4, Aao4, npcMob5.getNameId(), Aac5, Aao5, npcMob6.getNameId(), Aac6, Aao6, npcMob7.getNameId(), Aac7, Aao7, npcMob8.getNameId(), Aac8, Aao8, npcMob9.getNameId(), Aac9, Aao9, npcMob10.getNameId(), Aac10, Aao10};
        return info;
    }

    public void buytickets(L1NpcInstance npc, L1PcInstance pc) {
        L1NpcInstance npcMob1 = _Mob.get_npcMob1();
        L1NpcInstance npcMob2 = _Mob.get_npcMob2();
        L1NpcInstance npcMob3 = _Mob.get_npcMob3();
        L1NpcInstance npcMob4 = _Mob.get_npcMob4();
        L1NpcInstance npcMob5 = _Mob.get_npcMob5();
        L1NpcInstance npcMob6 = _Mob.get_npcMob6();
        L1NpcInstance npcMob7 = _Mob.get_npcMob7();
        L1NpcInstance npcMob8 = _Mob.get_npcMob8();
        L1NpcInstance npcMob9 = _Mob.get_npcMob9();
        L1NpcInstance npcMob10 = _Mob.get_npcMob10();
        if (_Mob.get_isWaiting()) {
            if (_Mob.get_isStart()) {
                this._htmlid = "maeno3";
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid));
            } else if ((npcMob1 != null) && (npcMob2 != null) && (npcMob3 != null) && (npcMob4 != null) && (npcMob5 != null) && (npcMob6 != null) && (npcMob7 != null) && (npcMob8 != null) && (npcMob9 != null) && (npcMob10 != null)) {
                int MobId = _Mob.get_MobId();
                ArrayList<int[]> Mobs = new ArrayList<>();
                int[] Mob1 = {npcMob1.getNpcId(), MobId};
                Mobs.add(Mob1);
                int[] Mob2 = {npcMob2.getNpcId(), MobId};
                Mobs.add(Mob2);
                int[] Mob3 = {npcMob3.getNpcId(), MobId};
                Mobs.add(Mob3);
                int[] Mob4 = {npcMob4.getNpcId(), MobId};
                Mobs.add(Mob4);
                int[] Mob5 = {npcMob5.getNpcId(), MobId};
                Mobs.add(Mob5);
                int[] Mob6 = {npcMob6.getNpcId(), MobId};
                Mobs.add(Mob6);
                int[] Mob7 = {npcMob7.getNpcId(), MobId};
                Mobs.add(Mob7);
                int[] Mob8 = {npcMob8.getNpcId(), MobId};
                Mobs.add(Mob8);
                int[] Mob9 = {npcMob9.getNpcId(), MobId};
                Mobs.add(Mob9);
                int[] Mob10 = {npcMob10.getNpcId(), MobId};
                Mobs.add(Mob10);
                pc.getMobSplist().set_copyMob(Mobs);
                pc.sendPackets(new S_ShopSellListMob(npc.getId(), Mobs));
            } else {
                this._htmlid = "maeno2";
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid));
            }
        } else {
            this._htmlid = "maeno5";
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid));
        }
    }

    public void selltickets(L1NpcInstance npc, L1PcInstance pc) {
        ArrayList<L1ItemInstance> list = sellList(pc);
        if (list.size() <= 0) {
            pc.sendPackets(new S_NoSell(npc));
        } else {
            pc.getMobSplist().set_copySellMob(list);
            pc.sendPackets(new S_ShopBuyListMob(npc.getId(), list));
        }
    }

    public void watchMobFight(L1PcInstance pc) {
        RandomMobTable.getInstance();
        int x = 32699;
        int y = 32896;
        if (pc.getInventory().consumeItem(40308, 100)) {
            try {
                pc.save();
                pc.beginGhost(x, y, (short) 93, true, 600);
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(189));
        }
    }

    public void Mobstatus(L1NpcInstance npc, L1PcInstance pc) {
        L1NpcInstance npcMob1 = _Mob.get_npcMob1();
        L1NpcInstance npcMob2 = _Mob.get_npcMob2();
        L1NpcInstance npcMob3 = _Mob.get_npcMob3();
        L1NpcInstance npcMob4 = _Mob.get_npcMob4();
        L1NpcInstance npcMob5 = _Mob.get_npcMob5();
        L1NpcInstance npcMob6 = _Mob.get_npcMob6();
        L1NpcInstance npcMob7 = _Mob.get_npcMob7();
        L1NpcInstance npcMob8 = _Mob.get_npcMob8();
        L1NpcInstance npcMob9 = _Mob.get_npcMob9();
        L1NpcInstance npcMob10 = _Mob.get_npcMob10();
        if ((npcMob1 != null) && (npcMob2 != null) && (npcMob3 != null) && (npcMob4 != null) && (npcMob5 != null) && (npcMob6 != null) && (npcMob7 != null) && (npcMob8 != null) && (npcMob9 != null) && (npcMob10 != null)) {
            this._htmlid = "mobbling4";
            this._data = status();
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid, this._data));
        } else {
            this._htmlid = "maeno5";
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid));
        }
    }
}