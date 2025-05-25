package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_PacketBoxDk;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

/**
 * 屠宰者
 *
 * @author dexc
 */
public class FOE_SLAYER extends SkillMode {//SRC0808
    private static Random _random = new Random();

    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        srcpc.setFoeSlayer(true);
        for (int i = 0; i < 3; i++) {
            cha.onAction(srcpc);
            if (i == 2) {// 第三次
                srcpc.setFoeSlayer(false);
                srcpc.set_weaknss(0, 0);
                srcpc.sendPackets(new S_PacketBoxDk(0));
            }
        }
        if (srcpc.isDragonKnight() && srcpc.getMeteLevel() >= 4 && (_random.nextInt(100) + 1) < ConfigSkillDragon.DK4RANDOM) {//SRC0808
            int shock = ConfigSkillDragon.DK4SHOCKTIME;
            cha.setSkillEffect(87, shock * 1000);
            L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_Paralysis(5, true));
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance tgnpc = (L1NpcInstance) cha;
                tgnpc.setParalyzed(true);
            }
        }
        srcpc.sendPacketsAll(new S_SkillSound(srcpc.getId(), 7020));// 屠宰者 加速封包
        srcpc.sendPacketsAll(new S_SkillSound(cha.getId(), 12119));// 屠宰者 特效動畫
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        for (int i = 0; i < 3; i++) {
            npc.attackTarget(cha);
        }
        npc.broadcastPacketAll(new S_SkillSound(cha.getId(), 7020));// 屠宰者 加速封包
        npc.broadcastPacketAll(new S_SkillSound(cha.getId(), 12119));// 屠宰者 特效動畫
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
