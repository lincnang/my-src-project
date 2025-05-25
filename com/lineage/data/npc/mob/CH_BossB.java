package com.lineage.data.npc.mob;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class CH_BossB extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(CH_BossB.class);

    public static NpcExecutor get() {
        return new CH_BossB();
    }

    public int type() {
        return 40;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if ((pc != null) && (pc.get_hardinR() != null)) {
                pc.get_hardinR().boss_b_death();
            }
            return pc;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void spawn(L1NpcInstance npc) {
        try {
            switch (npc.getNpcId()) {
                case 91295:
                    break;
                case 91296:
                    BossBR boss = new BossBR(npc);
                    GeneralThreadPool.get().execute(boss);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    static class BossBR implements Runnable {
        private final L1NpcInstance _npc;

        public BossBR(L1NpcInstance npc) {
            _npc = npc;
        }

        public void run() {
            try {
                if (!_npc.isDead()) {
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    _npc.broadcastPacketAll(new S_NpcChat(_npc, "$7588"));
                }
                if (!_npc.isDead()) {
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    _npc.broadcastPacketAll(new S_NpcChat(_npc, "$7591"));
                }
                if (!_npc.isDead()) {
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    _npc.broadcastPacketAll(new S_NpcChat(_npc, "$7593"));
                }
            } catch (Exception e) {
                CH_BossB._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.mob.CH_BossB JD-Core Version: 0.6.2
 */