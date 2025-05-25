package com.lineage.server.model;

import com.lineage.config.ConfigRate;

/**
 * 物品升級機率
 *
 * @author daien
 */
public class L1ItemUpdata {
    /**
     * 武器現有強化質大於等於9
     *
     * @param enchant_level_tmp 以強化質
     * @return 升級機率 1/100
     */
    public static double enchant_wepon_up9(double enchant_level_tmp) {
        if (enchant_level_tmp <= 0) {
            enchant_level_tmp = 1;
        }
        // (>> 1: 除) (<< 1: 乘)
        return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_WEAPON)) / (enchant_level_tmp * 2);
    }

    /**
     * 武器現有強化質小於9
     *
     * @param enchant_level_tmp 以強化質
     * @return 升級機率 1/100
     */
    public static double enchant_wepon_dn9(double enchant_level_tmp) {
        if (enchant_level_tmp <= 0) {
            enchant_level_tmp = 1;
        }
        // (>> 1: 除) (<< 1: 乘)
        return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_WEAPON)) / enchant_level_tmp;
    }

    /**
     * 防具現有強化質大於等於9
     *
     * @param enchant_level_tmp 以強化質
     * @return 升級機率 1/100
     */
    public static double enchant_armor_up9(double enchant_level_tmp) {
        if (enchant_level_tmp <= 0) {
            enchant_level_tmp = 1;
        }
        // (>> 1: 除) (<< 1: 乘)
        return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_ARMOR)) / (enchant_level_tmp * 2);
    }

    /**
     * 防具現有強化質小於9
     *
     * @param enchant_level_tmp 以強化質
     * @return 升級機率 1/100
     */
    public static double enchant_armor_dn9(double enchant_level_tmp) {
        if (enchant_level_tmp <= 0) {
            enchant_level_tmp = 1;
        }
        // (>> 1: 除) (<< 1: 乘)
        return (100 + (enchant_level_tmp * ConfigRate.ENCHANT_CHANCE_ARMOR)) / enchant_level_tmp;
    }
}
