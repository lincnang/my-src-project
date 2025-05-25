package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_NPCPack;

public class KIRTAS_BARRIER2 extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        npc.startKIRTAS_Timer();
        int dmg = magic.calcMagicDamage(11059);
        npc.setHiddenStatus(4);
        npc.setStatus(40);
        npc.setSkillEffect(11059, integer * 1000);
        npc.broadcastPacketAll(new S_NPCPack(npc));
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.KIRTAS_BARRIER2 JD-Core Version:
 * 0.6.2
 */