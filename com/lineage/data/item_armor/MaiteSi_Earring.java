package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MaiteSi_Earring extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(MaiteSi_Earring.class);
    private int _uhp_number = 0;

    public static ItemExecutor get() {
        return new MaiteSi_Earring();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            switch (data[0]) {
                case 0:// 解除裝備
                    pc.add_uhp_number(-_uhp_number);
                    break;
                case 1:// 裝備
                    pc.add_uhp_number(_uhp_number);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _uhp_number = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.MaiteSi_Earring JD-Core Version: 0.6.2
 */