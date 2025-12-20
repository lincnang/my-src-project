package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1PinkName;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.*;

public class SHAPE_CHANGE extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        final Random _random = new Random();
//        final int[] polyArray = {29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862, 3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378, 3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875, 3876};
        final int[] polyArray = {29,95,11346,11340,11339,11330}; //變形術
        final int pid = _random.nextInt(polyArray.length);
        final int polyId = polyArray[pid];
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            final int awakeSkillId = pc.getAwakeSkillId();
            if ((awakeSkillId == AWAKEN_ANTHARAS) || (awakeSkillId == AWAKEN_FAFURION) || (awakeSkillId == AWAKEN_VALAKAS)) {
                // 目前狀態中無法變身。
                pc.sendPackets(new S_ServerMessage(1384));
                return 0;
            }
            if (pc.getMapId() == 800) {
                return 0;
            }
            if (pc.getInventory().checkEquipped(20281) || pc.getInventory().checkEquipped(120281)) {// 變形控制戒指
                //pc.sendPackets(new S_ShowPolyList(pc.getId()));
                pc.sendPackets(new S_Message_YN(180));
                if (!pc.isShapeChange()) {
                    pc.setSummonMonster(false);
                    pc.setShapeChange(true);
                }
                // 感受到魔法保護的力量
                pc.sendPackets(new S_ServerMessage(966));
            } else {// 對手沒有裝備變戒
                if (srcpc.getId() == pc.getId()) {// 變身自己
                    //pc.sendPackets(new S_ShowPolyList(pc.getId()));// 顯示變身清單
                    pc.sendPackets(new S_Message_YN(180));
                    if (!pc.isShapeChange()) {
                        pc.setSummonMonster(false);
                        pc.setShapeChange(true);
                    }
                } else {// 變身其他玩家
                    pc.sendPackets(new S_ServerMessage(241, srcpc.getName())); // %0%s
                    // 把你變身。
                    L1PolyMorph.doPoly(pc, polyId, integer, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                }
            }
            // 攻擊對象是PC 設置紅名
            L1PinkName.onAction(pc, srcpc);
        } else if (cha instanceof L1MonsterInstance) {
            final L1MonsterInstance mob = (L1MonsterInstance) cha;
            if (mob.getLevel() < 50) {// 只能變身等級低於50級的怪物
                final int npcId = mob.getNpcTemplate().get_npcId();
                if ((npcId != 45464) && (npcId != 45473) && (npcId != 45488) && (npcId != 45497) // 四色
                        && (npcId != 45458) // 德雷克
                        && (npcId != 45752) // 炎魔(變身前)
                        && (npcId != 45492) // 庫曼
                        && (npcId != 46035) // 腐爛的 殭屍王
                        && (npcId != 99006)) {// 殘忍的牛鬼
                    L1PolyMorph.doPoly(mob, polyId, integer, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                }
            }
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        L1PolyMorph.undoPoly(cha);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.SOLID_CARRIAGE JD-Core Version:
 * 0.6.2
 */