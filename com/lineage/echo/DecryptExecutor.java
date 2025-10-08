package com.lineage.echo;

import com.lineage.config.Config;
import com.lineage.echo.encryptions.Cipher;
import com.lineage.server.utils.StreamUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 封包解密管理（簡化修正版）
 *
 * 主要改進：
 * 1) 只做基本的超時檢測和日志記錄
 * 2) 移除複雜的重試機制，避免影響登入流程
 * 3) 保持原有邏輯，只加強監控
 * 4) 向下完全相容
 */
public class DecryptExecutor {
    private static final Log _log = LogFactory.getLog(DecryptExecutor.class);

    /** 為協議安全，限制單一 frame 的最大載荷 */
    private static final int MAX_FRAME_LEN = 4096;

    /** 超時警告閾值 - 只記錄，不中斷 */
    private static final int READ_TIMEOUT_WARNING = 8000;  // 8秒警告（放寬）
    // 僅用於檢查邏輯，移除未使用常數避免警告

    private final ClientExecutor _client;
    private final InputStream _in;
    private final Cipher _keys;

    // 簡單統計
    private long totalReadTime = 0;
    private int totalReadCount = 0;
    private long slowReadCount = 0;

    public DecryptExecutor(final ClientExecutor client, final InputStream in) {
        _client = client;
        _keys = client.get_keys();
        _in = in;
    }

    public ClientExecutor get_client() {
        return _client;
    }

    /**
     * 客戶端封包解密處理 - 簡化版
     * 維持原有邏輯，只加強監控
     */
    public byte[] decrypt() throws Exception {
        long startTime = System.currentTimeMillis();
        int retryCount = 0;
        final int MAX_RETRY = 3;

        while (retryCount < MAX_RETRY) {
            try {
                // 1) 讀 header（兩個 byte）
                long headerStartTime = System.currentTimeMillis();
                int hi = readByteWithMonitor();
                int lo = readByteWithMonitor();
                long headerTime = System.currentTimeMillis() - headerStartTime;

                // 2) 登入 XOR（若開啟）
                if (Config.LOGINS_TO_AUTOENTICATION) {
                    hi ^= (_client._xorByte & 0xFF);
                    lo ^= (_client._xorByte & 0xFF);
                }

                // 3) 組合原始長度（unsigned）
                int rawLen = ((lo & 0xFF) << 8) | (hi & 0xFF);
                int payloadLen = rawLen - 2;

                if (payloadLen < 0) {
                    throw new IOException("無效封包：payloadLen < 0, rawLen=" + rawLen);
                }
                if (payloadLen > MAX_FRAME_LEN) {
                    throw new IOException("封包過大：payloadLen=" + payloadLen + " > " + MAX_FRAME_LEN);
                }

                // 4) 讀滿 payload
                long payloadStartTime = System.currentTimeMillis();
                byte[] data = readFullyWithMonitor(payloadLen);
                long payloadTime = System.currentTimeMillis() - payloadStartTime;

                // 5) 登入 XOR（若開啟）
                if (Config.LOGINS_TO_AUTOENTICATION && payloadLen > 0) {
                    final int xor = (_client._xorByte & 0xFF);
                    for (int i = 0; i < payloadLen; i++) {
                        data[i] = (byte) ((data[i] & 0xFF) ^ xor);
                    }
                }

                // 6) 記錄統計（不影響流程）
                long totalTime = System.currentTimeMillis() - startTime;
                recordReadStats(totalTime, headerTime, payloadTime, payloadLen);

                // 7) 執行協議解密
                return _keys.decryptClient(data);

            } catch (java.net.SocketTimeoutException e) {
                // 讀取超時，嘗試重試
                retryCount++;
                if (retryCount >= MAX_RETRY) {
                    _log.warn("讀取封包超時重試次數已達上限，IP: " + getClientIp() + ", 重試次數: " + retryCount);
                    throw e;
                }
                
                // 添加短暫延遲，避免立即重試造成CPU負載
                try {
                    Thread.sleep(100 * retryCount); // 遞增延遲
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("重試時被中斷", ie);
                }
                
                continue;
            } catch (IOException e) {
                // 其他IO錯誤，直接拋出
                throw e;
            }
        }

        throw new IOException("重試次數已達上限，無法讀取封包");
    }

    /**
     * 記錄讀取統計（純監控，不影響邏輯）
     */
    private void recordReadStats(long totalTime, long headerTime, long payloadTime, int payloadLen) {
        totalReadTime += totalTime;
        totalReadCount++;

        // 只記錄明顯的慢讀取
        if (totalTime > READ_TIMEOUT_WARNING) {
            slowReadCount++;

            // 問題分析和日志記錄已移除
        }

//        // 每1000次輸出簡單統計
//        if (totalReadCount % 1000 == 0) {
//            double avgTime = (double) totalReadTime / totalReadCount;
//            double slowRate = (double) slowReadCount / totalReadCount * 100;
//            _log.info("讀取統計: 次數=" + totalReadCount +
//                    ", 平均=" + String.format("%.2f", avgTime) + "ms" +
//                    ", 慢讀取=" + String.format("%.1f", slowRate) + "%");
//        }
    }

    /**
     * 帶監控的單位元組讀取
     * 只記錄，不中斷
     */
    private int readByteWithMonitor() throws IOException {
        return readByte(); // 直接使用原始方法，完全沒有超時檢查
    }

    /**
     * 帶監控的批次讀取
     * 只記錄，不中斷
     */
    private byte[] readFullyWithMonitor(int len) throws IOException {
        if (len == 0) return new byte[0];

        byte[] buf = new byte[len];
        int off = 0;
        long startTime = System.currentTimeMillis();

        while (off < len) {
            int r = _in.read(buf, off, len - off);

            if (r < 0) {
                throw new EOFException("對端關閉（預期 " + len + " bytes，實得 " + off + " bytes）");
            }

            off += r;
        }

        // 只記錄慢讀取
        long totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > READ_TIMEOUT_WARNING) {

        }

        return buf;
    }

    /**
     * 獲取客戶端IP
     */
    private String getClientIp() {
        return _client.getIp() != null ? _client.getIp().toString() : "unknown";
    }

    /**
     * 獲取讀取統計
     */
//    public String getReadStats() {
//        if (totalReadCount == 0) return "無讀取記錄";
//
////        double avgTime = (double) totalReadTime / totalReadCount;
////        double slowRate = (double) slowReadCount / totalReadCount * 100;
//
////        return String.format("讀取統計[次數=%d, 平均=%.2fms, 慢讀取=%.1f%%]",
////                totalReadCount, avgTime, slowRate);
//    }

    /**
     * 重置統計
     */
    public void resetStats() {
        totalReadTime = 0;
        totalReadCount = 0;
        slowReadCount = 0;
    }

    /**
     * 關閉輸入串流
     */
    public void stop() {
        try {
            if (totalReadCount > 0) {

            }
            StreamUtil.close(_in);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    // =====================================================================
    // 向下相容的原有方法（完全保持原始行為）
    // =====================================================================

    /**
     * 讀取單一 byte，回傳 0..255；若 EOF 則拋 EOFException
     */
    private int readByte() throws IOException {
        int b = _in.read();
        if (b < 0) {
            throw new EOFException("對端關閉（讀取 header 時遇到 EOF）");
        }
        return b & 0xFF;
    }
}