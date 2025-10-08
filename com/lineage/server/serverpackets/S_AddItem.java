package com.lineage.server.serverpackets;

import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品增加
 *
 * @author dexc
 */
public class S_AddItem extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 物品增加
     */
    public S_AddItem(final L1ItemInstance item) {
        final byte[] status = item.getStatusBytes();
        // XXX 7.6以上 物品的敘述在additem封包上無法超過 127個byte
        if (status.length > 127) {
            item.setIdentified(false); // 設置未鑒定
            WriteLogTxt.Recording("屬性顯示過多道具紀錄", "屬性顯示過多道具編號(" + item.getItemId() + ")，名稱(" + item.getItem().getName() + ").");
            System.out.println("道具編號(" + item.getItemId() + ")，名稱(" + item.getItem().getName() + ")屬性顯示過多");
        }
        writeC(S_ADD_INVENTORY);
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
            if (item.getChargeCount() > 0) {
                writeC(item.getChargeCount());// 可用次數
            } else {
                writeC(0x00);// 可用次數
            }
        }
        writeH(item.get_gfxid());
        writeC(item.getBless());
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
        if (item.getBless() >= 128) {
            statusX = 32;
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
        writeS(item.getViewName());
        //		if (!item.isIdentified()) {
        //			// 未鑒定 不發送詳細資訊
        //			writeC(0x00);
        //
        //		} else {
        //			// final byte[] status = item.getStatusBytes();
        //			writeC(status.length);
        //			for (final byte b : status) {
        //				writeC(b);
        //			}
        //		}
        if (!item.isIdentified()) {
            item.setIdentified(true);
        }
        // final byte[] status = item.getStatusBytes();
        writeC(status.length);
        for (final byte b : status) {
            writeC(b);
        }
        writeC(0x18);
        writeH(0);
        writeC(0);
        writeH(0);
        if (item.getItem().getType() == 10) { // 如果是法書，傳出法術編號
            writeC(0);
        } else {
            writeC(item.getEnchantLevel()); // 物品武卷等級
        }
        writeD(item.getId()); // 3.80 物品世界流水編號
        writeD(8); // 0:一般 2:? 8:釣魚-獲得道具
        writeD(0); // 0x01:可存一般 妖森 倉庫 0x02:可存角色專屬倉庫 0x04:可存血盟倉庫
        writeC(item.getBless() >= 128 ? 3 : item.getItem().isTradable() ? 7 : 2); //   // 7:可刪除,2:不可刪除,3:封印狀態
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
