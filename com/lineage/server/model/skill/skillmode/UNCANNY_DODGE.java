package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_PacketBoxIcon1;

import static com.lineage.server.model.skill.L1SkillId.UNCANNY_DODGE;

/**
 * 暗影閃避 106
 */
public class UNCANNY_DODGE extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if (!srcpc.hasSkillEffect(UNCANNY_DODGE)) {
            srcpc.setSkillEffect(UNCANNY_DODGE, integer * 1000);
            srcpc.add_dodge(5);
            // 更新閃避率顯示
            srcpc.sendPackets(new S_PacketBoxIcon1(true, srcpc.get_dodge()));
        }
        // set icon time
        // srcpc.sendPackets(new S_PacketBox(21, integer / 16));
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.add_dodge(-5);
            pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));
        }
    }
}
