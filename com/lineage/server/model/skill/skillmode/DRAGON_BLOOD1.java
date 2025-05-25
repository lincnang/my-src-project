package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_PacketBox;

public class DRAGON_BLOOD1 extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!srcpc.hasSkillEffect(6797)) {
            System.out.println("玩家:" + srcpc.getName() + " 發動地龍-龍之血痕效果");
            srcpc.addAc(-2);
            srcpc.addWater(50);
            srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
            srcpc.setSkillEffect(6797, integer * 1000);
            srcpc.sendPackets(new S_PacketBox(100, 82, integer / 60));
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
            pc.addAc(2);
            pc.addWater(-50);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.DRAGON_BLOOD1 JD-Core Version: 0.6.2
 */