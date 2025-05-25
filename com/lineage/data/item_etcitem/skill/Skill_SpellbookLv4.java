package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Skill_SpellbookLv4 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv4();
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
        if (nameId.equalsIgnoreCase("$532")) {  //(燃燒的火球)
            skillid = 25;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("$1862")) { //(魔力奪取)
            skillid = 26;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("$535")) { //(吸血鬼之吻)
            skillid = 27;
            attribute = 2;
        } else if (nameId.equalsIgnoreCase("$1860")) { //(魔法屏障)
            skillid = 28;
            attribute = 2;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 29;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 30;
            attribute = 0;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 31;
            attribute = 1;
        } else if (nameId.equalsIgnoreCase("-")) {
            skillid = 32;
            attribute = 0;
        }
        Skill_Check.check(pc, item, skillid, 4, attribute);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv4 JD-Core Version: 0.6.2
 */