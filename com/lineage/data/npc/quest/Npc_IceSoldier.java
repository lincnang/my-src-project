package com.lineage.data.npc.quest;

import com.lineage.data.event.ice.IceQueenSystem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_IceSoldier extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_IceSoldier.class);

    public static NpcExecutor get() {
        return new Npc_IceSoldier();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLevel() < 52) {
                return;
            }
            if (npc.getNpcId() == 80501) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "icq_1"));
            }
            if (npc.getNpcId() == 80502) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "icq_2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("enter")) {
            if (npc.getNpcId() == 80501) {// 敵對的 冰之女王禁衛兵
                if (cmd.equalsIgnoreCase("enter")) {
                    IceQueenSystem.getInstance().AttachPc(pc, 1);
                }
            } else if (npc.getNpcId() == 80502) {// 友好的 冰之女王禁衛兵
                if (cmd.equalsIgnoreCase("enter")) {
                    IceQueenSystem.getInstance().AttachPc(pc, 0);
                }
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Altar JD-Core Version: 0.6.2
 */