package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.Random;

public class Dice extends ItemExecutor {
    public static ItemExecutor get() {
        return new Dice();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        Random _random = new Random();
        int gfxid = 0;
        switch (itemId) {
            case 40325:
                if (pc.getInventory().checkItem(40318, 1L)) {
                    gfxid = 3237 + _random.nextInt(2);
                } else {
                    pc.sendPackets(new S_ServerMessage(299));
                }
                break;
            case 40326:
                if (pc.getInventory().checkItem(40318, 1L)) {
                    gfxid = 3229 + _random.nextInt(3);
                } else {
                    pc.sendPackets(new S_ServerMessage(299));
                }
                break;
            case 40327:
                if (pc.getInventory().checkItem(40318, 1L)) {
                    gfxid = 3241 + _random.nextInt(4);
                } else {
                    pc.sendPackets(new S_ServerMessage(299));
                }
                break;
            case 40328:
                if (pc.getInventory().checkItem(40318, 1L)) {
                    gfxid = 3204 + _random.nextInt(6);
                } else {
                    pc.sendPackets(new S_ServerMessage(299));
                }
                break;
        }
        if (gfxid != 0) {
            pc.getInventory().consumeItem(40318, 1L);
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Dice JD-Core Version: 0.6.2
 */