package com.lineage.server.templates;

public class L1ItemUpdate {
    public static final String _html_01 = "y_up_i0";
    public static final String _html_02 = "y_up_i1";
    public static final String _html_03 = "y_up_i2";
    private int _item_id;
    private int _toid;
    private int[] _needids = null;
    private int[] _needcounts = null;

    public int get_item_id() {
        return _item_id;
    }

    public void set_item_id(int _item_id) {
        this._item_id = _item_id;
    }

    public int get_toid() {
        return _toid;
    }

    public void set_toid(int _toid) {
        this._toid = _toid;
    }

    public int[] get_needids() {
        return _needids;
    }

    public void set_needids(int[] _needids) {
        this._needids = _needids;
    }

    public int[] get_needcounts() {
        return _needcounts;
    }

    public void set_needcounts(int[] _needcounts) {
        this._needcounts = _needcounts;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1ItemUpdate JD-Core Version: 0.6.2
 */