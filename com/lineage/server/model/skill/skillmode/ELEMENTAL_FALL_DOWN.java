package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_ServerMessage;

public class ELEMENTAL_FALL_DOWN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!cha.hasSkillEffect(133)) {
            int playerAttr = srcpc.getElfAttr();
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                switch (playerAttr) {
                    case 0:
                        srcpc.sendPackets(new S_ServerMessage(79));
                        break;
                    case 1:
                        pc.addEarth(-50);
                        pc.setAddAttrKind(1);
                        break;
                    case 2:
                        pc.addFire(-50);
                        pc.setAddAttrKind(2);
                        break;
                    case 4:
                        pc.addWater(-50);
                        pc.setAddAttrKind(4);
                        break;
                    case 8:
                        pc.addWind(-50);
                        pc.setAddAttrKind(8);
                        break;
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    default:
                        break;
                }
            } else if ((cha instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) cha;
                switch (playerAttr) {
                    case 0:
                        srcpc.sendPackets(new S_ServerMessage(79));
                        break;
                    case 1:
                        mob.addEarth(-50);
                        mob.setAddAttrKind(1);
                        break;
                    case 2:
                        mob.addFire(-50);
                        mob.setAddAttrKind(2);
                        break;
                    case 4:
                        mob.addWater(-50);
                        mob.setAddAttrKind(4);
                        break;
                    case 8:
                        mob.addWind(-50);
                        mob.setAddAttrKind(8);
                        break;
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                }
            }
        }
        cha.setSkillEffect(133, integer * 1000);
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
            int attr = pc.getAddAttrKind();
            switch (attr) {
                case 1:
                    pc.addEarth(50);
                    break;
                case 2:
                    pc.addFire(50);
                    break;
                case 4:
                    pc.addWater(50);
                    break;
                case 8:
                    pc.addWind(50);
                    break;
                case 3:
                case 5:
                case 6:
                case 7:
            }
            pc.setAddAttrKind(0);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        } else if ((cha instanceof L1NpcInstance)) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            int attr = npc.getAddAttrKind();
            switch (attr) {
                case 1:
                    npc.addEarth(50);
                    break;
                case 2:
                    npc.addFire(50);
                    break;
                case 4:
                    npc.addWater(50);
                    break;
                case 8:
                    npc.addWind(50);
                    break;
                case 3:
                case 5:
                case 6:
                case 7:
            }
            npc.setAddAttrKind(0);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.ELEMENTAL_FALL_DOWN JD-Core Version:
 * 0.6.2
 */