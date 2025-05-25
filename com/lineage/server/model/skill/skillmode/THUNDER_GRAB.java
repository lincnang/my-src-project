package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

public class THUNDER_GRAB extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = magic.calcMagicDamage(192);
        Random random = new Random();
        int bindtime = random.nextInt(4) + 1;
        if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(192))) {
            bindtime += cha.getSkillEffectTimeSec(192);
        }
        if (bindtime > 4) {
            bindtime = 4;
        }
        final boolean isProbability = magic.calcProbabilityMagic(192);
        if ((isProbability) && (!cha.hasSkillEffect(4000))) {
            cha.setSkillEffect(192, bindtime * 1000);
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4184));
                L1SpawnUtil.spawnEffect(81182, bindtime, pc.getX(), pc.getY(), srcpc.getMapId(), srcpc, 0);
                pc.sendPackets(new S_Paralysis(6, true));
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance tgnpc = (L1NpcInstance) cha;
                tgnpc.broadcastPacketX8(new S_SkillSound(tgnpc.getId(), 4184));
                L1SpawnUtil.spawnEffect(81182, bindtime, tgnpc.getX(), tgnpc.getY(), srcpc.getMapId(), srcpc, 0);
                // tgnpc.setParalyzed(true);
                tgnpc.setPassispeed(0);
            }
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = magic.calcMagicDamage(192);
        Random random = new Random();
        int bindtime = random.nextInt(4) + 1;
        if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(192))) {
            bindtime += cha.getSkillEffectTimeSec(192);
        }
        if (bindtime > 4) {
            bindtime = 4;
        }
        final boolean isProbability = magic.calcProbabilityMagic(192);
        if ((isProbability) && (!cha.hasSkillEffect(4000))) {
            cha.setSkillEffect(192, bindtime * 1000);
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4184));
                L1SpawnUtil.spawnEffect(81182, bindtime, pc.getX(), pc.getY(), npc.getMapId(), npc, 0);
                pc.sendPackets(new S_Paralysis(6, true));
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance tgnpc = (L1NpcInstance) cha;
                tgnpc.broadcastPacketAll(new S_SkillSound(tgnpc.getId(), 4184));
                L1SpawnUtil.spawnEffect(81182, bindtime, tgnpc.getX(), tgnpc.getY(), npc.getMapId(), npc, 0);
                // tgnpc.setParalyzed(true);
                tgnpc.setPassispeed(0);
            }
        }
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(6, false));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            // npc.setParalyzed(false);
            npc.setPassispeed(npc.getNpcTemplate().get_passispeed());
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.THUNDER_GRAB JD-Core Version: 0.6.2
 */