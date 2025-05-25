package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Valuable_Box_Fragment extends ItemExecutor {
    public static ItemExecutor get() {
        return new Valuable_Box_Fragment();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case 49093:
                if (pc.getInventory().checkItem(49094, 1L)) {
                    pc.getInventory().consumeItem(49093, 1L);
                    pc.getInventory().consumeItem(49094, 1L);
                    CreateNewItem.createNewItem(pc, 49095, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49094:
                if (pc.getInventory().checkItem(49093, 1L)) {
                    pc.getInventory().consumeItem(49093, 1L);
                    pc.getInventory().consumeItem(49094, 1L);
                    CreateNewItem.createNewItem(pc, 49095, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49097:
                if (pc.getInventory().checkItem(49098, 1L)) {
                    pc.getInventory().consumeItem(49097, 1L);
                    pc.getInventory().consumeItem(49098, 1L);
                    CreateNewItem.createNewItem(pc, 49099, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49098:
                if (pc.getInventory().checkItem(49097, 1L)) {
                    pc.getInventory().consumeItem(49097, 1L);
                    pc.getInventory().consumeItem(49098, 1L);
                    CreateNewItem.createNewItem(pc, 49099, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49269:
                if (pc.getInventory().checkItem(49270, 1L)) {
                    pc.getInventory().consumeItem(49270, 1L);
                    pc.getInventory().consumeItem(49269, 1L);
                    CreateNewItem.createNewItem(pc, 49274, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49270:
                if (pc.getInventory().checkItem(49269, 1L)) {
                    pc.getInventory().consumeItem(49269, 1L);
                    pc.getInventory().consumeItem(49270, 1L);
                    CreateNewItem.createNewItem(pc, 49274, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49271:
                if (pc.getInventory().checkItem(49272, 1L)) {
                    pc.getInventory().consumeItem(49272, 1L);
                    pc.getInventory().consumeItem(49271, 1L);
                    CreateNewItem.createNewItem(pc, 49275, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            case 49272:
                if (pc.getInventory().checkItem(49271, 1L)) {
                    pc.getInventory().consumeItem(49271, 1L);
                    pc.getInventory().consumeItem(49272, 1L);
                    CreateNewItem.createNewItem(pc, 49275, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Valuable_Box_Fragment JD-Core Version: 0.6.2
 */