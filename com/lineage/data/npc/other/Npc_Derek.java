package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyList;
import com.lineage.server.serverpackets.S_ShopSellList;

public class Npc_Derek extends NpcExecutor {
    private static boolean _spr = false;

    public static NpcExecutor get() {
        return new Npc_Derek();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "derek2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "derek1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("buy")) {
            pc.sendPackets(new S_ShopSellList(pc,npc.getId()));
        } else if (cmd.equalsIgnoreCase("sell")) {
            pc.sendPackets(new S_ShopBuyList(npc.getId(), pc));
        }
    }

    public int workTime() {
        return 15;
    }

    public void work(L1NpcInstance npc) {
        if (_spr) {
            _spr = false;
            npc.broadcastPacketX8(new S_DoActionGFX(npc.getId(), 7));
        } else {
            _spr = true;
            npc.broadcastPacketX8(new S_DoActionGFX(npc.getId(), 17));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Derek JD-Core Version: 0.6.2
 */