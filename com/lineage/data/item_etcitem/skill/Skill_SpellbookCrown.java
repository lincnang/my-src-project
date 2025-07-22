package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.*;

public class Skill_SpellbookCrown extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookCrown();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.isCrown()) {
            S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("魔法書 (精準目標)")) { //魔法書 (精準目標)
                skillid = 113;
                magicLv = 21;
            } else if (nameId.equalsIgnoreCase("魔法書 (勇猛武器)")) { //魔法書 (勇猛武器)
                skillid = 116;
                magicLv = 22;
            } else if (nameId.equalsIgnoreCase("魔法書 (灼熱靈氣：1階段)")) {
                skillid = 114;
                magicLv = 23;
            } else if (nameId.equalsIgnoreCase("魔法書 (勇猛盔甲)")) { //魔法書 (勇猛盔甲)
                skillid = 118;
                magicLv = 24;
            } else if (nameId.equalsIgnoreCase("魔法書 (勇猛意志)")) {
                skillid = 117;
                magicLv = 25;
            } else if (nameId.equalsIgnoreCase("魔法書 (灼熱靈氣：2階段)")) {
                skillid = 115;
                magicLv = 26;
            } else if (nameId.equalsIgnoreCase("魔法書(王者加護)")) { // 王者加護
                skillid = BRAVE_AVATAR;
                magicLv = 27;
            } else if (nameId.equalsIgnoreCase("魔法書(灼熱靈氣：3階段)")) { // 王者加護
                skillid = GRACE_AVATAR;
                magicLv = 26;
            } else if (nameId.equalsIgnoreCase("魔法書(王者之劍)")) { // 王者之劍
                skillid = KINGDOM_STUN;
                magicLv = 26;
            }
            Skill_Check.check(pc, item, skillid, magicLv, 4);
        }
    }
}
