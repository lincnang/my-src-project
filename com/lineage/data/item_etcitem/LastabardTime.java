package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LastabardTime extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(LastabardTime.class);

    public static ItemExecutor get() {
        return new LastabardTime();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getLastabardTime() > 0) {
            pc.setLastabardTime(0);
            pc.sendPackets(new S_ServerMessage("拉斯塔巴德地監已重置"));
            pc.getInventory().removeItem(item, 1L);
        } else {
            pc.sendPackets(new S_ServerMessage("該副本尚未需要重置"));
        }
        try {
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}