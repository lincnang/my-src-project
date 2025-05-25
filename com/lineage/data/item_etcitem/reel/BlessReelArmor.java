package com.lineage.data.item_etcitem.reel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.Zhufu;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Zhufu;

import java.util.Random;

public class BlessReelArmor extends ItemExecutor {
    private int _random = 0;

    private BlessReelArmor() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new BlessReelArmor();
    }

    @Override
    public void set_set(String[] set) {
        if (set.length > 1) {
            try {
                _random = Integer.parseInt(set[1]);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        final int objectId = data[0];
        final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
        if (targetItem == null || targetItem.getItem().getType2() != 2) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (targetItem.getBless() == 2) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        //1:helm, 2:armor, 3:T, 4:cloak, 5:glove, 6:boots, 7:shield, 8:amulet, 9:ring, 10:belt, 11:ring2, 12:earring
        if (targetItem.getItem().getType() < 1 || targetItem.getItem().getType() > 7) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (targetItem.getBless() == 0) {
            pc.sendPackets(new S_SystemMessage("你的防具已經祝福過了."));
            return;
        }
        int itemid = targetItem.getItem().getItemId();
        L1Zhufu zhufu = Zhufu.getInstance().getTemplate(itemid, 2);
        if (zhufu == null) {
            zhufu = Zhufu.getInstance().getTemplateByType(targetItem.getItem().getType(), 2);
            if (zhufu == null) {
                pc.sendPackets(new S_SystemMessage("此道具無法祝福."));
                return;
            }
        }
        if (!zhufu.getzhufu2()) {
            pc.sendPackets(new S_SystemMessage("此道具無法祝福."));
            return;
        }
        final Random rnd = new Random();
        pc.getInventory().removeItem(item, 1);
        if (rnd.nextInt(100) < _random) {
            targetItem.setBless(0);
            pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
            pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
            pc.sendPackets(new S_SystemMessage(new StringBuilder(targetItem.getLogName()).append("祝福成功.").toString()));
        } else {
            if (zhufu.getzhufu()) {
                pc.getInventory().deleteItem(targetItem);
                pc.sendPackets(new S_SystemMessage(new StringBuilder(targetItem.getLogName()).append("祝福失敗,然後發出藍色的光芒 消失了.").toString()));
            } else {
                pc.sendPackets(new S_SystemMessage(new StringBuilder(targetItem.getLogName()).append("祝福失敗.").toString()));
            }
        }
    }
}
