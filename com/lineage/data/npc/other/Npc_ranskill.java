package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.utils.RandomArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_ranskill extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_ranskill.class);

    public static NpcExecutor get() {
        return new Npc_ranskill();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "map_skillnp"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("npcskill1")) {
            if (!pc.getInventory().checkItem(44070, 5)) {
                pc.sendPackets(new S_SystemMessage("元寶不足5個"));
                return;
            }
            if (pc.hasSkillEffect(9900)) {
                return;
            }
            pc.getInventory().consumeItem(44070, 5);
            pc.setSkillEffect(9900, 1000);
            switch (RandomArrayList.getInc(13, 1)) {
                case 1:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9964, 60 * 60 * 1000);
                    pc.addStr(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[力量+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 2:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9965, 60 * 60 * 1000);
                    pc.addDex(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[敏捷+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 3:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9966, 60 * 60 * 1000);
                    pc.addInt(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[智力+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 4:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9967, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[攻擊+15]"));
                    pc.addDmgup(15);
                    pc.addBowDmgup(12);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 5:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9968, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[命中+10]"));
                    pc.addHitup(10);
                    pc.addBowHitup(7);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 6:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                        pc.sendPackets(new S_SPMR(pc));
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9969, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[魔法攻擊+5]"));
                    pc.addSp(5);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    break;
                case 7:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得掉寶率+1%]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 8:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9971, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[3%發動爆擊]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 9:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9972, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[3%武器附加沖暈效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg1(true);
                    break;
                case 10:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9973, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[10%附加吸血效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg2(true);
                    break;
                case 11:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9974, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[10%附加吸魔效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg3(true);
                    break;
                case 12:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9975, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得狩獵經驗提高]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 13:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9976, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得5%受到所有傷害/3]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
            }
        }
        if (cmd.equalsIgnoreCase("npcskill")) {
            if (pc.hasSkillEffect(99889)) {
                final int time = pc.getSkillEffectTimeSec(99889);
                pc.sendPackets(new S_ServerMessage("\\fX下次占卜時間剩餘:" + time));
                return;
            }
            if (!pc.getInventory().checkItem(40308, 5000000)) {
                pc.sendPackets(new S_SystemMessage("金幣不足500萬無法占卜"));
                return;
            }
            pc.getInventory().consumeItem(40308, 5000000);
            pc.setSkillEffect(99889, 120 * 60 * 1000);
            switch (RandomArrayList.getInc(13, 1)) {
                case 1:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9964, 60 * 60 * 1000);
                    pc.addStr(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[力量+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 2:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9965, 60 * 60 * 1000);
                    pc.addDex(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[敏捷+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 3:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9966, 60 * 60 * 1000);
                    pc.addInt(6);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[智力+6]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 4:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9967, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[攻擊+15]"));
                    pc.addDmgup(15);
                    pc.addBowDmgup(12);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 5:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9968, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[命中+10]"));
                    pc.addHitup(10);
                    pc.addBowHitup(7);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 6:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                        pc.sendPackets(new S_SPMR(pc));
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9969, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[魔法攻擊+5]"));
                    pc.addSp(5);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    break;
                case 7:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得掉寶率+1%]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 8:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9971, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[3%發動爆擊]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 9:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9972, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[3%武器附加沖暈效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg1(true);
                    break;
                case 10:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9973, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[10%附加吸血效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg2(true);
                    break;
                case 11:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9974, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[10%附加吸魔效果]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setbbdmg3(true);
                    break;
                case 12:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9975, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得狩獵經驗提高]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
                case 13:
                    if (pc.hasSkillEffect(9964)) {
                        pc.killSkillEffectTimer(9964);
                        pc.addStr(-6);
                    }
                    if (pc.hasSkillEffect(9965)) {
                        pc.killSkillEffectTimer(9965);
                        pc.addDex(-6);
                    }
                    if (pc.hasSkillEffect(9966)) {
                        pc.killSkillEffectTimer(9966);
                        pc.addInt(-6);
                    }
                    if (pc.hasSkillEffect(9967)) {
                        pc.killSkillEffectTimer(9967);
                        pc.addDmgup(-15);
                        pc.addBowDmgup(-12);
                    }
                    if (pc.hasSkillEffect(9968)) {
                        pc.killSkillEffectTimer(9968);
                        pc.addHitup(-10);
                        pc.addBowHitup(-7);
                    }
                    if (pc.hasSkillEffect(9969)) {
                        pc.killSkillEffectTimer(9969);
                        pc.addSp(-5);
                    }
                    if (pc.hasSkillEffect(9971)) {
                        pc.killSkillEffectTimer(9971);
                    }
                    if (pc.hasSkillEffect(9972)) {
                        pc.killSkillEffectTimer(9972);
                    }
                    if (pc.hasSkillEffect(9973)) {
                        pc.killSkillEffectTimer(9973);
                    }
                    if (pc.hasSkillEffect(9974)) {
                        pc.killSkillEffectTimer(9974);
                    }
                    if (pc.hasSkillEffect(9975)) {
                        pc.killSkillEffectTimer(9975);
                    }
                    if (pc.hasSkillEffect(9976)) {
                        pc.killSkillEffectTimer(9976);
                    }
                    pc.setSkillEffect(9976, 60 * 60 * 1000);
                    pc.sendPackets(new S_ServerMessage("魔法占卜[獲得5%受到所有傷害/3]"));
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_SPMR(pc));
                    break;
            }
        }
    }
}