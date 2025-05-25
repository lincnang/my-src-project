package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SPMR;

public class ILLUSION_LICH extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!cha.hasSkillEffect(209)) {
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.addSp(2);
                pc.sendPackets(new S_SPMR(pc));
                pc.setSkillEffect(209, integer * 1000);
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance tgnpc = (L1NpcInstance) cha;
                tgnpc.addSp(2);
                tgnpc.setSkillEffect(209, integer * 1000);
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
            pc.addSp(-2);
            pc.sendPackets(new S_SPMR(pc));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.addSp(-2);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.ILLUSION_LICH JD-Core Version: 0.6.2
 */