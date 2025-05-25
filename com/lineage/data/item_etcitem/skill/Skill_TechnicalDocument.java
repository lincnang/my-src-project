package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BLADE;

/**
 * <font color=#00800>技術書(騎士技能)</font><BR>
 * Technical Document
 *
 * @author dexc
 */
public class Skill_TechnicalDocument extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_TechnicalDocument();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.isKnight()) {
            S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int attribute = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$3259")) {
                skillid = 87;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("$4007")) {
                skillid = 88;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("$4008")) {
                skillid = 89;
                attribute = 5;
                magicLv = 32;
            } else if (nameId.equalsIgnoreCase("$4712")) {
                skillid = 90;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(單手劍)")) {
                skillid = 5;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(雙手劍)")) {
                skillid = 6;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("$4713")) { //反擊屏障
                skillid = 91;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(幻影之刃)")) {
                skillid = 93;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(反制攻擊)")) {
                skillid = 94;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(還擊)")) {
                skillid = 95;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("$23458")) { // 絕禦之刃
                skillid = ABSOLUTE_BLADE;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(騎士榮耀)")) {// 技術書(騎士榮耀)60
                skillid = 96;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(反擊屏障:專家)")) {// 技術書(反擊屏障:專家)86
                skillid = 86;
                attribute = 5;
                magicLv = 31;
            } else if (nameId.equalsIgnoreCase("技術書(暈眩之劍)")) {// 技術書(暈眩之劍)
                skillid = 85;
                attribute = 5;
                magicLv = 31;
            }
            // 檢查學習該法術是否成立
            Skill_Check.check(pc, item, skillid, magicLv, attribute);
        }
    }
}
