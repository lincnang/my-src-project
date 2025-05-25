package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;

public class ILLUSION_AVATAR extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (((cha instanceof L1PcInstance)) && (!cha.hasSkillEffect(219))) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addDmgup(10);
            cha.setSkillEffect(219, integer * 1000);
            if (srcpc.getMeteLevel() >= 4) {
                cha.setSkillEffect(1219, integer * 1000);              //SRC0808
            }
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addDmgup(-10);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.ILLUSION_AVATAR JD-Core Version:
 * 0.6.2
 */