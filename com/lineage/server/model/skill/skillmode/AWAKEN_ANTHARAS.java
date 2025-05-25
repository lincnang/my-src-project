package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;

/**
 * 覺醒：安塔瑞斯185
 */
public class AWAKEN_ANTHARAS extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (!srcpc.hasSkillEffect(AWAKEN_ANTHARAS)) {
            srcpc.addRegistSustain(10);
            srcpc.addAc(-3);
            srcpc.setSkillEffect(AWAKEN_ANTHARAS, integer * 1000);
            srcpc.sendPackets(new S_OwnCharStatus(srcpc));
        }
        srcpc.sendPacketsX8(new S_SkillSound(cha.getId(), 6975));
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
            pc.addRegistSustain(-10);
            pc.addAc(3);
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }
}
