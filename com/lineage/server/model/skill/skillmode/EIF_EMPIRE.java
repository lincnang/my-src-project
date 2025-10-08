package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.EIF_EMPIRE;

/**
 * 精靈之暈 隨機時間2~5秒、最大時間6秒
 * 每次施放都會強制刷新動畫與效果
 */
public class EIF_EMPIRE extends SkillMode {

    public EIF_EMPIRE() {}

    //@Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        final int dmg = 0;
        final Random random = new Random();

        // 1. 取得技能設定
        L1Skills skill = SkillsTable.get().getTemplate(EIF_EMPIRE);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 100; // 預設必中
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;

        // 2. 材料檢查與扣除
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                srcpc.sendPackets(new S_ServerMessage("精靈之暈所需道具不足！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 3. 命中率判斷
        int chance = baseChance + (diceChance > 0 ? random.nextInt(diceChance + 1) : 0);
        if (random.nextInt(100) >= chance) {
            return 0;
        }

        // 4. 計算暈眩秒數（隨機2~5，累加最大6）
        int shock = random.nextInt(4) + 3;// 3~6
        if (cha.hasSkillEffect(EIF_EMPIRE)) {
            shock += cha.getSkillEffectTimeSec(EIF_EMPIRE);
        }
        if (shock > 6) shock = 6;

        // 5. 解除現有暈眩動畫與效果，確保動畫會刷新
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(EIF_EMPIRE)) {
                pc.removeSkillEffect(EIF_EMPIRE);
                pc.setParalyzed(false);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
            }
        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            if (npc.hasSkillEffect(EIF_EMPIRE)) {
                npc.removeSkillEffect(EIF_EMPIRE);
                npc.setParalyzed(false);
            }
        }

        // 6. 設定新的暈眩與動畫
        cha.setSkillEffect(L1SkillId.EIF_EMPIRE, shock * 1000);
        L1SpawnUtil.spawnEffect(200425, shock, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
            pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 26, shock, false, false));
        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return dmg;
    }

    //@Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        return 0;
    }

    //@Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {}

    //@Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
        } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
                || (cha instanceof L1GuardianInstance) || (cha instanceof L1GuardInstance)
                || (cha instanceof L1PetInstance)) {
            final L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }
}
