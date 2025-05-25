package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class RUN_CLAN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        L1PcInstance pc = (L1PcInstance) cha;
        L1PcInstance clanPc = (L1PcInstance) World.get().findObject(integer);
        if (clanPc != null) {
            if ((pc.getMap().isEscapable()) || (pc.isGm())) {
                boolean castle_area = L1CastleLocation.checkInAllWarArea(clanPc.getX(), clanPc.getY(), clanPc.getMapId());
                if ((!castle_area) && ((clanPc.getMapId() == 0) || (clanPc.getMapId() == 4) || (clanPc.getMapId() == 304))) {
                    L1Teleport.teleport(pc, clanPc.getX(), clanPc.getY(), clanPc.getMapId(), 5, true);
                    return 0;
                }
                pc.sendPackets(new S_ServerMessage(1192));
                pc.sendPackets(new S_Paralysis(7, false));
            } else {
                pc.sendPackets(new S_ServerMessage(647));
                pc.sendPackets(new S_Paralysis(7, false));
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
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.RUN_CLAN JD-Core Version: 0.6.2
 */