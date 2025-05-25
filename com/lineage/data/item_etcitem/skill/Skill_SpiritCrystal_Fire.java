package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpiritCrystal_Fire extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpiritCrystal_Fire();
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
        } else if (pc.getElfAttr() != 2) {
            S_ServerMessage msg = new S_ServerMessage(684);
            pc.sendPackets(msg);
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$1837")) {
                skillid = 148;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("精靈水晶(舞躍之火)")) {
                skillid = 155;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$1851")) {
                skillid = 163;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$3267")) {
                skillid = 171;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$4714")) {
                skillid = 175;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$4715")) {
                skillid = 176;
                magicLv = 15;
            }
            Skill_Check.check(pc, item, skillid, magicLv, 3);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpiritCrystal_Fire JD-Core Version:
 * 0.6.2
 */