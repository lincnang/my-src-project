package com.lineage.server.timecontroller.event;

import com.lineage.server.serverpackets.S_GreenMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 聊天 計時器
 *
 * @author dexc
 */
public class WorldChatTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(WorldChatTimer.class);
    private static final LinkedBlockingQueue<String> _queue = new LinkedBlockingQueue<String>();
    private ScheduledFuture<?> _timer;

    public static void addchat(String chat) throws InterruptedException {
        _queue.put(chat);
    }

    public void start() {
        final int timeMillis = 1000;// 15秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final String chat = _queue.poll();
            if (chat != null) {
                World.get().broadcastPacketToAllkill(new S_GreenMessage(chat));
                TimeUnit.MILLISECONDS.sleep(3000);
                // World.get().broadcastPacketToAll(new
                // S_ServerMessage("\\fW"+chat));
            }
        } catch (final Exception e) {
            _log.error("屏幕公告時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final WorldChatTimer timer = new WorldChatTimer();
            timer.start();
        }
    }
}
