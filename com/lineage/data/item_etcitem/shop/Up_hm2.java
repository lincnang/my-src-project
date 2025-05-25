package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 銀色能量石 (MP提高10點) (最多使用500瓶) 設置範例:shop.Up_hm2 10 500
 */
public class Up_hm2 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Up_hm2.class);
    private int _value;
    private int _limit_count;

    public static ItemExecutor get() {
        return new Up_hm2();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        // 是否超過數量上限
        if (pc.get_other().get_addmp() >= _value * _limit_count) {
            pc.sendPackets(new S_SystemMessage("已超過可用數量上限: " + _limit_count + "個。"));
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        // 使用道具已增加的MP值
        pc.get_other().set_addmp(pc.get_other().get_addmp() + _value);
        //pc.addMaxMp(_value);
        pc.addBaseMaxMp((short) _value);
        pc.sendPackets(new S_MPUpdate(pc));
        try {
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _value = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _limit_count = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
