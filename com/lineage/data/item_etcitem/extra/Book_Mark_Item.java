package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1BookMark;

/**
 * 日版記憶座標
 */
public class Book_Mark_Item extends ItemExecutor {
    /**
     *
     */
    private Book_Mark_Item() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Book_Mark_Item();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        L1BookMark.Bookmarkitem(pc, item, item.getItem().getItemId(), false);
    }
}
