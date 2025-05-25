package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopAutoHpTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopAutoHp;

import java.util.ArrayList;

/**
 * 特殊商店<BR>
 * <p>
 * 購買自動喝水補魔道具
 */
public class S_AutoHpShopSellList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 購買自動喝水補魔道具
     *
     * @param type 第幾組
     */
    public S_AutoHpShopSellList(final L1PcInstance pc, final int type) {
        this.buildPacket(pc, type);
    }

    private void buildPacket(final L1PcInstance pc, final int type) {
        writeC(S_BUY_LIST);
        final int objId = pc.getId();
        writeD(objId);
        final ArrayList<L1ShopAutoHp> shopItems = ShopAutoHpTable.get().get(type);
        writeH(shopItems.size());
        int i = 0;
        for (final L1ShopAutoHp shopItem : shopItems) {
            i++;
            pc.get_otherList().add_autoHpList(shopItem, i);
            final L1Item item = shopItem.get_item();
            writeD(i);// 排序
            writeH(item.getGfxId());// 圖形
            writeD(1);// 售價
            writeS(item.getName());
            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            writeD(template.getUseType());// XXX 7.6新增商品分類
            // 降低封包量 不傳送詳細資訊
            writeC(0x00);
        }
        writeH(15636);
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
