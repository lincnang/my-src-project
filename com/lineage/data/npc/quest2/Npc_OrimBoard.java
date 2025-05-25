package com.lineage.data.npc.quest2;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.templates.L1Rank;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Npc_OrimBoard extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_OrimBoard.class);

    public static NpcExecutor get() {
        return new Npc_OrimBoard();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id_s"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("query")) {
            List<L1Rank> totalList = BoardOrimReading.get().getTotalList();
            List<L1Rank> tempList = new CopyOnWriteArrayList<>();
            int totalSize = 0;
            int i = 0;
            int r = 5;
            for (int n = totalList.size(); (i < r) && (i < n); i++) {
                L1Rank rank = (L1Rank) totalList.get(i);
                if (rank != null) {
                    tempList.add(rank);
                    totalSize += rank.getMemberSize();
                }
            }
            pc.sendPackets(new S_PacketBoxGree(tempList, totalSize, 0, 0));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Npc_OrimBoard JD-Core Version: 0.6.2
 */