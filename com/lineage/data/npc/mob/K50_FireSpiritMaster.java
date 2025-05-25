package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class K50_FireSpiritMaster extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(K50_FireSpiritMaster.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new K50_FireSpiritMaster();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if (pc.getQuest().isEnd(KnightLv50_1.QUEST.get_id())) {
                    return pc;
                }
                if (pc.getQuest().isStart(KnightLv50_1.QUEST.get_id())) {
                    switch (pc.getQuest().get_step(KnightLv50_1.QUEST.get_id())) {
                        case 3:
                            if (_random.nextInt(100) < 40) {
                                CreateNewItem.getQuestItem(pc, npc, 49161, 1L);
                            }
                            break;
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
 * com.lineage.data.npc.mob.K50_FireSpiritMaster JD-Core Version: 0.6.2
 */