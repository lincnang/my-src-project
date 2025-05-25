package com.lineage.server.model.skill.skillmode;

import com.add.PeepCard;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1WeaponSkill;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Random;

public class Phantom_Blade extends SkillMode {
    private static final Random _random = new Random();

    private static void broadcastPacket(L1Character cha, ServerBasePacket packet) {
        ArrayList<L1PcInstance> list = null;
        list = World.get().getVisiblePlayer(cha);
        for (L1PcInstance pc : list) {
            pc.sendPackets(packet);
        }
    }

    public int start(L1PcInstance pc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (cha == null) {
            return 0;
        }
        if (_random.nextInt(100) >= 100) {
            pc.sendPackets((ServerBasePacket) new S_ServerMessage(280));
            return 0;
        }
        if (L1WeaponSkill.isFreeze(cha)) {
            return 0;
        }
        if (cha instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) cha;
            PeepCard.TeleportPc(pc, (L1Object) targetPc);
        }
        if (!(cha instanceof L1NpcInstance)) {
            return 0;
        }
        return 0;
    }

    @Override
    public int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception {
        return 0;
    }

    @Override
    public void stop(L1Character paramL1Character) throws Exception {
    }

    @Override
    public void start(L1PcInstance paramL1PcInstance, Object paramObject) throws Exception {
    }
}
