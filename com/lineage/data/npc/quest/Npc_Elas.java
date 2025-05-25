package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Elas extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Elas.class);

    public static NpcExecutor get() {
        return new Npc_Elas();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isCrown()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));
            } else if (pc.isKnight()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));
            } else if (pc.isElf()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));
            } else if (pc.isWizard()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));
            } else if (pc.isDarkelf()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));
            } else if (pc.isDragonKnight()) {
                if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas6"));
                }
            } else if (pc.isIllusionist()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isDragonKnight()) {
            if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(49220)) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas5"));
                    } else {
                        CreateNewItem.getQuestItem(pc, npc, 49220, 1L);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas4"));
                    }
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
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Elas JD-Core Version: 0.6.2
 */