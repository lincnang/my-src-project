package com.lineage.server.templates;

public class L1PolyPower // src014
{
    private final int _polyId;
    private final int _ac;
    private final int _hp;
    private final int _mp;
    private final int _hpr;
    private final int _mpr;
    private final int _str;
    private final int _con;
    private final int _dex;
    private final int _wis;
    private final int _cha;
    private final int _int;
    private final int _sp;
    private final int _mr;
    private final int _hit_modifier;
    private final int _dmg_modifier;
    private final int _bow_hit_modifier;
    private final int _bow_dmg_modifier;
    private final int _magic_dmg_modifier;
    private final int _magic_dmg_reduction;
    private final int _reduction_dmg;
    private final int _defense_water;
    private final int _defense_wind;
    private final int _defense_fire;
    private final int _defense_earth;
    private final int _regist_stun;
    private final int _regist_stone;
    private final int _regist_sleep;
    private final int _regist_freeze;
    private final int _regist_sustain;
    private final int _regist_blind;
    private final int _regist_fear; // 恐怖耐性
    private int _addexp;
    private int _add_potion;
    private int _add_PVP;
    private int _add_PVP_R;
    private int _add_magic_hit;
    private int _add_StunLevel;
    private int _addCloseCritical;
    private int _addBowCritical;
    private int _skinId;
    private int _ignoreReduction; // 無視傷害減免

    public L1PolyPower(int polyId, int ac, int hp, int mp, int hpr, int mpr, int str, int con, int dex, int wis, int cha, int intel, int sp, int mr, int hit_modifier, int dmg_modifier, int bow_hit_modifier, int bow_dmg_modifier, int magic_dmg_modifier, int magic_dmg_reduction, int reduction_dmg, int defense_water, int defense_wind, int defense_fire, int defense_earth, int regist_stun, int regist_stone, int regist_sleep, int regist_freeze, int regist_sustain, int regist_blind, int regist_fear, int Exp, int potion, int PVP, int PVP_R, int magic_hit, int StunLv, int CloseCritical, int BowCritical, int skinId, int ignoreReduction) {
        this._polyId = polyId;
        this._ac = ac;
        this._hp = hp;
        this._mp = mp;
        this._hpr = hpr;
        this._mpr = mpr;
        this._str = str;
        this._con = con;
        this._dex = dex;
        this._wis = wis;
        this._cha = cha;
        this._int = intel;
        this._sp = sp;
        this._mr = mr;
        this._hit_modifier = hit_modifier;
        this._dmg_modifier = dmg_modifier;
        this._bow_hit_modifier = bow_hit_modifier;
        this._bow_dmg_modifier = bow_dmg_modifier;
        this._magic_dmg_modifier = magic_dmg_modifier;
        this._magic_dmg_reduction = magic_dmg_reduction;
        this._reduction_dmg = reduction_dmg;
        this._defense_water = defense_water;
        this._defense_wind = defense_wind;
        this._defense_fire = defense_fire;
        this._defense_earth = defense_earth;
        this._regist_stun = regist_stun;
        this._regist_stone = regist_stone;
        this._regist_sleep = regist_sleep;
        this._regist_freeze = regist_freeze;
        this._regist_sustain = regist_sustain;
        this._regist_blind = regist_blind;
        this._regist_fear = regist_fear;
        this._addexp = Exp;
        this._add_potion = potion;
        this._add_PVP = PVP;
        this._add_PVP_R = PVP_R;
        this._add_magic_hit = magic_hit;
        this._add_StunLevel = StunLv;
        this._addCloseCritical = CloseCritical;
        this._addBowCritical = BowCritical;
        this._skinId = skinId;
        this._ignoreReduction = ignoreReduction;
    }

    public final int getPolyId() {
        return this._polyId;
    }

    public final int getAc() {
        return this._ac;
    }

    public final int getHp() {
        return this._hp;
    }

    public final int getMp() {
        return this._mp;
    }

    public final int getHpr() {
        return this._hpr;
    }

    public final int getMpr() {
        return this._mpr;
    }

    public final int getStr() {
        return this._str;
    }

    public final int getCon() {
        return this._con;
    }

    public final int getDex() {
        return this._dex;
    }

    public final int getWis() {
        return this._wis;
    }

    public final int getCha() {
        return this._cha;
    }

    public final int getInt() {
        return this._int;
    }

    public final int getSp() {
        return this._sp;
    }

    public final int getMr() {
        return this._mr;
    }

    public final int getHitModifier() {
        return this._hit_modifier;
    }

    public final int getDmgModifier() {
        return this._dmg_modifier;
    }

    public final int getBowHitModifier() {
        return this._bow_hit_modifier;
    }

    public final int getBowDmgModifier() {
        return this._bow_dmg_modifier;
    }

    public final int getMagicDmgModifier() {
        return this._magic_dmg_modifier;
    }

    public final int getMagicDmgReduction() {
        return this._magic_dmg_reduction;
    }

    public final int getReductionDmg() {
        return this._reduction_dmg;
    }

    public final int getDefenseWater() {
        return this._defense_water;
    }

    public final int getDefenseWind() {
        return this._defense_wind;
    }

    public final int getDefenseFire() {
        return this._defense_fire;
    }

    public final int getDefenseEarth() {
        return this._defense_earth;
    }

    public final int getRegistStun() {
        return this._regist_stun;
    }

    public final int getRegistStone() {
        return this._regist_stone;
    }

    public final int getRegistSleep() {
        return this._regist_sleep;
    }

    public final int getRegistFreeze() {
        return this._regist_freeze;
    }

    public final int getRegistSustain() {
        return this._regist_sustain;
    }

    public final int getRegistBlind() {
        return this._regist_blind;
    }

    public final int getRegistFear() {
        return this._regist_fear;
    }

    /**
     * 經驗
     *
     */
    public int getExpPoint() {
        return _addexp;
    }

    /**
     * 增加藥水回復量%
     *
     */
    public int get_up_hp_potion() {
        // 假設有相應的屬性
        return _add_potion;
    }


    /**
     * 增加PVP攻擊
     *
     */
    public int getPvpDmg() {
        // 假設有相應的屬性
        return _add_PVP;
    }

    /**
     * 增加PVP減免
     *
     */
    public int getPvpDmg_R() {
        // 假設有相應的屬性
        return _add_PVP_R;
    }

    /**
     * 添加魔法命中
     */
    public int getOriginalMagicHit() {
        // 假設有相應的屬性
        return _add_magic_hit;
    }

    /**
     * 添加昏迷命中
     */
    public int getStunLevel() {
        return _add_StunLevel;
    }

    /**
     * 添加近距離爆擊
     */
    public int getCloseCritical() {
        return _addCloseCritical;
    }

    public void addCloseCritical(final int i) {
        _addCloseCritical += i;
    }


    /**
     * 添加-遠距離-爆擊
     */

    public int getBowCritical() {
        return _addCloseCritical;
    }

    public void addBowCritical(final int i) {
        _addCloseCritical += i;
    }

    public int getSkinId() {
        return _skinId;
    }

    /**
     * 無視傷害減免
     */
    public int getIgnoreReduction() {
        return _ignoreReduction;
    }


    /**
     * 添加多型效果
     */
    //     public static void addPolyEffects(L1PcInstance pc, int gfxId) {
    //         L1PolyPower polyPower = ExtraPolyPowerTable.getInstance().get(gfxId);
    //
    //         if (polyPower == null)
    //             return;
    //
    //         if (pc.getloginpoly() == 1) {
    //             pc.sendPackets(new S_ServerMessage("\\aD[此變身能力獲得以下能力]"));
    //
    //             if (polyPower.getStr() != 0) {
    //                 pc.addStr(polyPower.getStr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE力量+" + polyPower.getStr()));
    //             }
    //             if (polyPower.getDex() != 0) {
    //                 pc.addDex(polyPower.getDex());
    //                 pc.sendPackets(new S_ServerMessage("\\aE敏捷+" + polyPower.getDex()));
    //             }
    //             if (polyPower.getCon() != 0) {
    //                 pc.addCon(polyPower.getCon());
    //                 pc.sendPackets(new S_ServerMessage("\\aE體質+" + polyPower.getCon()));
    //             }
    //             if (polyPower.getInt() != 0) {
    //                 pc.addInt(polyPower.getInt());
    //                 pc.sendPackets(new S_ServerMessage("\\aE智力+" + polyPower.getInt()));
    //             }
    //             if (polyPower.getWis() != 0) {
    //                 pc.addWis(polyPower.getWis());
    //                 pc.sendPackets(new S_ServerMessage("\\aE精神+" + polyPower.getWis()));
    //             }
    //             if (polyPower.getCha() != 0) {
    //                 pc.addCha(polyPower.getCha());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魅力+" + polyPower.getCha()));
    //             }
    //             if (polyPower.getAc() != 0) {
    //                 pc.addAc(-polyPower.getAc());
    //                 pc.sendPackets(new S_ServerMessage("\\aE防禦+" + polyPower.getAc()));
    //             }
    //             if (polyPower.getHp() != 0) {
    //                 pc.addMaxHp(polyPower.getHp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE血量增加+" + polyPower.getHp()));
    //                 pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
    //             }
    //             if (polyPower.getMp() != 0) {
    //                 pc.addMaxMp(polyPower.getMp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔量增加+" + polyPower.getMp()));
    //                 pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    //             }
    //             if (polyPower.getHpr() != 0) {
    //                 pc.addHpr(polyPower.getHpr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE回血+" + polyPower.getHpr()));
    //                 pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
    //             }
    //             if (polyPower.getMpr() != 0) {
    //                 pc.addMpr(polyPower.getMpr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE回魔+" + polyPower.getMpr()));
    //                 pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    //             }
    //             if (polyPower.getDmgModifier() != 0) {
    //                 pc.addDmgup(polyPower.getDmgModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE近戰傷害+" + polyPower.getDmgModifier()));
    //             }
    //             if (polyPower.getHitModifier() != 0) {
    //                 pc.addHitup(polyPower.getHitModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE近戰命中+" + polyPower.getHitModifier()));
    //             }
    //             if (polyPower.getBowDmgModifier() != 0) {
    //                 pc.addBowDmgup(polyPower.getBowDmgModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE遠戰傷害+" + polyPower.getBowDmgModifier()));
    //             }
    //             if (polyPower.getBowHitModifier() != 0) {
    //                 pc.addBowHitup(polyPower.getBowHitModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE遠戰命中+" + polyPower.getBowHitModifier()));
    //             }
    // //            if (polyPower.getReductionDmg() != 0) {
    // //                pc.addother_ReductionDmg(polyPower.getReductionDmg());
    // //                pc.sendPackets(new S_ServerMessage("\\aE減免物理傷害+" + polyPower.getReductionDmg()));
    // //            }
    //             if (polyPower.getMr() != 0) {
    //                 pc.addMr(polyPower.getMr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗魔+" + polyPower.getMr()));
    //             }
    //             if (polyPower.getSp() != 0) {
    //                 pc.addSp(polyPower.getSp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔攻+" + polyPower.getSp()));
    //             }
    //             if (polyPower.getDefenseFire() != 0) {
    //                 pc.addFire(polyPower.getDefenseFire());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗火屬性+" + polyPower.getDefenseFire()));
    //             }
    //             if (polyPower.getDefenseWind() != 0) {
    //                 pc.addWind(polyPower.getDefenseWind());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗風屬性+" + polyPower.getDefenseWind()));
    //             }
    //             if (polyPower.getDefenseEarth() != 0) {
    //                 pc.addEarth(polyPower.getDefenseEarth());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗地屬性+" + polyPower.getDefenseEarth()));
    //             }
    //             if (polyPower.getDefenseWater() != 0) {
    //                 pc.addWater(polyPower.getDefenseWater());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗水屬性+" + polyPower.getDefenseWater()));
    //             }
    //             if (polyPower.getAddExp() != 0.0D) {
    //                 pc.addExpByArmor(polyPower.getAddExp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE經驗+" + polyPower.getAddExp() + "%"));
    //             }
    // //            if (polyPower.getPotion_Heal() != 0) {
    // //                pc.add_potion_heal(polyPower.getPotion_Heal());
    // //                pc.sendPackets(new S_ServerMessage("\\aE藥水回復量+" + polyPower.getPotion_Heal() + "%"));
    // //            }
    // //            if (polyPower.getPVPdmg() != 0) {
    // //                pc.add_PVPdmgg(polyPower.getPVPdmg());
    // //                pc.sendPackets(new S_ServerMessage("\\aEPVP傷害+" + polyPower.getPVPdmg()));
    // //            }
    // //            if (polyPower.getPVPdmgReduction() != 0) {
    // //                pc.addPVPdmgReduction(polyPower.getPVPdmgReduction());
    // //                pc.sendPackets(new S_ServerMessage("\\aEPVP減傷+" + polyPower.getPVPdmgReduction()));
    // //            }
    //             if (polyPower.getAddMagicHit() != 0) {
    //                 pc.addOriginalMagicHit(polyPower.getAddMagicHit());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔法命中+" + polyPower.getAddMagicHit()));
    //             }
    //             if (polyPower.getRegistStun() != 0) {
    //                 pc.addRegistStun(polyPower.getRegistStun());
    //                 pc.sendPackets(new S_ServerMessage("\\aE昏迷耐性+" + polyPower.getRegistStun()));
    //             }
    //             if (polyPower.getRegistStone() != 0) {
    //                 pc.addRegistStone(polyPower.getRegistStone());
    //                 pc.sendPackets(new S_ServerMessage("\\aE石化耐性+" + polyPower.getRegistStone()));
    //             }
    //             if (polyPower.getRegistSleep() != 0) {
    //                 pc.addRegistSleep(polyPower.getRegistSleep());
    //                 pc.sendPackets(new S_ServerMessage("\\aE睡眠耐性+" + polyPower.getRegistSleep()));
    //             }
    //             if (polyPower.getRegistFreeze() != 0) {
    //                 pc.add_regist_freeze(polyPower.getRegistFreeze());
    //                 pc.sendPackets(new S_ServerMessage("\\aE冰耐性+" + polyPower.getRegistFreeze()));
    //             }
    //             if (polyPower.getRegistSustain() != 0) {
    //                 pc.addRegistSustain(polyPower.getRegistSustain());
    //                 pc.sendPackets(new S_ServerMessage("\\aE支撐耐性+" + polyPower.getRegistSustain()));
    //             }
    //             if (polyPower.getRegistBlind() != 0) {
    //                 pc.addRegistBlind(polyPower.getRegistBlind());
    //                 pc.sendPackets(new S_ServerMessage("\\aE暗黑耐性+" + polyPower.getRegistBlind()));
    //             }
    //
    //             pc.sendPackets(new S_SPMR(pc));
    //             pc.sendPackets(new S_OwnCharStatus(pc));
    //             pc.sendPackets(new S_OwnCharStatus2(pc));
    //         }
    //     }

    /**
     * 移除多型效果
     */
    //     public static void removePolyEffects(L1PcInstance pc, int gfxId) {
    //         L1PolyPower polyPower = ExtraPolyPowerTable.getInstance().get(gfxId);
    //         if (polyPower == null)
    //             return;
    //
    //         if (pc.getloginpoly() == 1) {
    //             pc.setloginpoly(0);
    //             pc.sendPackets(new S_ServerMessage("\\aD[變身能力消失.恢復正常]"));
    //
    //             if (polyPower.getStr() != 0) {
    //                 pc.addStr(-polyPower.getStr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE力量-" + polyPower.getStr() + "[能力消失了]"));
    //             }
    //             if (polyPower.getDex() != 0) {
    //                 pc.addDex(-polyPower.getDex());
    //                 pc.sendPackets(new S_ServerMessage("\\aE敏捷-" + polyPower.getDex() + "[能力消失了]"));
    //             }
    //             if (polyPower.getCon() != 0) {
    //                 pc.addCon(-polyPower.getCon());
    //                 pc.sendPackets(new S_ServerMessage("\\aE體質-" + polyPower.getCon() + "[能力消失了]"));
    //             }
    //             if (polyPower.getInt() != 0) {
    //                 pc.addInt(-polyPower.getInt());
    //                 pc.sendPackets(new S_ServerMessage("\\aE智力-" + polyPower.getInt() + "[能力消失了]"));
    //             }
    //             if (polyPower.getWis() != 0) {
    //                 pc.addWis(-polyPower.getWis());
    //                 pc.sendPackets(new S_ServerMessage("\\aE精神-" + polyPower.getWis() + "[能力消失了]"));
    //             }
    //             if (polyPower.getCha() != 0) {
    //                 pc.addCha(-polyPower.getCha());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魅力-" + polyPower.getCha() + "[能力消失了]"));
    //             }
    //             if (polyPower.getAc() != 0) {
    //                 pc.addAc(polyPower.getAc());
    //                 pc.sendPackets(new S_ServerMessage("\\aE防禦-" + polyPower.getAc() + "[能力消失了]"));
    //             }
    //             if (polyPower.getHp() != 0) {
    //                 pc.addMaxHp(-polyPower.getHp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE血量減少-" + polyPower.getHp() + "[能力消失了]"));
    //                 pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
    //             }
    //             if (polyPower.getMp() != 0) {
    //                 pc.addMaxMp(-polyPower.getMp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔量減少-" + polyPower.getMp() + "[能力消失了]"));
    //                 pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    //             }
    //             if (polyPower.getHpr() != 0) {
    //                 pc.addHpr(-polyPower.getHpr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE回血-" + polyPower.getHpr() + "[能力消失了]"));
    //                 pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
    //             }
    //             if (polyPower.getMpr() != 0) {
    //                 pc.addMpr(-polyPower.getMpr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE回魔-" + polyPower.getMpr() + "[能力消失了]"));
    //                 pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    //             }
    //             if (polyPower.getDmgModifier() != 0) {
    //                 pc.addDmgup(-polyPower.getDmgModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE近戰傷害-" + polyPower.getDmgModifier() + "[能力消失了]"));
    //             }
    //             if (polyPower.getHitModifier() != 0) {
    //                 pc.addHitup(-polyPower.getHitModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE近戰命中-" + polyPower.getHitModifier() + "[能力消失了]"));
    //             }
    //             if (polyPower.getBowDmgModifier() != 0) {
    //                 pc.addBowDmgup(-polyPower.getBowDmgModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE遠戰傷害-" + polyPower.getBowDmgModifier() + "[能力消失了]"));
    //             }
    //             if (polyPower.getBowHitModifier() != 0) {
    //                 pc.addBowHitup(-polyPower.getBowHitModifier());
    //                 pc.sendPackets(new S_ServerMessage("\\aE遠戰命中-" + polyPower.getBowHitModifier() + "[能力消失了]"));
    //             }
    // //            if (polyPower.getReductionDmg() != 0) {
    // //                pc.addother_ReductionDmg(-polyPower.getReductionDmg());
    // //                pc.sendPackets(new S_ServerMessage("\\aE減免物理傷害-" + polyPower.getReductionDmg() + "[能力消失了]"));
    //             }
    //             if (polyPower.getMr() != 0) {
    //                 pc.addMr(-polyPower.getMr());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗魔-" + polyPower.getMr() + "[能力消失了]"));
    //             }
    //             if (polyPower.getSp() != 0) {
    //                 pc.addSp(-polyPower.getSp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔攻-" + polyPower.getSp() + "[能力消失了]"));
    //             }
    //             if (polyPower.getDefenseFire() != 0) {
    //                 pc.addFire(-polyPower.getDefenseFire());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗火屬性-" + polyPower.getDefenseFire() + "[能力消失了]"));
    //             }
    //             if (polyPower.getDefenseWind() != 0) {
    //                 pc.addWind(-polyPower.getDefenseWind());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗風屬性-" + polyPower.getDefenseWind() + "[能力消失了]"));
    //             }
    //             if (polyPower.getDefenseEarth() != 0) {
    //                 pc.addEarth(-polyPower.getDefenseEarth());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗地屬性-" + polyPower.getDefenseEarth() + "[能力消失了]"));
    //             }
    //             if (polyPower.getDefenseWater() != 0) {
    //                 pc.addWater(-polyPower.getDefenseWater());
    //                 pc.sendPackets(new S_ServerMessage("\\aE抗水屬性-" + polyPower.getDefenseWater() + "[能力消失了]"));
    //             }
    //             if (polyPower.getAddExp() != 0.0D) {
    //                 pc.addExpByArmor(-polyPower.getAddExp());
    //                 pc.sendPackets(new S_ServerMessage("\\aE經驗-" + polyPower.getAddExp() + "%.[能力消失了]"));
    //             }
    // //            if (polyPower.getPotion_Heal() != 0) {
    // //                pc.add_potion_heal(-polyPower.getPotion_Heal());
    // //                pc.sendPackets(new S_ServerMessage("\\aE藥水回復量-" + polyPower.getPotion_Heal() + "%.[能力消失了]"));
    // //            }
    // //            if (polyPower.getPVPdmg() != 0) {
    // //                pc.add_PVPdmgg(-polyPower.getPVPdmg());
    // //                pc.sendPackets(new S_ServerMessage("\\aEPVP傷害-" + polyPower.getPVPdmg() + "[能力消失了]"));
    // //            }
    // //            if (polyPower.getPVPdmgReduction() != 0) {
    // //                pc.addPVPdmgReduction(-polyPower.getPVPdmgReduction());
    // //                pc.sendPackets(new S_ServerMessage("\\aEPVP減傷-" + polyPower.getPVPdmgReduction() + "[能力消失了]"));
    // //            }
    //             if (polyPower.getAddMagicHit() != 0) {
    //                 pc.addOriginalMagicHit(-polyPower.getAddMagicHit());
    //                 pc.sendPackets(new S_ServerMessage("\\aE魔法命中-" + polyPower.getAddMagicHit() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistStun() != 0) {
    //                 pc.addRegistStun(-polyPower.getRegistStun());
    //                 pc.sendPackets(new S_ServerMessage("\\aE昏迷耐性-" + polyPower.getRegistStun() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistStone() != 0) {
    //                 pc.addRegistStone(-polyPower.getRegistStone());
    //                 pc.sendPackets(new S_ServerMessage("\\aE石化耐性-" + polyPower.getRegistStone() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistSleep() != 0) {
    //                 pc.addRegistSleep(-polyPower.getRegistSleep());
    //                 pc.sendPackets(new S_ServerMessage("\\aE睡眠耐性-" + polyPower.getRegistSleep() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistFreeze() != 0) {
    //                 pc.add_regist_freeze(-polyPower.getRegistFreeze());
    //                 pc.sendPackets(new S_ServerMessage("\\aE冰耐性-" + polyPower.getRegistFreeze() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistSustain() != 0) {
    //                 pc.addRegistSustain(-polyPower.getRegistSustain());
    //                 pc.sendPackets(new S_ServerMessage("\\aE支撐耐性-" + polyPower.getRegistSustain() + "[能力消失了]"));
    //             }
    //             if (polyPower.getRegistBlind() != 0) {
    //                 pc.addRegistBlind(-polyPower.getRegistBlind());
    //                 pc.sendPackets(new S_ServerMessage("\\aE暗黑耐性-" + polyPower.getRegistBlind() + "[能力消失了]"));
    //             }
    //
    //             pc.sendPackets(new S_SPMR(pc));
    //             pc.sendPackets(new S_OwnCharStatus(pc));
    //             pc.sendPackets(new S_OwnCharStatus2(pc));
    //         }
}

