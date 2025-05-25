package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.L1SpawnUtil;

public class BONE_BREAK extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = magic.calcMagicDamage(208);
        boolean isProbability = magic.calcProbabilityMagic(208);
        //final int rad = Random.nextInt(100) + 1;
        //if (rad < ConfigSkill.Skulldamage) {
        final int time = 2;
        if ((!cha.hasSkillEffect(208)) && (isProbability)) {
            cha.setSkillEffect(208, time * 1000);
            //L1SpawnUtil.spawnEffect(86123, 3, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);
            L1SpawnUtil.spawnEffect(86123, time, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_Paralysis(5, true));
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 13119));// 骷髏毀壞動畫
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance npc = (L1NpcInstance) cha;
                npc.setParalyzed(true);
                npc.broadcastPacketAll(new S_SkillSound(npc.getId(), 13119));// 骷髏毀壞動畫
            }
        }
        //}
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
            pc.sendPackets(new S_Paralysis(5, false));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }
}
