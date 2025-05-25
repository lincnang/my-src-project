package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ElitePlateMail_Antharas extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ElitePlateMail_Antharas.class);

    public static ItemExecutor get() {
        return new ElitePlateMail_Antharas();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            int reduceDmg = 0;
            if (item.getEnchantLevel() >= 7 && item.getEnchantLevel() <= 9) {
                reduceDmg += item.getEnchantLevel() - 6;
            }
            switch (data[0]) {
                case 0:
                    pc.addDamageReductionByArmor(-reduceDmg);
                    pc.set_venom_resist(-1);
                    break;
                case 1:
                    pc.addDamageReductionByArmor(reduceDmg);
                    pc.set_venom_resist(1);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.ElitePlateMail_Antharas JD-Core Version: 0.6.2
 */