package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AddExpPotion extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(AddExpPotion.class);
    private int _add_exp = 1;

    public static ItemExecutor get() {
        return new AddExpPotion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            final int oldLevel = pc.getLevel();
            final long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
            pc.getInventory().removeItem(item, 1L);
            long exp = (long) (_add_exp * needExp * 0.01);
            if (exp > 0) {
                pc.addExp(exp);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _add_exp = Integer.parseInt(set[1]);
            if (_add_exp <= 0) {
                _log.error("UserHpr 設置錯誤:最小恢復質小於等於0! 使用預設1");
                _add_exp = 1;
            }
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.hp.UserHpr JD-Core Version: 0.6.2
 */