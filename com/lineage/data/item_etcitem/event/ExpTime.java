package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;

public class ExpTime extends ItemExecutor {
    public static ItemExecutor get() {
        return new ExpTime();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (L1BuffUtil.cancelExpSkill(pc)) {
            pc.sendPackets(new S_ServerMessage("\\fX目前沒有一段經驗加倍藥水效果。"));
        }
        if (L1BuffUtil.cancelExpSkill_2(pc)) {
            pc.sendPackets(new S_ServerMessage("\\fY目前沒有二段經驗加倍藥水效果。"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.event.Exp65 JD-Core Version: 0.6.2
 */