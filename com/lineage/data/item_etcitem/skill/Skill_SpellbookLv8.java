package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpellbookLv8 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv8();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.isWizard()) {
            S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int attribute = 0;
            if (nameId.equalsIgnoreCase("魔法書 (靈魂昇華)")) { //(靈魂昇華)
                skillid = 57;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("$555")) { // (隱身術)
                skillid = 58;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$545")) { //(魔法相消術)
                skillid = 59;
                attribute = 2;
            } else if (nameId.equalsIgnoreCase("$1590")) { //(火風暴)
                skillid = 60;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$560")) { //(聖結界)
                skillid = 61;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (黑暗之手)")) { //(火牢)改黑暗之手
                skillid = 62;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$552")) { //(全部治癒術)
                skillid = 63;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$559")) { //(變形術)
                skillid = 64;
                attribute = 0;
            }
            Skill_Check.check(pc, item, skillid, 8, attribute);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv8 JD-Core Version: 0.6.2
 */