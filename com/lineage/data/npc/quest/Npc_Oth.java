package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Oth extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Oth.class);

    public static NpcExecutor get() {
        return new Npc_Oth();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv15_2.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth5"));
                } else if (pc.getLevel() >= ElfLv15_2.QUEST.get_questlevel()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth1"));
                    QuestClass.get().startQuest(pc, ElfLv15_2.QUEST.get_id());
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth6"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "oth2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isElf()) {
            if (pc.getQuest().isEnd(ElfLv15_2.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("request dex helmet of elven")) {
                getItem(pc, 20021);
            } else if (cmd.equalsIgnoreCase("request con helmet of elven")) {
                getItem(pc, 20039);
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private void getItem(L1PcInstance pc, int getid) {
        if (CreateNewItem.checkNewItem(pc, new int[]{40609, 40610, 40611, 40612}, new int[]{1, 1, 1, 1}) < 1L) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        } else {
            CreateNewItem.createNewItem(pc, new int[]{40609, 40610, 40611, 40612}, new int[]{1, 1, 1, 1}, new int[]{getid}, 1L, new int[]{1});
            QuestClass.get().endQuest(pc, ElfLv15_2.QUEST.get_id());
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Oth JD-Core Version: 0.6.2
 */