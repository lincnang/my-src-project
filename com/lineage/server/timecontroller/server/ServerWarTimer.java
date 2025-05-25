package com.lineage.server.timecontroller.server;

import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class ServerWarTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerWarTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 60 * 1000;// 1分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            ServerWarExecutor.get().checkWarTime();
        } catch (Exception e) {
            _log.error("城戰計時時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ServerWarTimer warTimer = new ServerWarTimer();
            warTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.server.ServerWarTimer JD-Core Version:
 * 0.6.2
 */