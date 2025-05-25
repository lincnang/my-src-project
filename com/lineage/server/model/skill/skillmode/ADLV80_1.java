package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.ability.S_WeightStatus;

/**
 * 卡瑞的祝福(地龍副本) HP+100 MP+50 體力恢復量+3 魔力恢復量+3 地屬性魔防+30 額外攻擊點數+1 攻擊成功+5 ER+30 現有負重
 * / 1.04
 *
 * @author dexc
 */
public class ADLV80_1 extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!srcpc.hasSkillEffect(4009)) {
            srcpc.addHpr(3);
            srcpc.addMpr(3);
            srcpc.addMaxHp(100);
            srcpc.addMaxMp(50);
            srcpc.addEarth(30);
            srcpc.addDmgup(1);
            srcpc.addBowDmgup(1);
            srcpc.addHitup(5);
            srcpc.addBowHitup(5);
            srcpc.addWeightReduction(4);
            srcpc.setSkillEffect(4009, integer * 1000);
            srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
            srcpc.sendPackets(new S_MPUpdate(srcpc));
            srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
            // 7.6
            srcpc.sendPackets(new S_WeightStatus(srcpc.getInventory().getWeight() * 100 / (int) srcpc.getMaxWeight(), srcpc.getInventory().getWeight(), (int) srcpc.getMaxWeight()));
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        cha.addMaxHp(-100);
        cha.addMaxMp(-50);
        cha.addEarth(-30);
        cha.addDmgup(-1);
        cha.addBowDmgup(-1);
        cha.addHitup(-5);
        cha.addBowHitup(-5);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addHpr(-3);
            pc.addMpr(-3);
            pc.addWeightReduction(-4);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.sendPackets(new S_MPUpdate(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
            // 7.6
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.ADLV80_1 JD-Core Version: 0.6.2
 */