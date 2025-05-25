package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;

/**
 * 更新物品使用狀態(背包)-可用次數
 *
 * @author DaiEn
 */
public class S_ItemAmount extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 更新物品使用狀態(背包)-可用次數
     *
     * @param item
     */
    public S_ItemAmount(final L1ItemInstance item, int test) {
        if (item == null) {
            return;
        }
        this.buildPacket(item);
    }

    private void buildPacket(final L1ItemInstance item) {
        this.writeC(S_HYPERTEXT_INPUT);
        this.writeD(item.getId());
        this.writeS(item.getViewName());
        // 定義數量顯示
        /*
         * int count = 0;
         *
         * if (item.getItem().getMaxChargeCount() > 0) { count =
         * item.getChargeCount();
         *
         * } else { count = (int) Math.min(item.getCount(), 2000000000); }
         */
        // 定義數量顯示
        int count = (int) Math.min(item.getCount(), 2000000000);
        // 數量
        this.writeD(count);
        // 可用數量
        // this.writeD(Math.min(item.getChargeCount(), 2000000000));
        // this.writeC(0x00);
        if (!item.isIdentified()) {
            // 未鑒定 不發送詳細資料
            this.writeC(0x00);
        } else {
            L1ItemStatus itemInfo = new L1ItemStatus(item);
            byte[] status = itemInfo.getStatusBytes(false).getBytes();
            this.writeC(status.length);
            for (final byte b : status) {
                this.writeC(b);
            }
        }
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
