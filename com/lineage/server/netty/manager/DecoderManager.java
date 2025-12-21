package com.lineage.server.netty.manager;

import com.lineage.server.netty.GameClient;
import com.lineage.server.netty.NettyChannelRegistry;
import com.lineage.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多執行緒解密池：將同一個 Channel 的封包固定分派到同一個 Worker，確保順序不亂。
 */
public final class DecoderManager {
    private static final Log _log = LogFactory.getLog(DecoderManager.class);

    // 移除預設值，改由 ConfigDecoder 管理
    private static volatile DecoderManager INSTANCE;

    public static DecoderManager get() {
        if (INSTANCE == null) {
            synchronized (DecoderManager.class) {
                if (INSTANCE == null) INSTANCE = new DecoderManager();
            }
        }
        return INSTANCE;
    }

    public static final class Task {
        public final Channel channel;
        public final byte[] encrypted;
        public Task(Channel ch, byte[] enc) { this.channel = ch; this.encrypted = enc; }
    }

    private final List<Worker> workers;

    // 自動清理機制
    private final ScheduledExecutorService cleanupExecutor;
    private final ReferenceQueue<Channel> channelRefQueue = new ReferenceQueue<>();

    // 追蹤已設置清理的 Channel
    private final ConcurrentHashMap<Channel, Boolean> channelsWithCleanup =
        new ConcurrentHashMap<>();

    // 追蹤每個 worker 的 packet 統計
    private final ConcurrentHashMap<Integer, AtomicLong> workerPacketCount =
        new ConcurrentHashMap<>();

    // 追蹤每個 worker 的熱門 Channel/IP
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, AtomicLong>> workerChannelPackets =
        new ConcurrentHashMap<>();

    // 清理配置 - 改由 ConfigDecoder 管理

    private DecoderManager() {
        // 使用 Config 的配置
        int workerCount;
        if (Config.DECODER_WORKER_THREADS == 0) {
            // 自動模式：使用 CPU 核心數，但至少 2 個
            workerCount = Math.max(2, Runtime.getRuntime().availableProcessors());
        } else {
            workerCount = Config.DECODER_WORKER_THREADS;
        }
        int queueCapacity = Config.DECODER_QUEUE_CAPACITY;

        workers = new ArrayList<Worker>(workerCount);
        for (int i = 0; i < workerCount; i++) {
            Worker w = new Worker("DecoderWorker-" + i, queueCapacity);
            workers.add(w);
            Thread t = new Thread(w, w.name);
            t.setDaemon(true);
            t.start();

            // 初始化統計
            workerPacketCount.put(i, new AtomicLong(0));
            workerChannelPackets.put(i, new ConcurrentHashMap<>());
        }

        // 啟動自動清理服務
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DecoderCleanup");
            t.setDaemon(true);
            return t;
        });

        // 啟動定期清理任務
        cleanupExecutor.scheduleAtFixedRate(
            new CleanupTask(),
            Config.DECODER_CLEANUP_INTERVAL_MINUTES,
            Config.DECODER_CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );

        // 啟動負載監控任務（如果啟用）
        if (Config.DECODER_PERFORMANCE_MONITORING) {
            cleanupExecutor.scheduleAtFixedRate(
                this::monitorWorkerLoad,
                10, // 10秒後開始
                10, // 每10秒監控一次
                TimeUnit.SECONDS
            );
        }
    }

    private int indexFor(Channel ch) {
        // 保序：同一 Channel 必須固定同一個 Worker
        // 使用 Channel hashCode 確保一致性
        // 修復 hashCode 負數問題
        return (ch.hashCode() & Integer.MAX_VALUE) % workers.size();
    }

    public void submit(Channel ch, byte[] encrypted) {
        Worker w = workers.get(indexFor(ch));
        Task task = new Task(ch, encrypted);

        // 設置 Channel 關閉時的清理
        setupChannelCleanup(ch);

        // 統計 packet 數量
        int workerIndex = workers.indexOf(w);
        workerPacketCount.get(workerIndex).incrementAndGet();

        String channelKey = ch.getRemoteAddress().toString();
        workerChannelPackets.get(workerIndex).computeIfAbsent(channelKey, k -> new AtomicLong(0))
            .incrementAndGet();

        // 增加 pending 計數
        w.pendingPerChannel.computeIfAbsent(ch, k -> new AtomicInteger(0)).incrementAndGet();

        if (!w.queue.offer(task)) {
            // 記錄詳細信息以便調試
            _log.error("[DecoderManager] Queue full - Worker " + workerIndex +
                     " size: " + w.getQueueSize() +
                     " for channel: " + ch.getRemoteAddress());

            // 減少剛才增加的計數
            AtomicInteger pending = w.pendingPerChannel.get(ch);
            if (pending != null) {
                pending.decrementAndGet();
                if (pending.get() <= 0) {
                    w.pendingPerChannel.remove(ch);
                }
            }

            // 記錄熱門統計
            logHotChannels(workerIndex);

            // 不丟包：關閉連線而不是丟棄任務
            _log.error("[DecoderManager] Closing channel due to queue full: " + ch.getRemoteAddress());
            cleanupChannel(ch);
            try { ch.close(); } catch (Throwable ignore) { }
        }
    }

    /**
     * 記錄熱門 channel/IP 統計
     */
    private void logHotChannels(int workerIndex) {
        try {
            ConcurrentHashMap<String, AtomicLong> channelStats = workerChannelPackets.get(workerIndex);
            if (channelStats == null) return;

            // 取前 5 個最熱門的 channel
            List<Map.Entry<String, AtomicLong>> topChannels = new ArrayList<>(channelStats.entrySet());
            topChannels.sort((a, b) -> Long.compare(b.getValue().get(), a.getValue().get()));

            StringBuilder sb = new StringBuilder();
            sb.append("[DecoderManager] Worker ").append(workerIndex)
              .append(" top channels (total packets=").append(workerPacketCount.get(workerIndex).get()).append("):");

            for (int i = 0; i < Math.min(5, topChannels.size()); i++) {
                Map.Entry<String, AtomicLong> entry = topChannels.get(i);
                if (i > 0) sb.append(", ");
                sb.append(entry.getKey()).append(":").append(entry.getValue().get());
            }

            _log.warn(sb.toString());
        } catch (Exception e) {
            _log.error("[DecoderManager] Error logging hot channels", e);
        }
    }

    /**
     * 設置 Channel 關閉時的自動清理
     */
    private void setupChannelCleanup(Channel ch) {
        // 如果 Channel 已經設置過清理，不再重複設置
        if (channelsWithCleanup.containsKey(ch)) {
            return;
        }

        // 標記已設置清理
        channelsWithCleanup.put(ch, Boolean.TRUE);

        // 添加關閉監聽器
        ch.getCloseFuture().addListener(future -> {
            cleanupChannel(ch);
            channelsWithCleanup.remove(ch);
        });
    }

    /**
     * 清理特定 Channel 的資源
     */
    private void cleanupChannel(Channel ch) {
        try {
            String channelKey = ch.getRemoteAddress().toString();

            // 清理所有 Worker 中的相關資料
            for (int i = 0; i < workers.size(); i++) {
                Worker worker = workers.get(i);
                worker.cleanupChannel(ch);

                // 清理 workerChannelPackets 中的統計資料
                ConcurrentHashMap<String, AtomicLong> channelStats = workerChannelPackets.get(i);
                if (channelStats != null) {
                    channelStats.remove(channelKey);
                }
            }
        } catch (Throwable e) {
            _log.warn("[DecoderManager] Error cleaning up channel: " + ch.getRemoteAddress(), e);
        }
    }

    /** 單個 Worker：取出任務，解密並交給 PacketHandler */
    private static final class Worker implements Runnable {
        private final String name;
        final LinkedBlockingQueue<Task> queue;
        // 簡易診斷：記錄版本驗證前的第一個 opcode - 使用弱引用
        private final ConcurrentHashMap<Channel, Boolean> preVerifyLogged = new ConcurrentHashMap<Channel, Boolean>();
        final ConcurrentHashMap<Channel, AtomicInteger> pendingPerChannel = new ConcurrentHashMap<Channel, AtomicInteger>();

        // 錯誤計數機制
        private int consecutiveErrors = 0;
        private static final int ERROR_THRESHOLD = 10;

        // 追蹤最後清理時間
        private volatile long lastCleanupTime = System.currentTimeMillis();

        private Worker(String name, int capacity) {
            this.name = name;
            this.queue = new LinkedBlockingQueue<Task>(capacity);
        }

        // 獲取當前 queue 大小（僅用於監控）
        public int getQueueSize() {
            return queue.size();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Task task = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (task == null) continue;

                    Channel ch = task.channel;
                    GameClient client = NettyChannelRegistry.client(ch);
                    if (client == null) {
                        _log.warn("[DecoderWorker] GameClient missing, close: " + ch.getRemoteAddress());
                        try { ch.close(); } catch (Throwable ignore) {}
                        continue;
                    }

                    byte[] decrypted = client.getEncryption().decrypt(task.encrypted);

                    // 驗證解密結果
                    if (decrypted == null || decrypted.length == 0) {
                        _log.warn("[DecoderWorker] Empty decrypted packet from " + ch.getRemoteAddress());

                        // 處理完畢：遞減待處理計數（非常重要！）
                        try {
                            AtomicInteger ctr = pendingPerChannel.get(ch);
                            if (ctr != null) {
                                int val = ctr.decrementAndGet();
                                if (val <= 0) {
                                    pendingPerChannel.remove(ch);
                                }
                            }
                        } catch (Throwable ignore) { }

                        continue;
                    }

                    // 交給原本的 PacketHandler（透過 registry 取得）
                    com.lineage.echo.PacketHandler handler = NettyChannelRegistry.handler(ch);
                    if (handler != null) {
                        // 診診斷：若尚未版本驗證，紀錄第一個 opcode
                        try {
                            if (!client.isVersionVerified() && preVerifyLogged.putIfAbsent(ch, Boolean.TRUE) == null && decrypted.length > 0) {
                                // int op = decrypted[0] & 0xFF;
                            }
                        } catch (Throwable ignore) {}

                        // 不跨池：所有封包都在 Worker 內同步處理，確保順序
                        handler.handlePacket(decrypted);
                    } else {
                        _log.warn("[DecoderWorker] PacketHandler missing for channel " + ch.getRemoteAddress());
                    }

                    // 處理完畢：遞減待處理計數（背壓機制已移除）
                    try {
                        AtomicInteger ctr = pendingPerChannel.get(ch);
                        if (ctr != null) {
                            int val = ctr.decrementAndGet();
                            if (val <= 0) {
                                pendingPerChannel.remove(ch);
                            }
                        }
                    } catch (Throwable ignore) { }

                    // 成功處理，重置錯誤計數
                    consecutiveErrors = 0;

                } catch (Throwable t) {
                    consecutiveErrors++;
                    _log.error("[DecoderWorker] error (count=" + consecutiveErrors + ")", t);

                    // 連續錯誤超過閾值，記錄警告並重置
                    if (consecutiveErrors >= ERROR_THRESHOLD) {
                        _log.warn("[DecoderWorker] " + name + " reached error threshold (" +
                                  consecutiveErrors + " errors). Resetting error counter.");

                        // 重置錯誤計數
                        consecutiveErrors = 0;

                        // 短暫休息以避免快速失敗循環
                        try {
                            Thread.sleep(1000); // 休息 1 秒
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break; // 如果被中斷，退出循環
                        }
                    }
                }
            }
        }

        /**
         * 清理特定 Channel 的資源
         */
        void cleanupChannel(Channel ch) {
            try {
                // 清理 preVerifyLogged
                preVerifyLogged.remove(ch);

                // 清理 pendingPerChannel
                AtomicInteger counter = pendingPerChannel.remove(ch);
                if (counter != null && counter.get() > 0) {
                    _log.warn("[DecoderWorker] Cleaning channel with pending packets: " +
                             ch.getRemoteAddress() + " pending=" + counter.get());
                }
            } catch (Throwable e) {
                _log.warn("[DecoderWorker] Error cleaning channel resources", e);
            }
        }

        /**
         * 執行定期清理
         */
        void performPeriodicCleanup() {
            try {
                long now = System.currentTimeMillis();
                long timeSinceLastCleanup = now - lastCleanupTime;

                // 如果距離上次清理不到設定時間，跳過
                if (timeSinceLastCleanup < Config.DECODER_CLEANUP_INTERVAL_MINUTES * 60 * 1000) {
                    return;
                }

                int cleanedPreVerify = 0;
                int cleanedPending = 0;

                // 清理已關閉的 Channel 記錄
                for (Channel ch : preVerifyLogged.keySet()) {
                    if (!ch.isOpen()) {
                        preVerifyLogged.remove(ch);
                        cleanedPreVerify++;
                    }
                }

                // 清理 pendingPerChannel 中的無效項目
                for (Channel ch : pendingPerChannel.keySet()) {
                    if (!ch.isOpen()) {
                        pendingPerChannel.remove(ch);
                        cleanedPending++;
                    } else {
                        AtomicInteger counter = pendingPerChannel.get(ch);
                        if (counter != null && counter.get() <= 0) {
                            pendingPerChannel.remove(ch);
                            cleanedPending++;
                        }
                    }
                }

                // 如果 preVerifyLogged 太大，清理最舊的項目
                if (preVerifyLogged.size() > Config.DECODER_MAX_PRE_VERIFY_LOGGED) {
                    int toRemove = preVerifyLogged.size() - Config.DECODER_MAX_PRE_VERIFY_LOGGED;
                    // 簡單的 LRU：移除前 N 個項目
                    for (Channel ch : preVerifyLogged.keySet().toArray(new Channel[0])) {
                        if (toRemove <= 0) break;
                        preVerifyLogged.remove(ch);
                        toRemove--;
                        cleanedPreVerify++;
                    }
                }

                lastCleanupTime = now;

                // 記錄清理統計
                if (cleanedPreVerify > 0 || cleanedPending > 0) {
                    _log.info("[DecoderWorker] " + name + " cleanup: preVerify=" +
                             cleanedPreVerify + " pending=" + cleanedPending +
                             " (preVerify total=" + preVerifyLogged.size() +
                             ", pending total=" + pendingPerChannel.size() + ")");
                }

            } catch (Throwable e) {
                _log.warn("[DecoderWorker] Error during periodic cleanup", e);
            }
        }
    }

    /**
     * 定期清理任務
     */
    private class CleanupTask implements Runnable {
        @Override
        public void run() {
            try {
                // 觸發所有 Worker 執行清理
                for (Worker worker : workers) {
                    worker.performPeriodicCleanup();
                }

                // 處理弱引用佇列（如果使用）
                Reference<? extends Channel> ref;
                while ((ref = channelRefQueue.poll()) != null) {
                    // 清理已回收的相關資源
                }

            } catch (Throwable e) {
                _log.error("[DecoderManager] Cleanup task error", e);
            }
        }
    }

    /**
     * 監控 Worker 負載
     */
    private void monitorWorkerLoad() {
        int totalTasks = 0;
        int maxQueueSize = 0;
        int maxQueueIndex = 0;
        int queueCapacity = Config.DECODER_QUEUE_CAPACITY;

        StringBuilder sb = new StringBuilder();
        sb.append("[DecoderManager] Worker Load Report: ");

        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            int queueSize = w.queue.size();
            totalTasks += queueSize;

            if (queueSize > maxQueueSize) {
                maxQueueSize = queueSize;
                maxQueueIndex = i;
            }

            if (i > 0) sb.append(", ");
            sb.append("W").append(i).append(":").append(queueSize);
        }

        sb.append(" [Total: ").append(totalTasks).append("]");
        sb.append(" [Capacity: ").append(queueCapacity).append("]");

        // 記錄負載最高的 worker
        // 動態警告閾值：容量的 80%
        int warningThreshold = (int) (queueCapacity * 0.8);

        if (maxQueueSize >= queueCapacity) {
            // 達到容量上限 - 嚴重警告
            _log.error(sb.toString() + " - CRITICAL: Worker " + maxQueueIndex +
                      " queue is FULL (" + maxQueueSize + "/" + queueCapacity + ")!");

            // 記錄熱門 channel/IP
            logHotChannels(maxQueueIndex);
        } else if (maxQueueSize > warningThreshold) {
            // 超過 80% 容量 - 警告
            _log.warn(sb.toString() + " - WARNING: Worker " + maxQueueIndex +
                      " has high load (" + maxQueueSize + "/" + queueCapacity +
                      ", " + (maxQueueSize * 100 / queueCapacity) + "%)");
        } else {
            // 正常狀態 - info 級別
            _log.info(sb.toString());
        }
    }


    
    /**
     * 關閉 DecoderManager，清理所有資源
     */
    public void shutdown() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}