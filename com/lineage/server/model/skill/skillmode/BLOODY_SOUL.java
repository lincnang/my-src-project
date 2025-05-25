package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;

/**
 * 魂體轉換
 */
public class BLOODY_SOUL extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        int ReiMp = 0;
        if (srcpc.getReincarnationSkill()[0] > 0) { // 妖精天賦技能神聖祝福
            ReiMp = srcpc.getReincarnationSkill()[0] * 2;
        }
        srcpc.setCurrentMp(srcpc.getCurrentMp() + 15 + ReiMp);
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
