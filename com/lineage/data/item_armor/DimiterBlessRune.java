package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DimiterBlessRune extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(DimiterBlessRune.class);
    private int _r;// 蒂蜜特的魔力回復發動機率
    private int _mp_min;// 最小回復量
    private int _mp_max;// 最大回復量
    private int _r2;// 蒂蜜特祝福發動機率
    private int _time;// 聖界持續時間

    public static ItemExecutor get() {
        return new DimiterBlessRune();
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
                case 0:// 脫下
                    pc.set_DimiterBless(0, 0, 0, 0, 0);
                    break;
                case 1:// 穿上
                    pc.set_DimiterBless(_r, _mp_min, _mp_max, _r2, _time);
                    break;
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
            _mp_min = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
        try {
            _mp_max = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
        try {
            _r2 = Integer.parseInt(set[4]);
        } catch (Exception localException) {
        }
        try {
            _time = Integer.parseInt(set[5]);
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.ElitePlateMail_Lindvior JD-Core Version: 0.6.2
 */