package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1CurseParalysis;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;

public class REMOVE_CURSE extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        cha.curePoison();
        boolean isCursed = false;
        if (cha.getParalysis() != null && cha.getParalysis() instanceof L1CurseParalysis) {
            isCursed = true;
        } else if (cha.hasSkillEffect(1010) || cha.hasSkillEffect(1011)) {
            isCursed = true;
        }

        if (isCursed) {
            cha.cureParalaysis();
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_Paralysis(1, false, 0));
            }
            cha.setParalyzed(false);
            cha.removeSkillEffect(1010);
            cha.removeSkillEffect(1011);
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.REMOVE_CURSE JD-Core Version: 0.6.2
 */