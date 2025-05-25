package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SetLevel_Reel extends ItemExecutor { //src016
    private static final Log _log = LogFactory.getLog(SetLevel_Reel.class);
    private int _levelupto = 1;

    public static ItemExecutor get() {
        return new SetLevel_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            pc.getInventory().removeItem(item, 1L);
            if (_levelupto > pc.getLevel()) {
                pc.setExp_Direct(ExpTable.getExpByLevel(_levelupto));
            } else {
                pc.sendPackets(new S_ServerMessage("你目前等級已經夠了"));
                return;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _levelupto = Integer.parseInt(set[1]);
            if (_levelupto <= 0) {
                _log.error("UserHpr 設置錯誤:最小恢復質小於等於0! 使用預設1");
                _levelupto = 50;
            }
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.hp.UserHpr JD-Core Version: 0.6.2
 */