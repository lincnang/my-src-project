package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.Controller.IntBonusManager;

public class INSIGHT extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!cha.hasSkillEffect(216)) {
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.addStr(1);
                pc.addCon(1);
                pc.addDex(1);
                pc.addWis(1);
                pc.addInt(1);
                // 智力變動後重新套用智力加成
                IntBonusManager.get().reapply(pc);
                pc.setSkillEffect(216, integer * 1000);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendDetails();
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance tgnpc = (L1NpcInstance) cha;
                tgnpc.addStr(1);
                tgnpc.addCon(1);
                tgnpc.addDex(1);
                tgnpc.addWis(1);
                tgnpc.addInt(1);
                tgnpc.setSkillEffect(216, integer * 1000);
            }
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addStr(-1);
            pc.addCon(-1);
            pc.addDex(-1);
            pc.addWis(-1);
            pc.addInt(-1);
            // 智力變動後重新套用智力加成
            IntBonusManager.get().reapply(pc);
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendDetails();
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.addStr(-1);
            tgnpc.addCon(-1);
            tgnpc.addDex(-1);
            tgnpc.addWis(-1);
            tgnpc.addInt(-1);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.INSIGHT JD-Core Version: 0.6.2
 */