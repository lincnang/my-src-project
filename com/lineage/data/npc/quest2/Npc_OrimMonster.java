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

import java.util.ArrayList;

public class Npc_OrimMonster extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_OrimMonster.class);

    public static NpcExecutor get() {
        return new Npc_OrimMonster();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if ((pc != null) && (pc.getMapId() == 9101)) {
                L1QuestUser quest = WorldQuest.get().get(npc.get_showId());
                if ((quest != null) && (quest.get_orimR() != null)) {
                    quest.add_score(npc.getNpcTemplate().get_quest_score());
                }
                if (npc.get_quest_id() > 0) {
                    ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
                    if (!targetList.isEmpty()) {
                        for (L1Character cha : targetList) {
                            if ((cha instanceof L1PcInstance)) {
                                L1PcInstance find_pc = (L1PcInstance) cha;
                                CreateNewItem.createNewItem(find_pc, npc.get_quest_id(), 1L);
                            }
                        }
                    }
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
 * com.lineage.data.npc.quest2.Npc_OrimMonster JD-Core Version: 0.6.2
 */