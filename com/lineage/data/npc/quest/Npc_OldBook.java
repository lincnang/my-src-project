package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_OldBook extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_OldBook.class);

    public static NpcExecutor get() {
        return new Npc_OldBook();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getQuest().get_step(83) != 255) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oldbook1"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oldbook2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            if ((cmd.equalsIgnoreCase("a")) && (pc.getQuest().get_step(83) != 255)) {
                CreateNewItem.createNewItem(pc, 49476, 1L);
                pc.getQuest().set_step(83, 255);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oldbook3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}