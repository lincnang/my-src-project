package com.lineage.data.npc.quest;

import com.lineage.data.event.ValakasRoom.ValakasRoomSystem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_DeathKnight extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_DeathKnight.class);

    public static NpcExecutor get() {
        return new Npc_DeathKnight();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLevel() >= 60) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fd_death"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fd_death2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("enter")) {
            if (pc.getLevel() >= 60) {
                if (!pc.getInventory().checkItem(5010, 1)) {
                    L1ItemInstance item = pc.getInventory().storeItem(5010, 1);
                    pc.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));
                    ValakasRoomSystem.getInstance().startRaid(pc);
                } else {
                    ValakasRoomSystem.getInstance().startRaid(pc);
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fd_death2"));
            }
        }
        pc.sendPackets(new S_CloseList(pc.getId()));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Altar JD-Core Version: 0.6.2
 */