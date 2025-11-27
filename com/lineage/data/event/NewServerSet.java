package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.EventTable;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.NewServerTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewServerSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(NewServerSet.class);
    private static volatile boolean _started = false;

    public static EventExecutor get() {
        return new NewServerSet();
    }

    public void execute(L1Event event) {
        if (event == null) {
            return;
        }
        try {
            int time = parseIntervalSeconds(event.get_eventother());
            if (time <= 0) {
                _log.warn("NewServerSet event interval 無效，使用預設 60 秒");
                time = 60;
            }
            NewServerTime chatTime = new NewServerTime();
            chatTime.start(time);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void ensureStarted() {
        if (_started) {
            return;
        }
        for (L1Event event : EventTable.get().getList().values()) {
            if (event == null) {
                continue;
            }
            if (!event.is_eventstart()) {
                continue;
            }
            if ("NewServerSet".equalsIgnoreCase(event.get_eventclass())) {
                NewServerSet.get().execute(event);
                return;
            }
        }
        String warn = "[NewServerSet] 未在 server_event 中啟用，請確認資料表設定";
        _log.warn(warn);
        System.out.println(warn);
    }

    private int parseIntervalSeconds(String raw) {
        if (raw == null) {
            return 0;
        }
        String normalized = raw.replace("，", ",").trim();
        if (normalized.isEmpty()) {
            return 0;
        }

        String[] tokens = normalized.split("[\\s,]+");
        String lastToken = tokens[tokens.length - 1];
        return Integer.parseInt(lastToken);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.NewServerSet JD-Core Version: 0.6.2
 */