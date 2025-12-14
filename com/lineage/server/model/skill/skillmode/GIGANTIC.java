package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_HPUpdate;

import static com.lineage.server.model.skill.L1SkillId.GIGANTIC;

/**
 * 體能強化226
 */
public class GIGANTIC extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (!cha.hasSkillEffect(GIGANTIC)) {
            final L1PcInstance pc = (L1PcInstance) cha;
            if (pc.getGiganticHp() > 0) {
                pc.addMaxHp(-pc.getGiganticHp());
            }
            final int HP = (pc.getBaseMaxHp() / 100) * (pc.getLevel() / 2);
            pc.setGiganticHp(HP);
            pc.addMaxHp(pc.getGiganticHp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
        cha.setSkillEffect(GIGANTIC, integer * 1000);
        return dmg;
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            final L1PcInstance pc = (L1PcInstance) cha;
            //final int HP = (pc.getBaseMaxHp() / 100) * (pc.getLevel() / 2);
            pc.addMaxHp(-pc.getGiganticHp());
            pc.setGiganticHp(0);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
    }

    @Override
    public int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void start(L1PcInstance paramL1PcInstance, Object paramObject) throws Exception {
        // TODO Auto-generated method stub
    }
}
