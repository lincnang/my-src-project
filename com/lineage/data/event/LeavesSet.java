package com.lineage.data.event;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.LeavesTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 殷海薩的祝福-休息系統<BR>
 * # 殷海薩的祝福-休息系統 DELETE FROM `server_event` WHERE `id`='56'; INSERT INTO
 * `server_event` VALUES ('56', '殷海薩的祝福-休息系統', 'LeavesSet', '1', '15,4000',
 * '說明:休息或登出的時間增加1%(單位:分鐘),設置時間增加的經驗質(EXP)');
 *
 * @author dexc
 */
public class LeavesSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(LeavesSet.class);
    /**
     * 殷海薩的祝福-休息系統
     */
    public static boolean START = false;
    /**
     * 說明:休息或登出的時間增加1%(單位:分鐘)
     */
    public static int TIME = 0;
    /**
     * 說明:設置時間增加的經驗質(EXP)
     */
    public static int EXP = 0;
    /**
     * 可增加EXP最大質
     */
    public static int MAXEXP = 0;

    public static EventExecutor get() {
        return new LeavesSet();
    }

    public void execute(L1Event event) {
        try {
            START = true;
            String[] set = event.get_eventother().split(",");
            try {
                TIME = Integer.parseInt(set[0]);
            } catch (Exception e) {
                TIME = 15;
                _log.error("未設定時間(使用預設15分鐘)");
            }
            try {
                EXP = Integer.parseInt(set[1]);
            } catch (Exception e) {
                EXP = 4000;
                _log.error("未設定增加的經驗值(使用預設4000)");
            }
            //MAXEXP = EXP * 200;
            MAXEXP = EXP * ConfigOtherSet2.LEAVES_MAXEXP;
            LeavesTime leavesTime = new LeavesTime();
            leavesTime.start();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
