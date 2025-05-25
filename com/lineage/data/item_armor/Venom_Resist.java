package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Venom_Resist extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Venom_Resist.class);

    public static ItemExecutor get() {
        return new Venom_Resist();
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
                case 0:
                    pc.set_venom_resist(-1);
                    break;
                case 1:
                    pc.set_venom_resist(1);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.Venom_Resist JD-Core Version: 0.6.2
 */