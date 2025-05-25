package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;

public class DECAY_POTION extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (cha.hasSkillEffect(71)) {
            return 0;
        }
        cha.set_decay_potion(true);
        cha.setSkillEffect(71, integer * 1000);
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (cha.hasSkillEffect(71)) {
            return 0;
        }
        cha.set_decay_potion(true);
        cha.setSkillEffect(71, integer * 1000);
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        cha.set_decay_potion(false);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.DECAY_POTION JD-Core Version: 0.6.2
 */