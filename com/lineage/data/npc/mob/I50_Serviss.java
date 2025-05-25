package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class I50_Serviss extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(I50_Serviss.class);

    public static NpcExecutor get() {
        return new I50_Serviss();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
                    if (pc.getInventory().checkItem(49206)) {
                        return pc;
                    }
                    switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
                        case 3:
                            CreateNewItem.getQuestItem(pc, npc, 49206, 1L);
                            pc.getQuest().set_step(IllusionistLv50_1.QUEST.get_id(), 4);
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
 * com.lineage.data.npc.mob.I50_Serviss JD-Core Version: 0.6.2
 */