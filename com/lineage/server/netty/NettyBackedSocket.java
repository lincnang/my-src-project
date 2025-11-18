package com.lineage.server.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 以 Netty Channel 為後端的假 Socket，提供 ClientExecutor 建構子所需介面。
 * 僅實作 ClientExecutor 會用到的方法，其餘丟出 UnsupportedOperationException。
 */
class NettyBackedSocket extends Socket {
    private final InetAddress remoteAddress;
    private final int remotePort;
    private final InetAddress localAddress;
    private final int localPort;
    private final InputStream in;
    private final OutputStream out;
    private volatile boolean closed = false;
    private volatile boolean connected = true;
    private volatile boolean bound = true;

    NettyBackedSocket(InetAddress remoteAddress, int remotePort,
                      InetAddress localAddress, int localPort,
                      InputStream in, OutputStream out) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.in = in;
        this.out = out;
    }

    @Override
    public InetAddress getInetAddress() { return remoteAddress; }

    @Override
    public int getPort() { return remotePort; }

    @Override
    public InetAddress getLocalAddress() { return localAddress; }

    @Override
    public int getLocalPort() { return localPort; }

    @Override
    public InputStream getInputStream() throws IOException { return in; }

    @Override
    public OutputStream getOutputStream() throws IOException { return out; }

    @Override
    public synchronized void close() throws IOException {
        closed = true;
        connected = false;
        if (out != null) out.close();
        if (in != null) in.close();
    }

    @Override
    public boolean isClosed() { return closed; }

    @Override
    public boolean isConnected() { return connected; }

    @Override
    public boolean isBound() { return bound; }

    // 其餘方法不會被使用，避免誤用
    @Override
    public void connect(SocketAddress endpoint) throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException { throw new UnsupportedOperationException(); }

    // ======== No-op overrides for socket options used by ClientExecutor ========
    @Override
    public void setTcpNoDelay(boolean on) {}

    @Override
    public void setKeepAlive(boolean on) {}

    @Override
    public void setReceiveBufferSize(int size) {}

    @Override
    public void setSendBufferSize(int size) {}

    @Override
    public void setSoTimeout(int timeout) {}

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {}

    @Override
    public void setSoLinger(boolean on, int linger) {}

    @Override
    public void setReuseAddress(boolean on) {}

    @Override
    public void setOOBInline(boolean on) {}
}
