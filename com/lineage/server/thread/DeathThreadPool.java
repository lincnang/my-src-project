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
public class DeathThreadPool {
    private static final Log _log = LogFactory.getLog(DeathThreadPool.class);
    private static final int SCHEDULED_CORE_POOL_SIZE = 1000 /*+ Config.MAX_ONLINE_USERS / 2*/;
    //private static final int SCHEDULED_CORE_POOL_SIZE = 500 + Config.MAX_ONLINE_USERS;// 原本為100
    private static DeathThreadPool _instance;
    private final int _pcSchedulerPoolSize = Config.MAX_ONLINE_USERS * 10;
    // 執行已提交的 Runnable 任務的對象。
    // 此接口提供一種將任務提交與每個任務將如何運行的機制（包括線程使用的細節、調度等）分離開來的方法。
    // 通常使用 Executor 而不是顯式地創建線程。例如，可能會使用以下方法，
    // 而不是為一組任務中的每個任務調用 new Thread(new(RunnableTask())).start()：
    private final Executor _executor;
    // 一個 ExecutorService，可安排在給定的延遲後運行或定期執行的命令。
    private final ScheduledExecutorService _scheduler;
    private final ScheduledExecutorService _pcScheduler;
    // ThreadPoolExecutor，它可另行安排在給定的延遲後運行命令，或者定期執行命令。
    // 需要多個輔助線程時，或者要求 ThreadPoolExecutor 具有額外的靈活性或功能時，此類要優於 Timer。
    // private ScheduledThreadPoolExecutor _poolExecutor;
    //private final int _pcSchedulerPoolSize = 1 + Config.MAX_ONLINE_USERS / 10;
    private final ScheduledExecutorService _aiScheduler;

    private DeathThreadPool() {
        // 創建一個可根據需要創建新線程的線程池，但是在以前構造的線程可用時將重用它們。
        _executor = Executors.newCachedThreadPool();
        // 常規(創建一個線程池，它可安排在給定延遲後運行命令或者定期地執行。)
        _scheduler = Executors.newScheduledThreadPool(SCHEDULED_CORE_POOL_SIZE, new PriorityThreadFactory("GSTPool", Thread.NORM_PRIORITY));
        // PC(創建一個線程池，它可安排在給定延遲後運行命令或者定期地執行。)
        _pcScheduler = Executors.newScheduledThreadPool(_pcSchedulerPoolSize, new PriorityThreadFactory("PSTPool", Thread.NORM_PRIORITY));
        // AI(創建一個線程池，它可安排在給定延遲後運行命令或者定期地執行。)
        _aiScheduler = Executors.newScheduledThreadPool(_pcSchedulerPoolSize, new PriorityThreadFactory("AITPool", Thread.NORM_PRIORITY));
    }

    public static DeathThreadPool get() {
        if (_instance == null) {
            _instance = new DeathThreadPool();
        }
        return _instance;
    }
    // Executor

    /**
     * 使該線程開始執行；Java 虛擬機調用該線程的 run 方法。
     *
     * @param r
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
     * @param t
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
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initialDelay, final long period) {
        try {
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
     * @return
     */
    public ScheduledFuture<?> pcSchedule(final L1PcMonitor r, final long delay) {
        try {
            if (delay <= 0) {
                // 在未來某個時間執行給定的命令。
                // 該命令可能在新的線程、已入池的線程或者正調用的線程中執行，這由 Executor 實現決定。
                this._executor.execute(r);
                return null;
            }
            // 創建並執行在給定延遲後啟用的一次性操作。
            return this._pcScheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
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
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(final TimerTask command, final long initialDelay, final long period) {
        try {
            /*
             * Timer timer = new Timer(); timer.scheduleAtFixedRate(command,
             * initialDelay, period); return timer;
             */
            return this._aiScheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (final RejectedExecutionException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        }
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
     * 如果由於任何原因（如垃圾回收或其他後臺活動）而延遲了某次執行，<BR>
     * 則將快速連續地出現兩次或更多的執行，從而使後續執行能夠「追趕上來」。<BR>
     * 從長遠來看，執行的頻率將正好是指定週期的倒數（假定 Object.wait(long) 所依靠的系統時鐘是準確的）。<BR>
     * <BR>
     * 固定速率執行適用於那些對絕對 時間敏感的重複執行活動，<BR>
     * 如每小時准點打鐘報時，或者在每天的特定時間運行已安排的維護活動。<BR>
     * 它還適用於那些完成固定次數執行的總計時間很重要的重複活動，<BR>
     * 如倒計時的計時器，每秒鐘滴答一次，共 10 秒鐘。<BR>
     * 最後，固定速率執行適用於安排多個重複執行的計時器任務，這些任務相互之間必須保持同步。<BR>
     * <BR>
     *
     * @param task   - 所要安排的任務。
     * @param delay  - 執行任務前的延遲時間，單位是毫秒。
     * @param period - 執行各後續任務之間的時間間隔，單位是毫秒。
     * @return
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
     * 創建並執行一個週期性的動作，成為啟用後第一次在給定的初始延遲，隨後與特定時期，這是將處決後initialDelay開始，
     * 然後initialDelay +句號，然後initialDelay+2*期，等上。如果有任何任務的執行遇到異常，隨後處決被抑制。否則，該任務將終止通過執行人取消或終止
     * 。如果執行此任務的任何花費比其週期長，那麼後續執行可能起步較晚，但不會同時執行。
     * <p>
     * 參數： 指揮執行任務 initialDelay時間推遲首次執行 期間連續執行之間的週期 單位的initialDelay和時期參數的時間單位 傳回：
     * 1 ScheduledFuture的代表尚待完成的任務，並且其get（）方法將拋出一個異常取消後 擲出：
     * RejectedExecutionException - 如果任務不能按期執行 NullPointerException異常 - 如果命令是空的
     * IllegalArgumentException - 如果週期小於或等於零
     *
     * @param r
     * @param initialDelay
     * @param period
     * @return
     */
    public ScheduledFuture<?> pcScheduleAtFixedRateHell(L1PcMonitor r, long initialDelay, long period) {
        return _pcScheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 根據需要創建新線程的對象。 使用線程工廠就無需再手工編寫對 new Thread 的調用了，從而允許應用程序使用特殊的線程子類、屬性等等。
     *
     * @author daien
     */
    private class PriorityThreadFactory implements ThreadFactory {
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
