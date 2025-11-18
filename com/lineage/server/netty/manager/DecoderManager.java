package com.lineage.server.netty.manager;

import com.lineage.server.netty.GameClient;
import com.lineage.server.netty.NettyChannelRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多執行緒解密池：將同一個 Channel 的封包固定分派到同一個 Worker，確保順序不亂。
 */
public final class DecoderManager {
    private static final Log _log = LogFactory.getLog(DecoderManager.class);

    private static final int DEFAULT_QUEUE_CAPACITY = 4096;
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

    private DecoderManager() {
        int cores = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors(), 8));
        workers = new ArrayList<Worker>(cores);
        for (int i = 0; i < cores; i++) {
            Worker w = new Worker("DecoderWorker-" + i, DEFAULT_QUEUE_CAPACITY);
            workers.add(w);
            Thread t = new Thread(w, w.name);
            t.setDaemon(true);
            t.start();
        }
    // _log.info("[DecoderManager] Started decryption pool with workers=" + cores);
    }

    private int indexFor(Channel ch) {
        int h = System.identityHashCode(ch) & 0x7fffffff;
        return h % workers.size();
    }

    public void submit(Channel ch, byte[] encrypted) {
        Worker w = workers.get(indexFor(ch));
        if (!w.offer(new Task(ch, encrypted))) {
            _log.error("[DecoderManager] Queue full, closing channel: " + ch.getRemoteAddress());
            try { ch.close(); } catch (Throwable ignore) { }
        }
    }

    /** 單個 Worker：取出任務，解密並交給 PacketHandler */
    private static final class Worker implements Runnable {
        private final String name;
        private final LinkedBlockingQueue<Task> queue;
        // 簡易診斷：記錄版本驗證前的第一個 opcode
        private final ConcurrentHashMap<Channel, Boolean> preVerifyLogged = new ConcurrentHashMap<Channel, Boolean>();
        private final ConcurrentHashMap<Channel, AtomicInteger> pendingPerChannel = new ConcurrentHashMap<Channel, AtomicInteger>();

        private Worker(String name, int capacity) {
            this.name = name;
            this.queue = new LinkedBlockingQueue<Task>(capacity);
        }

        boolean offer(Task t) {
            boolean enq = queue.offer(t);
            if (enq) {
                // 更新每頻道待處理計數
                AtomicInteger ctr = pendingPerChannel.get(t.channel);
                if (ctr == null) {
                    AtomicInteger created = new AtomicInteger(0);
                    AtomicInteger exist = pendingPerChannel.putIfAbsent(t.channel, created);
                    ctr = (exist != null) ? exist : created;
                }
                ctr.incrementAndGet();
                // 背壓機制已移除，不再暫停讀取
            }
            return enq;
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

                    // 交給原本的 PacketHandler（透過 registry 取得）
                    com.lineage.echo.PacketHandler handler = NettyChannelRegistry.handler(ch);
                    if (handler != null) {
                        // 診斷：若尚未版本驗證，紀錄第一個 opcode
                        try {
                            if (!client.isVersionVerified() && preVerifyLogged.putIfAbsent(ch, Boolean.TRUE) == null && decrypted.length > 0) {
                                // int op = decrypted[0] & 0xFF;
                            }
                        } catch (Throwable ignore) {}
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

                } catch (Throwable t) {
                    _log.error("[DecoderWorker] error", t);
                }
            }
        }
    }
}
