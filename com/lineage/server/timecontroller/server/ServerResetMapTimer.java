package com.lineage.server.timecontroller.server;

import com.add.L1ResetMapTime;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class ServerResetMapTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerResetMapTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 600 * 1000;// 10分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Calendar cal = Calendar.getInstance();
            if (cal.get(Calendar.HOUR_OF_DAY) == 23) {// 早上6點
                L1ResetMapTime.get().ResetTimingMap();
                _log.info("檢查重置限時地監時間軸執行");
            }
        } catch (Exception e) {
            _log.error("檢查重置限時地監時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ServerResetMapTimer resetMapTimer = new ServerResetMapTimer();
            resetMapTimer.start();
        }
    }
}
