package com.lineage.data.item_etcitem.extra;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

/**
 * 49312 時空之甕
 */
public class TimeBox extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TimeBox.class);
    private int _isItem;
    private int _isQuantity;

    /**
     *
     */
    private TimeBox() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new TimeBox();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    // @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 取得道具(時空之玉 * 2)
            CreateNewItem.createNewItem(pc, _isItem, _isQuantity);
            // 設置延遲使用機制
            final Timestamp ts = new Timestamp(System.currentTimeMillis());
            item.setLastUsed(ts);
            pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
            pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _isItem = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _isQuantity = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
    }
}
