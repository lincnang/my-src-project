package com.add.BigHot;

public class BigHotblingTimeList {
    private static BigHotblingTimeList _instance;
    boolean _isStart = false;
    boolean _isWaiting = false;
    boolean _isBuy = false;
    private int _BigHotId = 0;
    private String _BigHotId1 = null;
    private int _yuanbao = 0;
    private int _count1 = 0;
    private int _count2 = 0;
    private int _count3 = 0;
    private int _count4 = 0;
    private int _bigmoney1 = 0;
    private int _bigmoney2 = 0;
    private int _bigmoney3 = 0;

    public static BigHotblingTimeList BigHot() {
        if (_instance == null) {
            _instance = new BigHotblingTimeList();
        }
        return _instance;
    }

    public void clear() {
        this._isStart = false;
        this._isBuy = false;
        this._isWaiting = false;
        this._BigHotId = 0;
        this._BigHotId1 = null;
        this._yuanbao = 0;
        this._count1 = 0;
        this._count2 = 0;
        this._count3 = 0;
        this._count4 = 0;
        this._bigmoney1 = 0;
        this._bigmoney2 = 0;
        this._bigmoney3 = 0;
    }

    public boolean get_isStart() {
        return this._isStart;
    }

    public void set_isStart(boolean b) {
        this._isStart = b;
    }

    public boolean get_isBuy() {
        return this._isBuy;
    }

    public void set_isBuy(boolean b) {
        this._isBuy = b;
    }

    public boolean get_isWaiting() {
        return this._isWaiting;
    }

    public void set_isWaiting(boolean b) {
        this._isWaiting = b;
    }

    public int get_BigHotId() {
        return this._BigHotId;
    }

    public void set_BigHotId(int i) {
        this._BigHotId = i;
    }

    public String get_BigHotId1() {
        return this._BigHotId1;
    }

    public void set_BigHotId1(String i) {
        this._BigHotId1 = i;
    }

    public int get_yuanbao() {
        return this._yuanbao;
    }

    public void add_yuanbao(int i) {
        this._yuanbao += i;
    }

    public int get_count1() {
        return this._count1;
    }

    public void add_count1(int i) {
        this._count1 += i;
    }

    public int get_count2() {
        return this._count2;
    }

    public void add_count2(int i) {
        this._count2 += i;
    }

    public int get_count3() {
        return this._count3;
    }

    public void add_count3(int i) {
        this._count3 += i;
    }

    public int get_count4() {
        return this._count4;
    }

    public void add_count4(int i) {
        this._count4 += i;
    }

    public int get_bigmoney1() {
        return this._bigmoney1;
    }

    public int get_bigmoney2() {
        return this._bigmoney2;
    }

    public int get_bigmoney3() {
        return this._bigmoney3;
    }

    public void computationBigHot() {
        int AllMoney = this._yuanbao - this._count4 * 10;
        this._bigmoney1 = (AllMoney * 7 / 10);
        this._bigmoney2 = (AllMoney * 2 / 10);
        this._bigmoney3 = (AllMoney / 10);
    }
}
