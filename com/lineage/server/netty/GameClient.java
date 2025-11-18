package com.lineage.server.netty;

import com.lineage.echo.PacketHandler;
import com.lineage.list.OnlineUser;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;

/**
 * Netty 客戶端管理器
 * 完全取代原有的 ClientExecutor (Socket-based)
 * 
 * @author Netty Migration
 */
public class GameClient {
    private static final Log _log = LogFactory.getLog(GameClient.class);

    // === Netty 相關 ===
    private final Channel channel;
    private final LineageEncryption encryption;
    private final long seed;
    
    // === 封包處理 ===
    private final PacketHandler packetHandler;
    
    // === 連線資訊 ===
    private final String ip;
    private final long connectTime;
    private volatile long lastRecvTime;
    
    // === 帳號與角色 ===
    private L1Account account;
    private L1PcInstance activeChar;
    // 保留對 legacy client 的弱引用以統一路徑
    private ClientExecutor legacyClient;
    
    // === 狀態控制 ===
    private volatile boolean closed = false;
    private boolean handshakeSent = false;
    private volatile boolean versionVerified = false;

    /**
     * 建構子
     * @param channel Netty Channel
     * @param seed 加密種子
     */
    public GameClient(Channel channel, long seed) {
        this.channel = channel;
        this.seed = seed;
        this.connectTime = System.currentTimeMillis();
        this.lastRecvTime = System.currentTimeMillis();
        
        // 取得 IP
        this.ip = channel.getRemoteAddress().toString();
        
        // 初始化加密器
        this.encryption = new LineageEncryption();
        this.encryption.initKeys((int) seed);
        
    // 使用原本的 PacketHandler，透過橋接的 ClientExecutor 介面在 Handler 中取用
    this.packetHandler = null; // 延後於 GameClientHandler 綁定
    }

    /**
     * 發送握手封包 (S_KEY + seed)
     */
    public void sendHandshake() {
        if (handshakeSent) {
            _log.warn("[GameClient] 握手封包已發送,忽略重複呼叫");
            return;
        }

        try {
            // First packet 固定值
            byte[] firstPacket = new byte[]{3, -35, 10, 51, -57, 47, -16, 59};
            int contentLength = 1 + 4 + firstPacket.length; // opcode + seed(4bytes) + firstPacket
            int outLength = contentLength + 2; // 與舊路徑一致，長度欄位=內容長度+2
            
            // 由 Encoder 統一處理加密與長度，此處直接包成原始 bytes 丟到 channel
            // 手握封包不需經由 ServerBasePacket，直接以原始 bytes 發送
            org.jboss.netty.buffer.ChannelBuffer buffer = org.jboss.netty.buffer.ChannelBuffers.buffer(2 + contentLength);
            buffer.writeByte(outLength & 0xFF);
            buffer.writeByte((outLength >> 8) & 0xFF);
            int S_KEY = com.lineage.server.serverpackets.OpcodesServer.S_KEY;
            buffer.writeByte(S_KEY);
            buffer.writeByte((int) (seed & 0xFF));
            buffer.writeByte((int) ((seed >> 8) & 0xFF));
            buffer.writeByte((int) ((seed >> 16) & 0xFF));
            buffer.writeByte((int) ((seed >> 24) & 0xFF));
            buffer.writeBytes(firstPacket);
            org.jboss.netty.channel.ChannelFuture f = channel.write(buffer);
            f.addListener(new org.jboss.netty.channel.ChannelFutureListener() {
                @Override
                public void operationComplete(org.jboss.netty.channel.ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        _log.warn("[GameClient] 握手封包送出失敗: " + ip, future.getCause());
                    }
                }
            });
            handshakeSent = true;
            
        } catch (Exception e) {
            _log.error("[GameClient] 發送握手封包失敗: " + ip, e);
        }
    }

    /** 於逾時前允許重送一次握手封包（某些客戶端可能遺失第一包）。*/
    public void resendHandshake() {
        try {
            byte[] firstPacket = new byte[]{3, -35, 10, 51, -57, 47, -16, 59};
            int contentLength = 1 + 4 + firstPacket.length;
            int outLength = contentLength + 2;
            org.jboss.netty.buffer.ChannelBuffer buffer = org.jboss.netty.buffer.ChannelBuffers.buffer(2 + contentLength);
            buffer.writeByte(outLength & 0xFF);
            buffer.writeByte((outLength >> 8) & 0xFF);
            int S_KEY = com.lineage.server.serverpackets.OpcodesServer.S_KEY;
            buffer.writeByte(S_KEY);
            buffer.writeByte((int) (seed & 0xFF));
            buffer.writeByte((int) ((seed >> 8) & 0xFF));
            buffer.writeByte((int) ((seed >> 16) & 0xFF));
            buffer.writeByte((int) ((seed >> 24) & 0xFF));
            buffer.writeBytes(firstPacket);
            org.jboss.netty.channel.ChannelFuture f = channel.write(buffer);
            f.addListener(new org.jboss.netty.channel.ChannelFutureListener() {
                @Override
                public void operationComplete(org.jboss.netty.channel.ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        _log.warn("[GameClient] 重送握手封包失敗: " + ip, future.getCause());
                    }
                }
            });
        } catch (Exception e) {
            _log.error("[GameClient] 重送握手封包異常: " + ip, e);
        }
    }

    /**
     * 發送封包給客戶端
     * @param packet 伺服器封包
     */
    public void sendPacket(ServerBasePacket packet) {
        if (closed || !channel.isConnected()) {
            _log.warn("[GameClient] 連線已關閉,無法發送封包: " + ip);
            return;
        }

        try {
            // 統一路徑：交由舊 EncryptExecutor 處理，避免與 Netty Encoder 產生不同加密流程
            ClientExecutor lc = this.legacyClient;
            if (lc != null) {
                lc.out().encrypt(packet);
            } else {
                // 後備：若尚未綁定 legacyClient，才走 Encoder
                channel.write(packet);
            }
        } catch (Exception e) {
            _log.error("[GameClient] 發送封包異常: " + ip, e);
        }
    }

    /**
     * 處理接收到的封包 (已解密)
     * @param data 解密後的封包資料
     */
    public void handlePacket(byte[] data) {
        try {
            // 更新最後接收時間
            lastRecvTime = System.currentTimeMillis();
            
            // GameClient 僅作為傳遞者，實際處理在 GameClientHandler 綁定的 legacy PacketHandler
            // 此類保留方法供 handler 呼叫
            
        } catch (Exception e) {
            _log.error("[GameClient] 封包處理異常: " + ip, e);
        }
    }

    /**
     * 關閉連線
     */
    public void close() {
        if (closed) {
            return;
        }
        closed = true;

        try {
            // 先嘗試優雅通知客戶端斷線
            if (legacyClient != null && isConnected()) {
                try {
                    legacyClient.out().encrypt(new com.lineage.server.serverpackets.S_Disconnect());
                } catch (Throwable ignore) { /* ignore */ }
                try {
                    legacyClient.close();
                } catch (Throwable ignore) { /* ignore */ }
            }

            // 登出角色
            if (activeChar != null) {
                _log.info("[GameClient] 角色登出: " + activeChar.getName());
                activeChar.logout();
                activeChar = null;
            }
            
            // 從在線列表移除
            if (account != null) {
                OnlineUser.get().remove(account.get_login());
                _log.info("[GameClient] 帳號登出: " + account.get_login());
                account = null;
            }
            
            // 關閉 Channel
            if (channel.isConnected()) {
                channel.close();
            }
            
            // 記錄離線訊息
            String accountName = (account != null) ? account.get_login() : "未登入";
            String charName = (activeChar != null) ? activeChar.getName() : "無角色";
            long onlineTime = (System.currentTimeMillis() - connectTime) / 1000;
            
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n--------------------------------------------------");
            stringBuilder.append("\n       客戶端 離線: (");
            stringBuilder.append(accountName + " / " + charName + " / ");
            stringBuilder.append(ip + ") 完成連線中斷!!");
            stringBuilder.append("\n       在線時間: " + onlineTime + "秒");
            stringBuilder.append("\n--------------------------------------------------");
            _log.info(stringBuilder.toString());
            
        } catch (Exception e) {
            _log.error("[GameClient] 關閉連線時發生異常: " + ip, e);
        }
    }

    // ========== Getters & Setters ==========

    public Channel getChannel() {
        return channel;
    }

    public LineageEncryption getEncryption() {
        return encryption;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler; // 可能為 null，實際使用以 GameClientHandler 綁定的為準
    }

    public long getSeed() {
        return seed;
    }

    public String getIp() {
        return ip;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public long getLastRecvTime() {
        return lastRecvTime;
    }

    public L1Account getAccount() {
        return account;
    }

    public void setAccount(L1Account account) {
        this.account = account;
    }

    public L1PcInstance getActiveChar() {
        return activeChar;
    }

    public void setActiveChar(L1PcInstance pc) {
        this.activeChar = pc;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    // 供 Handler 綁定 legacy client
    public void bindLegacyClient(ClientExecutor client) {
        this.legacyClient = client;
    }

    // 版本驗證狀態
    public boolean isVersionVerified() { return versionVerified; }
    public void setVersionVerified(boolean v) { this.versionVerified = v; }
}
