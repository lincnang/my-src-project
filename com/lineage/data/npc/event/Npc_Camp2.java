package com.lineage.data.npc.event;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Camp2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Camp2.class);

    public static NpcExecutor get() {
        return new Npc_Camp2();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            String[] userName = new String[11];
            String[] names = RankingHeroTimer.userNameScore();
            System.arraycopy(names, 0, userName, 0, names.length);
            userName[10] = "全職業";
            if (userName != null) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rank168", userName));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}