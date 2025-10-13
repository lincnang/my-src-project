package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SPMR;

import static com.lineage.server.model.skill.L1SkillId.SHADOW_ARMOR;

/**
 * 影之防護99
 */
public class SHADOW_ARMOR extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            synchronized (pc) {
                if (!pc.hasSkillEffect(SHADOW_ARMOR)) {
                    pc.setSkillEffect(SHADOW_ARMOR, integer * 1000);
                    pc.addMr(5);
                    pc.sendPackets(new S_SPMR(pc));
                }
            }
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addMr(-5);
            pc.sendPackets(new S_SPMR(pc));
        }
    }
}
