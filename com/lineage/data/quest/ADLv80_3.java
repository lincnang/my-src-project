package com.lineage.data.quest;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.timecontroller.quest.AD80_3_Timer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ADLv80_3 extends QuestExecutor {
    public static final int MAPID = 1017;
    private static final Log _log = LogFactory.getLog(ADLv80_3.class);
    public static L1Quest QUEST;

    public static QuestExecutor get() {
        return new ADLv80_3();
    }

    public void execute(L1Quest quest) {
        try {
            QUEST = quest;
            AD80_3_Timer ad80_3Timer = new AD80_3_Timer();
            ad80_3Timer.start();
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
    }

    public void stopQuest(L1PcInstance pc) {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.quest.ADLv80_3 JD-Core Version: 0.6.2
 */