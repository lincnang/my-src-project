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
        String[] sec = ConfigSkillKnight.STUN_SEC.split("~");
        int min = Integer.parseInt(sec[0].trim());
        int max = Integer.parseInt(sec[1].trim());
        int shock = random.nextInt(max - min + 1) + min; // 依 SHOCK_STUN_TIMER 隨機
        // 騎士額外加秒（保持原行為）
        if (srcpc.isKnight() && srcpc.getMeteLevel() >= 2) {   //SRC0808
            shock += ConfigSkillKnight.K2;
        }
        // 以設定上限為最大值
        if (shock > max) shock = max; // 保障不超過設定上限
        // 設定實際效果，並用實際剩餘秒數播放特效，確保同步
        cha.setSkillEffect(87, shock * 1000);
        int durationSec = cha.getSkillEffectTimeSec(87);
        L1SpawnUtil.spawnEffect(81162, durationSec, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        Random random = new Random();
        String[] sec = ConfigSkillKnight.STUN_SEC.split("~");
        int min = Integer.parseInt(sec[0].trim());
        int max = Integer.parseInt(sec[1].trim());
        int shock = random.nextInt(max - min + 1) + min;
        if (shock > max) shock = max;
        cha.setSkillEffect(87, shock * 1000);
        int durationSec = cha.getSkillEffectTimeSec(87);
        L1SpawnUtil.spawnEffect(81162, durationSec, cha.getX(), cha.getY(), npc.getMapId(), npc, 0);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
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
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
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