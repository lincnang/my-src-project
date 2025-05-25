package com.lineage.server.templates;

import com.lineage.server.datatables.ItemTable;

/**
 * 特殊商店<BR>
 * <p>
 * 購買自動喝水補魔道具
 */
public class L1ShopAutoHp {
    private int _id; // id
    private int _itemId; // item id
    private L1Item _item; // item

    public L1ShopAutoHp(final int _id, final int itemId) {
        this._id = _id;
        this._itemId = itemId;
        this._item = ItemTable.get().getTemplate(itemId);
    }

    public L1ShopAutoHp(final int itemId) {
        this._id = -1;
        this._itemId = itemId;
        this._item = ItemTable.get().getTemplate(itemId);
    }

    public int get_id() {
        return this._id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_itemId() {
        return this._itemId;
    }

    public void set_itemId(int _itemId) {
        this._itemId = _itemId;
    }

    public L1Item get_item() {
        return this._item;
    }

    public void set_item(L1Item _item) {
        this._item = _item;
    }
}
