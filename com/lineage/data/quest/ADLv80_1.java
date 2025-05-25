package com.lineage.data.quest;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Quest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ADLv80_1 extends QuestExecutor {
    public static final int MAPID = 1005;
    private static final Log _log = LogFactory.getLog(ADLv80_1.class);
    public static L1Quest QUEST;

    public static QuestExecutor get() {
        return new ADLv80_1();
    }

    public void execute(L1Quest quest) {
        try {
            QUEST = quest;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void startQuest(L1PcInstance pc) {
        try {
            if (QUEST.check(pc)) {
                if (pc.getLevel() >= QUEST.get_questlevel()) {
                    if (pc.getQuest().get_step(QUEST.get_id()) != 1) {
                        pc.getQuest().set_step(QUEST.get_id(), 1);
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(pc, "y_q_not1"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(pc, "y_q_not2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void endQuest(L1PcInstance pc) {
        try {
            if (!pc.getQuest().isEnd(QUEST.get_id())) {
                pc.getQuest().set_end(QUEST.get_id());
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void showQuest(L1PcInstance pc) {
        try {
            if ("y_q_ad80_1" != null) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "y_q_ad80_1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void stopQuest(L1PcInstance pc) {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.quest.ADLv80_1 JD-Core Version: 0.6.2
 */