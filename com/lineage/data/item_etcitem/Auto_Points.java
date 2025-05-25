package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 掛機點數
 *
 * @author 老爹
 */
public class Auto_Points extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Auto_Points.class);
    private int _points;

    public static ItemExecutor get() {
        return new Auto_Points();
    }

    /**
     * 使用
     */
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        pc.get_other().set_Auto_Points(pc.get_other().get_Auto_Points() + _points);
        pc.sendPackets(new S_SystemMessage("您的可掛機時間已經充值 " + _points + " 秒鐘"));
        final int time = pc.get_other().get_Auto_Points();
        pc.sendPackets(new S_SystemMessage("您目前可掛機剩餘時間為 " + time + " 秒鐘"));
        pc.getInventory().removeItem(item, 1);
        try {
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _points = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}