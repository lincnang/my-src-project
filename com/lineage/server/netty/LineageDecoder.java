package com.lineage.server.netty;

import com.lineage.server.netty.manager.DecoderManager;
import com.lineage.echo.ClientExecutor;
import com.lineage.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Lineage 封包解碼器
 * 負責:
 * 1. 解析封包長度 (2 bytes little-endian)
 * 2. 解密封包資料
 * 3. 傳遞給 GameClientHandler
 * 
 * @author Netty Migration
 */
public class LineageDecoder extends FrameDecoder {
    private static final Log _log = LogFactory.getLog(LineageDecoder.class);
    private static final int MAX_PACKET_SIZE = 16384; // 16KB 最大封包大小（驗證後）
    private static final int MAX_PACKET_SIZE_PRE_VERIFY = 1024; // 握手完成前的最大長度（避免攻擊/掃描）
    private static final int MIN_ENCRYPTED_PAYLOAD = 4; // Cipher.decryptClient 需要至少 4 bytes 才不會 AIOOBE

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        // 至少需要 2 bytes 長度頭
        if (buffer.readableBytes() < 2) {
            return null; // 等待更多資料
        }

        // 標記當前讀取位置
        buffer.markReaderIndex();

        // 讀取封包長度 (little-endian)，若啟用 AutoAuthentication，需先還原 XOR
        int lowRaw = buffer.readByte() & 0xFF;   // 低位元組在前（raw）
        int highRaw = buffer.readByte() & 0xFF;  // 高位元組在後（raw）

        int low = lowRaw;
        int high = highRaw;
        if (Config.LOGINS_TO_AUTOENTICATION) {
            try {
                ClientExecutor legacy = NettyChannelRegistry.legacy(channel);
                if (legacy != null) {
                    int xor = legacy._xorByte & 0xFF;
                    low = (lowRaw ^ xor) & 0xFF;
                    high = (highRaw ^ xor) & 0xFF;
                }
            } catch (Throwable ignore) { /* 若無法取得 legacy，沿用 raw 值，後續長度檢查會保護 */ }
        }
        int length = (low) | (high << 8);
    int payloadLen = length - 2; // 協議長度包含2個長度位元組

        // 防護: 檢查握手/版本驗證前的封包大小上限
        boolean preVerify = false;
        try {
            com.lineage.server.netty.GameClient gc = NettyChannelRegistry.client(channel);
            preVerify = (gc == null || !gc.isVersionVerified());
        } catch (Throwable ignore) { preVerify = true; }

        int maxAllowed = preVerify ? MAX_PACKET_SIZE_PRE_VERIFY : MAX_PACKET_SIZE;

        // 防護:檢查封包長度是否合理（以有效負載衡量）
        if (payloadLen < MIN_ENCRYPTED_PAYLOAD || payloadLen > maxAllowed) {
            _log.error("【封包異常】異常封包長度 (可能是版本不符或攻擊): length=" + length + ", payload=" + payloadLen + " from " + channel.getRemoteAddress());
            buffer.resetReaderIndex();
            channel.close(); // 關閉異常連線
            return null;
        }

        // 檢查是否有完整封包
        if (buffer.readableBytes() < payloadLen) {
            buffer.resetReaderIndex(); // 重置讀取位置
            return null; // 等待完整封包
        }

        // 讀取加密資料；若啟用 AutoAuthentication，先還原 XOR 再交由 Cipher 解密
        byte[] encrypted = new byte[payloadLen];
        buffer.readBytes(encrypted);
        if (Config.LOGINS_TO_AUTOENTICATION) {
            try {
                ClientExecutor legacy = NettyChannelRegistry.legacy(channel);
                if (legacy != null) {
                    int xor = legacy._xorByte & 0xFF;
                    for (int i = 0; i < encrypted.length; i++) {
                        encrypted[i] = (byte) (((int) encrypted[i] & 0xFF) ^ xor);
                    }
                }
            } catch (Throwable ignore) { }
        }

        // 導流到多執行緒解密池，並停止往上游傳遞
        DecoderManager.get().submit(channel, encrypted);
        return null;
    }
}
