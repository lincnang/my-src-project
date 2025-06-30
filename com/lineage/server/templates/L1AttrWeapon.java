package com.lineage.server.templates;

public class L1AttrWeapon {
    private final int _attrId;
    private final String _name;
    private final int _stage;
    private final int _chance;
    private final int _probability;
    private final double _type_bind;
    private final double _type_drain_hp;
    private final int _type_drain_mp;
    private final double _type_dmgup;
    private final int _type_range;
    private final int _type_range_dmg;
    private final int _type_light_dmg;
    private final boolean _type_skill_1;
    private final boolean _type_skill_2;
    private final boolean _type_skill_3;
    private final double _type_skill_time;
    private final String[] _type_poly_list;
    private final boolean _type_remove_weapon;
    private final boolean _type_remove_doll;
    private final int _type_remove_armor;
    private final int _extradmg;
    private final int _extrabowdmg;
    private final int _gfixd;
    private int _attrDmg;
    private int _arrtDmgCritical;
    private int _arrtDmgCriticalPro;

    public L1AttrWeapon(int attrId, String name, int stage, int chance, int probability, double type_bind, double type_drain_hp, int type_drain_mp, double type_dmgup, int type_range, int type_range_dmg, int type_light_dmg, boolean type_skill_1, boolean type_skill_2, boolean type_skill_3, double type_skill_time, String[] type_poly_list, boolean type_remove_weapon, boolean type_remove_doll, int type_remove_armor, int extrabowdmg, int extradmg, int gfixd, int attrDmg, int arrtDmgCritical, int arrtDmgCriticalPro) {
        _attrId = attrId;
        _name = name;
        _stage = stage;
        _chance = chance;
        _probability = probability;
        _type_bind = type_bind;
        _type_drain_hp = type_drain_hp;
        _type_drain_mp = type_drain_mp;
        _type_dmgup = type_dmgup;
        _type_range = type_range;
        _type_range_dmg = type_range_dmg;
        _type_light_dmg = type_light_dmg;
        _type_skill_1 = type_skill_1;
        _type_skill_2 = type_skill_2;
        _type_skill_3 = type_skill_3;
        _type_skill_time = type_skill_time;
        _type_poly_list = type_poly_list;
        _type_remove_weapon = type_remove_weapon;
        _type_remove_doll = type_remove_doll;
        _type_remove_armor = type_remove_armor;
        _extradmg = extradmg;
        _extrabowdmg = extrabowdmg;
        _gfixd = gfixd;
        _attrDmg = attrDmg;
        _arrtDmgCritical = arrtDmgCritical;
        _arrtDmgCriticalPro = arrtDmgCriticalPro;
    }

    public int getAttrId() {
        return _attrId;
    }

    public final String getName() {
        return _name;
    }

    public final int getStage() {
        return _stage;
    }

    public final int getChance() {
        return _chance;
    }

    public final int getProbability() {
        return _probability;
    }

    public final double getTypeBind() {
        return _type_bind;
    }

    public final double getTypeDrainHp() {
        return _type_drain_hp;
    }

    public final int getTypeDrainMp() {
        return _type_drain_mp;
    }

    public final double getTypeDmgup() {
        return _type_dmgup;
    }

    public final int getTypeRange() {
        return _type_range;
    }

    public final int getTypeRangeDmg() {
        return _type_range_dmg;
    }

    public final int getTypeLightDmg() {
        return _type_light_dmg;
    }

    public final boolean getTypeSkill1() {
        return _type_skill_1;
    }

    public final boolean getTypeSkill2() {
        return _type_skill_2;
    }

    public final boolean getTypeSkill3() {
        return _type_skill_3;
    }

    public final double getTypeSkillTime() {
        return _type_skill_time;
    }

    public final String[] getTypePolyList() {
        return _type_poly_list;
    }

    public final boolean getTypeRemoveWeapon() {
        return _type_remove_weapon;
    }

    public final boolean getTypeRemoveDoll() {
        return _type_remove_doll;
    }

    public final int getExtraDmg() {

        return _extradmg;
    }

    public final int getExtraBowDmg() {

        return _extrabowdmg;
    }

    public final int getTypeRemoveArmor() {
        return _type_remove_armor;
    }

    public final int getGfxId() {
        return _gfixd;
    }

    public int getAttrDmg() {
        return _attrDmg;
    }

    public int getArrtDmgCritical() {
        return _arrtDmgCritical;
    }

    public int getArrtDmgCriticalPro() {
        return _arrtDmgCriticalPro;
    }

}
