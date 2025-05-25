package com.lineage.data.item_etcitem.doll;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DollRecover extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(DollRecover.class);

    public static ItemExecutor get() {
        return new DollRecover();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            int targObjId = data[0];
            L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            if (tgItem.get_time() != null) {
                pc.sendPackets(new S_ServerMessage(210, tgItem.getItem().getNameId()));
                return;
            }
            L1Doll doll = DollPowerTable.get().get_type(tgItem.getItemId());
            if (doll == null) {
                pc.sendPackets(new S_ServerMessage(2477));
                return;
            }
            if (pc.getDoll(tgItem.getId()) != null) {
                pc.sendPackets(new S_ServerMessage(1181));
                return;
            }
            pc.getInventory().removeItem(item, 1L);
            pc.getInventory().removeItem(tgItem, 1L);
            L1ItemInstance tginfo_item = ItemTable.get().createItem(83101);
            if (tginfo_item != null) {
                tginfo_item.setIdentified(true);
                tginfo_item.setCount(1L);
                pc.getInventory().storeItem(tginfo_item);
                pc.sendPackets(new S_ServerMessage("\\fT獲得" + tginfo_item.getName()));
                return;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.doll.DollSolution JD-Core Version: 0.6.2
 */