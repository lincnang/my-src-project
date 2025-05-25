package com.lineage.server.datatables;//src016

import com.lineage.DatabaseFactory;
import com.lineage.data.EventClass;
import com.lineage.server.templates.L1Event;
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

public class EventTable {
    private static final Log _log = LogFactory.getLog(EventTable.class);
    private static final Map<Integer, L1Event> _eventList = new HashMap<Integer, L1Event>();
    private static EventTable _instance;

    public static EventTable get() {
        if (_instance == null) {
            _instance = new EventTable();
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
            pstm = con.prepareStatement("SELECT * FROM `server_event` ORDER BY `id`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String eventname = rs.getString("eventname");
                String eventclass = rs.getString("eventclass");
                boolean eventstart = rs.getBoolean("eventstart");
                String eventother = rs.getString("eventother").replaceAll(" ", "");
                String eventother2 = rs.getString("eventother2").replaceAll(" ", "");
                if (eventstart) {
                    L1Event event = new L1Event();
                    event.set_eventid(id);
                    event.set_eventname(eventname);
                    event.set_eventclass(eventclass);
                    event.set_eventstart(eventstart);
                    event.set_eventother(eventother);
                    event.set_eventother2(eventother2);
                    event.set_next_time(rs.getTimestamp("next_time"));
                    EventClass.get().addList(id, eventclass);
                    _eventList.put(new Integer(id), event);
                    EventClass.get().startEvent(event);
                }
            }
            _log.info("讀取->活動設置資料數量: " + _eventList.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void loadIplimit() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_event` WHERE id=24");
            rs = pstm.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String eventname = rs.getString("eventname");
                String eventclass = rs.getString("eventclass");
                boolean eventstart = rs.getBoolean("eventstart");
                String eventother = rs.getString("eventother").replaceAll(" ", "");
                String eventother2 = rs.getString("eventother2").replaceAll(" ", "");
                if (eventstart) {
                    L1Event event = new L1Event();
                    event.set_eventid(id);
                    event.set_eventname(eventname);
                    event.set_eventclass(eventclass);
                    event.set_eventstart(eventstart);
                    event.set_eventother(eventother);
                    event.set_eventother2(eventother2);
                    EventClass.get().addList(id, eventclass);
                    _eventList.put(new Integer(id), event);
                    EventClass.get().startEvent(event);
                }
            }
            _log.info("讀取->網咖IP設置資料數量: " + _eventList.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public L1Event getTemplate(int id) {
        return (L1Event) _eventList.get(new Integer(id));
    }

    public Map<Integer, L1Event> getList() {
        return _eventList;
    }

    public int size() {
        return _eventList.size();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.EventTable JD-Core Version: 0.6.2
 */