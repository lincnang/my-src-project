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
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 封包輸出管理
 *
 * @author dexc
 */
public class PacketSc implements Runnable {
    private static final Log _log = LogFactory.getLog(PacketSc.class);
    private static final int STAT_INTERVAL_MS = 1000;
    private static final int STAT_PKT_THRESHOLD = 500;
    private static final int STAT_QUEUE_THRESHOLD = 200;
    private static final int STAT_BYTES_THRESHOLD = 128 * 1024;

    private final Queue<byte[]> _queue;
    private final AtomicInteger _queueSize;
    private final ClientExecutor _client;
    private final EncryptExecutor _executor;
    private final Cipher _keys;
    private long _statWindowStart = System.currentTimeMillis();
    private int _statPackets;
    private long _statBytes;
    private int _statMaxQueue;

    public PacketSc(final ClientExecutor client, final EncryptExecutor executor) {
        _client = client;
        _keys = client.get_keys();
        _executor = executor;
        _queue = new ConcurrentLinkedQueue<>();
        _queueSize = new AtomicInteger();
    }

    /**
     * 加入工作列隊
     *
     */
    private void requestWork(final byte[] data) {
        _queue.offer(data);
        int currentSize = _queueSize.incrementAndGet();
        
        // 診斷代碼: 當佇列突然堆積超過閾值時，印出是誰塞進來的
        if (currentSize > 150 && currentSize % 50 == 0) {
            _log.info("[High Queue Debug] CurrentSize=" + currentSize + ", Source Trace:");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            // 印出前 8 層堆疊，跳過前 2 層 (getStackTrace 和 requestWork 自己)
            for (int i = 2; i < Math.min(stackTrace.length, 10); i++) {
                _log.info("    at " + stackTrace[i]);
            }
        }
    }

    /**
     * 加密封包
     *
     */
    public void encrypt(final ServerBasePacket packet) throws Exception {
        final byte[] encrypt = packet.getContent();
        // _log.info("加密封包: 長度: "+encrypt.length);
        if ((encrypt.length > 0) && (_executor.out() != null)) {
            /*
             * final int opid = encrypt[0]; if (opid == -1) { _log.error(
             * "拒絕發送: " + packet.getType() + " OPID: " + opid); return; }
             */
            if (ConfigLIN.opcode_S) {
                _log.info("服務端: " + packet.getType() + "\nOP ID: " + (encrypt[0] & 0xff) + "\nInfo:\n" + PacketPrint.get().printData(encrypt, encrypt.length));
            }
            /*
             * char ac[] = new char[encrypt.length]; ac =
             * UChar8.fromArray(encrypt); // 加密 ac = _keys.encrypt(ac); if (ac
             * == null) { return; } encrypt = UByte8.fromArray(ac);
             */
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
                int queued = _queueSize.get();
                if (queued > _statMaxQueue) {
                    _statMaxQueue = queued;
                }
                boolean hasPacket = false;
                for (final Iterator<byte[]> iter = _queue.iterator(); iter.hasNext(); ) {
                    final byte[] decrypt = iter.next();// 返回迭代的下一個元素。
                    // 從迭代器指向的 collection 中移除迭代器返回的最後一個元素
                    iter.remove();
                    int newSize = _queueSize.decrementAndGet();
                    if (newSize < 0) {
                        _queueSize.set(0);
                    }
                    outPacket(decrypt);
                    recordStat(decrypt.length + 2);
                    hasPacket = true;
                    /* 移除可疑的異常停頓 by 聖子默默
                     * TimeUnit.MILLISECONDS.sleep(1);
                     */
                }
                if (hasPacket) {
                    try {
                        _executor.out().flush();
                    } catch (IOException e) {
                        _executor.stop();
                    }
                }
                maybeLogStats(System.currentTimeMillis());
                // 隊列為空 休眠
                TimeUnit.MILLISECONDS.sleep(10);
            }
            // finalize();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 移除此 collection 中的所有元素
            _queue.clear();
            _queueSize.set(0);
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
            // 刷新此輸出流並強制寫出所有緩衝的輸出位元組。
            //_executor.out().flush();
            /*
             * long tt = System.currentTimeMillis(); _log.info("加密封包: 長度: "
             * +decrypt.length+"/"+(tt - _client.TTL.get(1)));
             * _client.TTL.put(1, tt);
             */
        } catch (final IOException e) {
            // 輸出異常
            // _log.error(e.getLocalizedMessage(), e);
            _executor.stop();
        }
    }

    public void stop() {
        // 移除此 collection 中的所有元素
        _queue.clear();
        _queueSize.set(0);
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
