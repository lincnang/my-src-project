package com.lineage.server.templates;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Object;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1Npc extends L1Object implements Cloneable {
    private static final Log _log = LogFactory.getLog(L1Npc.class);
    private static final long serialVersionUID = 1L;
    private int _npcid;
    private String _name;
    private String _impl;
    private int _level;
    private int _hp;
    private int _mp;
    private int _ac;
    private int _str;
    private int _con;
    private int _dex;
    private int _wis;
    private int _int;
    private int _mr;
    private int _exp;
    private int _lawful;
    private String _size;
    private int _weakAttr;
    private int _ranged;
    private boolean _agrososc;
    private boolean _agrocoi;
    private boolean _tameable;
    private int _passispeed;
    private int _atkspeed;
    private boolean _agro;
    private int _gfxid;
    private String _nameid;
    private int _undead;
    private int _poisonatk;
    private int _paralysisatk;
    private int _family;
    private int _agrofamily;
    private int _agrogfxid1;
    private int _agrogfxid2;
    private boolean _picupitem;
    private int _digestitem;
    private boolean _bravespeed;
    private int _hprinterval;
    private int _hpr;
    private int _mprinterval;
    private int _mpr;
    private boolean _teleport;
    private int _randomlevel;
    private int _randomhp;
    private int _randommp;
    private int _randomac;
    private int _randomexp;
    private int _randomlawful;
    private int _damagereduction;
    private boolean _hard;
    private boolean _doppel;
    private boolean _tu;
    private boolean _erase;
    private int bowActId = 0;
    private int _karma;
    private int _transformId;
    private int _transformGfxId;
    private int _atkMagicSpeed;
    private int _subMagicSpeed;
    private int _lightSize;
    private boolean _amountFixed;
    private boolean _changeHead;
    private boolean _isCantResurrect;
    private boolean _isBroadcast;
    private String _classname;
    private NpcExecutor _class;
    private boolean _talk = false;
    private boolean _action = false;
    private boolean _attack = false;
    private boolean _death = false;
    private boolean _work = false;
    private boolean _spawn = false;
    private boolean _boss = false;
    private int _quest_score;
    private int _DropGround;
    private int _WeaponSoulExp; // 武器劍靈系統
    /**
     * 怪物圖鑒20171020
     **/
    private int _bookId = 0;

    public L1Npc clone() {
        try {
            return (L1Npc) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

    public int get_npcId() {
        return _npcid;
    }

    public void set_npcId(int i) {
        _npcid = i;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String s) {
        _name = s;
    }

    public String getImpl() {
        return _impl;
    }

    public void setImpl(String s) {
        _impl = s;
    }

    public int get_level() {
        return _level;
    }

    public void set_level(int i) {
        _level = i;
    }

    public int get_hp() {
        return _hp;
    }

    public void set_hp(int i) {
        _hp = i;
    }

    public int get_mp() {
        return _mp;
    }

    public void set_mp(int i) {
        _mp = i;
    }

    public int get_ac() {
        return _ac;
    }

    public void set_ac(int i) {
        _ac = i;
    }

    public int get_str() {
        return _str;
    }

    public void set_str(int i) {
        _str = i;
    }

    public int get_con() {
        return _con;
    }

    public void set_con(int i) {
        _con = i;
    }

    public int get_dex() {
        return _dex;
    }

    public void set_dex(int i) {
        _dex = i;
    }

    public int get_wis() {
        return _wis;
    }

    public void set_wis(int i) {
        _wis = i;
    }

    public int get_int() {
        return _int;
    }

    public void set_int(int i) {
        _int = i;
    }

    public int get_mr() {
        return _mr;
    }

    public void set_mr(int i) {
        _mr = i;
    }

    public int get_exp() {
        return _exp;
    }

    public void set_exp(int i) {
        _exp = i;
    }

    public int get_lawful() {
        return _lawful;
    }

    public void set_lawful(int i) {
        _lawful = i;
    }

    public String get_size() {
        return _size;
    }

    public void set_size(String s) {
        _size = s;
    }

    public boolean isSmall() {
        return _size.equalsIgnoreCase("small");
    }

    public boolean isLarge() {
        return _size.equalsIgnoreCase("large");
    }

    public int get_weakAttr() {
        return _weakAttr;
    }

    public void set_weakAttr(int i) {
        _weakAttr = i;
    }

    public int get_ranged() {
        return _ranged;
    }

    public void set_ranged(int i) {
        _ranged = i;
    }

    public boolean is_agrososc() {
        return _agrososc;
    }

    public void set_agrososc(boolean flag) {
        _agrososc = flag;
    }

    public boolean is_agrocoi() {
        return _agrocoi;
    }

    public void set_agrocoi(boolean flag) {
        _agrocoi = flag;
    }

    public boolean isTamable() {
        return _tameable;
    }

    public void setTamable(boolean flag) {
        _tameable = flag;
    }

    public int get_passispeed() {
        return _passispeed;
    }

    public void set_passispeed(int i) {
        _passispeed = i;
    }

    public int get_atkspeed() {
        return _atkspeed;
    }

    public void set_atkspeed(int i) {
        _atkspeed = i;
    }

    public boolean is_agro() {
        return _agro;
    }

    public void set_agro(boolean flag) {
        _agro = flag;
    }

    public int get_gfxid() {
        return _gfxid;
    }

    public void set_gfxid(int i) {
        _gfxid = i;
    }

    public String get_nameid() {
        return _nameid;
    }

    public void set_nameid(String s) {
        _nameid = s;
    }

    /**
     * NPC屬性系
     *
     * @return <BR>
     * 0:無 1:不死系 2:惡魔系 3:殭屍系 4:不死系(治療系無傷害/無法使用起死回生) 5:狼人系 6:龍系
     */
    public int get_undead() {
        return _undead;
    }

    public void set_undead(int i) {
        _undead = i;
    }

    public int get_poisonatk() {
        return _poisonatk;
    }

    public void set_poisonatk(int i) {
        _poisonatk = i;
    }

    public int get_paralysisatk() {
        return _paralysisatk;
    }

    public void set_paralysisatk(int i) {
        _paralysisatk = i;
    }

    public int get_family() {
        return _family;
    }

    public void set_family(int i) {
        _family = i;
    }

    public int get_agrofamily() {
        return _agrofamily;
    }

    public void set_agrofamily(int i) {
        _agrofamily = i;
    }

    public int is_agrogfxid1() {
        return _agrogfxid1;
    }

    public void set_agrogfxid1(int i) {
        _agrogfxid1 = i;
    }

    public int is_agrogfxid2() {
        return _agrogfxid2;
    }

    public void set_agrogfxid2(int i) {
        _agrogfxid2 = i;
    }

    public boolean is_picupitem() {
        return _picupitem;
    }

    public void set_picupitem(boolean flag) {
        _picupitem = flag;
    }

    public int get_digestitem() {
        return _digestitem;
    }

    public void set_digestitem(int i) {
        _digestitem = i;
    }

    public boolean is_bravespeed() {
        return _bravespeed;
    }

    public void set_bravespeed(boolean flag) {
        _bravespeed = flag;
    }

    public int get_hprinterval() {
        return _hprinterval;
    }

    public void set_hprinterval(int i) {
        _hprinterval = (i / 1000);
    }

    public int get_hpr() {
        return _hpr;
    }

    public void set_hpr(int i) {
        _hpr = i;
    }

    public int get_mprinterval() {
        return _mprinterval;
    }

    public void set_mprinterval(int i) {
        _mprinterval = (i / 1000);
    }

    public int get_mpr() {
        return _mpr;
    }

    public void set_mpr(int i) {
        _mpr = i;
    }

    public boolean is_teleport() {
        return _teleport;
    }

    public void set_teleport(boolean flag) {
        _teleport = flag;
    }

    public int get_randomlevel() {
        return _randomlevel;
    }

    public void set_randomlevel(int i) {
        _randomlevel = i;
    }

    public int get_randomhp() {
        return _randomhp;
    }

    public void set_randomhp(int i) {
        _randomhp = i;
    }

    public int get_randommp() {
        return _randommp;
    }

    public void set_randommp(int i) {
        _randommp = i;
    }

    public int get_randomac() {
        return _randomac;
    }

    public void set_randomac(int i) {
        _randomac = i;
    }

    public int get_randomexp() {
        return _randomexp;
    }

    public void set_randomexp(int i) {
        _randomexp = i;
    }

    public int get_randomlawful() {
        return _randomlawful;
    }

    public void set_randomlawful(int i) {
        _randomlawful = i;
    }

    public int get_damagereduction() {
        return _damagereduction;
    }

    public void set_damagereduction(int i) {
        _damagereduction = i;
    }

    public boolean is_hard() {
        return _hard;
    }

    public void set_hard(boolean flag) {
        _hard = flag;
    }

    public boolean is_doppel() {
        return _doppel;
    }

    public void set_doppel(boolean flag) {
        _doppel = flag;
    }

    public boolean get_IsTU() {
        return _tu;
    }

    public void set_IsTU(boolean i) {
        _tu = i;
    }

    public boolean get_IsErase() {
        return _erase;
    }

    public void set_IsErase(boolean i) {
        _erase = i;
    }

    public int getBowActId() {
        return bowActId;
    }

    public void setBowActId(int i) {
        bowActId = i;
    }

    public int getKarma() {
        return _karma;
    }

    public void setKarma(int i) {
        _karma = i;
    }

    public int getTransformId() {
        return _transformId;
    }

    public void setTransformId(int transformId) {
        _transformId = transformId;
    }

    public int getTransformGfxId() {
        return _transformGfxId;
    }

    public void setTransformGfxId(int i) {
        _transformGfxId = i;
    }

    public int getAtkMagicSpeed() {
        return _atkMagicSpeed;
    }

    public void setAtkMagicSpeed(int atkMagicSpeed) {
        _atkMagicSpeed = atkMagicSpeed;
    }

    public int getSubMagicSpeed() {
        return _subMagicSpeed;
    }

    public void setSubMagicSpeed(int subMagicSpeed) {
        _subMagicSpeed = subMagicSpeed;
    }

    public int getLightSize() {
        return _lightSize;
    }

    public void setLightSize(int lightSize) {
        _lightSize = lightSize;
    }

    public boolean isAmountFixed() {
        return _amountFixed;
    }

    public void setAmountFixed(boolean fixed) {
        _amountFixed = fixed;
    }

    public boolean getChangeHead() {
        return _changeHead;
    }

    public void setChangeHead(boolean changeHead) {
        _changeHead = changeHead;
    }

    public boolean isCantResurrect() {
        return _isCantResurrect;
    }

    public void setCantResurrect(boolean isCantResurrect) {
        _isCantResurrect = isCantResurrect;
    }

    public String get_classname() {
        return _classname;
    }

    public void set_classname(String classname) {
        _classname = classname;
    }

    public NpcExecutor getNpcExecutor() {
        return _class;
    }

    public void setNpcExecutor(NpcExecutor _class) {
        try {
            if (_class == null) {
                return;
            }
            this._class = _class;
            int type = _class.type();
            if (type >= 32) {
                _spawn = true;
                type -= 32;
            }
            if (type >= 16) {
                _work = true;
                type -= 16;
            }
            if (type >= 8) {
                _death = true;
                type -= 8;
            }
            if (type >= 4) {
                _attack = true;
                type -= 4;
            }
            if (type >= 2) {
                _action = true;
                type -= 2;
            }
            if (type >= 1) {
                _talk = true;
                type--;
            }
            if (type > 0) {
                _log.error("獨立判斷項數組設定錯誤:餘數大於0 NpcId: " + _npcid);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean talk() {
        return _talk;
    }

    public boolean action() {
        return _action;
    }

    public boolean attack() {
        return _attack;
    }

    public boolean death() {
        return _death;
    }

    public boolean work() {
        return _work;
    }

    public boolean spawn() {
        return _spawn;
    }

    public boolean is_boss() {
        return _boss;
    }

    public void set_boss(boolean boss) {
        _boss = boss;
    }

    public int get_quest_score() {
        return _quest_score;
    }

    public void set_quest_score(int quest_score) {
        _quest_score = quest_score;
    }

    public boolean isBroadcast()//src014
    {
        return this._isBroadcast;
    }

    public void setBroadcast(boolean isBroadcast) {
        this._isBroadcast = isBroadcast;
    }

    public int getDropGround() {
        return this._DropGround;
    }

    public void setDropGround(int i) {
        this._DropGround = i;
    }

    public int getWeaponSoulExp() { // 武器劍靈系統
        return this._WeaponSoulExp;
    }

    public void setWeaponSoulExp(int i) { // 武器劍靈系統
        this._WeaponSoulExp = i;
    }

    public int getBookId() {
        return _bookId;
    }

    public void setBookId(int i) {
        _bookId = i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Npc JD-Core Version: 0.6.2
 */