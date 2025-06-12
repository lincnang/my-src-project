package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

public class HASTE3X extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        srcpc.sendPackets(new S_ServerMessage(1065));// 將發生神秘的奇跡力量。
        srcpc.sendPacketsX8(new S_SkillSound(srcpc.getId(), 2945));
        srcpc.setSkillEffect(STATUS_BRAVE3, integer * 1000);
        srcpc.broadcastPacketAll(new S_Liquor(srcpc.getId(), 0x08));
        if (srcpc.getMoveSpeed() != 2) {
            srcpc.setMoveSpeed(1);
        }
        return 0;
    }

    @Override
    public int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
