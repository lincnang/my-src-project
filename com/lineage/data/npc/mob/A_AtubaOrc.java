package com.lineage.data.npc.mob;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv15_1;
import com.lineage.data.quest.ElfLv15_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class A_AtubaOrc extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(A_AtubaOrc.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new A_AtubaOrc();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                if ((pc.getQuest().isStart(ElfLv15_2.QUEST.get_id())) && (_random.nextInt(100) < 20)) {
                    CreateNewItem.getQuestItem(pc, npc, 40612, 1L);
                }
                if ((pc.getQuest().isStart(ALv15_1.QUEST.get_id())) && (_random.nextInt(100) < 10)) {
                    CreateNewItem.getQuestItem(pc, npc, 40135, 1L);
                }
            }
            return pc;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
