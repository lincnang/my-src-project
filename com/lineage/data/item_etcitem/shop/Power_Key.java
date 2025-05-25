package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Power_Key extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Power_Key.class);

    public static ItemExecutor get() {
        return new Power_Key();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            int itemobj = data[0];
            L1ItemInstance tgitem = pc.getInventory().getItem(itemobj);
            if (tgitem == null) {
                return;
            }
            int itemid = item.getItemId();
            if (tgitem.getItem().getType() == 16) {
                if (ItemBoxTable.get().is_key(tgitem.getItemId(), itemid)) {
                    if (pc.getInventory().removeItem(item, 1L) != 1L) {
                        return;
                    }
                    ItemBoxTable.get().get_key(pc, tgitem, itemid);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.Power_Key JD-Core Version: 0.6.2
 */