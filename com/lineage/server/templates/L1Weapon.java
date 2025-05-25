package com.lineage.server.templates;

import java.util.Random;

public class L1Weapon extends L1Item {
    private static final long serialVersionUID = 1L;
    private static Random _random = new Random();
    private int _add_dmg_min = 0;
    private int _add_dmg_max = 0;
    private int _range = 0;
    private int _hitModifier = 0;
    private int _dmgModifier = 0;
    private int _doubleDmgChance;
    private int _magicDmgModifier = 0;
    private int _canbedmg = 0;

    //private static int _stunPVP;
    public void set_add_dmg(int add_dmg_min, int add_dmg_max) {
        _add_dmg_min = add_dmg_min;
        _add_dmg_max = add_dmg_max;
    }

    public int get_add_dmg() {
        int add_dmg = 0;
        if ((_add_dmg_min != 0) && (_add_dmg_max != 0)) {
            add_dmg = _add_dmg_min + _random.nextInt(_add_dmg_max - _add_dmg_min);
        }
        return add_dmg;
    }

    public int getRange() {
        return _range;
    }

    public void setRange(int i) {
        _range = i;
    }

    public int getHitModifier() {
        return _hitModifier;
    }

    public void setHitModifier(int i) {
        _hitModifier = i;
    }

    public int getDmgModifier() {
        return _dmgModifier;
    }

    public void setDmgModifier(int i) {
        _dmgModifier = i;
    }

    public int getDoubleDmgChance() {
        return _doubleDmgChance;
    }

    public void setDoubleDmgChance(int i) {
        _doubleDmgChance = i;
    }

    public int getMagicDmgModifier() {
        return _magicDmgModifier;
    }

    public void setMagicDmgModifier(int i) {
        _magicDmgModifier = i;
    }

    public int get_canbedmg() {
        return _canbedmg;
    }

    public void set_canbedmg(int i) {
        _canbedmg = i;
    }

    //public static int get_stunPVP() {
    //return _stunPVP;
    //}
    //public void set_stunPVP(int stunPVP) {
    //_stunPVP = stunPVP;
    //}
    public boolean isTwohandedWeapon() {
        int weapon_type = getType();
        boolean bool = (weapon_type == 3) || (weapon_type == 4) || (weapon_type == 5) || (weapon_type == 11) || (weapon_type == 12) || (weapon_type == 15) || (weapon_type == 16) || (weapon_type == 18);
        return bool;
    }
}
