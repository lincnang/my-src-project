package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_NewSkillIcon;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.Blood_strength;

/**
 * 力量之血232
 */
public class Blood_strength extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (cha.hasSkillEffect(9973)) {
            cha.killSkillEffectTimer(9973);
        }
        if (srcpc.hasSkillEffect(Blood_strength)) {
            srcpc.removeSkillEffect(Blood_strength);
        }
        srcpc.setSkillEffect(9973, 60 * 60 * 1000);
        srcpc.sendPackets(new S_ServerMessage("攻擊[附加吸血效果]"));
        srcpc.sendPackets(new S_OwnCharStatus2(srcpc));
        srcpc.sendPackets(new S_SPMR(srcpc));
        srcpc.setSkillEffect(Blood_strength, 720 * 1000);
        srcpc.sendPackets(new S_NewSkillIcon(Blood_strength, true, 720));
        return dmg;
    }

    @Override
    public int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void stop(L1Character paramL1Character) throws Exception {
    }

    @Override
    public void start(L1PcInstance paramL1PcInstance, Object paramObject) throws Exception {
        // TODO Auto-generated method stub
    }
}
