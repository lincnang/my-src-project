package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillIllusion;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;

public class JOY_OF_PAIN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = (srcpc.getMaxHp() - srcpc.getCurrentHp()) / 5;
        if (dmg > ConfigSkillIllusion.JOY_OF_PAIN_DMG) {
            dmg = ConfigSkillIllusion.JOY_OF_PAIN_DMG;
        }
        if (!cha.hasSkillEffect(218)) {
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.setSkillEffect(218, integer * 1000);
                pc.sendPacketsX8(new S_SkillSound(cha.getId(), 6528));
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance npc = (L1NpcInstance) cha;
                npc.setSkillEffect(218, integer * 1000);
                npc.broadcastPacketX8(new S_SkillSound(cha.getId(), 6528));
            }
        }
        return dmg;
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
 * com.lineage.server.model.skill.skillmode.JOY_OF_PAIN JD-Core Version: 0.6.2
 */