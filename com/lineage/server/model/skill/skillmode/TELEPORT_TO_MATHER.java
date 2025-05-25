package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.Teleportation;

import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

public class TELEPORT_TO_MATHER extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        L1PcInstance pc = (L1PcInstance) cha;
        if (pc.hasSkillEffect(THUNDER_GRAB) && ConfigSkillDragon.SLAY_BREAK_NOT_TELEPORT == true) {
            pc.sendPackets(new S_ServerMessage("\\fY身上有奪命之雷的效果無法瞬移"));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
        if ((pc.getMap().isEscapable()) || (pc.isGm())) {
            if (pc.getTradeID() != 0) {
                L1Trade trade = new L1Trade();
                trade.tradeCancel(pc);
            }
            pc.setTeleportX(33047);
            pc.setTeleportY(32338);
            pc.setTeleportMapId((short) 4);
            pc.setTeleportHeading(5);
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
            Teleportation.teleportation(pc);
        } else {
            pc.sendPackets(new S_ServerMessage(276));
            pc.sendPackets(new S_Paralysis(7, false));
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.TELEPORT_TO_MATHER JD-Core Version:
 * 0.6.2
 */