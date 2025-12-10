package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * skills_無法檔魔屏 資料讀取與查詢
 * 設定魔法屏障不可抵擋的魔法
 */
public class SkillsNoCounterMagicTable {
    private static final Log _log = LogFactory.getLog(SkillsNoCounterMagicTable.class);
    private static SkillsNoCounterMagicTable _instance;
    private volatile Set<Integer> _skillIds = Collections.emptySet();

    public static SkillsNoCounterMagicTable get() {
        if (_instance == null) {
            _instance = new SkillsNoCounterMagicTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Set<Integer> newIds = new HashSet<>();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `skills_無法檔魔屏`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int skillId = rs.getInt("技能編號");
                newIds.add(skillId);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _skillIds = newIds;
        _log.info("讀取->[skills_無法檔魔屏] 總筆數: " + _skillIds.size() + " (" + timer.get() + "ms)");
    }

    public boolean isNoCounterMagic(int skillId) {
        return _skillIds.contains(skillId);
    }
}
