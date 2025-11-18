package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillBrave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

public class HASTE3X extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int timeMillis = integer * 1000;
        // 需求：暗影加速施放勇敢類加速，且可與三段加速共存
        if (srcpc.hasSkillEffect(STATUS_BRAVE3)) {
            // 三段存在時，不變更移動速度與三段圖示，僅補上勇敢狀態與圖示
            int objId = srcpc.getId();
            srcpc.setSkillEffect(STATUS_BRAVE, timeMillis);
            srcpc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
            srcpc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));
//            srcpc.sendPacketsX8(new S_SkillSound(objId, 751));
            return 0;
        }
        // 未有三段時,自行實作勇敢邏輯(不發送751特效)
        L1BuffUtil.braveStart(srcpc); // 處理技能衝突
        srcpc.setSkillEffect(STATUS_BRAVE, timeMillis);
        int objId = srcpc.getId();
        srcpc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
        srcpc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));
        // 不發送 751 特效: srcpc.sendPacketsX8(new S_SkillSound(objId, 751));
        srcpc.setBraveSpeed(1);
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
