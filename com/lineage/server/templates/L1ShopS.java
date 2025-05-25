package com.lineage.server.templates;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.sql.Timestamp;

public class L1ShopS {
    private int _id;
    private int _item_obj_id;
    private int _user_obj_id;
    private int _buy_obj_id;// 購買人OBJID
    private String _buy_name;// 購買人姓名
    private int _adena;
    private Timestamp _overtime;
    private int _end;
    private String _none;
    private L1ItemInstance _item = null;

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        _id = id;
    }

    public int get_item_obj_id() {
        return _item_obj_id;
    }

    public void set_item_obj_id(int itemObjId) {
        _item_obj_id = itemObjId;
    }

    public int get_user_obj_id() {
        return _user_obj_id;
    }

    public void set_user_obj_id(int userObjId) {
        _user_obj_id = userObjId;
    }

    public int get_adena() {
        return _adena;
    }

    public void set_adena(int adena) {
        _adena = adena;
    }

    public Timestamp get_overtime() {
        return _overtime;
    }

    public void set_overtime(Timestamp overtime) {
        _overtime = overtime;
    }

    public int get_end() {
        return _end;
    }

    public void set_end(int end) {
        _end = end;
    }

    public String get_none() {
        return _none;
    }

    public void set_none(String none) {
        _none = none;
    }

    public L1ItemInstance get_item() {
        return _item;
    }

    public void set_item(L1ItemInstance item) {
        _item = item;
    }

    /**
     * 購買人OBJID
     *
     * @return the _user_obj_id
     */
    public int get_buy_objid() {
        return _buy_obj_id;
    }

    /**
     * 購買人OBJID
     *
     */
    public void set_buy_objid(int buyObjId) {
        _buy_obj_id = buyObjId;
    }

    /**
     * 購買人名字
     *
     * @return the _user_obj_id
     */
    public String get_buy_name() {
        return _buy_name;
    }

    /**
     * 購買人名字
     *
     */
    public void set_buy_name(String name) {
        _buy_name = name;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1ShopS JD-Core Version: 0.6.2
 */