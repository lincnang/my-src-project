package com.lineage.server.model.skill;

import com.lineage.server.datatables.MobSkillTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1MobSkill;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1SkillDelayforMob {
    private static final Log _log = LogFactory.getLog(L1SkillDelayforMob.class);

    public static void onSkillUse(L1NpcInstance npc, int time, int idx) {
        try {
            L1MobSkill mobSkillTemplate = MobSkillTable.getInstance().getTemplate(npc.getNpcTemplate().get_npcId());
            mobSkillTemplate.setSkillDelayIdx(idx, true);
            GeneralThreadPool.get().schedule(new SkillDelayTimer(npc, idx), time);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    static class SkillDelayTimer implements Runnable {
        private L1NpcInstance _npc;
        private int _idx;

        public SkillDelayTimer(L1NpcInstance npc, int idx) {
            _npc = npc;
            _idx = idx;
        }

        public void run() {
            stopDelayTimer(_idx);
        }

        public void stopDelayTimer(int idx) {
            L1MobSkill mobSkillTemplate = MobSkillTable.getInstance().getTemplate(_npc.getNpcTemplate().get_npcId());
            mobSkillTemplate.setSkillDelayIdx(idx, false);
        }
    }
}
