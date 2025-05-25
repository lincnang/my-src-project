package com.lineage.server.templates;

import com.lineage.list.OnlineUser;

public class L1Bank {
    private String _account_name = null;
    private long _adena_count = 0L;
    private String _pass = null;

    public String get_account_name() {
        return _account_name;
    }

    public void set_account_name(String _account_name) {
        this._account_name = _account_name;
    }

    public long get_adena_count() {
        return _adena_count;
    }

    public void set_adena_count(long _adena_count) {
        this._adena_count = _adena_count;
    }

    public String get_pass() {
        return _pass;
    }

    public void set_pass(String _pass) {
        this._pass = _pass;
    }

    public boolean isLan() {
        return OnlineUser.get().isLan(_account_name);
    }

    public boolean isEmpty() {
        return _adena_count <= 0L;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Bank JD-Core Version: 0.6.2
 */