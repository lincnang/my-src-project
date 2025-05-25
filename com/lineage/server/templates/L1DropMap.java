package com.lineage.server.templates;

/**
 * 掉落物品資料(指定地圖)
 *
 * @author daien
 */
public class L1DropMap {
    private final int _enchant_min; // 最小強化值 by terry0412
    private final int _enchant_max; // 最大強化值 by terry0412
    int _mapid;
    int _itemId;
    int _min;
    int _max;
    int _chance;

    public L1DropMap(int mapid, int itemId, final int enchant_min, final int enchant_max, int min, int max, int chance) {
        _mapid = mapid;
        _itemId = itemId;
        _min = min;
        _max = max;
        this._enchant_min = enchant_min;
        this._enchant_max = enchant_max;
        _chance = chance;
    }

    /**
     * 指定地圖
     *
     */
    public int get_mapid() {
        return this._mapid;
    }

    /**
     * 機率
     *
     */
    public int getChance() {
        return this._chance;
    }

    /**
     * 物品編號
     *
     */
    public int getItemid() {
        return this._itemId;
    }

    /**
     * 最大量
     *
     */
    public int getMax() {
        return this._max;
    }

    /**
     * 最小量
     *
     */
    public int getMin() {
        return this._min;
    }

    /**
     * 最大強化值
     *
     */
    public int getEnchantMax() {
        return this._enchant_max;
    }

    /**
     * 最小強化值
     *
     */
    public int getEnchantMin() {
        return this._enchant_min;
    }
}
