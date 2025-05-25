package com.lineage.data.event.gambling;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 比賽控制數據
 *
 * @author dexc
 */
public class Gambling extends GamblingConfig {
    private final Map<Integer, GamblingNpc> _npcidMap = new HashMap<>();
    // private int _no;
    public int WIN;
    // 冠軍參賽者
    private GamblingNpc _onenpc;
    private long _adena;
    private long _upadena;
    private Random _random = new Random();
    /*
     * public Gambling(int no) { _no = no; }
     */

    /**
     * 清除紀錄
     */
    public void clear() {
        _npcidMap.clear();
        this._onenpc = null;
        this._adena = 0;
    }
    /**
     * 傳回本場編號
     *
     * @return
     */
    /*
     * public int no() { return _no; }
     */

    /**
     * 傳回本場總下注金額
     *
     */
    public long get_allAdena() {
        return this._adena;
    }

    /**
     * 建立參賽NPC
     *
     */
    public void set_gmaNpc(final long previous) {
        if (GamblingConfig.ISGFX) {// 賽狗外型
            GamblingConfig.ISGFX = false;
        } else {// 食人妖寶寶外型
            GamblingConfig.ISGFX = true;
        }
        WIN = _random.nextInt(5);
        int i = 0;
        while (_npcidMap.size() < 5) {
            final int npcid = NPCID[_random.nextInt(NPCID.length)];
            if (_npcidMap.get(npcid) == null) {
                final GamblingNpc gamnpc = new GamblingNpc(this);
                gamnpc.showNpc(npcid, i++);
                _npcidMap.put(npcid, gamnpc);
            }
        }
        // 加入累積金
        this._upadena = previous;
    }

    /**
     * 目前下注金額
     *
     */
    public long get_allRate() {
        long adena = 0;
        for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
            adena += gamblingNpc.get_adena();
        }
        return adena + this._upadena;
    }

    /**
     * 計算賠率
     */
    public void set_allRate() {
        long adena = this._upadena;
        // 計算全部NPC累積押注
        for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
            adena += gamblingNpc.get_adena();
        }
        this._adena = adena;
        // System.out.println("總收入: "+_adena);
        for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
            this.set_npcRate(gamblingNpc, adena);
        }
    }

    /**
     * 計算每個NPC該獲取的賠率
     *
     */
    private void set_npcRate(final GamblingNpc npc, final long adena) {
        double chip = npc.get_adena();// 該NPC總共下注金額
        if (adena != 0 && chip != 0) {
            // 收取10%佣金
            double aftertp = adena;
            double rate = aftertp / chip;// 賠率
            // 設置最高賠率限制條件
            if (rate > 100.0D) {
                rate = 100.0D;
            }
            if (rate < 2.0D) {
                rate = 2.0D;
            }
            // System.out.println("賠率: "+rate);
            npc.set_rate(rate);
        }
    }

    /**
     * 傳回全部參賽者
     *
     */
    public Map<Integer, GamblingNpc> get_allNpc() {
        return _npcidMap;
    }

    /**
     * 傳回冠軍
     *
     */
    public GamblingNpc get_oneNpc() {
        return this._onenpc;
    }

    /**
     * 設置冠軍
     *
     */
    public void set_oneNpc(final GamblingNpc onenpc) {
        this._onenpc = onenpc;
    }

    /**
     * 啟動線程
     */
    public void startGam() {
        for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
            gamblingNpc.getStart();
        }
    }

    /**
     * 刪除參賽者
     */
    public void delAllNpc() {
        for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
            gamblingNpc.delNpc();
        }
    }
}
