package com.lineage.server.templates;

/**
 * 帳號登入暫存
 *
 * @author admin
 */
public class L1AccountsCount {
    private String _Accounts;
    private long _TIMES1;

    public L1AccountsCount() {
    }

    public L1AccountsCount(final String accounts, final long time) {
        _Accounts = accounts;
        _TIMES1 = time;
    }

    public String get_Accounts() {
        return _Accounts;
    }

    public void set_Accounts(final String i) {
        _Accounts = i;
    }

    public long get_TIMES1() {
        return _TIMES1;
    }

    public void set_TIMES1(final long i) {
        _TIMES1 = i;
    }
}