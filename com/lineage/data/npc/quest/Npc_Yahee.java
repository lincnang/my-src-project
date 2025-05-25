package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class Npc_Yahee extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Yahee.class);

    public static NpcExecutor get() {
        return new Npc_Yahee();
    }

    public int type() {
        return 33;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.get_hardinR() != null) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_ep009"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html05"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void spawn(L1NpcInstance npc) {
        YaheeR yaheeR = new YaheeR(npc);
        GeneralThreadPool.get().execute(yaheeR);
    }

    class YaheeR implements Runnable {
        private final L1NpcInstance _npc;

        public YaheeR(L1NpcInstance npc) {
            _npc = npc;
        }

        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(7000L);
                _npc.broadcastPacketAll(new S_NpcChat(_npc, "$7657"));
            } catch (Exception e) {
                Npc_Yahee._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Yahee JD-Core Version: 0.6.2
 */