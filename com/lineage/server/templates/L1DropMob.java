package com.lineage.server.templates;

/**
 * 掉落物品資料(全怪物)
 *
 * @author daien
 */
public class L1DropMob {
    int _min;
    int _max;
    int _chance;

    /**
     * 最小量
     *
     * @return
     */
    public int getMin() {
        return this._min;
    }

    public void setMin(int i) {
        this._min = i;
    }

    /**
     * 最大量
     *
     * @return
     */
    public int getMax() {
        return this._max;
    }

    public void setMax(int i) {
        this._max = i;
    }

    /**
     * 機率
     *
     * @return
     */
    public int getChance() {
        return this._chance;
    }

    public void setChance(int i) {
        this._chance = i;
    }
}
