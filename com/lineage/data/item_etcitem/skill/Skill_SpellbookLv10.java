package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpellbookLv10 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv10();
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
            if (nameId.equalsIgnoreCase("魔法書 (究極光裂術(古代)")) { //冰雪暴>>究極光裂術(古代)
                skillid = 73;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("魔法書 (召喚守護者)")) { //(終極返生術)>>召喚守護者
                skillid = 74;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (神聖迅猛:古代)")) { //召喚術>>神聖迅猛:古代
                skillid = 75;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (魔法大師)")) { //沉睡之霧>>魔法大師
                skillid = 76;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (黑暗之星)")) { //黑暗之星
                skillid = 77;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (黑暗之盾)")) { //終極返生術>>黑暗之盾
                skillid = 78;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書 (集體聖結界)")) { //集體聖結界
                skillid = 79;
                attribute = 1;
            } else if (nameId.equalsIgnoreCase("魔法書(治愈逆行)")) { //治愈逆行
                skillid = 80;
                attribute = 1;
            }
			// 統一於條件分支後觸發學習流程
			if (skillid != 0) {
				Skill_Check.check(pc, item, skillid, 10, attribute);
			}
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv10 JD-Core Version:
 * 0.6.2
 */