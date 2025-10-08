package com.lineage.server.threads.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledFuture;

public class CharacterQuickCheckThread implements Runnable {
    private static final Log _log = LogFactory.getLog(CharacterQuickCheckThread.class);
    private static volatile ScheduledFuture<?> _future;

    public static void start() {
        if (_future == null || _future.isCancelled()) {
            _future = GeneralThreadPool.get().scheduleAtFixedRate(new CharacterQuickCheckThread(), 0L, 60_000L);
            _log.info("CharacterQuickCheckThread scheduled (fixed-rate 60s).");
        }
    }

    public static void stop() {
        try {
            if (_future != null) {
                GeneralThreadPool.get().cancel(_future, true);
                _future = null;
                _log.info("CharacterQuickCheckThread stopped.");
            }
        } catch (Throwable t) {
            _log.error("Failed to stop CharacterQuickCheckThread", t);
        }
    }

    @Override
    public void run() {
        try {
            for (L1PcInstance pc : World.get().getAllPlayers()) {
                if (pc == null) {
                    continue;
                }
                try {
                    if (pc.getNetConnection() == null) {
                        continue;
                    }
                    // 可擴充：若連線類暴露連線狀態則於此檢查並處理
                } catch (Throwable ignore) {
                }
            }
        } catch (Throwable t) {
            _log.error("CharacterQuickCheckThread execution error", t);
        }
    }
}


