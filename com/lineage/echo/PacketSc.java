package com.lineage.echo;

import com.lineage.config.ConfigLIN;
import com.lineage.echo.encryptions.Cipher;
import com.lineage.echo.encryptions.PacketPrint;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 封包輸出管理 (改用 BlockingQueue 阻塞式等待，降低 CPU 使用與延遲)
 *
 * @author dexc
 */
public class PacketSc implements Runnable {
    private static final Log _log = LogFactory.getLog(PacketSc.class);
    private static final int STAT_INTERVAL_MS = 1000;
    private static final int STAT_PKT_THRESHOLD = 500;
    private static final int STAT_QUEUE_THRESHOLD = 200;
    private static final int STAT_BYTES_THRESHOLD = 128 * 1024;

    /** 使用 BlockingQueue 支援阻塞式等待 */
    private final BlockingQueue<byte[]> _queue;
    private final ClientExecutor _client;
    private final EncryptExecutor _executor;
    private final Cipher _keys;
    private long _statWindowStart = System.currentTimeMillis();
    private int _statPackets;
    private long _statBytes;
    private int _statMaxQueue;
    /** 統計用的佇列大小快照 */
    private volatile int _currentQueueSize = 0;

    public PacketSc(final ClientExecutor client, final EncryptExecutor executor) {
        _client = client;
        _keys = client.get_keys();
        _executor = executor;
        // 使用 LinkedBlockingQueue，容量設為 Integer.MAX_VALUE (無限制)
        _queue = new LinkedBlockingQueue<byte[]>();
    }

    /**
     * 加入工作列隊
     *
     */
    private void requestWork(final byte[] data) {
        // BlockingQueue.offer() 會立即返回 true/false
        boolean added = _queue.offer(data);
        if (added) {
            _currentQueueSize = _queue.size();

            // 診斷代碼: 當佇列突然堆積超過閾值時，印出是誰塞進來的
            if (_currentQueueSize > 150 && _currentQueueSize % 50 == 0) {
                _log.info("[High Queue Debug] CurrentSize=" + _currentQueueSize + ", Source Trace:");
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                // 印出前 8 層堆疊，跳過前 2 層 (getStackTrace 和 requestWork 自己)
                for (int i = 2; i < Math.min(stackTrace.length, 10); i++) {
                    _log.info("    at " + stackTrace[i]);
                }
            }
        }
    }

    /**
     * 加密封包
     *
     */
    public void encrypt(final ServerBasePacket packet) throws Exception {
        final byte[] encrypt = packet.getContent();
        if ((encrypt.length > 0) && (_executor.out() != null)) {
            if (ConfigLIN.opcode_S) {
                _log.info("服務端: " + packet.getType() + "\nOP ID: " + (encrypt[0] & 0xff) + "\nInfo:\n" + PacketPrint.get().printData(encrypt, encrypt.length));
            }
            // 記錄明文封包供診斷（反射呼叫，避免編譯期依賴）
            try {
                java.lang.reflect.Method m = _client.getClass().getMethod("traceOutbound", byte[].class);
                m.invoke(_client, encrypt);
            } catch (Throwable ignore) { }
            final byte[] data = Arrays.copyOf(encrypt, encrypt.length);
            _keys.encryptHash(data);
            requestWork(data);
        }
    }

    @Override
    public void run() {
        try {
            while (_client.get_socket() != null) {
                byte[] data = null;

                try {
                    // 阻塞式等待：有封包立即取回，沒封包最多等 3 秒
                    // 這樣就不需要 sleep 輪詢，降低 CPU 使用
                    data = _queue.poll(3000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // 被中斷，繼續迴圈
                    continue;
                }

                if (data != null) {
                    // 更新統計
                    int currentSize = _queue.size();
                    _currentQueueSize = currentSize;
                    if (currentSize > _statMaxQueue) {
                        _statMaxQueue = currentSize;
                    }

                    // 輸出封包
                    outPacket(data);
                    recordStat(data.length + 2);

                    // 立即 flush 確保送出
                    try {
                        _executor.out().flush();
                    } catch (IOException e) {
                        _executor.stop();
                    }

                    // 繼續處理佇列中剩餘的封包（非阻塞）
                    while ((data = _queue.poll()) != null) {
                        currentSize = _queue.size();
                        _currentQueueSize = currentSize;
                        if (currentSize > _statMaxQueue) {
                            _statMaxQueue = currentSize;
                        }

                        outPacket(data);
                        recordStat(data.length + 2);
                    }

                    // 處理完一批後統一 flush
                    try {
                        _executor.out().flush();
                    } catch (IOException e) {
                        _executor.stop();
                    }
                }

                maybeLogStats(System.currentTimeMillis());
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 移除此 collection 中的所有元素
            _queue.clear();
            _currentQueueSize = 0;
        }
    }

    /**
     * 輸出封包
     *
     */
    private void outPacket(final byte[] decrypt) {
        try {
            final int outLength = decrypt.length + 2;
            // 將指定的位元組寫入此輸出流。
            _executor.out().write(outLength & 0xff);
            _executor.out().write((outLength >> 8) & 0xff);
            // 將 decrypt.length 個位元組從指定的 byte 陣列寫入此輸出流。
            _executor.out().write(decrypt);
        } catch (final IOException e) {
            // 輸出異常
            _executor.stop();
        }
    }

    public void stop() {
        // 移除此 collection 中的所有元素
        _queue.clear();
        _currentQueueSize = 0;
    }

    private void recordStat(int bytes) {
        _statPackets++;
        _statBytes += bytes;
    }

    private void maybeLogStats(long now) {
        if (now - _statWindowStart < STAT_INTERVAL_MS) {
            return;
        }
        if (_statPackets >= STAT_PKT_THRESHOLD
                || _statBytes >= STAT_BYTES_THRESHOLD
                || _statMaxQueue >= STAT_QUEUE_THRESHOLD) {
            if (_log.isInfoEnabled()) {
                String account = _client.getAccountName();
                String ip = _client.getIpString();
                L1PcInstance pc = _client.getActiveChar();
                String pcName = pc != null ? pc.getName() : "-";
                _log.info("[PKTSTAT] ip=" + ip
                        + " acct=" + (account != null ? account : "-")
                        + " pc=" + pcName
                        + " pkts=" + _statPackets
                        + " bytes=" + _statBytes
                        + " maxQ=" + _statMaxQueue);
            }
        }
        _statWindowStart = now;
        _statPackets = 0;
        _statBytes = 0;
        _statMaxQueue = 0;
    }
}
