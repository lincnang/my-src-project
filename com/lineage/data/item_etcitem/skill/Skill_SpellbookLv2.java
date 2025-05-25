package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Skill_SpellbookLv2 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv2();
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
        if (nameId.equalsIgnoreCase("$522")) {   //(解毒術)
            skillid = 9;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$1858")) { //(負重強化)
            skillid = 10;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$526")) { //(無所遁形術)
            skillid = 11;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1586")) { //(岩牢)
            skillid = 12;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$523")) {  //(寒冷戰慄)
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$524")) { // (毒咒)
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$525")) { // (擬似魔法武器)
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1583")) { //(地獄之牙)
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1584")) { //魔法書 (火箭)
            skillid = 0;
            attribute = 0;
        }
        Skill_Check.check(pc, item, skillid, 2, attribute);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv2 JD-Core Version: 0.6.2
 */