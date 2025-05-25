package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import william.Honor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 魔法卷軸 (聖結界)
 */
public class Honor_Lv extends ItemExecutor {

    private static final Log _log = LogFactory.getLog(Honor_Lv.class);

    /**
     *
     */
    private Honor_Lv() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Honor_Lv();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
                        final L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        pc.getInventory().removeItem(item, 1L);

        Honor.getInstance().checkHonor(pc,false,false);
        pc.sendPackets(new S_CloseList(pc.getId()));
    }
}
