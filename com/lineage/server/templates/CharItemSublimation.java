package com.lineage.server.templates;

/**
 * 昇華物品
 */
public class CharItemSublimation {
    private int _item_obj_id;
    private String _item_name;
    private int _char_obj_id;
    private int _Type;
    private int _Lv;

    public int get_item_obj_id() {
        return _item_obj_id;
    }

    public void set_item_obj_id(final int i) {
        _item_obj_id = i;
    }

    public String get_item_name() {
        return _item_name;
    }

    public void set_item_name(final String i) {
        _item_name = i;
    }

    public int get_char_obj_id() {
        return _char_obj_id;
    }

    public void set_char_obj_id(final int i) {
        _char_obj_id = i;
    }

    public int getType() {
        return _Type;
    }

    public void setType(final int i) {
        _Type = i;
    }

    public int getLv() {
        return _Lv;
    }

    public void setLv(final int i) {
        _Lv = i;
    }

}
