package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.Random;

public class AttrWeaponDraw extends ItemExecutor {
    private static final Random _random = new Random();
    private int _chance;
    private int _stage;

    public static ItemExecutor get() {
        return new AttrWeaponDraw();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器提取封頂的屬性卷軸。"));
            return;
        }
        if (tgItem.getBless() >= 128) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        int attrEnchantKind = tgItem.getAttrEnchantKind();
        int attrEnchantLevel = tgItem.getAttrEnchantLevel();
        if (attrEnchantKind == 0) {
            pc.sendPackets(new S_ServerMessage("您的武器沒有強化屬性。"));
            return;
        }
        if (attrEnchantLevel != _stage) {
            pc.sendPackets(new S_ServerMessage("只能提取最高封頂的屬性卷軸。"));
            return;
        }
        int giveItem = 0;
        if (attrEnchantKind == 1) {
            giveItem = 641309;
        } else if (attrEnchantKind == 2) {
            giveItem = 641310;
        } else if (attrEnchantKind == 4) {
            giveItem = 641311;
        } else if (attrEnchantKind == 3) {
            giveItem = 641312;
        } else if (attrEnchantKind == 5) {
            giveItem = 641313;
        } else if (attrEnchantKind == 6) {
            giveItem = 641314;
        } else if (attrEnchantKind == 7) {
            giveItem = 641315;
        } else if (attrEnchantKind == 8) {
            giveItem = 641316;
        } else if (attrEnchantKind == 9) {
            giveItem = 641317;
        } else if (attrEnchantKind == 10) {
            giveItem = 641318;
        } else if (attrEnchantKind == 11) {
            giveItem = 641319;
        }
        if (this._chance > _random.nextInt(100) + 1) {
            if (giveItem != 0) {
                pc.getInventory().removeItem(item, 1L);
                pc.getInventory().storeItem(giveItem, 1L);
                pc.sendPackets(new S_ServerMessage("封頂的屬性強化卷軸提取成功。"));
                tgItem.setAttrEnchantKind(0);
                tgItem.setAttrEnchantLevel(0);
                pc.getInventory().updateItem(tgItem, 3072);
                pc.getInventory().saveItem(tgItem, 3072);
            }
        } else {
            pc.getInventory().removeItem(item, 1L);
            pc.sendPackets(new S_ServerMessage("封頂的屬性強化卷軸提取失敗。"));
        }
        try {
            pc.save();
        } catch (Exception localException) {
        }
    }

    public void set_set(String[] set) {
        try {
            _chance = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _stage = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
    }
}
