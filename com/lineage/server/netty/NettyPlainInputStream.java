package com.lineage.server.netty;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 供舊 DecryptExecutor 讀取的 InputStream，
 * 由 Netty 解碼器將已解密的資料段 push 進來。
 */
class NettyPlainInputStream extends InputStream {
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
    private byte[] current;
    private int index;
    private volatile boolean closed = false;

    public void offer(byte[] data) {
        if (data == null || data.length == 0 || closed) return;
        queue.offer(data);
    }

    @Override
    public int read() throws IOException {
        if (closed) return -1;
        if (current == null || index >= current.length) {
            try {
                current = queue.take();
                index = 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted", e);
            }
        }
        return current[index++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (closed) return -1;
        if (b == null) throw new NullPointerException();
        if (off < 0 || len < 0 || off + len > b.length) throw new IndexOutOfBoundsException();
        int c = read();
        if (c == -1) return -1;
        b[off] = (byte) c;
        int read = 1;
        while (read < len) {
            if (current != null && index < current.length) {
                int n = Math.min(len - read, current.length - index);
                System.arraycopy(current, index, b, off + read, n);
                index += n;
                read += n;
            } else {
                break; // 讓上層多次呼叫 read
            }
        }
        return read;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        queue.clear();
    }
}
