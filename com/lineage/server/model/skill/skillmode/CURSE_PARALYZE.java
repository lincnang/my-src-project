package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1CurseParalysis;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;

public class CURSE_PARALYZE extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if ((!cha.hasSkillEffect(157)) && (!cha.hasSkillEffect(50))) {
            if ((cha instanceof L1PcInstance)) {
                L1CurseParalysis.curse(cha, 5000, 4000, 1);
            } else if ((cha instanceof L1MonsterInstance)) {
                L1CurseParalysis.curse(cha, 5000, 4000, 0);
            }
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if ((!cha.hasSkillEffect(157)) && (!cha.hasSkillEffect(50))) {
            if ((cha instanceof L1PcInstance)) {
                L1CurseParalysis.curse(cha, 5000, 4000, 1);
            } else if ((cha instanceof L1MonsterInstance)) {
                L1CurseParalysis.curse(cha, 5000, 4000, 0);
            }
        }
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPacketsAll(new S_Poison(cha.getId(), 0));
            pc.sendPackets(new S_Paralysis(1, false, 0));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.broadcastPacketAll(new S_Poison(cha.getId(), 0));
            npc.setParalyzed(false);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.CURSE_PARALYZE JD-Core Version:
 * 0.6.2
 */