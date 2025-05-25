package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;

/**
 * <font color=#00800>42516 巴風特雕像)</font><BR>
 * 點選後可獲得 HP+100,MP+100, 近距離傷害+5,近距離命中+10,遠距離傷害+5,遠距離命中+10,
 * 魔攻+5,力敏智+1，使用時間10分鐘，冷卻時間20分鐘
 *
 * @author dexc
 */
public class BAPHOMET extends SkillMode {
    public BAPHOMET() {
    }

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;
        if (!srcpc.hasSkillEffect(L1SkillId.Baphomet)) {
            srcpc.addMaxHp(100);
            srcpc.addMaxMp(100);
            srcpc.addDmgup(5);
            srcpc.addHitup(10);
            srcpc.addBowDmgup(5);
            srcpc.addBowHitup(10);
            srcpc.addSp(5);
            srcpc.addStr(1);
            srcpc.addDex(1);
            srcpc.addInt(1);
            srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
            srcpc.sendPackets(new S_MPUpdate(srcpc));
            srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
            srcpc.sendPackets(new S_SPMR(srcpc));
            srcpc.sendPackets(new S_OwnCharStatus2(srcpc));
            srcpc.setSkillEffect(L1SkillId.Baphomet, integer * 1000);
            // 1065:將發生神秘的奇跡力量。
            srcpc.sendPackets(new S_ServerMessage(1065));
        }
        return dmg;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;
        return dmg;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop(final L1Character srcpc) throws Exception {
        srcpc.addMaxHp(-100);
        srcpc.addMaxMp(-100);
        srcpc.addDmgup(-5);
        srcpc.addHitup(-10);
        srcpc.addBowDmgup(-5);
        srcpc.addBowHitup(-10);
        srcpc.addSp(-5);
        srcpc.addStr(-1);
        srcpc.addDex(-1);
        srcpc.addInt(-1);
        if ((srcpc instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) srcpc;
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.sendPackets(new S_MPUpdate(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus2(pc));
        }
    }
}
