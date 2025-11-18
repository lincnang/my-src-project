package com.lineage.server.thread;

import com.lineage.config.Config;
import com.lineage.server.model.monitor.L1PcMonitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 線程管理中心
 *
 * @author dexc
 */
public class GeneralThreadPool {//src032
    private static final Log _log = LogFactory.getLog(GeneralThreadPool.class);
    private static GeneralThreadPool _instance;

    // === L1J 簡化執行緒池架構 ===
    // 主要執行緒池（用於即時、短期的任務）
    private final Executor _executor;

    // 通用排程池（處理所有排程任務:AI、系統、技能等）
    private final ScheduledExecutorService _scheduler;

    // 玩家專用排程池（用於玩家監控、獨立任務）
    private final ScheduledExecutorService _pcScheduler;

    // 關機狀態旗標：啟動關機程序時設為 true，阻止新的排程任務
    private volatile boolean _shuttingDown = false;

    private GeneralThreadPool() {
        // 載入配置
        try {
            com.lineage.config.ThreadPoolSet.load();
        } catch (Exception e) {
        }
        // 計算基礎執行緒池大小
        int maxPlayers = Config.MAX_ONLINE_USERS;
        int corePoolSize = calculateCorePoolSize(maxPlayers);
        // 1. 主要執行緒池（動態，用於即時任務）
        _executor = Executors.newCachedThreadPool(
                new PriorityThreadFactory("MainExecutor", Thread.NORM_PRIORITY));
        // 2. 通用排程池（AI、系統、技能、高低優先級任務共用）
    _scheduler = Executors.newScheduledThreadPool(corePoolSize,
        new PriorityThreadFactory("GeneralScheduler", Thread.NORM_PRIORITY));
    // 關閉時的行為與拒絕策略調整，避免噪音與殭屍任務
    if (_scheduler instanceof ScheduledThreadPoolExecutor) {
        ScheduledThreadPoolExecutor ste = (ScheduledThreadPoolExecutor) _scheduler;
        ste.setRemoveOnCancelPolicy(true);
        ste.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        ste.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        ste.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }
        // 3. 玩家專用排程池（處理玩家獨立任務，如 L1PcMonitor）
        // 若有配置可在外部調整，這裡採用穩健的動態值
        int pcSchedulerSize = Math.max(20, corePoolSize / 4);
    _pcScheduler = Executors.newScheduledThreadPool(pcSchedulerSize,
        new PriorityThreadFactory("PcScheduler", Thread.NORM_PRIORITY));
    if (_pcScheduler instanceof ScheduledThreadPoolExecutor) {
        ScheduledThreadPoolExecutor ste = (ScheduledThreadPoolExecutor) _pcScheduler;
        ste.setRemoveOnCancelPolicy(true);
        ste.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        ste.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        ste.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }
    }
    
    /**
     * 根據伺服器規模計算核心執行緒池大小
     */
    private int calculateCorePoolSize(int maxPlayers) {
        // 最後根據最大玩家數動態計算
        // 基礎公式：基礎執行緒(50) + 每10個玩家1個執行緒
        int baseThreads = 50;
        int playerThreads = maxPlayers / 10;
        int totalThreads = baseThreads + playerThreads;
        
        // 設定上下限
        totalThreads = Math.max(100, totalThreads);  // 最少100
        totalThreads = Math.min(500, totalThreads);  // 最多500
        
        return totalThreads;
    }

    public static GeneralThreadPool get() {
        if (_instance == null) {
            _instance = new GeneralThreadPool();
        }
        return _instance;
    }
    // Executor

    /**
     * 使該線程開始執行；Java 虛擬機調用該線程的 run 方法。
     *
     */
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
     * 使該線程開始執行；Java 虛擬機調用該線程的 run 方法。
     *
     */
    public void execute(final Thread t) {
        try {
            t.start();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 創建並執行在給定延遲後啟用的一次性操作。
     *
     * @param r     要執行的任務
     * @param delay 從現在開始延遲執行的時間
     */
    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        try {
            if (_shuttingDown || _scheduler.isShutdown() || _scheduler.isTerminated()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("skip schedule during shutdown: " + (r != null ? r.getClass().getName() : "null"));
                }
                return null;
            }
            if (delay <= 0) {
                if (!_shuttingDown && _executor != null) {
                    _executor.execute(r);
                }
                return null;
            }
            return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            if (_shuttingDown || _scheduler.isShutdown()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("schedule rejected (shutting down)");
                }
            } else {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    /**
     * 創建並執行一個在給定初始延遲後首次啟用的定期操作，後續操作具有給定的週期；<BR>
     * 也就是將在 initialDelay 後開始執行，然後在 initialDelay+period 後執行，<BR>
     * 接著在 initialDelay + 2 * period 後執行，依此類推。<BR>
     * 如果任務的任何一個執行遇到異常，則後續執行都會被取消。<BR>
     * 否則，只能通過執行程序的取消或終止方法來終止該任務。<BR>
     * 如果此任務的任何一個執行要花費比其週期更長的時間，則將推遲後續執行，但不會同時執行。 <BR>
     * <BR>
     *
     * @param r            要執行的任務
     * @param initialDelay 首次執行的延遲時間
     * @param period       連續執行之間的週期
     */
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initialDelay, final long period) {
        try {
            if (_shuttingDown || _scheduler.isShutdown() || _scheduler.isTerminated()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("skip scheduleAtFixedRate during shutdown: " + (r != null ? r.getClass().getName() : "null"));
                }
                return null;
            }
            return this._scheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
    // ScheduledExecutorService

    /**
     * 創建並執行在給定延遲後啟用的一次性操作。
     *
     * @param r     - 要執行的任務
     * @param delay - 從現在開始延遲執行的時間
     */
    public ScheduledFuture<?> pcSchedule(final L1PcMonitor r, final long delay) {
        try {
            if (_shuttingDown || _pcScheduler.isShutdown() || _pcScheduler.isTerminated()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("skip pcSchedule during shutdown: " + (r != null ? r.getClass().getName() : "null"));
                }
                return null;
            }
            if (delay <= 0) {
                this._executor.execute(r);
                return null;
            }
            return this._pcScheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            if (_shuttingDown || _pcScheduler.isShutdown()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("pcSchedule rejected (shutting down)");
                }
            } else {
                _log.error(e.getLocalizedMessage(), e);
            }
            return null;
        }
    }
// ScheduledThreadPoolExecutor

/**
 * 創建並執行一個在給定初始延遲後首次啟用的定期操作，後續操作具有給定的週期； 也就是將在 initialDelay 後開始執行，然後在
 * initialDelay+period 後執行， 接著在 initialDelay + 2 * period 後執行，依此類推。
 * 如果任務的任何一個執行遇到異常，則後續執行都會被取消。
 * <p>
 * 否則，只能通過執行程序的取消或終止方法來終止該任務。 如果此任務的任何一個執行要花費比其週期更長的時間，則將推遲後續執行，但不會同時執行。
 *
 * @param command      - 要執行的任務
 * @param initialDelay - 首次執行的延遲時間
 * @param period       - 連續執行之間的週期
 */
public ScheduledFuture<?> scheduleAtFixedRate(final TimerTask command, final long initialDelay, final long period) {
    try {
        if (_shuttingDown || _scheduler.isShutdown() || _scheduler.isTerminated()) {
            if (_log.isDebugEnabled()) {
                _log.debug("skip scheduleAtFixedRate(TimerTask) during shutdown: " + (command != null ? command.getClass().getName() : "null"));
            }
            return null;
        }
        return this._scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    } catch (final RejectedExecutionException e) {
        if (_shuttingDown || _scheduler.isShutdown()) {
            if (_log.isDebugEnabled()) {
                _log.debug("scheduleAtFixedRate(TimerTask) rejected (shutting down)");
            }
        } else {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}

/**
 * 以固定延遲重複執行任務（上一輪完成後延遲 period 再執行下一輪）。
 */
public ScheduledFuture<?> scheduleWithFixedDelay(final TimerTask command, final long initialDelay, final long period) {
    try {
        if (_shuttingDown || _scheduler.isShutdown() || _scheduler.isTerminated()) {
            if (_log.isDebugEnabled()) {
                _log.debug("skip scheduleWithFixedDelay during shutdown: " + (command != null ? command.getClass().getName() : "null"));
            }
            return null;
        }
        return this._scheduler.scheduleWithFixedDelay(command, initialDelay, period, TimeUnit.MILLISECONDS);
    } catch (final RejectedExecutionException e) {
        if (_shuttingDown || _scheduler.isShutdown()) {
            if (_log.isDebugEnabled()) {
                _log.debug("scheduleWithFixedDelay rejected (shutting down)");
            }
        } else {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}

/**
 * 相容 int 型別參數的多載。
 */
public ScheduledFuture<?> scheduleWithFixedDelay(final TimerTask command, final int initialDelay, final int period) {
        return scheduleWithFixedDelay(command, (long) initialDelay, (long) period);
    }

    /**
     * 試圖取消對此任務的執行。 如果任務已完成、或已取消，或者由於某些其他原因而無法取消，則此嘗試將失敗。 當調用 cancel
     * 時，如果調用成功，而此任務尚未啟動，則此任務將永不運行。 如果任務已經啟動，則 mayInterruptIfRunning
     * 參數確定是否應該以試圖停止任務的方式來中斷執行此任務的線程。 此方法返回後，對 isDone() 的後續調用將始終返回 true。 如果此方法返回
     * true，則對 isCancelled() 的後續調用將始終返回 true。
     *
     * @param future                - 一個延遲的、結果可接受的操作，可將其取消。
     * @param mayInterruptIfRunning - 如果應該中斷執行此任務的線程，則為 true；否則允許正在運行的任務運行完成
     */
    public void cancel(final ScheduledFuture<?> future, boolean mayInterruptIfRunning) {
        try {
            future.cancel(mayInterruptIfRunning);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TIMER

    /**
     * 安排指定的任務在指定的延遲後開始進行重複的固定速率執行。<BR>
     * 以近似固定的時間間隔（由指定的週期分隔）進行後續執行。<BR>
     * 在固定速率執行中，根據已安排的初始執行時間來安排每次執行。<BR>
     * 如果由於任何原因（如垃圾回收或其他後台活動）而延遲了某次執行，<BR>
     * 則將快速連續地出現兩次或更多的執行，從而使後續執行能夠「追趕上來」。<BR>
     * 從長遠來看，執行的頻率將正好是指定週期的倒數（假定 Object.wait(long) 所依靠的系統時鐘是準確的）。<BR>
     * <BR>
     * 固定速率執行適用於那些對絕對 時間敏感的重複執行活動，<BR>
     * 如每小時准點打鍾報時，或者在每天的特定時間運行已安排的維護活動。<BR>
     * 它還適用於那些完成固定次數執行的總計時間很重要的重複活動，<BR>
     * 如倒計時的計時器，每秒鐘滴答一次，共 10 秒鐘。<BR>
     * 最後，固定速率執行適用於安排多個重複執行的計時器任務，這些任務相互之間必須保持同步。<BR>
     * <BR>
     *
     * @param task   - 所要安排的任務。
     * @param delay  - 執行任務前的延遲時間，單位是毫秒。
     * @param period - 執行各後續任務之間的時間間隔，單位是毫秒。
     */
    public Timer aiScheduleAtFixedRate(final TimerTask task, final long delay, final long period) {
        try {
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, delay, period);
            return timer;
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
    // 取消任務計時器

    /**
     * 取消任務計時器 取消此計時器任務。如果任務安排為一次執行且還未運行，<BR>
     * 或者尚未安排，則永遠不會運行。如果任務安排為重複執行，<BR>
     * 則永遠不會再運行。（如果發生此調用時任務正在運行，則任務將運行完，但永遠不會再運行。） <BR>
     * <BR>
     * 注意，從重複的計時器任務的 run 方法中調用此方法絕對保證計時器任務不會再運行。 <BR>
     * <BR>
     * 此方法可以反覆調用；第二次和以後的調用無效。<BR>
     * <BR>
     *
     * @param task - 所要安排的任務。
     */
    public void cancel(final TimerTask task) {
        try {
            // 取消此計時器任務。
            task.cancel();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取得遊戲邏輯執行緒池（向後兼容，返回通用排程池）
     */
    public ScheduledExecutorService getGameScheduler() {
        return _scheduler;
    }
    
    /**
     * 取得高優先級執行緒池（向後兼容，返回通用排程池）
     */
    public ScheduledExecutorService getHighPriorityScheduler() {
        return _scheduler;
    }
    
    /**
     * 取得低優先級執行緒池（向後兼容，返回通用排程池）
     */
    public ScheduledExecutorService getLowPriorityScheduler() {
        return _scheduler;
    }
    
    /**
     * 執行遊戲邏輯任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> scheduleGameTask(Runnable command, long delay) {
        try {
            if (delay <= 0) {
                _scheduler.execute(command);
                return null;
            }
            return _scheduler.schedule(command, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            _log.error("遊戲邏輯任務執行失敗", e);
            return null;
        }
    }
    
    /**
     * 執行高優先級任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> scheduleHighPriorityTask(Runnable command, long delay) {
        return scheduleGameTask(command, delay);
    }
    
    /**
     * 執行低優先級任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> scheduleLowPriorityTask(Runnable command, long delay) {
        return scheduleGameTask(command, delay);
    }
    
    // === 向後兼容方法（重定向到新的執行緒池） ===
    
    /**
     * 取得假人系統執行緒池（向後兼容，使用通用排程池）
     */
    public ScheduledExecutorService getDeScheduler() {
        return _scheduler;
    }
    
    /**
     * 取得玩家其他功能執行緒池（向後兼容，使用通用排程池）
     */
    public ScheduledExecutorService getPcOtherScheduler() {
        return _scheduler;
    }
    
    /**
     * 取得自動掛機執行緒池（向後兼容，使用通用排程池）
     */
    public ScheduledExecutorService getPcAutoScheduler() {
        return _scheduler;
    }
    
    /**
     * 執行假人系統任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> scheduleDeTask(Runnable command, long delay) {
        return scheduleGameTask(command, delay);
    }
    
    /**
     * 執行玩家其他功能任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> schedulePcOtherTask(Runnable command, long delay) {
        return scheduleGameTask(command, delay);
    }
    
    /**
     * 執行自動掛機任務（向後兼容，使用通用排程池）
     */
    public ScheduledFuture<?> schedulePcAutoTask(Runnable command, long delay) {
        return scheduleGameTask(command, delay);
    }
    
    /**
     * 獲得 pc 專用執行緒池（向後兼容，返回玩家專用池）
     */
    public ScheduledExecutorService pcScheduler() {
        return _pcScheduler;
    }
    
    /**
     * 獲得 ai 專用執行緒池（向後兼容，返回通用排程池）
     */
    public ScheduledExecutorService aiScheduler() {
        return _scheduler;
    }
    
    /**
     * 安全關閉執行緒池與排程器
     */
    public void shutdown() {
        try {
            _shuttingDown = true; // 先設為關機狀態，阻止新任務進入
            if (_executor instanceof ExecutorService) {
                ((ExecutorService) _executor).shutdown();
            }
        } catch (final Exception e) {
            _log.error("shutdown executor error", e);
        }
        try {
            _scheduler.shutdown();
        } catch (final Exception e) {
            _log.error("shutdown scheduler error", e);
        }
        try {
            _pcScheduler.shutdown();
        } catch (final Exception e) {
            _log.error("shutdown pcScheduler error", e);
        }
    }

    /**
     * 是否處於關機狀態（或正在關機）
     */
    public boolean isShuttingDown() {
        return _shuttingDown
                || _scheduler.isShutdown() || _scheduler.isTerminated()
                || _pcScheduler.isShutdown() || _pcScheduler.isTerminated();
    }

    /**
     * 獲取執行緒池狀態資訊（用於監控和診斷）
     */
    public String getPoolStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GeneralThreadPool 狀態 ===\n");
        
        // 通用排程池狀態
        if (_scheduler instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) _scheduler;
            sb.append("[通用排程池]\n");
            sb.append("  核心執行緒: ").append(scheduler.getCorePoolSize()).append("\n");
            sb.append("  最大執行緒: ").append(scheduler.getMaximumPoolSize()).append("\n");
            sb.append("  當前執行緒: ").append(scheduler.getPoolSize()).append("\n");
            sb.append("  活躍執行緒: ").append(scheduler.getActiveCount()).append("\n");
            sb.append("  佇列任務數: ").append(scheduler.getQueue().size()).append("\n");
            sb.append("  已完成任務: ").append(scheduler.getCompletedTaskCount()).append("\n");
        }
        
        // 玩家專用池狀態
        if (_pcScheduler instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor pcScheduler = (ScheduledThreadPoolExecutor) _pcScheduler;
            sb.append("[玩家專用池]\n");
            sb.append("  核心執行緒: ").append(pcScheduler.getCorePoolSize()).append("\n");
            sb.append("  最大執行緒: ").append(pcScheduler.getMaximumPoolSize()).append("\n");
            sb.append("  當前執行緒: ").append(pcScheduler.getPoolSize()).append("\n");
            sb.append("  活躍執行緒: ").append(pcScheduler.getActiveCount()).append("\n");
            sb.append("  佇列任務數: ").append(pcScheduler.getQueue().size()).append("\n");
            sb.append("  已完成任務: ").append(pcScheduler.getCompletedTaskCount()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 記錄執行緒池狀態到日誌
     */
    public void logPoolStatus() {
        _log.info(getPoolStatus());
    }

    /**
     * 根據需要創建新線程的對象。 使用線程工廠就無需再手工編寫對 new Thread 的調用了，從而允許應用程序使用特殊的線程子類、屬性等等。
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
            this._prio = prio;
            this._name = name;
            this._group = new ThreadGroup(this._name);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(this._group, r);
            t.setName(this._name + "-" + this._threadNumber.getAndIncrement());
            t.setPriority(this._prio);
            return t;
        }

        @SuppressWarnings("unused")
        public ThreadGroup getGroup() {
            return this._group;
        }
    }
}
