package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;

import java.util.Random;

public class EARTH_BIND extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        Random rad = new Random();
        int i = rad.nextInt(6) + 6;
        if (!srcpc.castleWarResult()) {
            cha.setSkillEffect(157, i * 1000);
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
            } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                final L1NpcInstance npc = (L1NpcInstance) cha;
                npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
                npc.setParalyzed(true);
            }
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        Random rad = new Random();
        int i = rad.nextInt(12) + 1;
        cha.setSkillEffect(157, i * 1000);
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
        } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
            final L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.broadcastPacketAll(new S_Poison(tgnpc.getId(), 2));
            tgnpc.setParalyzed(true);
        }
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPacketsAll(new S_Poison(pc.getId(), 0));
            pc.sendPackets(new S_Paralysis(4, false));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.broadcastPacketAll(new S_Poison(npc.getId(), 0));
            npc.setParalyzed(false);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.DECAY_POTION JD-Core Version: 0.6.2
 */