package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.List;

/**
 * 物品名單(背包)
 *
 * @author dexc
 */
public class S_InvList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 物品名單(背包)
     */
    public S_InvList(final List<L1ItemInstance> items) {
        writeC(S_ADD_INVENTORY_BATCH);
        writeC(items.size()); // 道具數量
        for (final L1ItemInstance item : items) {
            writeD(item.getId());
            writeH(item.getItem().getItemDescId());
            if (item.getItemId() == 642226 || item.getItemId() == 642227) { // 成長果實系統(Tam幣)
                writeH(0x0044);
            } else {
                int type = item.getItem().getUseType();
                if (type < 0) {
                    type = 0;
                }
                if ((type == 96) || (type >= 98)) {
                    writeC(26);
                } else if (type == 97) {
                    writeC(27);
                } else {
                    writeC(type);// 使用類型
                }
                if (item.getChargeCount() <= 0) {
                    writeC(0x00);// 可用次數
                } else {
                    writeC(item.getChargeCount());// 可用次數
                }
            }
            writeH(item.get_gfxid());// 圖示
            writeC(item.getBless());// 祝福狀態
            writeExp(item.getCount());// 數量
            int statusX = 0;
            if (item.isIdentified()) {
                statusX |= 1;
            }
            if (!item.getItem().isTradable()) {
                statusX |= 2;
            }
            if (item.getItem().isCantDelete()) {
                statusX |= 4;
            }
            if ((item.getItem().get_safeenchant() < 0) || (item.getItem().getUseType() == -3) || (item.getItem().getUseType() == -2)) {
                statusX |= 8;
            }
            // 設定可存倉標誌，讓物品顯示在列表中
            statusX |= 16;
            if (item.getBless() >= 128) {
                statusX = 32;
                // 設定可存倉標誌，讓物品顯示在列表中
                statusX |= 16;
                if (item.isIdentified()) {
                    statusX |= 1;
                    statusX |= 2;
                    statusX |= 4;
                    statusX |= 8;
                } else {
                    statusX |= 2;
                    statusX |= 4;
                    statusX |= 8;
                }
            }
            writeC(statusX);
            writeS(item.getViewName());// 名稱
            if (!item.isIdentified()) {
                // 未見定狀態 不需發送詳細資料
                writeC(0x00);
            } else {
                final byte[] status = item.getStatusBytes();
                writeC(status.length);
                for (final byte b : status) {
                    writeC(b);
                }
            }
            writeC(0x18); // 指後面(以下)敘述的長度
            writeC(0);
            writeH(0);
            writeH(0);
            if (item.getItem().getType() == 10) { // 如果是法書，傳出法術編號
                writeC(0);
            } else {
                writeC(item.getEnchantLevel()); // 物品武卷等級
            }
            writeD(item.getId()); // 3.80 物品世界流水編號
            writeD(8);// 0:一般 2:? 8:釣魚-獲得道具
            int warehouseBit = 0;
            // 設定標誌讓物品顯示在列表中，實際限制由伺服器控制
            if (item.getItem().isWarehouseable()) {
                warehouseBit |= 1;
            } else {
                // 即使不可存倉也設定標誌，讓物品顯示在列表中
                warehouseBit |= 1;
            }
            if (item.getItem().isClanWarehouseable()) {
                warehouseBit |= 4;
            } else {
                // 即使不可存盟倉也設定標誌，讓物品顯示在列表中
                warehouseBit |= 4;
            }
            writeD(warehouseBit);
            // 7:可刪除,2:不可刪除,3:封印狀態
            // 強制設定為可刪除，讓物品能顯示在倉庫列表中
            writeC(item.getBless() >= 128 ? 3 : 7);
            int type = item.getItem().getType();
            int type2 = item.getItem().getType2();
            if (type == 15 && type2 == 2) {
                writeD(7738);// img 1;
            } else if (type == 18 && type2 == 2) {
                writeD(7739);// img 2;
            } else if (type == 17 && type2 == 2) {
                writeD(7740);// img 3;
            } else if (item.getEnchantLevel() > 0) {
                final int img = item.getEnchantLevel() + 8042;
                writeD(Math.max(8042, Math.min(img, 8056)));// img
            } else {
                writeD(0);// img
            }
            writeC(0);
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
