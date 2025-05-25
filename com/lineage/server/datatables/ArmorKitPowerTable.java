package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ArmorKitPower;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ArmorKitPowerTable {
    private static final Log _log = LogFactory.getLog(ArmorKitPowerTable.class);
    private static final Map<Integer, L1ArmorKitPower> _armorKitPowerList = new LinkedHashMap<>();
    private static ArmorKitPowerTable _instance;

    public static ArmorKitPowerTable getInstance() {
        if (_instance == null) {
            _instance = new ArmorKitPowerTable();
        }
        return _instance;
    }

    public static void effectBuff(final L1PcInstance pc, final L1ArmorKitPower value, final int negative) {
        pc.addAc(value.getAc() * negative);
        pc.addMaxHp(value.getHp() * negative);
        pc.addMaxMp(value.getMp() * negative);
        pc.addHpr(value.getHpr() * negative);
        pc.addMpr(value.getMpr() * negative);
        pc.addStr(value.getStr() * negative);
        pc.addCon(value.getCon() * negative);
        pc.addDex(value.getDex() * negative);
        pc.addWis(value.getWis() * negative);
        pc.addCha(value.getCha() * negative);
        pc.addInt(value.getInt() * negative);
        pc.addSp(value.getSp() * negative);
        pc.addMr(value.getMr() * negative);
        pc.addHitup(value.getHitModifier() * negative);
        pc.addDmgup(value.getDmgModifier() * negative);
        pc.addBowHitup(value.getBowHitModifier() * negative);
        pc.addBowDmgup(value.getBowDmgModifier() * negative);
        pc.addMagicDmgModifier(value.getMagicDmgModifier() * negative);
        pc.addMagicDmgReduction(value.getMagicDmgReduction() * negative);
        pc.addDamageReductionByArmor(value.getReductionDmg() * negative);
        pc.addWater(value.getDefenseWater() * negative);
        pc.addWind(value.getDefenseWind() * negative);
        pc.addFire(value.getDefenseFire() * negative);
        pc.addEarth(value.getDefenseEarth() * negative);
        pc.addRegistStun(value.getRegistStun() * negative);
        pc.addRegistStone(value.getRegistStone() * negative);
        pc.addRegistSleep(value.getRegistSleep() * negative);
        pc.add_regist_freeze(value.getRegistFreeze() * negative);
        pc.addRegistSustain(value.getRegistSustain() * negative);
        pc.addRegistBlind(value.getRegistBlind() * negative);
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 寶_套裝能力資料設置 ORDER BY armor_type");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int armorType = rs.getInt("armor_type");
                final int armorKitCount = rs.getInt("armor_kit_count");
                final int ac = rs.getInt("ac");
                final int hp = rs.getInt("hp");
                final int mp = rs.getInt("mp");
                final int hpr = rs.getInt("hpr");
                final int mpr = rs.getInt("mpr");
                final int str = rs.getInt("str");
                final int con = rs.getInt("con");
                final int dex = rs.getInt("dex");
                final int wis = rs.getInt("wis");
                final int cha = rs.getInt("cha");
                final int intel = rs.getInt("intel");
                final int sp = rs.getInt("sp");
                final int mr = rs.getInt("mr");
                final int hit_modifier = rs.getInt("hit_modifier");
                final int dmg_modifier = rs.getInt("dmg_modifier");
                final int bow_hit_modifier = rs.getInt("bow_hit_modifier");
                final int bow_dmg_modifier = rs.getInt("bow_dmg_modifier");
                final int magic_dmg_modifier = rs.getInt("magic_dmg_modifier");
                final int magic_dmg_reduction = rs.getInt("magic_dmg_reduction");
                final int reduction_dmg = rs.getInt("reduction_dmg");
                final int defense_water = rs.getInt("defense_water");
                final int defense_wind = rs.getInt("defense_wind");
                final int defense_fire = rs.getInt("defense_fire");
                final int defense_earth = rs.getInt("defense_earth");
                final int regist_stun = rs.getInt("regist_stun");
                final int regist_stone = rs.getInt("regist_stone");
                final int regist_sleep = rs.getInt("regist_sleep");
                final int regist_freeze = rs.getInt("regist_freeze");
                final int regist_sustain = rs.getInt("regist_sustain");
                final int regist_blind = rs.getInt("regist_blind");
                final int gift_box = rs.getInt("gift_box");
                final L1ArmorKitPower meteAbility = new L1ArmorKitPower(armorType, armorKitCount, ac, hp, mp, hpr, mpr, str, con, dex, wis, cha, intel, sp, mr, hit_modifier, dmg_modifier, bow_hit_modifier, bow_dmg_modifier, magic_dmg_modifier, magic_dmg_reduction, reduction_dmg, defense_water, defense_wind, defense_fire, defense_earth, regist_stun, regist_stone, regist_sleep, regist_freeze, regist_sustain, regist_blind, gift_box);
                final int index = armorType * 10000 + armorKitCount;
                _armorKitPowerList.put(index, meteAbility);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->[系統]_套裝能力資料設置數量: " + _armorKitPowerList.size() + "(" + timer.get() + "ms)");
    }

    public L1ArmorKitPower get(final int armorType, final int armorKitCount) {
        final int index = armorType * 10000 + armorKitCount;
        final L1ArmorKitPower temp = _armorKitPowerList.get(index);
        if (temp != null) {
            return temp;
        }
        return null;
    }
}
