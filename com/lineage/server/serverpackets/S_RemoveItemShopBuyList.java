package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

/**
 * 特殊商店<BR>
 * <p>
 * 取消刪除物品
 */
public class S_RemoveItemShopBuyList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 取消刪除物品
     *
     */
    public S_RemoveItemShopBuyList(final L1PcInstance pc, final int objid) {
        writeC(S_BUY_LIST);
        writeD(objid);
        // 取回PC刪物名單
        final int size = pc.getRemoveItemInventory().getSize();
        if (size > 0) {
            writeH(size);
        } else {
            writeH(0x0000);
            return;
        }
        int i = 0;
        for (final Object itemObject : pc.getRemoveItemInventory().getItems()) {
            i++;
            final L1ItemInstance item = (L1ItemInstance) itemObject;
            pc.get_otherList().add_removeItemBuyList(item, i);
            writeD(i);
            writeH(item.get_gfxid());// 圖形
            writeD(1);// 售價
            writeS(item.getName());
            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            writeD(template.getUseType());// XXX 7.6新增商品分類
            writeC(0x00);// 降低封包量 不傳送詳細資訊
        }
        writeH(15458);
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
