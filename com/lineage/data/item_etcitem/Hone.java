package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_PacketBoxDurability;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Hone extends ItemExecutor {
    public static ItemExecutor get() {
        return new Hone();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        if ((item1.getItem().getType2() != 0) && (item1.get_durability() > 0)) {
            if (item1.getItem().getType2() == 2 && item1.isEquipped()) {
                pc.addAc(-1);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.getInventory().recoveryDamage(item1);
            } else {
                pc.getInventory().recoveryDamage(item1);
            }
            String msg0 = item1.getLogName();
            if (item1.get_durability() == 0) {
                pc.sendPackets(new S_ServerMessage(464, msg0));
            } else {
                pc.sendPackets(new S_ServerMessage(463, msg0));
            }
            pc.sendPackets(new S_PacketBoxDurability(item1));
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Hone JD-Core Version: 0.6.2
 */