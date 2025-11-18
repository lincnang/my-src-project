package com.lineage.server.netty;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 將傳統 OutputStream 寫入導向到 Netty Channel。
 * PacketSc 會先寫入長度兩個 byte 再寫入內容，並在最後 flush()
 * 這裡在 flush 時一次性送出。
 */
class NettyChannelOutputStream extends OutputStream {
    private final Channel channel;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
    private volatile boolean closed = false;
    // 簡單批次策略：當累積超過此閾值時嘗試送出
    private static final int AUTO_FLUSH_THRESHOLD = 8 * 1024; // 8KB

    NettyChannelOutputStream(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) return;
        buffer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (closed) return;
        if (b == null) return;
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        buffer.write(b, off, len);
        // 若累積資料過大，嘗試自動送出，避免佔用過多記憶體（不再檢查 isWritable，交由 Netty 內部處理）
        if (buffer.size() >= AUTO_FLUSH_THRESHOLD && channel.isConnected()) {
            internalFlush();
        }
    }

    @Override
    public void flush() throws IOException {
        if (closed) return;
        if (!channel.isConnected()) {
            buffer.reset();
            return;
        }
        internalFlush();
    }

    @Override
    public void close() throws IOException {
        closed = true;
        buffer.reset();
    }

    private void internalFlush() {
        byte[] bytes = buffer.toByteArray();
        if (bytes.length > 0) {
            channel.write(ChannelBuffers.wrappedBuffer(bytes));
        }
        buffer.reset();
    }
}
