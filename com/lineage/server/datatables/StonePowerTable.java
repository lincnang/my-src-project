package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1StonePower;
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

/**
 * 類名稱：凹槽石頭種類<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public class StonePowerTable {  //src039
    private static final Log _log = LogFactory.getLog(StonePowerTable.class);
    private static final Map<Integer, L1StonePower> _stoneList = new LinkedHashMap<Integer, L1StonePower>();
    private static StonePowerTable _instance;

    public static StonePowerTable getInstance() {
        if (_instance == null) {
            _instance = new StonePowerTable();
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
            pstm = conn.prepareStatement("SELECT * FROM 寶_武防打洞系統 ORDER BY 道具編號");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final String note = rs.getString("顯示名稱");
                final int id = rs.getInt("道具編號");
                final int ac = rs.getInt("防禦力");
                final int hp = rs.getInt("血量");
                final int mp = rs.getInt("魔量");
                final int hpr = rs.getInt("回血量");
                final int mpr = rs.getInt("回魔量");
                final int str = rs.getInt("力量");
                final int con = rs.getInt("體質");
                final int dex = rs.getInt("敏捷");
                final int wis = rs.getInt("精神");
                final int cha = rs.getInt("魅力");
                final int intel = rs.getInt("智慧");
                final int sp = rs.getInt("魔攻");
                final int mr = rs.getInt("魔防");
                final int hit_modifier = rs.getInt("近戰攻擊命中");
                final int dmg_modifier = rs.getInt("近戰攻擊傷害");
                final int bow_hit_modifier = rs.getInt("遠距攻擊命中");
                final int bow_dmg_modifier = rs.getInt("遠距攻擊傷害");
                final int magic_dmg_modifier = rs.getInt("額外魔法傷害");
                final int magic_dmg_reduction = rs.getInt("魔法傷害減免");
                final int reduction_dmg = rs.getInt("全傷害減免");
                final int defense_water = rs.getInt("水屬性");
                final int defense_wind = rs.getInt("風屬性");
                final int defense_fire = rs.getInt("火屬性");
                final int defense_earth = rs.getInt("地屬性");
                final int regist_stun = rs.getInt("昏迷耐性");
                final int regist_stone = rs.getInt("石化耐性");
                final int regist_sleep = rs.getInt("睡眠耐性");
                final int regist_freeze = rs.getInt("寒冰耐性");
                final int regist_sustain = rs.getInt("支撑耐性");
                final int regist_blind = rs.getInt("暗黑耐性");
                final L1StonePower l1StonePower = new L1StonePower(note, id, ac, hp, mp, hpr, mpr, str, con, dex, wis, cha, intel, sp, mr, hit_modifier, dmg_modifier, bow_hit_modifier, bow_dmg_modifier, magic_dmg_modifier, magic_dmg_reduction, reduction_dmg, defense_water, defense_wind, defense_fire, defense_earth, regist_stun, regist_stone, regist_sleep, regist_freeze, regist_sustain, regist_blind);
                _stoneList.put(id, l1StonePower);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->[寶]_武防打洞系統: : " + _stoneList.size() + "(" + timer.get() + "ms)");
    }

    public final L1StonePower get(final int id) {
        return _stoneList.get(id);
    }
}
