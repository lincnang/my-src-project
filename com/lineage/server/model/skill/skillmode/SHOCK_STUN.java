package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillKnight;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

public class SHOCK_STUN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        Random random = new Random();
        int shock = random.nextInt(3) + 2;
        if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(87))) {
            shock += cha.getSkillEffectTimeSec(87);
        }
        if (srcpc.isKnight() && srcpc.getMeteLevel() >= 2) {   //SRC0808
            shock += ConfigSkillKnight.K2;
        }
        if (shock > 4) {
            shock = 4;
        }
        cha.setSkillEffect(87, shock * 1000);
        L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
            pc.setParalyzed(true);
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        Random random = new Random();
        String[] sec = ConfigSkillKnight.STUN_SEC.split("~");
        int shock = random.nextInt(Integer.parseInt(sec[1]) - Integer.parseInt(sec[0]) + 1) + Integer.parseInt(sec[0]);
        // 取回目標是否已被施展沖暈
        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(87)) {
            shock += cha.getSkillEffectTimeSec(87);// 累計時間
        }
        if (shock > 8) {// 最大沖暈時間8秒
            shock = 8;
        }
        cha.setSkillEffect(87, shock * 1000);
        L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), npc.getMapId(), npc, 0);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
            pc.setParalyzed(true);
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1GuardianInstance)) || ((cha instanceof L1GuardInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, false));
            pc.setParalyzed(false);  // 修復：清除玩家麻痹狀態
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1GuardianInstance)) || ((cha instanceof L1GuardInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.SHOCK_STUN JD-Core Version: 0.6.2
 */