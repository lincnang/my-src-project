package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.*;

public class Skill_DarkSpiritCrystal extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_DarkSpiritCrystal();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.isDarkelf()) {
            S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int attribute = 6;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$2518")) {
                skillid = 97;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("$2519")) {
                skillid = 98;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("$2520")) {
                skillid = 99;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("英雄技能卡(暗影衝刺)")) {
                skillid = Shadow_Dash;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("$3172")) {
                skillid = 109;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("$2522")) {
                skillid = 101;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("$2523")) {
                skillid = 102;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("$2524")) {
                skillid = 103;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("英雄技能卡(暗影加速)")) {
                skillid = 104;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("傳說技能卡(暗影恢復)")) {
                skillid = 110;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("$2526")) {
                skillid = 105;
                magicLv = 43;
            } else if (nameId.equalsIgnoreCase("$2527")) {
                skillid = 106;
                magicLv = 43;
            } else if (nameId.equalsIgnoreCase("$2528")) {
                skillid = 107;
                magicLv = 43;
            } else if (nameId.equalsIgnoreCase("$2529")) {
                skillid = 108;
                magicLv = 43;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(神秘提升)")) {
                skillid = 111;
                magicLv = 43;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(破壞盔甲)")) {
                skillid = 112;
                magicLv = 44;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(刺客)")) {
                skillid = M_ASSASSIN;
                magicLv = 41;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(狂暴)")) {
                skillid = DARKELF_BERSERK;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(路西法)")) {
                skillid = PASSIVE_LUCIFER;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗影衝擊)")) {
                skillid = PASSIVE_SHADOW_IMPACT;
                magicLv = 42;
            } else if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗影暈眩)")) {
                skillid = Shadow_Daze;
                magicLv = 42;
            }
            Skill_Check.check(pc, item, skillid, magicLv, attribute);
        }
    }
}
