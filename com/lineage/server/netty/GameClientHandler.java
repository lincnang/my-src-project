package com.lineage.server.netty;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.EncryptExecutor;
import com.lineage.echo.PacketHandler;
import com.lineage.echo.encryptions.Cipher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Netty 業務處理器
 * 負責處理連線生命週期和封包路由
 * 
 * @author Netty Migration
 */
public class GameClientHandler extends SimpleChannelHandler {
    private static final Log _log = LogFactory.getLog(GameClientHandler.class);
    // 簡單的排程器（共用）
    private static final ScheduledExecutorService SCHED = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = e.getChannel();
        
        // 取得純 IP 位址
        java.net.InetSocketAddress remoteAddress = (java.net.InetSocketAddress) channel.getRemoteAddress();
        String ipAddr = remoteAddress.getAddress().getHostAddress();
        
        // 記錄連線訊息
        int port = ((java.net.InetSocketAddress) channel.getLocalAddress()).getPort();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n--------------------------------------------------");
        stringBuilder.append("\n       客戶端 連線遊戲伺服器 服務端口:(").append(port).append(")");
        stringBuilder.append("\n       ").append(ipAddr);
        stringBuilder.append("\n--------------------------------------------------");
        _log.info(stringBuilder.toString());
        
        // 生成隨機 seed
        long seed = (long) (Math.random() * Integer.MAX_VALUE);
        
        // 建立 GameClient
        GameClient client = new GameClient(channel, seed);
        
    // 註冊到共用註冊器
    NettyChannelRegistry.client(channel, client);
        
        // 建立橋接用的 Input/OutputStream
        NettyPlainInputStream in = new NettyPlainInputStream();
        NettyChannelOutputStream out = new NettyChannelOutputStream(channel);

        // 建立一個最小化的 Socket 讓 ClientExecutor 可運作
        java.net.InetSocketAddress remote = (java.net.InetSocketAddress) channel.getRemoteAddress();
        java.net.InetSocketAddress local = (java.net.InetSocketAddress) channel.getLocalAddress();
        NettyBackedSocket fakeSocket = new NettyBackedSocket(
                remote.getAddress(), remote.getPort(),
                local.getAddress(), local.getPort(),
                in, out);

        // 生成對應的 ClientExecutor（使用假 Socket）
        ClientExecutor legacyClient = new ClientExecutor(fakeSocket);
        // 對齊 Cipher seed
        Cipher keys = legacyClient.get_keys();
        keys.initKeys((int) seed);
        // 讓 EncryptExecutor 使用我們的 seed，避免第一包不同步
    EncryptExecutor enc = legacyClient.out();
    enc.setSeed(seed);
    enc.satrt(); // 啟動封包輸出工作執行緒（不呼叫 outStart 以避免重複握手）

    // 建立並綁定舊 PacketHandler
    PacketHandler handler = new PacketHandler(legacyClient);
    NettyChannelRegistry.handler(channel, handler);
    NettyChannelRegistry.legacy(channel, legacyClient);
        // 綁定到 GameClient 以統一發包路徑
        client.bindLegacyClient(legacyClient);

        // 若開啟 Autoentication，先發送 4 bytes authdata（與舊 EncryptExecutor.outStart 相同順序）
        if (Config.LOGINS_TO_AUTOENTICATION) {
            long auth = legacyClient._authdata;
            org.jboss.netty.buffer.ChannelBuffer authBuf = org.jboss.netty.buffer.ChannelBuffers.buffer(4);
            authBuf.writeByte((int) (auth & 0xFF));
            authBuf.writeByte((int) ((auth >> 8) & 0xFF));
            authBuf.writeByte((int) ((auth >> 16) & 0xFF));
            authBuf.writeByte((int) ((auth >> 24) & 0xFF));
            channel.write(authBuf);
        }

        // 發送握手封包
        client.sendHandshake();
        
        _log.debug("[GameClientHandler] GameClient 已建立並發送握手: " + ipAddr);

        // 安排版本驗證逾時(8 秒未完成 C_VERSION 則關閉),並在 3 秒時重送一次握手
        final GameClient gcRef = client;
        // 3 秒重送一次握手（避免客戶端漏包）
        SCHED.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!gcRef.isClosed() && !gcRef.isVersionVerified()) {
                        gcRef.resendHandshake();
                    }
                } catch (Throwable ignore) { }
            }
        }, 3, TimeUnit.SECONDS);

        SCHED.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!gcRef.isClosed() && !gcRef.isVersionVerified()) {
                        _log.warn("[GameClientHandler] 版本驗證逾時，關閉: " + gcRef.getIp());
                        gcRef.close();
                    }
                } catch (Throwable ignore) { }
            }
        }, 8, TimeUnit.SECONDS);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // 解碼後的封包資料 (已去掉2-byte長度與解密)
        byte[] data = (byte[]) e.getMessage();
        Channel channel = e.getChannel();
        
        // 取得 GameClient
    GameClient client = NettyChannelRegistry.client(channel);
        if (client == null) {
            _log.warn("[GameClientHandler] GameClient 不存在,關閉連線");
            channel.close();
            return;
        }
        
        // 直接透過舊 PacketHandler 執行路由
        // PacketHandler 需要 ClientExecutor 實例，但我們沒有持有，
        // 因此將其保存於 Channel 的屬性中
        PacketHandler handler = NettyChannelRegistry.handler(channel);
        if (handler != null) {
            handler.handlePacket(data);
        } else {
            // fallback：使用 GameClient 處理（但預期不會走到）
            client.handlePacket(data);
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = e.getChannel();
    GameClient client = NettyChannelRegistry.client(channel);
        // 補發 S_Disconnect 給客戶端（盡力而為）
        com.lineage.echo.ClientExecutor legacy = NettyChannelRegistry.legacy(channel);
        if (legacy != null) {
            try {
                legacy.markExpectedClose();
            } catch (Throwable ignore) { /* ignore */ }
            try { legacy.out().encrypt(new com.lineage.server.serverpackets.S_Disconnect()); } catch (Throwable ignore) {}
            try { legacy.close(); } catch (Throwable ignore) {}
        }
        if (client != null) client.close();
    NettyChannelRegistry.client(channel, null);
    NettyChannelRegistry.handler(channel, null);
    NettyChannelRegistry.legacy(channel, null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable cause = e.getCause();
        Channel channel = e.getChannel();
        GameClient client = NettyChannelRegistry.client(channel);
        
        String ip = client != null ? client.getIp() : channel.getRemoteAddress().toString();
        
        // 只記錄非正常關閉的異常
        boolean expectedDisconnect = false;
        if (cause instanceof java.nio.channels.ClosedChannelException) {
            expectedDisconnect = true;
        } else if (cause instanceof java.io.IOException) {
            String msg = cause.getMessage();
            if (msg == null) {
                expectedDisconnect = true;
            } else {
                String normalized = msg.toLowerCase(Locale.ROOT);
                if (normalized.contains("connection reset")
                        || normalized.contains("forcibly closed")
                        || normalized.contains("existing connection was forcibly closed")
                        || msg.contains("遠端主機已強制關閉一個現存的連線")) {
                    expectedDisconnect = true;
                }
            }
        }

        if (!expectedDisconnect) {
            _log.error("[GameClientHandler] 處理異常: " + ip, cause);
        } else if (_log.isDebugEnabled()) {
            _log.debug("[GameClientHandler] 連線已由遠端關閉: " + ip + " msg=" + String.valueOf(cause.getMessage()));
        }
        
        // 輸出最近出站封包追蹤
        // 關閉連線
        // 補發 S_Disconnect
        com.lineage.echo.ClientExecutor legacy = NettyChannelRegistry.legacy(channel);
        if (legacy != null) {
            try { legacy.out().encrypt(new com.lineage.server.serverpackets.S_Disconnect()); } catch (Throwable ignore) {}
            try { legacy.close(); } catch (Throwable ignore) {}
        }
        if (client != null) client.close();
        channel.close();
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            IdleStateEvent evt = (IdleStateEvent) e;
            Channel channel = evt.getChannel();
            GameClient client = NettyChannelRegistry.client(channel);

            // 讀/全閒置則斷線
            if (evt.getState() == IdleState.READER_IDLE || evt.getState() == IdleState.ALL_IDLE) {
                com.lineage.echo.ClientExecutor legacy = NettyChannelRegistry.legacy(channel);
                if (legacy != null) {
                    try { legacy.out().encrypt(new com.lineage.server.serverpackets.S_Disconnect()); } catch (Throwable ignore) {}
                    try { legacy.close(); } catch (Throwable ignore) {}
                }
                if (client != null) client.close();
                channel.close();
                return;
            }
        }
        super.handleUpstream(ctx, e);
    }
}
