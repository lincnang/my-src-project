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
import java.util.HashMap;
import java.util.Map;

public class QuestMapTable {
    private static final Log _log = LogFactory.getLog(QuestMapTable.class);
    private static final Map<Integer, Integer> _mapList = new HashMap<Integer, Integer>();
    private static final Map<Integer, Integer> _timeList = new HashMap<Integer, Integer>();
    private static QuestMapTable _instance;

    public static QuestMapTable get() {
        if (_instance == null) {
            _instance = new QuestMapTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_quest_maps`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int mapid = rs.getInt("mapid");
                int time = rs.getInt("time");
                int users = rs.getInt("users");
                _mapList.put(new Integer(mapid), new Integer(users));
                if (time > 0) {
                    _timeList.put(new Integer(mapid), new Integer(time));
                }
            }
            _log.info("讀取->Quest(副本)地圖設置資料數量: " + _mapList.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public boolean isQuestMap(int mapid) {
        return _mapList.get(new Integer(mapid)) != null;
    }

    public int getTemplate(int mapid) {
        if (_mapList.get(new Integer(mapid)) != null) {
            return ((Integer) _mapList.get(new Integer(mapid))).intValue();
        }
        return -1;
    }

    public Integer getTime(int mapid) {
        return (Integer) _timeList.get(new Integer(mapid));
    }

    public Map<Integer, Integer> getList() {
        return _mapList;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.QuestMapTable JD-Core Version: 0.6.2
 */