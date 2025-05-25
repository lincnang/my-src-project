package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class TimeBox extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TimeBox.class);

    public static ItemExecutor get() {
        return new TimeBox();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            CreateNewItem.createNewItem(pc, 56214, 1L);
            CreateNewItem.createNewItem(pc, 56215, 1L);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            item.setLastUsed(ts);
            pc.getInventory().updateItem(item, 32);
            pc.getInventory().saveItem(item, 32);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.TimeBox JD-Core Version: 0.6.2
 */