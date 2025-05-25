package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1SkillEnhance;
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
 * skills_技能強化 資料讀取
 */
public final class SkillEnhanceTable {

    private static final Log _log = LogFactory.getLog(SkillEnhanceTable.class);

    // 結構： skillId -> (bookLevel -> L1SkillEnhance)
    private static final Map<Integer, Map<Integer, L1SkillEnhance>> _enhanceMap = new HashMap<>();

    private static SkillEnhanceTable _instance;

    public static SkillEnhanceTable get() {
        if (_instance == null) {
            _instance = new SkillEnhanceTable();
        }
        return _instance;
    }

    /**
     * 載入資料
     */
    public static void load() {
        _enhanceMap.clear();
        final PerformanceTimer timer = new PerformanceTimer();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;

        try {
            con = DatabaseFactory.get().getConnection();
            // 讀取資料表 skills_技能強化
            pstm = con.prepareStatement("SELECT * FROM `skills_技能強化`");
            rs = pstm.executeQuery();

            while (rs.next()) {
                L1SkillEnhance data = new L1SkillEnhance();
                data.setId(rs.getInt("Id"));
                data.setSkillId(rs.getInt("技能編號"));
                data.setNote(rs.getString("備註說明"));
                data.setBookLevel(rs.getInt("技能吃書等級"));
                data.setSetting1(rs.getInt("設定1"));
                data.setSetting2(rs.getInt("設定2"));
                data.setSetting3(rs.getInt("設定3"));
                data.setSetting4(rs.getDouble("設定4"));
                // 將 skillId 和 bookLevel 存入局部變數
                int skillId = data.getSkillId();
                int bookLevel = data.getBookLevel();

                // 建立多層Map存放
                Map<Integer, L1SkillEnhance> lvMap = _enhanceMap.computeIfAbsent(data.getSkillId(), k -> new HashMap<>());
                lvMap.put(data.getBookLevel(), data);
                count++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        _log.info("讀取->[skills_技能強化] 總筆數: " + count + " (" + timer.get() + "ms)");
    }

    /**
     * 取得指定 skillId、指定等級(bookLevel) 的強化資料
     */
    public L1SkillEnhance getEnhanceData(int skillId, int bookLevel) {
        Map<Integer, L1SkillEnhance> lvMap = _enhanceMap.get(skillId);
        if (lvMap == null) {
            return null;
        }
        return lvMap.get(bookLevel);
    }

    /**
     * (可選) 取得某個 skillId 下所有等級的強化資料
     */
    public Map<Integer, L1SkillEnhance> getEnhanceBySkillId(int skillId) {
        return _enhanceMap.get(skillId);
    }
}
