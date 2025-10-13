package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillBrave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;

public class HASTE3X extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 取消其他勇敢系統衝突，改套用一段勇敢效果，時間採用 skills.buffDuration(秒)
        L1BuffUtil.braveStart(srcpc);
        srcpc.setSkillEffect(STATUS_BRAVE, integer * 1000);
        srcpc.setBraveSpeed(1);
        // 僅顯示勇敢一段圖示
        srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 1, integer));
        srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 1, 0));
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
