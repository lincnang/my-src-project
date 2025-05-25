package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;

public class ImmuntToHarm extends ItemExecutor {
    public static ItemExecutor get() {
        return new ImmuntToHarm();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        if (pc.getInventory().removeItem(item, 1L) >= 1L) {
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, 68, pc.getId(), 0, 0, 0, 2);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.magicreel.ImmuntToHarm JD-Core Version: 0.6.2
 */