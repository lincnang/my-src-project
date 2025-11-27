package com.lineage.server.timecontroller.event;

import com.lineage.config.ConfigDescs;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class NewServerTime extends TimerTask {
    private static final Log _log = LogFactory.getLog(NewServerTime.class);
    private int _count = 1;
    private int _time = 0;
    private ScheduledFuture<?> _timer;

    public void start(int time) {
        _time = time;
        int timeMillis = time * 1000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            int total = ConfigDescs.get_show_size();
            if (total <= 0) {
                String warn = "[NewServerTime] 無公告內容，請檢查定時公告循環設定表";
                _log.warn(warn);
                System.out.println(warn);
                return;
            }

            if (_count > total) {
                _count = 1;
            }

            String message = ConfigDescs.getShow(_count);
                if (message != null && !message.trim().isEmpty()) {
                    World.get().broadcastPacketToAll(new S_SystemMessage(message));
            } else {
                String warn = "[NewServerTime] 取得的公告為空，索引=" + _count;
                _log.warn(warn);
                System.out.println(warn);
            }

            _count = nextIndex(_count, total);
        } catch (Exception e) {
            _log.error("服務器介紹與教學時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            NewServerTime timerTask = new NewServerTime();
            timerTask.start(_time);
        }
    }

    private int nextIndex(int current, int total) {
        if (total <= 0) {
            return 1;
        }
        int next = current + 1;
        if (next > total) {
            next = 1;
        }
        return next;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.NewServerTime JD-Core Version: 0.6.2
 */