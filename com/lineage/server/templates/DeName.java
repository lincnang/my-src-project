package com.lineage.server.templates;

public class DeName {
    private int _deobjid;
    private String _name;
    private int _type;
    private int _sex;
    private int _clanid;

    public DeName(int deobjid, String name, int type, int sex, int clanid) {
        _deobjid = deobjid;
        _name = name;
        _type = type;
        _sex = sex;
        _clanid = clanid;
    }

    public int get_deobjid() {
        return _deobjid;
    }

    public void set_deobjid(int deobjid) {
        _deobjid = deobjid;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String name) {
        _name = name;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int type) {
        _type = type;
    }

    public int get_sex() {
        return _sex;
    }

    public void set_sex(int sex) {
        _sex = sex;
    }

    public int get_clanid() {
        return _clanid;
    }

    public void set_clanid(int clanid) {
        _clanid = clanid;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.DeName JD-Core Version: 0.6.2
 */