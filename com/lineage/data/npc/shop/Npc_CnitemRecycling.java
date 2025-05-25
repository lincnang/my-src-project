package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyListCn;

public class Npc_CnitemRecycling extends NpcExecutor {
    private String _htmlid = null;

    public static NpcExecutor get() {
        return new Npc_CnitemRecycling();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (_htmlid != null) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tzmerchant"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("sell")) {
            pc.sendPackets(new S_ShopBuyListCn(pc, npc));
        }
    }

    public void set_set(String[] set) {
        try {
            _htmlid = set[1];
        } catch (Exception e) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.Npc_Strange JD-Core Version: 0.6.2
 */