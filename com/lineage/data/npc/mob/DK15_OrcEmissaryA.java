package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class DK15_OrcEmissaryA extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(DK15_OrcEmissaryA.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new DK15_OrcEmissaryA();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isEnd(DragonKnightLv15_1.QUEST.get_id())) {
                    return pc;
                }
                if (pc.getQuest().isStart(DragonKnightLv15_1.QUEST.get_id())) {
                    if (pc.getInventory().checkItem(49218)) {
                        return pc;
                    }
                    if (_random.nextInt(100) < 40) {
                        CreateNewItem.getQuestItem(pc, npc, 49218, 1L);
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
 * com.lineage.data.npc.mob.DK15_OrcEmissaryA JD-Core Version: 0.6.2
 */