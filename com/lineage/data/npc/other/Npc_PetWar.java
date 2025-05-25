package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PetMatch;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_PetWar extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_PetWar.class);

    public static NpcExecutor get() {
        return new Npc_PetWar();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "petmatcher"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            boolean isCloseList = false;
            String[] temp = cmd.split(",");
            int objid2 = Integer.parseInt(temp[2]);
            Object[] petlist = pc.getPetList().values().toArray();
            if (petlist.length > 0) {
                pc.sendPackets(new S_ServerMessage(1187));
                return;
            }
            if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
                pc.sendPackets(new S_ServerMessage(1182));
                return;
            }
            temp[0].equalsIgnoreCase("ent");
            if (isCloseList) {
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_PetWar JD-Core Version: 0.6.2
 */