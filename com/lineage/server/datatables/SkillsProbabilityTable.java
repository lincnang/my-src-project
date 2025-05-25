package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 負面技能幾率設置DB化
 */
public class SkillsProbabilityTable {
    private static final Log _log = LogFactory.getLog(SkillsProbabilityTable.class);
    private static SkillsProbabilityTable _instance;
    private final Map<Integer, L1Skills> _skills = new HashMap<Integer, L1Skills>();

    private SkillsProbabilityTable() {
        load();
    }

    public static SkillsProbabilityTable get() {
        if (_instance == null) {
            _instance = new SkillsProbabilityTable();
        }
        return _instance;
    }

    public static void reload() {
        SkillsProbabilityTable oldInstance = _instance;
        _instance = new SkillsProbabilityTable();
        oldInstance._skills.clear();
        oldInstance = null;
    }

    private void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `功能_負面技能機率`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int skill_id = rs.getInt("技能編號");
                final L1Skills l1skills = SkillsTable.get().getTemplate(skill_id);
                if (l1skills == null) {
                    _log.error("寶_負面技能幾率DB化設置資料 不存在技能編號: " + skill_id);
                }
                l1skills.setSkillId(skill_id);
                l1skills.setName(rs.getString("技能名稱"));
                l1skills.setProbability_Lv1(rs.getInt("小於被攻擊者等級時幾率"));
                l1skills.setProbability_Lv2(rs.getInt("等於被攻擊者等級時幾率"));
                l1skills.setProbability_Lv3(rs.getInt("高於被攻擊者等級時幾率"));
                l1skills.setProbability_Mr(rs.getInt("被攻擊者魔防扣幾率百分比"));
                l1skills.set_intel_add_probability(rs.getInt("自身智力增加幾率百分比"));
                l1skills.set_intel_add_probability_max(rs.getInt("自身智力增加幾率上限"));
                _skills.put(new Integer(skill_id), l1skills);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->寶_負面技能幾率DB化設置資料數量: " + this._skills.size() + "(" + timer.get() + "ms)");
    }

    public L1Skills getTemplate(final int skill_id) {
        if (_skills.containsKey(skill_id)) {
            return _skills.get(skill_id);
        }
        return null;
    }
}
