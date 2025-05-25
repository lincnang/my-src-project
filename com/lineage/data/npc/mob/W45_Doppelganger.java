package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class W45_Doppelganger extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(W45_Doppelganger.class);

    public static NpcExecutor get() {
        return new W45_Doppelganger();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
                    return pc;
                }
                if (pc.getQuest().isStart(WizardLv45_1.QUEST.get_id())) {
                    if (pc.getInventory().checkItem(40536)) {
                        return pc;
                    }
                    if (pc.getInventory().checkItem(40542)) {
                        return pc;
                    }
                    L1ItemInstance item = npc.getInventory().checkItemX(40032, 1L);
                    if ((item != null) && (npc.getInventory().removeItem(item, 1L) == 1L)) {
                        CreateNewItem.getQuestItem(pc, npc, 40542, 1L);
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
 * com.lineage.data.npc.mob.W45_Doppelganger JD-Core Version: 0.6.2
 */