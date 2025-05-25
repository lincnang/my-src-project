package com.lineage.server.timecontroller.event;

import com.lineage.config.ConfigDescs;
import com.lineage.server.serverpackets.S_HelpMessage;
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
            World.get().broadcastPacketToAll(new S_HelpMessage(ConfigDescs.getShow(_count)));
            _count += 1;
            if (_count >= ConfigDescs.get_show_size()) {
                _count = 1;
            }
        } catch (Exception e) {
            _log.error("服務器介紹與教學時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            NewServerTime timerTask = new NewServerTime();
            timerTask.start(_time);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.NewServerTime JD-Core Version: 0.6.2
 */