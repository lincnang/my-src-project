package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class Npc_Touma extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Touma.class);

    public static NpcExecutor get() {
        return new Npc_Touma();
    }

    public int type() {
        return 17;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "touma1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
    }

    public int workTime() {
        return 20;
    }

    public void work(L1NpcInstance npc) {
        if (npc.getStatus() != 4) {
            npc.setStatus(4);
            npc.broadcastPacketAll(new S_NPCPack(npc));
        }
        Work work = new Work(npc);
        work.getStart();
    }

    private static class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 30));
                    TimeUnit.MILLISECONDS.sleep(_spr);
                }
            } catch (Exception e) {
                Npc_Touma._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Touma JD-Core Version: 0.6.2
 */