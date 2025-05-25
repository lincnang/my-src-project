package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillBrave;

public class BLOODLUST extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        L1PcInstance pc = (L1PcInstance) cha;
        L1BuffUtil.braveStart(pc);
        pc.setSkillEffect(186, integer * 1000);
        pc.setBraveSpeed(1);// 勇水速度
        pc.sendPackets(new S_SkillBrave(pc.getId(), 1, integer));
        pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        cha.setBraveSpeed(0);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.BLOODLUST JD-Core Version: 0.6.2
 */