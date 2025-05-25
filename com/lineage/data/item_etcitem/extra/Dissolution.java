package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ResolventEXTable;
import com.lineage.server.datatables.ResolventTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Dissolution extends ItemExecutor {
    public static ItemExecutor get() {
        return new Dissolution();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        useResolvent(pc, item1, item);
    }

    private void useResolvent(L1PcInstance pc, L1ItemInstance item, L1ItemInstance resolvent) {
        if ((item == null) || (resolvent == null)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
            if (item.getEnchantLevel() != 0) {
                pc.sendPackets(new S_ServerMessage(1161));
                return;
            }
            if (item.isEquipped()) {
                pc.sendPackets(new S_ServerMessage(1161));
                return;
            }
        }
        int tgitemId = item.getItemId();
        long crystalCount = ResolventTable.get().getCrystalCount(tgitemId);
        boolean check = false;
        check = ResolventEXTable.get().getCrystalCount(pc, tgitemId);
        if ((crystalCount == 0L) && (!check)) {
            pc.sendPackets(new S_ServerMessage(1161));
            return;
        }
        pc.getInventory().removeItem(item, 1L);
    }
}
