package com.lineage.server.templates;

import java.sql.Timestamp;

/**
 * 古文字名稱記錄
 *
 * @author daien
 */
public class L1ItemPower_name {
    private int _item_obj_id;
    private int _power_id;
    private String _power_name;
    private int _dice;
    // 魔法武器DIY系統(附魔石類型) by terry0412
    private L1MagicWeapon _magic_weapon;
    // 魔法武器DIY系統(使用期限) by terry0412
    private Timestamp _date_time;
    private L1BossWeapon _boss_weapon;
    private int _boss_lv;
    private Timestamp _boss_date_time;
    // 超能附魔
    private int _super_rune_1;
    private int _super_rune_2;
    private int _super_rune_3;
    private int _super_rune_4;
    private L1CriticalHitStone _critical_hit_stone;

    public int get_item_obj_id() {
        return this._item_obj_id;
    }

    public void set_item_obj_id(int item_obj_id) {
        this._item_obj_id = item_obj_id;
    }

    public int get_power_id() {
        return _power_id;
    }

    public void set_power_id(int _power_id) {
        this._power_id = _power_id;
    }

    public String get_power_name() {
        return _power_name;
    }

    public void set_power_name(String _power_name) {
        this._power_name = _power_name;
    }

    public int get_dice() {
        return _dice;
    }

    public void set_dice(int _dice) {
        this._dice = _dice;
    }

    public final L1MagicWeapon get_magic_weapon() {
        return _magic_weapon;
    }

    public final void set_magic_weapon(final L1MagicWeapon value) {
        this._magic_weapon = value;
    }

    public final Timestamp get_date_time() {
        return _date_time;
    }

    public final void set_date_time(final Timestamp value) {
        this._date_time = value;
    }

    public final L1BossWeapon get_boss_weapon() {
        return this._boss_weapon;
    }

    public final void set_boss_weapon(L1BossWeapon value) {
        this._boss_weapon = value;
    }

    public int get_boss_lv() {
        return this._boss_lv;
    }

    public void set_boss_lv(int _boss_lv) {
        this._boss_lv = _boss_lv;
    }

    public final Timestamp get_boss_date_time() {
        return this._boss_date_time;
    }

    public final void set_boss_date_time(Timestamp value) {
        this._boss_date_time = value;
    }

    public int get_super_rune_1() {
        return _super_rune_1;
    }

    public void set_super_rune_1(int _super_rune_1) {
        this._super_rune_1 = _super_rune_1;
    }

    public int get_super_rune_2() {
        return _super_rune_2;
    }

    public void set_super_rune_2(int _super_rune_2) {
        this._super_rune_2 = _super_rune_2;
    }

    public int get_super_rune_3() {
        return _super_rune_3;
    }

    public void set_super_rune_3(int _super_rune_3) {
        this._super_rune_3 = _super_rune_3;
    }

    public int get_super_rune_4() {
        return _super_rune_4;
    }

    //爆擊寶石
    public void set_super_rune_4(int _super_rune_4) {
        this._super_rune_4 = _super_rune_4;
    }

    public final L1CriticalHitStone get_critical_hit_stone() {
        return _critical_hit_stone;
    }

    public final void set_critical_hit_stone(final L1CriticalHitStone value) {
        _critical_hit_stone = value;
    }
}