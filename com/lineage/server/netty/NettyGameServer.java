package com.lineage.server.netty;

import com.lineage.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Netty 遊戲伺服器
 * 完全取代原有的 Socket 監聽機制
 * 
 * @author Netty Migration
 */
public class NettyGameServer {
    private static final Log _log = LogFactory.getLog(NettyGameServer.class);
    
    private ServerBootstrap bootstrap;
    private final List<Channel> serverChannels = new ArrayList<Channel>();
    private boolean running = false;

    /**
     * 啟動 Netty 伺服器
     */
    public void start() {
        if (running) {
            _log.warn("[NettyGameServer] 伺服器已經在運行中");
            return;
        }

        try {
            _log.info("[NettyGameServer] 正在啟動 Netty 伺服器...");
            
            // 建立 ServerBootstrap
            bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(), // Boss 執行緒池 (接受連線)
                    Executors.newCachedThreadPool()  // Worker 執行緒池 (處理 I/O)
                )
            );

            // 設定 Pipeline 工廠
            bootstrap.setPipelineFactory(new LineagePipelineFactory());

            // 設定 Socket 選項
            bootstrap.setOption("child.keepAlive", true);           // TCP Keep-Alive
            bootstrap.setOption("child.tcpNoDelay", true);          // 禁用 Nagle 演算法
            bootstrap.setOption("child.receiveBufferSize", 256 * 1024); // 接收緩衝區 256KB
            bootstrap.setOption("child.sendBufferSize", 256 * 1024);    // 發送緩衝區 256KB
            // 寫入高/低水位：降低 writability 抖動（零風險調參）
            bootstrap.setOption("child.writeBufferHighWaterMark", 4 * 1024 * 1024); // 4MB
            bootstrap.setOption("child.writeBufferLowWaterMark",  2 * 1024 * 1024); // 2MB
            bootstrap.setOption("connectTimeoutMillis", 30000);     // 連線超時 30秒
            bootstrap.setOption("reuseAddress", true);              // 重用位址

            // 綁定端口（支援以 '-' 分隔的多端口，例如 "2000-2001-2002"）
            String portsConfig = Config.GAME_SERVER_PORT;
            String[] parts = portsConfig.split("-");
            for (String p : parts) {
                int port = Integer.parseInt(p.trim());
                Channel ch = bootstrap.bind(new InetSocketAddress(port));
                serverChannels.add(ch);
                _log.info("[NettyGameServer] 綁定端口: " + port);
            }

            running = true;
            
            _log.info("=========================================");
            _log.info("  Netty 遊戲伺服器已成功啟動!");
            _log.info("  監聽端口: " + Config.GAME_SERVER_PORT);
            _log.info("  架構: Netty Channel (非阻塞 I/O)");
            _log.info("=========================================");

        } catch (Exception e) {
            _log.error("[NettyGameServer] 啟動失敗", e);
            shutdown();
            throw new RuntimeException("Netty 伺服器啟動失敗", e);
        }
    }

    /**
     * 關閉 Netty 伺服器
     */
    public void shutdown() {
        if (!running) {
            return;
        }

        _log.info("[NettyGameServer] 正在關閉 Netty 伺服器...");

        try {
            // 關閉伺服器 Channels
            if (!serverChannels.isEmpty()) {
                for (Channel ch : serverChannels) {
                    if (ch != null) {
                        ch.close().awaitUninterruptibly();
                    }
                }
                serverChannels.clear();
            }

            // 釋放資源
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
                bootstrap = null;
            }

            running = false;
            
            _log.info("[NettyGameServer] Netty 伺服器已關閉");

        } catch (Exception e) {
            _log.error("[NettyGameServer] 關閉時發生異常", e);
        }
    }

    /**
     * 檢查是否正在運行
     */
    public boolean isRunning() {
        return running;
    }
}
