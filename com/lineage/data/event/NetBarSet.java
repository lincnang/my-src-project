package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class NetBarSet extends EventExecutor {
    public static final Map<String, Integer> EXIPLIST = new HashMap<>();
    private static final Log _log = LogFactory.getLog(NetBarSet.class);

    public static EventExecutor get() {
        return new NetBarSet();
    }

    public void execute(L1Event event) {
        String[] set = event.get_eventother().split(",");// IP
        String[] set2 = event.get_eventother2().split("#");// 最大同時登入數
        if (set.length == set2.length) {
            for (int i = 0; i < set.length; i++) {
                String ipstring = set[i];
                EXIPLIST.put(ipstring, Integer.valueOf(set2[i]));
            }
        } else {
            _log.warn("網咖IP登入設定錯誤");
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.SkillTeacherSet JD-Core Version: 0.6.2
 */