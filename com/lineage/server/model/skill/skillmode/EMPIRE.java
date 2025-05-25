package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.EMPIRE;

/**
 * 暈眩之劍 隨機時間2~5秒、最大時間6秒
 *
 * @author dexc
 */
public class EMPIRE extends SkillMode {

    public EMPIRE() {
    }

    //@Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        final int dmg = 0;
        final Random random = new Random();
        int shock = random.nextInt(4) + 3;// 隨機時間2~5

        // 取回目標是否已被施展衝暈
        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(EMPIRE)) {
            shock += cha.getSkillEffectTimeSec(EMPIRE);// 累計時間
        }

        if (shock > 6) {// 最大衝暈時間6秒
            shock = 6;
        }

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