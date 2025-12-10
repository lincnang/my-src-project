package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SkillBrave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

public class HASTE3X extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int timeMillis = integer * 1000;
        
        // 處理技能衝突：靜默移除所有二段加速狀態 (包含行走加速、風之疾走等)
        // 必須發送 S_SkillBrave(0) 以確保客戶端移除舊的加速圖示與效果，避免疊加導致超速
        if (srcpc.hasSkillEffect(L1SkillId.HOLY_WALK)) {
            srcpc.killSkillEffectTimer(L1SkillId.HOLY_WALK);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.MOVING_ACCELERATION)) {
            srcpc.killSkillEffectTimer(L1SkillId.MOVING_ACCELERATION);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.WIND_WALK)) {
            srcpc.killSkillEffectTimer(L1SkillId.WIND_WALK);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.STATUS_BRAVE2)) {
            srcpc.killSkillEffectTimer(L1SkillId.STATUS_BRAVE2);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.STATUS_BRAVE)) {
            srcpc.killSkillEffectTimer(L1SkillId.STATUS_BRAVE);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.STATUS_ELFBRAVE)) {
            srcpc.killSkillEffectTimer(L1SkillId.STATUS_ELFBRAVE);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.STATUS_RIBRAVE)) {
            srcpc.killSkillEffectTimer(L1SkillId.STATUS_RIBRAVE);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }
        if (srcpc.hasSkillEffect(L1SkillId.BLOODLUST)) {
            srcpc.killSkillEffectTimer(L1SkillId.BLOODLUST);
            srcpc.sendPackets(new S_SkillBrave(srcpc.getId(), 0, 0));
            srcpc.broadcastPacketAll(new S_SkillBrave(srcpc.getId(), 0, 0));
        }

        // 設定暗影加速效果 (使用 Shadow_ACCELERATION)
        srcpc.setSkillEffect(L1SkillId.Shadow_ACCELERATION, timeMillis);
        int objId = srcpc.getId();
        srcpc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
        srcpc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));
        // srcpc.sendPacketsX8(new S_SkillSound(objId, 751)); // 特效

        // 設定移動速度數值
        // 若已有三段加速 (STATUS_BRAVE3)，則維持三段速度 (5)
        // 否則設定為二段速度 (1)
        if (srcpc.hasSkillEffect(STATUS_BRAVE3)) {
            srcpc.setBraveSpeed(5);
        } else {
            srcpc.setBraveSpeed(1);
        }
        
        // 重置加速器檢測計時器，避免因速度切換導致的誤判 (反白手)
        if (srcpc.speed_Attack() != null) {
            srcpc.speed_Attack().Reset();
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
