package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.EXOTIC_VITALIZE;

/**
 * 覺醒：法利昂190
 */
public class AWAKEN_FAFURION extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (!srcpc.hasSkillEffect(AWAKEN_FAFURION)) {
            srcpc.add_regist_freeze(10);
            srcpc.setSkillEffect(EXOTIC_VITALIZE, integer * 1000);
            srcpc.setSkillEffect(AWAKEN_FAFURION, integer * 1000);
            srcpc.sendPackets(new S_OwnCharStatus(srcpc));
        }
        srcpc.sendPacketsX8(new S_SkillSound(cha.getId(), 6976));
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
            pc.add_regist_freeze(-10);
            pc.killSkillEffectTimer(EXOTIC_VITALIZE);
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }
}
