package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CKEW50_Chino extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(CKEW50_Chino.class);

    public static NpcExecutor get() {
        return new CKEW50_Chino();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.hasSkillEffect(4007)) {
                    return pc;
                }
                if (pc.getInventory().checkItem(49168)) {
                    return pc;
                }
                CreateNewItem.getQuestItem(pc, npc, 49168, 1L);
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
 * com.lineage.data.npc.mob.CKEW50_Chino JD-Core Version: 0.6.2
 */