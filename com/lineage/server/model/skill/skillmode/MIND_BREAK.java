package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 心靈破壞
 *
 * @author dexc
 */
public class MIND_BREAK extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int reMp = 5;
        L1ItemInstance weapon = srcpc.getWeapon(); // 幻術天賦技能心靈破壞
        if (weapon != null) {
            if (srcpc.getReincarnationSkill()[1] > 0 && weapon.getItem().getType1() == 58) { // 奇古獸
                reMp += srcpc.getReincarnationSkill()[1] * 15;
            }
        }
        int sp = srcpc.getSp();
        double dmg = sp * 3.8D;
        srcpc.sendPacketsAll(new S_SkillSound(cha.getId(), 6553));
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            int newMp = pc.getCurrentMp() - reMp;
            if (newMp < 0) {
                newMp = 0;
            }
            pc.setCurrentMp(newMp);
            pc.receiveDamage(srcpc, dmg, false, false);
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            int newMp = npc.getCurrentMp() - reMp;
            if (newMp < 0) {
                newMp = 0;
            }
            npc.setCurrentMp(newMp);
            npc.receiveDamage(srcpc, (int) dmg);
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
