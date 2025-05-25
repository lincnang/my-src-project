package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class K45_RightHandBandit extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(K45_RightHandBandit.class);

    public static NpcExecutor get() {
        return new K45_RightHandBandit();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
                    return pc;
                }
                if (pc.getQuest().isStart(KnightLv45_1.QUEST.get_id())) {
                    if (pc.getInventory().checkItem(20026)) {
                        return pc;
                    }
                    CreateNewItem.getQuestItem(pc, npc, 20026, 1L);
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
 * com.lineage.data.npc.mob.K45_RightHandBandit JD-Core Version: 0.6.2
 */