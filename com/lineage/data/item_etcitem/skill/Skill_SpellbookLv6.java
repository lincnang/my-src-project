package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Skill_SpellbookLv6 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv6();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        String nameId = item.getItem().getNameId();
        int skillid = 0;
        int attribute = 0;
        if (nameId.equalsIgnoreCase("$544")) { //(加速術)
            skillid = 41;
            attribute = 2;
        } else if (nameId.equalsIgnoreCase("$538")) { // (極道落雷)
            skillid = 42;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1588")) { //(烈炎術)
            skillid = 43;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$543")) {  //體質強化
            skillid = 44;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$546")) { //(地裂術)
            skillid = 45;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 46;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 47;
            attribute = 2;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 48;
            attribute = 0;
        }
        Skill_Check.check(pc, item, skillid, 6, attribute);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv6 JD-Core Version: 0.6.2
 */