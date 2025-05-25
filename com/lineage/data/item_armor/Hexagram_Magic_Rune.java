package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Hexagram_Magic_Rune extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Hexagram_Magic_Rune.class);
    private int _r = 0;
    private int _hp_min = 0;
    private int _hp_max = 0;
    private int _gfx = 0;

    public static ItemExecutor get() {
        return new Hexagram_Magic_Rune();
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
                    pc.set_Hexagram_Magic_Rune(0, 0, 0, 0);
                    break;
                case 1:
                    pc.set_Hexagram_Magic_Rune(_r, _hp_min, _hp_max, _gfx);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _r = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _hp_min = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
        try {
            _hp_max = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
        try {
            _gfx = Integer.parseInt(set[4]);
        } catch (Exception localException2) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.ElitePlateMail_Fafurion JD-Core Version: 0.6.2
 */