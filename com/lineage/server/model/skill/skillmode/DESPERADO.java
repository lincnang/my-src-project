package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.L1SpawnUtil;

import static com.lineage.server.model.skill.L1SkillId.DESPERADO;

public class DESPERADO extends SkillMode {
    public DESPERADO() {
    }

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;
        int grip = 6;//random.nextInt(5) + 4;// 隨機時間2~5
        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(DESPERADO)) {
            grip += cha.getSkillEffectTimeSec(DESPERADO);// 累計時間
        }
        if (grip > 6) {
            grip = 6;
        }
        final boolean isProbability = magic.calcProbabilityMagic(230);
        if ((isProbability)) {
            L1SpawnUtil.spawnEffect(93005, grip, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
            cha.setSkillEffect(DESPERADO, grip * 1000);
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                //pc.sendPacketsAll(new S_SkillSound(pc.getId(), 12760));
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_DESPERADO, true));
                srcpc.sendPacketsAll(new S_SkillSound(pc.getId(), 12758));
            } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                final L1NpcInstance npc = (L1NpcInstance) cha;
                //npc.broadcastPacketAll(new S_SkillSound(npc.getId(), 12760));
                srcpc.sendPacketsAll(new S_SkillSound(npc.getId(), 12758));
                npc.setGripped(true);
            }
        }
        return dmg;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_DESPERADO, false));
        } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
            final L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setGripped(false);
        }
    }
}
