package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1SkillEnhance;

import java.util.Random;

/**
 * 三重矢
 */

public class TRIPLE_ARROW extends SkillMode {
    private static final int TRIPLE_ARROW_SKILL_ID = 132;
    private Random _random = new Random();


    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = doBaseTripleArrow(srcpc, cha);
        int bonus = calculateExtraDamage(srcpc);
        srcpc.addTripleArrowCount(cha);
        return dmg + bonus;

    }

    private int doBaseTripleArrow(L1PcInstance srcpc, L1Character cha) {
        int dmg = 0;

        // 讓目標播放被攻擊動畫（這裡不會影響 triple arrow 的計數）
        for (int i = 0; i < 3; i++) {
            cha.onAction(srcpc);
        }
        // 播放三重矢動畫效果
        srcpc.sendPacketsAll(new S_SkillSound(srcpc.getId(), 4394));
//        srcpc.sendPacketsAll(new S_SkillSound(srcpc.getId(), 11764));
        srcpc.sendPacketsAll(new S_SkillSound(srcpc.getId(), 11782));
        return dmg;
    }

    private int calculateExtraDamage(L1PcInstance srcpc) {
        int bonusDamage = 0;
        int currentLevel = srcpc.getSkillLevel(TRIPLE_ARROW_SKILL_ID);
        L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(TRIPLE_ARROW_SKILL_ID, currentLevel);
        if (enhanceData != null) {
            int fixedBonus = enhanceData.getSetting1();
            int randomBonus = enhanceData.getSetting2();
            bonusDamage = fixedBonus;
            if (randomBonus > 0) {
                bonusDamage += _random.nextInt(randomBonus + 1);
            }
        }
        return bonusDamage;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        for (int i = 3; i > 0; i--) {
            npc.attackTarget(cha);
        }
        npc.broadcastPacketAll(new S_SkillSound(npc.getId(), 4394));// 加速封包
        npc.broadcastPacketAll(new S_SkillSound(npc.getId(), 11782));// 特效動畫
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}