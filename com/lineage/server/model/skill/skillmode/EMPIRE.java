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

import static com.lineage.server.model.skill.L1SkillId.EMPIRE;

/**
 * 暈眩之劍 隨機時間2~5秒、最大時間6秒
 */
public class EMPIRE extends SkillMode {

    public EMPIRE() {}

    //@Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        final int dmg = 0;
        final Random random = new Random();

        // 取得技能DB設定
        L1Skills skill = SkillsTable.get().getTemplate(EMPIRE);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 100;
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;

        // 1. 判斷消耗材料
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                srcpc.sendPackets(new S_ServerMessage("施放暈眩之劍所需道具不足！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 2. 命中判斷
        int chance = baseChance + (diceChance > 0 ? random.nextInt(diceChance + 1) : 0);
        if (random.nextInt(100) >= chance) {
            srcpc.sendPackets(new S_ServerMessage("暈眩之劍未命中！"));
            return 0;
        }

        // 3. 暈眩秒數計算
        int shock = random.nextInt(4) + 3;// 隨機時間2~5
        if (!srcpc.getMap().isArrowPassable(cha.getX(), cha.getY())) return 0;
        // 取回目標是否已被施展衝暈
        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(EMPIRE)) {
            shock += cha.getSkillEffectTimeSec(EMPIRE);// 累計時間
        }
        if (shock > 6) shock = 6;

        cha.setSkillEffect(L1SkillId.EMPIRE, shock * 1000);
        // 騎士技能(暈眩之劍)
        L1SpawnUtil.spawnEffect(200300, shock, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);

        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
            pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 26, shock, false, false)); // Xml自製
        } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
            final L1NpcInstance tgnpc = (L1NpcInstance) cha;
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
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

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
