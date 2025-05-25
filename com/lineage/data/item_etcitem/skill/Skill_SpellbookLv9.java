package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Skill_SpellbookLv9 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_SpellbookLv9();
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
            // 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
            // 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
            int attribute = 0;
            if (nameId.equalsIgnoreCase("$563")) { //流星雨
                skillid = 65;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$566")) { //(究極光裂術)
                skillid = 66;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$1872")) { // (絕對屏障)
                skillid = 67;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("魔法書 (神諭)")) { //藥水霜化術>>神諭
                skillid = 68;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("魔法書 (意志專注)")) { //集體傳送術>>意志專注
                skillid = 69;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("魔法書 (古代啟示)")) {//冰矛圍籬>>古代啟示
                skillid = 70;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("魔法書 (破壞之鏡)")) {//治癒能量風暴>>破壞之鏡
                skillid = 71;
                attribute = 0;
            } else if (nameId.equalsIgnoreCase("$1871")) { //(強力無所遁形術)
                skillid = 72;
                attribute = 0;
            }
            //			if (nameId.equalsIgnoreCase("$557")) { // (雷霆風暴)
            //				skillid = 65;
            //				attribute = 0;
            //			} else if (nameId.equalsIgnoreCase("$558")) { // (沉睡之霧)
            //				skillid = 66;
            //				attribute = 2;
            //			} else if (nameId.equalsIgnoreCase("$561")) { //集體傳送術>>意志專注
            //				skillid = 69;
            //				attribute = 0;
            //			} else if (nameId.equalsIgnoreCase("$1870")) { //藥水霜化術>>神諭
            //				skillid = 71;
            //				attribute = 0;
            Skill_Check.check(pc, item, skillid, 9, attribute);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.Skill_SpellbookLv9 JD-Core Version: 0.6.2
 */