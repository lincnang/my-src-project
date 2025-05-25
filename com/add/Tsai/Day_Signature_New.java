package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 簽到獎勵
 *
 * @author 老爹
 */
public class Day_Signature_New {
    private static final Log _log = LogFactory.getLog(Day_Signature_New.class);
    private static final Map<Integer, Day_Signature_New> _DaySignature = Maps.newHashMap();
    private static Day_Signature_New _instance;
    private int _id;
    private int _itemId;
    private int _itemCount;

    public static Day_Signature_New get() {
        if (_instance == null) {
            _instance = new Day_Signature_New();
        }
        return _instance;
    }

    public int getId() {
        return _id;
    }

    private void setId(int i) {
        _id = i;
    }

    public int get_itemId() {
        return _itemId;
    }

    private void set_itemId(int i) {
        _itemId = i;
    }

    public int get_itemCount() {
        return _itemCount;
    }

    private void set_itemCount(int i) {
        _itemCount = i;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_新簽到獎勵`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("id");
                final Day_Signature_New Day = new Day_Signature_New();
                Day.set_itemId(rs.getInt("itemid"));
                Day.set_itemCount(rs.getInt("itemcount"));
                _DaySignature.put(id, Day);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->每日簽到獎勵系統(新): " + i + "(" + timer.get() + "ms)");
    }

    public int DaySize() {
        return _DaySignature.size();
    }

    public Day_Signature_New getDay(final int id) {
        return _DaySignature.get(id);
    }
}