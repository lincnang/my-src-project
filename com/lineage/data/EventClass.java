package com.lineage.data;

import com.lineage.DatabaseFactory;
import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class EventClass {  //src016
    private static final Log _log = LogFactory.getLog(EventClass.class);
    private static final Map<Integer, EventExecutor> _classList = new HashMap<>();
    private static EventClass _instance;

    public static EventClass get() {
        if (_instance == null) {
            _instance = new EventClass();
        }
        return _instance;
    }

    public void addList(int eventid, String className) {
        if (className.equals("0")) {
            return;
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.lineage.data.event.");
            stringBuilder.append(className);
            Class<?> cls = Class.forName(stringBuilder.toString());
            EventExecutor exe = (EventExecutor) cls.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            _classList.put(eventid, exe);
        } catch (ClassNotFoundException e) {
            String error = "發生[Event(活動設置)檔案]錯誤, 檢查檔案是否存在:" + className + " EventId:" + eventid;
            _log.error(error);
            DataError.isError(_log, error, e);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException |
                 IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void startEvent(L1Event event) {
        try {
            EventExecutor exe = (EventExecutor) _classList.get(event.get_eventid());
            if (exe != null) {
                exe.execute(event);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public final void updateEventNextTime(final int event_id, final Timestamp next_time) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE server_event SET next_time=? WHERE id=?");
            pstm.setTimestamp(1, next_time);
            pstm.setInt(2, event_id);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
