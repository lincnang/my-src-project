package com.lineage.data.item_armor;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ElitePlateMail_Fafurion extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ElitePlateMail_Fafurion.class);
    private int _r = 0;
    private int _hp_min = 0;
    private int _hp_max = 0;

    public static ItemExecutor get() {
        return new ElitePlateMail_Fafurion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            int itemId = item.getItemId();
            if (itemId >= 21204 && itemId <= 21207) {
                if (item.getEnchantLevel() >= 7 && item.getEnchantLevel() <= 9) {
                    _hp_min += (item.getEnchantLevel() - 6) * 10;
                    _hp_max += (item.getEnchantLevel() - 6) * 10;
                }
            }
            //if (itemId >= 21381 && itemId <= 21395) {
            if (itemId >= 21381 && itemId <= 21395 || itemId >= 20001301 && itemId <= 20001308 || itemId >= 20001311 && itemId <= 20001318 || itemId >= 20001321 && itemId <= 20001328) {
                int i = 0;
                //if (itemId >= 21393 && itemId <= 21395) {
                if (itemId >= 21381 && itemId <= 21395 || itemId >= 20001301 && itemId <= 20001308 || itemId >= 20001311 && itemId <= 20001318 || itemId >= 20001321 && itemId <= 20001328) {
                    i = 2;
                } else {
                    i = 1;
                }
                switch (data[0]) {
                    case 0: // 解除裝備
                        pc.set_soulHp_val(0, 0, 0);
                        pc.set_soulHp(0);
                        break;
                    case 1: // 裝備
                        pc.set_soulHp_val(_r, _hp_min, _hp_max);
                        pc.set_soulHp(i);
                        break;
                }
            } else {
                switch (data[0]) {
                    case 0: // 解除裝備
                        pc.set_elitePlateMail_Fafurion(0, 0, 0);
                        break;
                    case 1: // 裝備
                        pc.set_elitePlateMail_Fafurion(_r, _hp_min, _hp_max);
                        break;
                }
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
    }
}
