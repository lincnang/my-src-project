package com.lineage.data.npc.quest2;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Mimic extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Mimic.class);

    public static NpcExecutor get() {
        return new Npc_Mimic();
    }

    public int type() {
        return 12;
    }

    public void attack(L1PcInstance pc, L1NpcInstance npc) {
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if ((pc != null) && (npc.get_quest_id() > 0)) {
                L1QuestUser quest = WorldQuest.get().get(npc.get_showId());
                if ((quest != null) && (quest.get_mapid() == 9101) && (quest.get_orimR() != null)) {
                    CreateNewItem.createNewItem(pc, 56252, 1L);
                    quest.get_orimR().checkQuestOrder(pc, npc.get_quest_id());
                }
            }
            return pc;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Npc_Mimic JD-Core Version: 0.6.2
 */