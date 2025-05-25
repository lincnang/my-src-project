package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpiritCrystal_Earth extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpiritCrystal_Earth();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.isElf()) {
            S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else if (pc.getElfAttr() != 1) {
            S_ServerMessage msg = new S_ServerMessage(684);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$1840")) {
                skillid = 151;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1841")) {
                skillid = 152;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1846")) {
                skillid = 157;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("精靈水晶(大地的護衛)")) {
                skillid = 159;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$1856")) {
                skillid = 168;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$3265")) {
                skillid = 169;
                magicLv = 15;
            }
            Skill_Check.check(pc, item, skillid, magicLv, 3);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpiritCrystal_Earth JD-Core
 * Version: 0.6.2
 */