package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Endia extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Endia.class);

    public static NpcExecutor get() {
        return new Npc_Endia();
    }

    public int type() {
        return 7;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (npc.getMaster() != null) {
                if (npc.getMaster().equals(pc)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq2"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq4"));
                }
                return;
            }
            npc.onTalkAction(pc);
            if (pc.isDarkelf()) {
                if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq4"));
                    return;
                }
                if (pc.getLevel() >= DarkElfLv50_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq4"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq4"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (npc.getMaster() != null) {
            return;
        }
        if (pc.isDarkelf()) {
            if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
                isCloseList = true;
            } else if (pc.getLevel() >= DarkElfLv50_1.QUEST.get_questlevel()) {
                switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
                    case 1:
                        if (!cmd.equalsIgnoreCase("start")) {
                            break;
                        }
                        L1Npc l1npc = NpcTable.get().getTemplate(71094);
                        new L1FollowerInstance(l1npc, npc, pc);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "endiaq2"));
                        break;
                    default:
                        isCloseList = true;
                        break;
                }
            } else {
                isCloseList = true;
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public void attack(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (npc.getNpcId() != 71094) {
                return;
            }
            if (pc == null) {
                return;
            }
            for (L1Object object : World.get().getVisibleObjects(npc)) {
                if ((object instanceof L1NpcInstance)) {
                    L1NpcInstance tgnpc = (L1NpcInstance) object;
                    if (tgnpc.getNpcTemplate().get_npcId() == 70811) {
                        if (npc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
                            if (tgnpc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
                                pc.getQuest().set_step(DarkElfLv50_1.QUEST.get_id(), 2);
                                CreateNewItem.getQuestItem(pc, npc, 40582, 1L);
                                npc.setParalyzed(true);
                                npc.deleteMe();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Endia JD-Core Version: 0.6.2
 */