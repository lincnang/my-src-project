package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

/**
 * 暗影衝擊（黑妖被動技能：攻擊時1%暈眩，玩家/NPC皆有特效動畫）
 */
public class SHADOW_IMPACT extends SkillMode {

    private static final int SKILL_ID = L1SkillId.PASSIVE_SHADOW_IMPACT;
    private static final int BASE_CHANCE = 1;   // 測試100%，正式1
    private static final int SHOCK_SEC = 2;       // 固定麻痺2秒
    private static final Random _random = new Random();

    /**
     * 主動觸發（攻擊時用）
     */
    public static void tryStun(L1PcInstance attacker, L1Character target) {
        if (attacker == null || target == null) return;
        if (!attacker.hasPassiveShadowImpact()) return;

        int roll = _random.nextInt(100) + 1;
        if (roll > BASE_CHANCE) return; // 超過機率不發動

        int shock = SHOCK_SEC; // 固定2秒

        // 如果已經麻痺，先解除再重加
        if (target.hasSkillEffect(SKILL_ID)) {
            target.removeSkillEffect(SKILL_ID);
            target.setParalyzed(false);
            if (target instanceof L1PcInstance)
                ((L1PcInstance) target).sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
        }

        // 玩家或NPC都產生地圖動畫特效
        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
            pc.setParalyzed(true);
            pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 26, shock, false, false));
            L1SpawnUtil.spawnEffect(200426, shock, pc.getX(), pc.getY(), pc.getMapId(), pc, 0);
        } else if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            npc.setParalyzed(true);
            // 對NPC也產生動畫（不論有無玩家在場）
            L1SpawnUtil.spawnEffect(200426, shock, npc.getX(), npc.getY(), npc.getMapId(), npc, 0);
        }

        // 設定技能麻痺狀態（務必在動畫/ICON後，否則特效可能無法正確出現）
        target.setSkillEffect(SKILL_ID, shock * 1000);

        // 攻擊者訊息（可註解掉）
        attacker.sendPackets(new S_ServerMessage("【暗影衝擊】發動！目標暈眩" + shock + "秒"));
    }

    /**
     * 被動解除（到期時自動呼叫）
     */
    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.setParalyzed(false);
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
        } else if (cha instanceof L1NpcInstance) {
            final L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }

    // 必須補齊父類 SkillMode 的抽象方法（被動技能都不用實作內容）
    @Override
    public void start(com.lineage.server.model.Instance.L1PcInstance pc, Object obj) throws Exception {}

    @Override
    public int start(com.lineage.server.model.Instance.L1PcInstance pc,
                     com.lineage.server.model.L1Character cha,
                     com.lineage.server.model.L1Magic magic,
                     int integer) throws Exception {
        return 0;
    }

    @Override
    public int start(com.lineage.server.model.Instance.L1NpcInstance npc,
                     com.lineage.server.model.L1Character cha,
                     com.lineage.server.model.L1Magic magic,
                     int integer) throws Exception {
        return 0;
    }
}
