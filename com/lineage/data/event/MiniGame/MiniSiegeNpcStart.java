package com.lineage.data.event.MiniGame;

import com.lineage.config.ConfigThebes;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 底比斯大戰遊戲NPC(次元之門)啟動控制項
 *
 * @author dexc
 */
public class MiniSiegeNpcStart implements Runnable {
    private static final Log _log = LogFactory.getLog(MiniSiegeNpcStart.class);
    private static MiniSiegeNpcStart _instance;

    public MiniSiegeNpcStart() {
        GeneralThreadPool.get().execute(this);
    }

    public static MiniSiegeNpcStart getInstance() {
        if (_instance == null) {
            _instance = new MiniSiegeNpcStart();
        }
        return _instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                MiniSiegeNpcChack();
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private void MiniSiegeNpcChack() {
        final Calendar cals = Calendar.getInstance();
        if (ConfigThebes.MiniSiege_StartTime != null && ConfigThebes.MiniSiege_StartTime.before(cals)) {
            MiniSiegeNpc.getStart(); // 啟動
            ConfigThebes.MiniSiege_StartTime = null;
        }
    }
}