package com.lineage.server.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 線程管理中心(PC)
 *
 * @author dexc
 */
public class PcAutoThreadPoolNew {
    private static final Log _log = LogFactory.getLog(PcAutoThreadPoolNew.class);
    private static PcAutoThreadPoolNew _instance;
    private final int _pcautoSchedulerPoolSize = 300;
    private final Executor _executor;
    private final ScheduledExecutorService _scheduler;

    private PcAutoThreadPoolNew() {
        _executor = Executors.newCachedThreadPool();
        _scheduler = Executors.newScheduledThreadPool(_pcautoSchedulerPoolSize, new PriorityThreadFactory("PcAuto", Thread.NORM_PRIORITY));
    }

    //private final int _pcautoSchedulerPoolSize = 1000 + Config.MAX_ONLINE_USERS / 2;
    public static PcAutoThreadPoolNew get() {
        if (_instance == null) {
            _instance = new PcAutoThreadPoolNew();
        }
        return _instance;
    }

    public void execute(final Runnable r) {
        try {
            if (_executor == null) {
                final Thread t = new Thread(r);
                t.start();
            } else {
                _executor.execute(r);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 創建並執行在給定延遲後啟用的一次性操作。
     *
     * @param r     要執行的任務
     * @param delay 從現在開始延遲執行的時間
     * @return
     */
    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        try {
            if (delay <= 0) {
                _executor.execute(r);
                return null;
            }
            return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final TimerTask command, final long initialDelay, final long period) {
        try {
            return _scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void cancel(final ScheduledFuture<?> future, boolean mayInterruptIfRunning) {
        try {
            future.cancel(mayInterruptIfRunning);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 根據需要創建新線程的對象。
     * 使用線程工廠就無需再手工編寫對 new Thread 的調用了，從而允許應用程序使用特殊的線程子類、屬性等等。
     *
     * @author daien
     */
    private static class PriorityThreadFactory implements ThreadFactory {
        private final int _prio;
        private final String _name;
        private final AtomicInteger _threadNumber = new AtomicInteger(1);
        private final ThreadGroup _group;

        /**
         * PriorityThreadFactory
         *
         * @param name 線程名稱
         * @param prio 優先等級
         */
        public PriorityThreadFactory(final String name, final int prio) {
            _prio = prio;
            _name = name;
            _group = new ThreadGroup(_name);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(_group, r);
            t.setName(_name + "-" + _threadNumber.getAndIncrement());
            t.setPriority(_prio);
            return t;
        }

        @SuppressWarnings("unused")
        public ThreadGroup getGroup() {
            return _group;
        }
    }
}
