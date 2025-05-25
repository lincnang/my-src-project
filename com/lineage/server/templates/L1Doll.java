package com.lineage.server.templates;  //src017

import com.lineage.server.model.doll.L1DollExecutor;

import java.util.ArrayList;

public class L1Doll {
    private int _itemid;
    private ArrayList<L1DollExecutor> powerList = null;
    private int[] _need;
    private int[] _counts;
    private int _time;
    private int _gfxid;
    private String _nameid;
    private int _Doll_Ac;
    private int _Doll_Hit;
    private int _Doll_Dmg;
    private int _Doll_DmgBow;
    private int _Doll_HitBow;
    private int _Doll_Hp;
    private int _Doll_Mp;
    private int _Doll_Sp;
    private int _Doll_Stat_Str;
    private int _Doll_Stat_Con;
    private int _Doll_Stat_Dex;
    private int _Doll_Stat_Int;
    private int _Doll_Stat_Wis;
    private int _Doll_Stat_Cha;
    private int _Doll_Mr;
    private int _Doll_DefenseWater;
    private int _Doll_DefenseWind;
    private int _Doll_DefenseFire;
    private int _Doll_DefenseEarth;
    private int _Doll_Regist_Stun;
    private int _Doll_Regist_Stone;
    private int _Doll_Regist_Sleep;
    private int _Doll_Regist_Freeze;
    private int _Doll_Regist_Sustain;
    private int _Doll_Regist_Blind;
    private String _Annotation1;
    private String _Annotation2;
    private String _Annotation3;
    private String _Annotation4;
    private String _Annotation5;
    private String _className; // 類別名稱
    private int _int1; // type1
    private int _int2; // type2
    private int _int3; // type3
    private String _note; // 說明
    // 7.6
    private int _level;

    public int get_itemid() {
        return _itemid;
    }

    public void set_itemid(int _itemid) {
        this._itemid = _itemid;
    }

    public ArrayList<L1DollExecutor> getPowerList() {
        return powerList;
    }

    public void setPowerList(ArrayList<L1DollExecutor> powerList) {
        this.powerList = powerList;
    }

    public int[] get_need() {
        return _need;
    }

    public void set_need(int[] _need) {
        this._need = _need;
    }

    public int[] get_counts() {
        return _counts;
    }

    public void set_counts(int[] _counts) {
        this._counts = _counts;
    }

    public int get_gfxid() {
        return _gfxid;
    }

    public void set_gfxid(int _gfxid) {
        this._gfxid = _gfxid;
    }

    public String get_nameid() {
        return _nameid;
    }

    public void set_nameid(String _nameid) {
        this._nameid = _nameid;
    }

    public int get_time() {
        return _time;
    }

    public void set_time(int _time) {
        this._time = _time;
    }

    public int get_Doll_Ac() {
        return _Doll_Ac;
    }

    public void set_Doll_Ac(int _Doll_Ac) {
        this._Doll_Ac = _Doll_Ac;
    }

    public int get_Doll_Hit() {
        return _Doll_Hit;
    }

    public void set_Doll_Hit(int _Doll_Hit) {
        this._Doll_Hit = _Doll_Hit;
    }

    public int get_Doll_Dmg() {
        return _Doll_Dmg;
    }

    public void set_Doll_Dmg(int _Doll_Dmg) {
        this._Doll_Dmg = _Doll_Dmg;
    }

    public int get_Doll_DmgBow() {
        return _Doll_DmgBow;
    }

    public void set_Doll_DmgBow(int _Doll_DmgBow) {
        this._Doll_DmgBow = _Doll_DmgBow;
    }

    public int get_Doll_HitBow() {
        return _Doll_HitBow;
    }

    public void set_Doll_HitBow(int _Doll_HitBow) {
        this._Doll_HitBow = _Doll_HitBow;
    }

    public int get_Doll_Hp() {
        return _Doll_Hp;
    }

    public void set_Doll_Hp(int _Doll_Hp) {
        this._Doll_Hp = _Doll_Hp;
    }

    public int get_Doll_Mp() {
        return _Doll_Mp;
    }

    public void set_Doll_Mp(int _Doll_Mp) {
        this._Doll_Mp = _Doll_Mp;
    }

    public int get_Doll_Sp() {
        return _Doll_Sp;
    }

    public void set_Doll_Sp(int _Doll_Sp) {
        this._Doll_Sp = _Doll_Sp;
    }

    public int get_Doll_Stat_Str() {
        return _Doll_Stat_Str;
    }

    public void set_Doll_Stat_Str(int _Doll_Stat_Str) {
        this._Doll_Stat_Str = _Doll_Stat_Str;
    }

    public int get_Doll_Stat_Con() {
        return _Doll_Stat_Con;
    }

    public void set_Doll_Stat_Con(int _Doll_Stat_Con) {
        this._Doll_Stat_Con = _Doll_Stat_Con;
    }

    public int get_Doll_Stat_Dex() {
        return _Doll_Stat_Dex;
    }

    public void set_Doll_Stat_Dex(int _Doll_Stat_Dex) {
        this._Doll_Stat_Dex = _Doll_Stat_Dex;
    }

    public int get_Doll_Stat_Int() {
        return _Doll_Stat_Int;
    }

    public void set_Doll_Stat_Int(int _Doll_Stat_Int) {
        this._Doll_Stat_Int = _Doll_Stat_Int;
    }

    public int get_Doll_Stat_Wis() {
        return _Doll_Stat_Wis;
    }

    public void set_Doll_Stat_Wis(int _Doll_Stat_Wis) {
        this._Doll_Stat_Wis = _Doll_Stat_Wis;
    }

    public int get_Doll_Stat_Cha() {
        return _Doll_Stat_Cha;
    }

    public void set_Doll_Stat_Cha(int _Doll_Stat_Cha) {
        this._Doll_Stat_Cha = _Doll_Stat_Cha;
    }

    public int get_Doll_Mr() {
        return _Doll_Mr;
    }

    public void set_Doll_Mr(int _Doll_Mr) {
        this._Doll_Mr = _Doll_Mr;
    }

    public int get_Doll_DefenseWater() {
        return _Doll_DefenseWater;
    }

    public void set_Doll_DefenseWater(int _Doll_DefenseWater) {
        this._Doll_DefenseWater = _Doll_DefenseWater;
    }

    public int get_Doll_DefenseWind() {
        return _Doll_DefenseWind;
    }

    public void set_Doll_DefenseWind(int _Doll_DefenseWind) {
        this._Doll_DefenseWind = _Doll_DefenseWind;
    }

    public int get_Doll_DefenseFire() {
        return _Doll_DefenseFire;
    }

    public void set_Doll_DefenseFire(int _Doll_DefenseFire) {
        this._Doll_DefenseFire = _Doll_DefenseFire;
    }

    public int get_Doll_DefenseEarth() {
        return _Doll_DefenseEarth;
    }

    public void set_Doll_DefenseEarth(int _Doll_DefenseEarth) {
        this._Doll_DefenseEarth = _Doll_DefenseEarth;
    }

    public int get_Doll_Regist_Stun() {
        return _Doll_Regist_Stun;
    }

    public void set_Doll_Regist_Stun(int _Doll_Regist_Stun) {
        this._Doll_Regist_Stun = _Doll_Regist_Stun;
    }

    public int get_Doll_Regist_Stone() {
        return _Doll_Regist_Stone;
    }

    public void set_Doll_Regist_Stone(int _Doll_Regist_Stone) {
        this._Doll_Regist_Stone = _Doll_Regist_Stone;
    }

    public int get_Doll_Regist_Sleep() {
        return _Doll_Regist_Sleep;
    }

    public void set_Doll_Regist_Sleep(int _Doll_Regist_Sleep) {
        this._Doll_Regist_Sleep = _Doll_Regist_Sleep;
    }

    public int get_Doll_Regist_Freeze() {
        return _Doll_Regist_Freeze;
    }

    public void set_Doll_Regist_Freeze(int _Doll_Regist_Freeze) {
        this._Doll_Regist_Freeze = _Doll_Regist_Freeze;
    }

    public int get_Doll_Regist_Sustain() {
        return _Doll_Regist_Sustain;
    }

    public void set_Doll_Regist_Sustain(int _Doll_Regist_Sustain) {
        this._Doll_Regist_Sustain = _Doll_Regist_Sustain;
    }

    public int get_Doll_Regist_Blind() {
        return _Doll_Regist_Blind;
    }

    public void set_Doll_Regist_Blind(int _Doll_Regist_Blind) {
        this._Doll_Regist_Blind = _Doll_Regist_Blind;
    }

    public String get_Annotation1() {
        return _Annotation1;
    }

    public void set_Annotation1(String _Annotation1) {
        this._Annotation1 = _Annotation1;
    }

    public String get_Annotation2() {
        return _Annotation2;
    }

    public void set_Annotation2(String _Annotation2) {
        this._Annotation2 = _Annotation2;
    }

    public String get_Annotation3() {
        return _Annotation3;
    }

    public void set_Annotation3(String _Annotation3) {
        this._Annotation3 = _Annotation3;
    }

    public String get_Annotation4() {
        return _Annotation4;
    }

    public void set_Annotation4(String _Annotation4) {
        this._Annotation4 = _Annotation4;
    }

    public String get_Annotation5() {
        return _Annotation5;
    }

    public void set_Annotation5(String _Annotation5) {
        this._Annotation5 = _Annotation5;
    }
    public String getClassName() {
        return _className;
    }

    public int get_int1() {
        return _int1;
    }

    public int get_int2() {
        return _int2;
    }

    public int get_int3() {
        return _int3;
    }

    public String get_note() {
        return _note;
    }

    // 7.6
    public int get_level() {
        return _level;
    }

    public void set_level(final int _level) {
        this._level = _level;
    }
}
