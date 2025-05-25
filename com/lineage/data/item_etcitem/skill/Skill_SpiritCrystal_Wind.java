package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpiritCrystal_Wind extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpiritCrystal_Wind();
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
        } else if (pc.getElfAttr() != 8) {
            S_ServerMessage msg = new S_ServerMessage(684);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$1838")) {
                skillid = 149;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1839")) {
                skillid = 150;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1845")) {
                skillid = 156;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$1854")) {
                skillid = 166;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$1855")) {
                skillid = 167;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$4718")) {
                skillid = 174;
                magicLv = 15;
            }
            Skill_Check.check(pc, item, skillid, magicLv, 3);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpiritCrystal_Wind JD-Core Version:
 * 0.6.2
 */