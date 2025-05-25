package com.lineage.echo;

import com.lineage.config.ConfigLIN;
import com.lineage.echo.encryptions.Cipher;
import com.lineage.echo.encryptions.PacketPrint;
import com.lineage.server.serverpackets.ServerBasePacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 封包輸出管理
 *
 * @author dexc
 */
public class PacketSc implements Runnable {
    private static final Log _log = LogFactory.getLog(PacketSc.class);
    private final Queue<byte[]> _queue;
    private final ClientExecutor _client;
    private final EncryptExecutor _executor;
    private final Cipher _keys;

    public PacketSc(final ClientExecutor client, final EncryptExecutor executor) {
        _client = client;
        _keys = client.get_keys();
        _executor = executor;
        _queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * 加入工作列隊
     *
     */
    private void requestWork(final byte[] data) {
        _queue.offer(data);
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
            final byte[] data = Arrays.copyOf(encrypt, encrypt.length);
            _keys.encryptHash(data);
            requestWork(data);
        }
    }

    @Override
    public void run() {
        try {
            while (_client.get_socket() != null) {
                for (final Iterator<byte[]> iter = _queue.iterator(); iter.hasNext(); ) {
                    final byte[] decrypt = iter.next();// 返回迭代的下一個元素。
                    // 從迭代器指向的 collection 中移除迭代器返回的最後一個元素
                    iter.remove();
                    outPacket(decrypt);
                    /* 移除可疑的異常停頓 by 聖子默默
                     * TimeUnit.MILLISECONDS.sleep(1);
                     */
                }
                // 隊列為空 休眠
                TimeUnit.MILLISECONDS.sleep(10);
            }
            // finalize();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 移除此 collection 中的所有元素
            _queue.clear();
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
            _executor.out().flush();
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
    }
}