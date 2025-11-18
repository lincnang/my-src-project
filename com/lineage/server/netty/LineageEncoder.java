package com.lineage.server.netty;

import com.lineage.server.serverpackets.ServerBasePacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Lineage 封包編碼器
 * 負責:
 * 1. 加密封包資料
 * 2. 加上長度頭 (2 bytes little-endian)
 * 3. 發送給客戶端
 * 
 * @author Netty Migration
 */
public class LineageEncoder extends OneToOneEncoder {
    private static final Log _log = LogFactory.getLog(LineageEncoder.class);

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        // 只處理 ServerBasePacket
        if (!(msg instanceof ServerBasePacket)) {
            return msg;
        }

        ServerBasePacket packet = (ServerBasePacket) msg;
        
        // 取得 GameClient
    GameClient client = NettyChannelRegistry.client(channel);
        if (client == null || !client.getEncryption().isInitialized()) {
            _log.warn("[LineageEncoder] GameClient 或加密器未就緒");
            return null;
        }

        try {
            // 取得原始封包資料
            byte[] data = packet.getContent();
            
            // 加密
            byte[] encrypted = client.getEncryption().encrypt(data);
            int length = encrypted.length;
            
            // 建立 ChannelBuffer: [length(2)] + [encrypted data]
            ChannelBuffer buffer = ChannelBuffers.buffer(2 + length);
            
            // 寫入長度 (little-endian)
            buffer.writeByte(length & 0xFF);
            buffer.writeByte((length >> 8) & 0xFF);
            
            // 寫入加密資料
            buffer.writeBytes(encrypted);
            
            if (_log.isDebugEnabled()) {
                _log.debug("[LineageEncoder] 加密封包: length=" + length);
            }
            
            return buffer;
            
        } catch (Exception e) {
            _log.error("[LineageEncoder] 加密失敗: " + client.getIp(), e);
            return null;
        }
    }
}
