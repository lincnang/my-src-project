package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1SuperRune;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuperRuneTable {
    private static final Log _log = LogFactory.getLog(SuperRuneTable.class);
    private static final Map<Integer, L1SuperRune> _superRuneList = new LinkedHashMap<>();
    private static final List<Integer> _idList = new ArrayList<>();
    private static SuperRuneTable _instance;

    public static SuperRuneTable getInstance() {
        if (_instance == null) {
            _instance = new SuperRuneTable();
        }
        return _instance;
    }

    public final void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 超能符石系統 ORDER BY id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final String note = rs.getString("note");
                final int id = rs.getInt("id");
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
                final L1SuperRune spuerRune = new L1SuperRune(note, id, ac, hp, mp, hpr, mpr, str, con, dex, wis, cha, intel, sp, mr, hit_modifier, dmg_modifier, bow_hit_modifier, bow_dmg_modifier, magic_dmg_modifier, magic_dmg_reduction, reduction_dmg, defense_water, defense_wind, defense_fire, defense_earth, regist_stun, regist_stone, regist_sleep, regist_freeze, regist_sustain, regist_blind);
                _superRuneList.put(id, spuerRune);
                _idList.add(id);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->超能符石系統種類: : " + _superRuneList.size() + "(" + timer.get() + "ms)");
    }

    public final L1SuperRune get(final int id) {
        return _superRuneList.get(id);
    }

    public final List<Integer> getSuperIdList() {
        return _idList;
    }
}
