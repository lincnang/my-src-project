package com.lineage.server.templates;

import java.util.Calendar;

public class L1Castle {
    private int _id;
    private String _name;
    private Calendar _warTime;
    private int _taxRate;
    private long _publicMoney;

    public L1Castle(int id, String name) {
        _id = id;
        _name = name;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Calendar getWarTime() {
        return _warTime;
    }

    public void setWarTime(Calendar i) {
        _warTime = i;
    }

    public int getTaxRate() {
        return _taxRate;
    }

    public void setTaxRate(int i) {
        _taxRate = i;
    }

    public long getPublicMoney() {
        return _publicMoney;
    }

    public void setPublicMoney(long i) {
        _publicMoney = i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Castle JD-Core Version: 0.6.2
 */