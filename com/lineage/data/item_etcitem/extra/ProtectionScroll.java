package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class ProtectionScroll extends ItemExecutor {
    public static ItemExecutor get() {
        return new ProtectionScroll();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem != null) {
            if (tgItem.getItem().get_safeenchant() <= -1) {// 無法使用防武卷強化的道具
                pc.sendPackets(new S_ServerMessage(1309));
                return;
            }
            if (tgItem.getproctect() == true) {// 正在保護中
                pc.sendPackets(new S_ServerMessage(1300));
                return;
            }
            if (tgItem.getItem().getType2() == 0) {// 道具類
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            final int use_type = tgItem.getItem().getUseType();
            if ((use_type == 23) || (use_type == 24) || (use_type == 37) || (use_type == 40)) {// 飾品類
                pc.sendPackets(new S_ServerMessage(1309));
                return;
            }
            tgItem.setproctect(true);
            pc.sendPackets(new S_ServerMessage(1308, tgItem.getLogName()));
            pc.getInventory().removeItem(item, 1);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.extra.ProtectionScrollElyos JD-Core Version:
 * 0.6.2
 */