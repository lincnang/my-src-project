package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C30_MutantGiantQueenAnt extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(C30_MutantGiantQueenAnt.class);

    public static NpcExecutor get() {
        return new C30_MutantGiantQueenAnt();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
                    return pc;
                }
                if (pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
                    if (pc.getInventory().checkItem(40547)) {
                        return pc;
                    }
                    CreateNewItem.getQuestItem(pc, npc, 40547, 1L);
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
 * com.lineage.data.npc.mob.C30_MutantGiantQueenAnt JD-Core Version: 0.6.2
 */