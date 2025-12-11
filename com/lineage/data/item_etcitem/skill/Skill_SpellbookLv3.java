package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Skill_SpellbookLv3 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv3();
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
        if (nameId.equalsIgnoreCase("$531")) {  //(鎧甲護持)
            skillid = 17;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1585")) { //起死回生術
            skillid = 18;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$1861")) { //(冥想術)
            skillid = 19;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$528")) {//(寒冰氣息)
            skillid = 20;
            attribute = 2;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 80;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 22;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 23;
            attribute = 0;
        }
        Skill_Check.check(pc, item, skillid, 3, attribute);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv3 JD-Core Version: 0.6.2
 */