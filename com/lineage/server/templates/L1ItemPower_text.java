package com.lineage.server.templates;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 古文字能力紀錄
 *
 * @author daien
 */
public class L1ItemPower_text {
    private static final Log _log = LogFactory.getLog(L1ItemPower_text.class);
    private int _id;
    private int[] _power_ids;// 文字組合
    private int _ac;// 防禦力
    private int _hp;// HP
    private int _mp;// MP
    private int _hpr;// HPR
    private int _mpr;// MPR
    private int _Str;
    private int _Con;
    private int _Dex;
    private int _Cha;
    private int _Int;
    private int _Wis;
    private int _mr;// 抗魔
    private int _sp;// 魔功
    private int _hit;// 命中
    private int _dmgup;// 物理攻擊
    private int _bowhit;// 弓的命中
    private int _bowdmgup;// 弓的攻擊
    private int _dice_dmg;// 機率給予爆擊
    private int _dmg;// 機率給予爆擊質
    private int _dodge;// 迴避攻擊
    private int _dice_hp;// 機率-吸血
    private int _sucking_hp;// 機率-吸血質
    private int _dice_mp;// 機率-吸魔
    private int _sucking_mp;// 機率-吸魔質
    private int _double_dmg;// 機率發動加倍的攻擊力
    private int _lift;// 機率可以將對方的武防裝備解除
    private int _defense_water;// 水
    private int _defense_wind;// 風
    private int _defense_fire;// 火
    private int _defense_earth;// 地
    private int _regist_stun;// 暈眩耐性
    private int _regist_stone;// 石化耐性
    private int _regist_sleep;// 睡眠耐性
    private int _regist_freeze;// 寒冰耐性
    private int _regist_sustain;// 支撐耐性;
    private int _regist_blind;// 暗黑耐性
    private int _weaponMD;// 暗黑耐性
    private int _weaponMDC;// 暗黑耐性
    private int _reducedmg;// 暗黑耐性
    private int _reduceMdmg;// 暗黑耐性
    private int[] _gfx;// 動畫組合
    private String _msg;// 效果文字

    public boolean check_pc(final L1PcInstance pc) {
        try {
            int is = 0;
            final int length = _power_ids.length;
            final int[] ch = new int[length];
            System.arraycopy(_power_ids, 0, ch, 0, length);
            final L1ItemInstance weapon = pc.getWeapon();
            if (check_item(weapon, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor1 = pc.getInventory().getItemEquipped(2, 1);// 頭盔
            if (check_item(armor1, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor2 = pc.getInventory().getItemEquipped(2, 2);// 盔甲
            if (check_item(armor2, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor3 = pc.getInventory().getItemEquipped(2, 3);// 內衣
            if (check_item(armor3, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor4 = pc.getInventory().getItemEquipped(2, 4);// 斗篷
            if (check_item(armor4, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor5 = pc.getInventory().getItemEquipped(2, 5);// 手套
            if (check_item(armor5, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor6 = pc.getInventory().getItemEquipped(2, 6);// 靴子
            if (check_item(armor6, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor7 = pc.getInventory().getItemEquipped(2, 7);// 盾
            if (check_item(armor7, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor13 = pc.getInventory().getItemEquipped(2, 13);// 臂甲
            if (check_item(armor13, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
            final L1ItemInstance armor16 = pc.getInventory().getItemEquipped(2, 16);// 臂甲
            if (check_item(armor16, ch)) {
                is += 1;
                if (is == length) {
                    return true;
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    // 裝備檢查
    private boolean check_item(final L1ItemInstance item, int[] ch) {
        try {
            if (item == null) {
                return false;
            }
            if (item.get_power_name() != null) {
                for (int i = 0; i < ch.length; i++) {
                    if (item.get_power_name().get_power_id() == ch[i]) {
                        ch[i] = -1;
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 套裝達成 設置能力
     */
    public void add_pc_power(final L1PcInstance pc) {
        try {
            if (_ac != 0) {
                pc.addAc(_ac);
            }
            if (_hp != 0) {
                pc.addMaxHp(_hp);
            }
            if (_mp != 0) {
                pc.addMaxMp(_mp);
            }
            if (_hpr != 0) {
                pc.addHpr(_hpr);
            }
            if (_mpr != 0) {
                pc.addMpr(_mpr);
            }
            if (_mr != 0) {
                pc.addMr(_mr);
                // 更改人物魔法攻擊與魔法防禦
                pc.sendPackets(new S_SPMR(pc));
            }
            if (_Str != 0) {
                pc.addStr(_Str);
            }
            if (_Con != 0) {
                pc.addCon(_Con);
            }
            if (_Dex != 0) {
                pc.addDex(_Dex);
            }
            if (_Cha != 0) {
                pc.addCha(_Cha);
            }
            if (_Int != 0) {
                pc.addInt(_Int);
            }
            if (_Wis != 0) {
                pc.addWis(_Wis);
            }
            if (_sp != 0) {
                pc.addSp(_sp);
                // 更改人物魔法攻擊與魔法防禦
                pc.sendPackets(new S_SPMR(pc));
            }
            if (_hit != 0) {
                pc.addHitup(_hit);
            }
            if (_dmgup != 0) {
                pc.addDmgup(_dmgup);
            }
            if (_bowhit != 0) {
                pc.addBowHitup(_bowhit);
            }
            if (_bowdmgup != 0) {
                pc.addBowDmgup(_bowdmgup);
            }
            if (_dice_dmg != 0 && _dmg != 0) {// 機率給予爆擊 / 機率給予爆擊質
                pc.set_dmgAdd(_dice_dmg, _dmg);
            }
            if (_dodge != 0) {// 迴避攻擊
                pc.set_evasion(_dodge);
            }
            if (_dice_hp != 0 && _sucking_hp != 0) {// 機率-吸血 / 機率-吸血質
                pc.add_dice_hp(_dice_hp, _sucking_hp);
            }
            if (_dice_mp != 0 && _sucking_mp != 0) {// 機率-吸魔 / 機率-吸魔質
                pc.add_dice_mp(_dice_mp, _sucking_mp);
            }
            if (_double_dmg != 0) {// 機率發動加倍的攻擊力
                pc.add_double_dmg(_double_dmg);
            }
            if (_lift != 0) {// 機率可以將對方的武防裝備解除
                pc.add_lift(_lift);
            }
            if (_defense_water != 0) {
                pc.addWater(_defense_wind);
            }
            if (_defense_wind != 0) {
                pc.addWind(_defense_wind);
            }
            if (_defense_fire != 0) {
                pc.addFire(_defense_fire);
            }
            if (_defense_earth != 0) {
                pc.addEarth(_defense_earth);
            }
            if (_regist_stun != 0) {
                pc.addRegistStun(_regist_stun);
            }
            if (_regist_stone != 0) {
                pc.addRegistStone(_regist_stone);
            }
            if (_regist_sleep != 0) {
                pc.addRegistSleep(_regist_sleep);
            }
            if (_regist_freeze != 0) {
                pc.add_regist_freeze(_regist_freeze);
            }
            if (_regist_sustain != 0) {
                pc.addRegistSustain(_regist_sustain);
            }
            if (_regist_blind != 0) {
                pc.addRegistBlind(_regist_blind);
            }
            if (_weaponMD != 0) {
                pc.addweaponMD(_weaponMD);
            }
            if (_weaponMDC != 0) {
                pc.addweaponMDC(_weaponMDC);
            }
            if (_reducedmg != 0) {
                pc.addreducedmg(_reducedmg);
            }
            if (_reduceMdmg != 0) {
                pc.addreduceMdmg(_reduceMdmg);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void remove_pc_power(final L1PcInstance pc) {
        try {
            if (_ac != 0) {
                pc.addAc(-_ac);
            }
            if (_hp != 0) {
                pc.addMaxHp(-_hp);
            }
            if (_mp != 0) {
                pc.addMaxMp(-_mp);
            }
            if (_hpr != 0) {
                pc.addHpr(-_hpr);
            }
            if (_mpr != 0) {
                pc.addMpr(-_mpr);
            }
            if (_mr != 0) {
                pc.addMr(-_mr);
                // 更改人物魔法攻擊與魔法防禦
                pc.sendPackets(new S_SPMR(pc));
            }
            if (_Str != 0) {
                pc.addStr(-_Str);
            }
            if (_Con != 0) {
                pc.addCon(-_Con);
            }
            if (_Dex != 0) {
                pc.addDex(-_Dex);
            }
            if (_Cha != 0) {
                pc.addCha(-_Cha);
            }
            if (_Int != 0) {
                pc.addInt(-_Int);
            }
            if (_Wis != 0) {
                pc.addWis(-_Wis);
            }
            if (_sp != 0) {
                pc.addSp(-_sp);
                // 更改人物魔法攻擊與魔法防禦
                pc.sendPackets(new S_SPMR(pc));
            }
            if (_hit != 0) {
                pc.addHitup(-_hit);
            }
            if (_dmgup != 0) {
                pc.addDmgup(-_dmgup);
            }
            if (_bowhit != 0) {
                pc.addBowHitup(-_bowhit);
            }
            if (_bowdmgup != 0) {
                pc.addBowDmgup(-_bowdmgup);
            }
            if (_dice_dmg != 0 && _dmg != 0) {// 機率給予爆擊 / 機率給予爆擊質
                pc.set_dmgAdd(-_dice_dmg, -_dmg);
            }
            if (_dodge != 0) {// 迴避攻擊
                pc.set_evasion(-_dodge);
            }
            if (_dice_hp != 0 && _sucking_hp != 0) {// 機率-吸血 / 機率-吸血質
                pc.add_dice_hp(-_dice_hp, -_sucking_hp);
            }
            if (_dice_mp != 0 && _sucking_mp != 0) {// 機率-吸魔 / 機率-吸魔質
                pc.add_dice_mp(-_dice_mp, -_sucking_mp);
            }
            if (_double_dmg != 0) {// 機率發動加倍的攻擊力
                pc.add_double_dmg(-_double_dmg);
            }
            if (_lift != 0) {// 機率可以將對方的武防裝備解除
                pc.add_lift(-_lift);
            }
            if (_defense_water != 0) {
                pc.addWater(-_defense_wind);
            }
            if (_defense_wind != 0) {
                pc.addWind(-_defense_wind);
            }
            if (_defense_fire != 0) {
                pc.addFire(-_defense_fire);
            }
            if (_defense_earth != 0) {
                pc.addEarth(-_defense_earth);
            }
            if (_regist_stun != 0) {
                pc.addRegistStun(-_regist_stun);
            }
            if (_regist_stone != 0) {
                pc.addRegistStone(-_regist_stone);
            }
            if (_regist_sleep != 0) {
                pc.addRegistSleep(-_regist_sleep);
            }
            if (_regist_freeze != 0) {
                pc.add_regist_freeze(-_regist_freeze);
            }
            if (_regist_sustain != 0) {
                pc.addRegistSustain(-_regist_sustain);
            }
            if (_regist_blind != 0) {
                pc.addRegistBlind(-_regist_blind);
            }
            if (_weaponMD != 0) {
                pc.addweaponMD(-_weaponMD);
            }
            if (_weaponMDC != 0) {
                pc.addweaponMDC(-_weaponMDC);
            }
            if (_reducedmg != 0) {
                pc.addreducedmg(-_reducedmg);
            }
            if (_reduceMdmg != 0) {
                pc.addreduceMdmg(-_reduceMdmg);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int[] getPower_id() {
        return _power_ids;
    }

    public void setPower_id(int[] out) {
        this._power_ids = out;
    }

    public int getAc() {
        return _ac;
    }

    public void setAc(int ac) {
        this._ac = ac;
    }

    public int getHp() {
        return _hp;
    }

    public void setHp(int hp) {
        this._hp = hp;
    }

    public int getMp() {
        return _mp;
    }

    public void setMp(int mp) {
        this._mp = mp;
    }

    public int getHpr() {
        return _hpr;
    }

    public void setHpr(int hpr) {
        this._hpr = hpr;
    }

    public int getMpr() {
        return _mpr;
    }

    public void setMpr(int mpr) {
        this._mpr = mpr;
    }

    public int getStr() {
        return _Str;
    }

    public void setStr(int Str) {
        this._Str = Str;
    }

    public int getCon() {
        return _Con;
    }

    public void setCon(int Con) {
        this._Con = Con;
    }

    public int getDex() {
        return _Dex;
    }

    public void setDex(int Dex) {
        this._Dex = Dex;
    }

    public int getCha() {
        return _Cha;
    }

    public void setCha(int Cha) {
        this._Cha = Cha;
    }

    public int getInt() {
        return _Int;
    }

    public void setInt(int Int) {
        this._Int = Int;
    }

    public int getWis() {
        return _Wis;
    }

    public void setWis(int Wis) {
        this._Wis = Wis;
    }

    public int getMr() {
        return _mr;
    }

    public void setMr(int mr) {
        this._mr = mr;
    }

    public int getSp() {
        return _sp;
    }

    public void setSp(int sp) {
        this._sp = sp;
    }

    public int getHit() {
        return _hit;
    }

    public void setHit(int hit) {
        this._hit = hit;
    }

    public int getDmgup() {
        return _dmgup;
    }

    public void setDmgup(int dmgup) {
        this._dmgup = dmgup;
    }

    public int getBowhit() {
        return _bowhit;
    }

    public void setBowhit(int bowhit) {
        this._bowhit = bowhit;
    }

    public int getBowdmgup() {
        return _bowdmgup;
    }

    public void setBowdmgup(int bowdmgup) {
        this._bowdmgup = bowdmgup;
    }

    public int getDice_dmg() {
        return _dice_dmg;
    }

    public void setDice_dmg(int dice_dmg) {
        this._dice_dmg = dice_dmg;
    }

    public int getDmg() {
        return _dmg;
    }

    public void setDmg(int dmg) {
        this._dmg = dmg;
    }

    public int getDodge() {
        return _dodge;
    }

    public void setDodge(int dodge) {
        this._dodge = dodge;
    }

    public int getDice_hp() {
        return _dice_hp;
    }

    public void setDice_hp(int dice_hp) {
        this._dice_hp = dice_hp;
    }

    public int getSucking_hp() {
        return _sucking_hp;
    }

    public void setSucking_hp(int sucking_hp) {
        this._sucking_hp = sucking_hp;
    }

    public int getDice_mp() {
        return _dice_mp;
    }

    public void setDice_mp(int dice_mp) {
        this._dice_mp = dice_mp;
    }

    public int getSucking_mp() {
        return _sucking_mp;
    }

    public void setSucking_mp(int sucking_mp) {
        this._sucking_mp = sucking_mp;
    }

    public int getDouble_dmg() {
        return _double_dmg;
    }

    public void setDouble_dmg(int double_dmg) {
        this._double_dmg = double_dmg;
    }

    public int getLift() {
        return _lift;
    }

    public void setLift(int lift) {
        this._lift = lift;
    }

    public int getDefense_water() {
        return _defense_water;
    }

    public void setDefense_water(int defense_water) {
        this._defense_water = defense_water;
    }

    public int getDefense_wind() {
        return _defense_wind;
    }

    public void setDefense_wind(int defense_wind) {
        this._defense_wind = defense_wind;
    }

    public int getDefense_fire() {
        return _defense_fire;
    }

    public void setDefense_fire(int defense_fire) {
        this._defense_fire = defense_fire;
    }

    public int getDefense_earth() {
        return _defense_earth;
    }

    public void setDefense_earth(int defense_earth) {
        this._defense_earth = defense_earth;
    }

    public int getRegist_stun() {
        return _regist_stun;
    }

    public void setRegist_stun(int regist_stun) {
        this._regist_stun = regist_stun;
    }

    public int getRegist_stone() {
        return _regist_stone;
    }

    public void setRegist_stone(int regist_stone) {
        this._regist_stone = regist_stone;
    }

    public int getRegist_sleep() {
        return _regist_sleep;
    }

    public void setRegist_sleep(int regist_sleep) {
        this._regist_sleep = regist_sleep;
    }

    public int getRegist_freeze() {
        return _regist_freeze;
    }

    public void setRegist_freeze(int regist_freeze) {
        this._regist_freeze = regist_freeze;
    }

    public int getRegist_sustain() {
        return _regist_sustain;
    }

    public void setRegist_sustain(int regist_sustain) {
        this._regist_sustain = regist_sustain;
    }

    public int getRegist_blind() {
        return _regist_blind;
    }

    public void setRegist_blind(int regist_blind) {
        this._regist_blind = regist_blind;
    }

    public int getweaponMD() {
        return _weaponMD;
    }

    public void setweaponMD(int weaponMD) {
        this._weaponMD = weaponMD;
    }

    public int getweaponMDC() {
        return _weaponMDC;
    }

    public void setweaponMDC(int weaponMDC) {
        this._weaponMDC = weaponMDC;
    }

    public int getreducedmg() {
        return _reducedmg;
    }

    public void setreducedmg(int reducedmg) {
        this._reducedmg = reducedmg;
    }

    public int gereduceMdmg() {
        return _weaponMDC;
    }

    public void setreduceMdmg(int reduceMdmg) {
        this._reduceMdmg = reduceMdmg;
    }

    public int[] getGfx() {
        return _gfx;
    }

    public void setGfx(int[] out) {
        this._gfx = out;
    }

    public String getMsg() {
        return _msg;
    }

    public void setMsg(String msg) {
        this._msg = msg;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
