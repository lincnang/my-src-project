package com.lineage.data.npc.gam;

import com.add.Mobbling.L1Mobble;
import com.add.Mobbling.MobblingTimeList;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_Mobbling extends NpcExecutor {
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();

    public static NpcExecutor get() {
        return new Npc_Mobbling();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (_Mob.get_isStart()) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mobbling3"));
        } else if (_Mob.get_isBuy()) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mobbling"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "maeno5"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equals("buy")) {
            L1Mobble.getInstance().buytickets(npc, pc);
        } else if (cmd.equals("sell")) {
            L1Mobble.getInstance().selltickets(npc, pc);
        } else if (cmd.equalsIgnoreCase("LookMob1")) {
            L1Mobble.getInstance().Mobstatus(npc, pc);
        } else if (cmd.equalsIgnoreCase("LookMob2")) {
            L1Mobble.getInstance().watchMobFight(pc);
        }
    }
}
/*
 * Location: C:\Users\kenny\Desktop\ Qualified Name:
 * com.lineage.data.npc.gam.Npc_Mobbling JD-Core Version: 0.6.2
 */