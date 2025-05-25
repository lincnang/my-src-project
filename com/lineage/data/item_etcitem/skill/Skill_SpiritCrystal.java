package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.SOUL_BARRIER;

public class Skill_SpiritCrystal extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpiritCrystal();
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
        } else {
            String nameId = item.getItem().getNameId();
            int skillid = 0;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("$1829")) {
                skillid = 129;
                magicLv = 11;
            } else if (nameId.equalsIgnoreCase("$1830")) {
                skillid = 130;
                magicLv = 11;
            } else if (nameId.equalsIgnoreCase("$1831")) {
                skillid = 131;
                magicLv = 11;
            } else if (nameId.equalsIgnoreCase("$1832")) {
                skillid = 137;
                magicLv = 12;
            } else if (nameId.equalsIgnoreCase("$1833")) {
                skillid = 138;
                magicLv = 12;
            } else if (nameId.equalsIgnoreCase("$3261")) {
                skillid = 132;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$23459")) { // 魔力護盾
                skillid = SOUL_BARRIER;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1834")) {
                skillid = 145;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1835")) {
                skillid = 146;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$1836")) {
                skillid = 147;
                magicLv = 13;
            } else if (nameId.equalsIgnoreCase("$3262")) {
                skillid = 133;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$1842")) {
                skillid = 153;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$1843")) {
                skillid = 154;
                magicLv = 14;
            } else if (nameId.equalsIgnoreCase("$3263")) {
                skillid = 134;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$1849")) {
                skillid = 161;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("$1850")) {
                skillid = 162;
                magicLv = 15;
            } else if (nameId.equalsIgnoreCase("精靈水晶(重擊之矢)")) {
                skillid = 136;
                magicLv = 15;

            }

            Skill_Check.check(pc, item, skillid, magicLv, 3);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpiritCrystal JD-Core Version:
 * 0.6.2
 */