package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_PacketBox;

public class STRIKER_GALE extends SkillMode {  //SRC0808
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        cha.setSkillEffect(174, integer * 1000);
        if (srcpc.getMeteLevel() >= 4) {
            cha.setSkillEffect(1174, integer * 1000);
        }
        if ((cha instanceof L1PcInstance)) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, 0));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            return 0;
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        cha.setSkillEffect(174, integer * 1000);
        if ((cha instanceof L1PcInstance)) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, 0));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            return 0;
        }
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1GuardianInstance)) || ((cha instanceof L1GuardInstance)) || ((cha instanceof L1PetInstance))) {
            return;
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.SHOCK_STUN JD-Core Version: 0.6.2
 */