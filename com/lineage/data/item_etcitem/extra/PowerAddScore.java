package com.lineage.data.item_etcitem.extra;

import com.lineage.data.event.CampSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 增加陣營貢獻度 [value]
 *
 * @author terry0412
 */
public class PowerAddScore extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PowerAddScore.class);
    private int _value;

    /**
     *
     */
    private PowerAddScore() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new PowerAddScore();
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
        if (pc == null) {
            return;
        }
        pc.getInventory().removeItem(item, 1);
        // 你得到了 %0 積分。
        pc.sendPackets(new S_ServerMessage("\\fX你得到了" + _value + "積分"));
        pc.get_other().add_score(_value);
        if (CampSet.CAMPSTART) {
            // 陣營系統啟用 XXX
            if (pc.get_c_power() != null && pc.get_c_power().get_c1_type() != 0) {
                final int lv = C1_Name_Type_Table.get().getLv(pc.get_c_power().get_c1_type(), pc.get_other().get_score());
                if (lv != pc.get_c_power().get_power().get_c1_id()) {
                    pc.get_c_power().set_power(pc, false);
                    pc.sendPackets(new S_ServerMessage("\\fR階級變更:" + pc.get_c_power().get_power().get_c1_name_type()));
                    pc.sendPacketsAll(new S_ChangeName(pc, true));
                }
            }
        }
        try {
            pc.save();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _value = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
