package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Skill_SpellbookLv1 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv1();
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
        if (nameId.equalsIgnoreCase("$520")) {  //魔法書 (光箭)
            skillid = 1;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$518")) { //魔法書 (日光術)
            skillid = 2;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$519")) { //魔法書 (保護罩)
            skillid = 3;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$520")) { //魔法書 (光箭)
            skillid = 1;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$521")) { //魔法書 (指定傳送)
            skillid = 4;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1581")) {
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1582")) {
            skillid = 0;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1857")) {
            skillid = 0;
            attribute = 0;
        }
        Skill_Check.check(pc, item, skillid, 1, attribute);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv1 JD-Core Version: 0.6.2
 */