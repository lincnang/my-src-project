package com.lineage.server.datatables.storage;

import com.lineage.server.templates.CharItemSublimation;

public abstract interface CharItemSublimationStorage {

    public abstract void load(boolean paramBoolean);

    public abstract void storeItem(CharItemSublimation paramCharItemSublimation) throws Exception;

    public abstract void updateItem(CharItemSublimation paramCharItemSublimation);

    public abstract void deleteItem(int paramInt);
    // ✅ 新增：支援讀取單一昇華物品
    CharItemSublimation loadItem(int item_obj_id);
}
