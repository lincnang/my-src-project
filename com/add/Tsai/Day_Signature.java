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
public class Day_Signature {
    private static final Log _log = LogFactory.getLog(Day_Signature.class);
    private static final Map<Integer, Day_Signature> _DaySignature = Maps.newHashMap();
    private static Day_Signature _instance;
    private int _id;
    private int _day;
    private String _msg;
    private String _item;
    private String _itemenchant;
    private String _itemcount;
    private int _makeup;
    private int _makeupcount;

    public static Day_Signature get() {
        if (_instance == null) {
            _instance = new Day_Signature();
        }
        return _instance;
    }

    public int getId() {
        return _id;
    }

    public int getDay() {
        return _day;
    }

    private void setDay(int day) {
        _day = day;
    }

    public String getMsg() {
        return _msg;
    }

    private void setMsg(String s) {
        _msg = s;
    }

    public String getItem() {
        return _item;
    }

    private void setItem(String s) {
        _item = s;
    }

    public String getEnchant() {
        return _itemenchant;
    }

    private void setEnchant(String s) {
        _itemenchant = s;
    }

    public String getCount() {
        return _itemcount;
    }

    private void setCount(String s) {
        _itemcount = s;
    }

    public int getMakeUp() {
        return _makeup;
    }

    private void setMakeUp(int i) {
        _makeup = i;
    }

    public int getMakeUpC() {
        return _makeupcount;
    }

    private void setMakeUpC(int i) {
        _makeupcount = i;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `每日簽到獎勵系統_舊`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final Day_Signature Day = new Day_Signature();
                Day.setDay(rs.getInt("日期"));
                Day.setMsg(rs.getString("顯示文字"));
                Day.setItem(rs.getString("領取道具編號"));
                Day.setEnchant(rs.getString("領取道具加成"));
                Day.setCount(rs.getString("領取道具數量"));
                Day.setMakeUp(rs.getInt("補簽道具編號"));
                Day.setMakeUpC(rs.getInt("補簽道具數量"));
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
        _log.info("讀取->每日簽到獎勵系統: " + i + "(" + timer.get() + "ms)");
    }

    public int DaySize() {
        return _DaySignature.size();
    }

    public Day_Signature getDay(final int id) {
        return _DaySignature.get(id);
    }
}