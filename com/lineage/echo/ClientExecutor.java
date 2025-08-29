package com.lineage.echo;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigIpCheck;
import com.lineage.echo.encryptions.Cipher;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import java.net.SocketException;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.StreamUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * 客戶資料處理 - 簡單修正版
 *
 * 主要修正：
 * 1) 調整連線參數，提高穩定性
 * 2) 改善 BadPacket 計數器重置機制
 * 3) 優化 Socket 設定
 * 4) 改善異常處理日志
 *
 * @author dexc + Lin強化版 + 簡單修正
 */
public class ClientExecutor extends OpcodesClient implements Runnable {

    private static final Log _log = LogFactory.getLog(ClientExecutor.class);

    /** 移動最大封包處理量（沿用既有語義） */
    private static final int M = 3;
    /** 人物其他動作最大封包處理量（沿用既有語義） */
    private static final int O = 2;

    // === 連線參數 - 進一步優化，減少斷線問題 ===
    /** 握手期間讀取逾時（進一步放寬到90秒） */
    private static final int READ_TIMEOUT_MS_HANDSHAKE = 90_000;
    /** 握手後讀取逾時（放寬到15分鐘，適應慢速網絡） */
    private static final int READ_TIMEOUT_MS_AFTER = 900_000;
    /** 閒置殺手（放寬到2小時，避免正常玩家被踢） */
    private static final int IDLE_KILL_MS = 7_200_000;
    /** Watchdog 週期（調整為5分鐘，減少頻繁檢查） */
    private static final int WATCHDOG_PERIOD_MS = 300_000;
    /** 過長封包允許的最大長度（進一步放寬） */
    private static final int MAX_PACKET_LEN = 16384;
    /** 過長/非法封包容忍次數（提高到20次，避免網絡抖動誤判） */
    private static final int BAD_PACKET_THRESHOLD = 20;

    // === 協議/加解密 ===
    public int _xorByte = (byte) 0xf0;
    public long _authdata;
    private Cipher _keys;

    // === 連線/帳號/角色 ===
    private Socket _csocket;
    private L1Account _account;            // 連線帳戶資料
    private L1PcInstance _activeChar;      // 登入人物資料
    private StringBuilder _ip;             // 連線IP資料
    private StringBuilder _mac;            // MAC資料

    // === 狀態 ===
    private volatile boolean _isStrat = true; // 沿用舊名，作為主循環旗標
    private volatile boolean closed = false;  // close() 重入保護
    private int _kick = 0;
    private int _error = -1;                 // 錯誤次數（沿用）
    private int _saveInventory = 0;
    
    // === 连接监控相关 ===
    private long _connectTime = System.currentTimeMillis(); // 连接建立时间
    private volatile long _lastRecv = System.currentTimeMillis(); // 最后接收数据时间
    private int _savePc = 0;

    // 封包處理
    private EncryptExecutor _encrypt;      // 封包加密管理
    private DecryptExecutor _decrypt;      // 封包解密管理
    private PacketHandlerExecutor _handler;// 資料處理者

    // 任務 future（可取消）
    private Future<?> moveFuture;
    private Future<?> otherFuture;
    private Future<?> watchdogFuture;

    // 心跳/閒置 - 改進活動檢測
    private volatile long lastRecvAt = System.currentTimeMillis();
    private volatile long lastActivityAt = System.currentTimeMillis(); // 最後活動時間

    // 封包防護計數 - 新增重置機制
    private int badPacketCount = 0;
    private long lastBadPacketTime = 0;
    private static final long BAD_PACKET_RESET_MS = 600_000; // 10分鐘重置（更寬鬆）

    /**
     * 啟用設置 - 優化Socket參數
     */
    public ClientExecutor(final Socket socket) throws IOException {
        _csocket = Objects.requireNonNull(socket, "socket");

        // === 優化Socket參數設定 - 防止Connection Reset ===
        try {
            // 1. TCP基本優化
            _csocket.setTcpNoDelay(true);              // 禁用Nagle算法，減少延遲
            _csocket.setKeepAlive(true);               // 啟用TCP KeepAlive檢測
            
            // 2. 合理的緩衝區大小 - 避免過大導致內存問題
            _csocket.setReceiveBufferSize(256 * 1024); // 256KB 接收緩衝區
            _csocket.setSendBufferSize(256 * 1024);    // 256KB 發送緩衝區
            
            // 3. 連接超時設置
            _csocket.setSoTimeout(READ_TIMEOUT_MS_HANDSHAKE);
            // 注意：Socket.setConnectTimeout只在連接時有效，這裡設置SO_TIMEOUT
            // _csocket.setConnectTimeout(30000);      // 連接超時在accept時已設定
            
            // 4. 性能優化 - 優先考慮連接時間和延遲
            _csocket.setPerformancePreferences(1, 2, 0);
            
            // 5. 優雅關閉設置 - 防止強制RST
            _csocket.setSoLinger(true, 30);            // 30秒內完成數據傳輸後關閉
            
            // 6. 重用地址 - 防止TIME_WAIT狀態問題
            _csocket.setReuseAddress(true);
            
            // 7. 禁用OOB數據 - 避免異常數據干擾
            _csocket.setOOBInline(false);
            
            _log.debug("Socket參數設置完成 - IP: " + _csocket.getInetAddress().getHostAddress() + 
                      " 本地端口: " + _csocket.getLocalPort() + " 遠程端口: " + _csocket.getPort());
                      
        } catch (Throwable t) {
            _log.warn("設定 socket 參數發生例外（使用系統預設）: " + t.getMessage(), t);
        }

        // 伺服器捆綁（沿用）
        if (Config.LOGINS_TO_AUTOENTICATION) {
            final int randomNumber = (int) ((Math.random() * 253D) + 1.0D);
            _xorByte = (randomNumber % 255) + 1;
            _authdata = new BigInteger(Integer.toString(_xorByte))
                    .modPow(new BigInteger(Config.RSA_KEY_E), new BigInteger(Config.RSA_KEY_N))
                    .longValue();
        }

        _ip = new StringBuilder().append(_csocket.getInetAddress().getHostAddress());
        _handler = new PacketHandler(this);
        _keys = new Cipher();
        _decrypt = new DecryptExecutor(this, _csocket.getInputStream());
        _encrypt = new EncryptExecutor(this, _csocket.getOutputStream());
        
        // 記錄新連線到監控器
        try {
            String clientId = this.toString();
            if (clientId != null && !clientId.isEmpty()) {
                ConnectionMonitor.getInstance().recordConnection(clientId);
                // 記錄到網路統計
                if (_ip != null) {
                    NetworkStats.getInstance().recordNewConnection(_ip.toString());
                }
            }
        } catch (Exception e) {
            // 忽略監控器錯誤，不影響主要流程
            _log.debug("記錄連線到監控器時發生錯誤", e);
        }
    }

    /** 舊介面，維持存在（目前未使用） */
    public void start() {
    }

    // =====================================================================
    // Run Loop
    // =====================================================================
    @Override
    public void run() {
        try {
            // 啟動封包處理排程（沿用你原始設計，但我們接住 Future 以便關閉）
            final PacketHc m = new PacketHc(this, M);
            moveFuture = GeneralThreadPool.get().schedule(m, 0);

            final PacketHc o = new PacketHc(this, O);
            otherFuture = GeneralThreadPool.get().schedule(o, 0);

            // 設定自動保存時間
            set_savePc(Config.AUTOSAVE_INTERVAL);
            set_saveInventory(Config.AUTOSAVE_INTERVAL_INVENTORY);

            // 加密端啟動（維持你原本的 API；若拼字是 satrt 就沿用）
            _encrypt.satrt();
            _encrypt.outStart();

            // 啟動 watchdog：檢查閒置連線
            watchdogFuture = GeneralThreadPool.get().schedule(new IdleWatchdog(), WATCHDOG_PERIOD_MS);

            boolean isEcho = false; // 是否完成版本回聲（握手）

            try {
                while (_isStrat) {
                    final byte[] decrypt = readPacket();
                    if (decrypt == null) {

                        break;
                    }

                    // 更新最後收包時間和活動時間
                    touchRecv();
                    updateActivity();

                    // === 改良的封包長度防呆 ===
                    if (decrypt.length > MAX_PACKET_LEN) {
                        long currentTime = System.currentTimeMillis();

                        // 關鍵修正：如果5分鐘沒有異常封包，重置計數器
                        if (currentTime - lastBadPacketTime > BAD_PACKET_RESET_MS) {
                            badPacketCount = 0;

                        }

                        badPacketCount++;
                        lastBadPacketTime = currentTime;

                        _log.warn("客戶端送出長度異常封包: " + _ip + " 帳號:" + (_account != null ? _account.get_login() : "未登入")
                                + " len=" + decrypt.length + " count=" + badPacketCount);

                        if (badPacketCount > BAD_PACKET_THRESHOLD) {
                            _log.error("異常封包次數過多，關閉連線: " + _ip + " count=" + badPacketCount);
                            // 不加入黑名單，只是關閉連線
                            break;
                        }
                        // 未達門檻就忽略此包，繼續循環
                        continue;
                    }

                    // 連線狀態/帳號檢查
                    if (_account != null) {
                        if (!OnlineUser.get().isLan(_account.get_login())) {
                            break;
                        }
                        if (!_account.is_isLoad()) {
                            break;
                        }
                    }

                    // 協定流程
                    final int opcode = decrypt[0] & 0xFF;

                    if (_activeChar == null) {
                        if (opcode == C_VERSION) { // 要求接收伺服器版本（握手完成）
                            try {
                                _csocket.setSoTimeout(READ_TIMEOUT_MS_AFTER); // 握手後改較長但非 0
                            } catch (Throwable ignore) { /* ignore */ }
                            LanSecurityManager.BANIPPACK.remove(_ip.toString());
                            isEcho = true;
                            
                            // 記錄握手成功
                            try {
                                if (_ip != null) {
                                    NetworkStats.getInstance().recordSuccessfulConnection(_ip.toString());
                                }
                            } catch (Exception e) {
                                _log.debug("記錄握手成功時發生錯誤", e);
                            }
                        } else if (opcode == C_LOGIN) {
                            // 確保保存時程
                            set_savePc(Config.AUTOSAVE_INTERVAL);
                            set_saveInventory(Config.AUTOSAVE_INTERVAL_INVENTORY);
                        }
                        if (isEcho) {
                            _handler.handlePacket(decrypt);
                        }
                        continue;
                    }

                    if (!isEcho) {
                        // 未握手成功前的任何封包都忽略（避免垃圾包）
                        continue;
                    }

                    // 交由 handler 處理
                    _handler.handlePacket(decrypt);
                }

            } catch (java.net.SocketException e) {
                // === 改善的Connection Reset處理 ===
                String msg = e.getMessage();
                String clientInfo = "IP: " + getIp() + " 帳號: " + (_account != null ? _account.get_login() : "未登入");
                
                if (msg != null && msg.toLowerCase().contains("connection reset")) {
                    // Connection Reset 詳細分析和記錄
                    _log.warn("Connection Reset 檢測 - " + clientInfo + " 原因: " + analyzeConnectionReset(e));
                    
                    // 記錄到連接監控系統
                    recordConnectionReset(e);
                    
                    // 嘗試優雅處理斷線
                    handleConnectionReset();
                    
                } else if (msg != null && (msg.toLowerCase().contains("connection aborted") || 
                                         msg.toLowerCase().contains("software caused connection abort"))) {
                    _log.warn("連接被中止 - " + clientInfo + " 原因: " + msg);
                    recordConnectionAbort(e);
                    
                } else if (msg != null && msg.toLowerCase().contains("broken pipe")) {
                    _log.warn("管道破裂 - " + clientInfo + " 原因: " + msg);
                    recordBrokenPipe(e);
                    
                } else {
                    _log.info("客戶端連線異常 (SocketException) - " + clientInfo + " 原因: " + msg);
                }
            } catch (EOFException e) {
                // 常見：正常關閉

            } catch (Exception e) {
                // 其他所有非預期的錯誤
                _log.error("處理客戶端 " + this + " 封包時發生未知錯誤", e);
            }

        } finally {
            // 這裡是最終的保護，無論前面發生何事都會被執行
            // 1) 先停止主循環
            _isStrat = false;

            // 2) 取消排程任務（避免殘留線程）
            cancelQuietly(moveFuture);
            cancelQuietly(otherFuture);
            cancelQuietly(watchdogFuture);

            // 3) 移出自動保存
            set_savePc(-1);
            set_saveInventory(-1);

            // 4) 關閉 I/O 與釋放資源
            close();
        }
    }

    // =====================================================================
    // Connection Reset 診斷和處理
    // =====================================================================
    
    /**
     * 分析Connection Reset的可能原因
     */
    private String analyzeConnectionReset(SocketException e) {
        StringBuilder analysis = new StringBuilder();
        
        // 1. 檢查連接時長
        long connectionTime = System.currentTimeMillis() - _lastRecv;
        if (connectionTime > IDLE_KILL_MS) {
            analysis.append("長時間無活動(").append(connectionTime / 1000).append("秒)");
        }
        
        // 2. 檢查Socket狀態
        if (_csocket != null) {
            analysis.append(" Socket狀態[");
            analysis.append("已連接:").append(_csocket.isConnected());
            analysis.append(" 已關閉:").append(_csocket.isClosed());
            analysis.append(" 已綁定:").append(_csocket.isBound());
            analysis.append("]");
        }
        
        // 3. 檢查網絡條件
        String clientIp = getIp() != null ? getIp().toString() : "unknown";
        if (clientIp != null) {
            if (clientIp.startsWith("192.168.") || clientIp.startsWith("10.") || clientIp.startsWith("172.")) {
                analysis.append(" 內網IP");
            } else {
                analysis.append(" 外網IP");
            }
        }
        
        // 4. 檢查當前狀態
        if (_account != null) {
            analysis.append(" 已登入");
        } else {
            analysis.append(" 未登入");
        }
        
        return analysis.toString();
    }
    
    /**
     * 記錄Connection Reset事件
     */
    private void recordConnectionReset(SocketException e) {
        try {
            // 記錄到連接監控器
            ConnectionMonitor.getInstance().recordDisconnection(
                this.toString(), 
                "CONNECTION_RESET", 
                e.getMessage()
            );
            
            // 更新統計計數器
            NetworkStats.getInstance().incrementConnectionResetCount();
            
        } catch (Exception ex) {
            _log.debug("記錄Connection Reset事件時發生錯誤", ex);
        }
    }
    
    /**
     * 記錄Connection Aborted事件
     */
    private void recordConnectionAbort(SocketException e) {
        try {
            ConnectionMonitor.getInstance().recordDisconnection(
                this.toString(), 
                "CONNECTION_ABORTED", 
                e.getMessage()
            );
            NetworkStats.getInstance().incrementConnectionAbortCount();
        } catch (Exception ex) {
            _log.debug("記錄Connection Abort事件時發生錯誤", ex);
        }
    }
    
    /**
     * 記錄Broken Pipe事件
     */
    private void recordBrokenPipe(SocketException e) {
        try {
            ConnectionMonitor.getInstance().recordDisconnection(
                this.toString(), 
                "BROKEN_PIPE", 
                e.getMessage()
            );
            NetworkStats.getInstance().incrementBrokenPipeCount();
        } catch (Exception ex) {
            _log.debug("記錄Broken Pipe事件時發生錯誤", ex);
        }
    }
    
    /**
     * 處理Connection Reset - 嘗試優雅關閉
     */
    private void handleConnectionReset() {
        try {
            // 1. 如果有角色在線，嘗試保存數據
            if (_activeChar != null && !_activeChar.isGhost()) {
                _log.info("Connection Reset - 嘗試保存角色數據: " + _activeChar.getName());
                
                // 強制保存角色數據
                try {
                    _activeChar.save();
                    _log.info("角色數據保存成功: " + _activeChar.getName());
                } catch (Exception saveEx) {
                    _log.error("保存角色數據失敗: " + _activeChar.getName(), saveEx);
                }
                
                // 從遊戲世界移除
                try {
                    _activeChar.logout();
                } catch (Exception logoutEx) {
                    _log.error("角色登出處理失敗", logoutEx);
                }
            }
            
            // 2. 處理斷線（重連功能已移除，簡化處理）
            if (_account != null) {
                _log.debug("玩家斷線: " + _account.get_login());
            }
            
            // 3. 記錄斷線信息用於後續分析
            logDisconnectionInfo();
            
        } catch (Exception e) {
            _log.error("處理Connection Reset時發生錯誤", e);
        }
    }
    
    /**
     * 記錄斷線詳細信息
     */
    private void logDisconnectionInfo() {
        StringBuilder info = new StringBuilder();
        info.append("斷線詳情 - ");
        info.append("IP: ").append(getIp());
        
        if (_account != null) {
            info.append(" 帳號: ").append(_account.get_login());
        }
        
        if (_activeChar != null) {
            info.append(" 角色: ").append(_activeChar.getName());
            info.append(" 等級: ").append(_activeChar.getLevel());
            info.append(" 位置: ").append(_activeChar.getMapId()).append("(").append(_activeChar.getX()).append(",").append(_activeChar.getY()).append(")");
        }
        
        long sessionTime = System.currentTimeMillis() - _connectTime;
        info.append(" 連線時長: ").append(sessionTime / 1000).append("秒");
        
        _log.info(info.toString());
    }

    // =====================================================================
    // 監控 & 工具
    // =====================================================================

    /** 更新最後收包時間（心跳/閒置監控用） */
    private void touchRecv() {
        _lastRecv = System.currentTimeMillis();
    }
    
    /**
     * 更新活動時間 - 用於防止誤判閒置
     */
    private void updateActivity() {
        lastActivityAt = System.currentTimeMillis();
        lastRecvAt = System.currentTimeMillis(); // 同時更新收包時間
    }
    
    /**
     * 手動觸發活動更新 - 供外部調用
     */
    public void touchActivity() {
        updateActivity();
    }

    /** 增強的 watchdog：檢查閒置和連線健康狀態 */
    private final class IdleWatchdog implements Runnable {
        @Override
        public void run() {
            try {
                if (!_isStrat) return;
                
                // 1. 檢查閒置時間 - 使用活動時間而非收包時間
                long idleByActivity = System.currentTimeMillis() - lastActivityAt;
                long idleByReceive = System.currentTimeMillis() - lastRecvAt;
                
                // 只有在活動時間和收包時間都超過閾值時才判定為閒置
                if (idleByActivity > IDLE_KILL_MS && idleByReceive > IDLE_KILL_MS) {
                    _log.info("閒置超時，關閉連線: " + getIpString() + 
                              " 活動閒置=" + (idleByActivity/1000) + "秒, 收包閒置=" + (idleByReceive/1000) + "秒");
                    kick();
                    return;
                }
                
                // 記錄接近閒置的警告
                if (idleByActivity > IDLE_KILL_MS * 0.8) { // 80% 閒置時間時警告
                    _log.warn("連線接近閒置超時: " + getIpString() + 
                              " 活動閒置=" + (idleByActivity/1000) + "秒");
                }
                
                // 2. 檢查Socket連線狀態
                if (_csocket != null && (_csocket.isClosed() || !_csocket.isConnected())) {
                    _log.info("Socket連線已斷開，清理資源: " + getIpString());
                    kick();
                    return;
                }
                
                // 3. 檢查異常封包頻率
                if (badPacketCount > BAD_PACKET_THRESHOLD / 2) {
                    _log.warn("異常封包頻率較高: " + getIpString() + " count=" + badPacketCount);
                }
                
            } catch (Throwable t) {
                _log.warn("IdleWatchdog 例外（忽略）", t);
            } finally {
                // 自我重排（仍在運行才繼續）
                if (_isStrat) {
                    watchdogFuture = GeneralThreadPool.get().schedule(new IdleWatchdog(), WATCHDOG_PERIOD_MS);
                }
            }
        }
    }

    private static void cancelQuietly(Future<?> f) {
        if (f != null) {
            try {
                f.cancel(true);
            } catch (Throwable ignore) { /* ignore */ }
        }
    }

    // =====================================================================
    // 關閉/離線
    // =====================================================================

    /**
     * 關閉連線線程（可重入）
     */
    public void close() {
        if (closed) return; // 重入保護
        closed = true;
        try {
            String mac = (_mac != null) ? _mac.toString() : null;
            if (_csocket == null) return;

            _kick = 0;

            // 從 OnlineUser 移除
            if (_account != null) {
                OnlineUser.get().remove(_account.get_login());
            }

            // 角色離線
            if (_activeChar != null) {
                quitGame();
            }

            final String ipAddr = getIpString();
            String account = null;
            if (_kick < 1 && _account != null) {
                account = _account.get_login();
            }

            // 停止加解密
            try { if (_decrypt != null) _decrypt.stop(); } catch (Throwable ignore) {}
            try { if (_encrypt != null) _encrypt.stop(); } catch (Throwable ignore) {}

            // 關閉 socket
            StreamUtil.close(_csocket);

            // One-IP 控制
            if (ConfigIpCheck.ISONEIP && _ip != null) {
                LanSecurityManager.ONEIPMAP.remove(ipAddr);
            }

            // 釋放引用
            _handler = null;
            _mac = null;
            _ip = null;
            _activeChar = null;
            _account = null;
            _decrypt = null;
            _encrypt = null;
            _csocket = null;
            _keys = null;

            // 記錄離線 - 簡化版

            
            // 記錄斷線原因到監控器
            String disconnectReason = "正常關閉";
            if (_kick > 0) {
                disconnectReason = "系統踢出";
            } else if (_error >= 2) {
                disconnectReason = "錯誤次數過多";
            }
            try {
                ConnectionMonitor.getInstance().recordDisconnection(this.toString(), disconnectReason, "Client close");
            } catch (Exception e) {
                // 忽略監控器錯誤，不影響主要流程

            }

        } catch (final Exception ignore) {
            // 不做額外處理，避免關閉流程卡住
        }
    }

    /**
     * 中斷連線（發送斷線封包 -> 離線流程）
     */
    public boolean kick() {
        try {
            if (_encrypt != null) {
                _encrypt.encrypt(new S_Disconnect());
            }
        } catch (final Exception ignore) { /* ignore */ }
        quitGame();
        _kick = 1;
        _isStrat = false;
        close();
        return true;
    }

    /**
     * 人物離開遊戲的處理
     */
    public void quitGame() {
        try {
            if (_activeChar == null) return;
            synchronized (_activeChar) {
                QuitGame.quitGame(_activeChar);
                _activeChar = null;
            }
        } catch (final Exception ignore) { /* ignore */ }
    }

    // =====================================================================
    // 封包讀取
    // =====================================================================

    /**
     * 封包解密
     * @return 封包資料（可能為 null）
     * @throws Exception 讓外層 run-loop 分流處理
     */
    private byte[] readPacket() throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            byte[] result = _decrypt.decrypt();
            long readTime = System.currentTimeMillis() - startTime;

            // 記錄過長的讀取時間並檢查連線健康
            if (readTime > 15000) {
                _log.warn("封包讀取耗時過長: " + readTime + "ms, IP: " + getIp());
                
                // 如果讀取時間超過30秒，可能網路有問題
                if (readTime > 30000) {
                    _log.error("封包讀取嚴重超時，可能網路異常: " + getIp());
                }
            }

            return result;
        } catch (java.net.SocketTimeoutException e) {
            _log.warn("封包讀取超時: " + getIp() + " - " + e.getMessage());
            throw e;
        } catch (java.io.IOException e) {
            _log.warn("封包讀取IO錯誤: " + getIp() + " - " + e.getMessage());
            throw e;
        } catch (final Exception e) {
            _log.error("封包讀取未知錯誤: " + getIp(), e);
            throw e;
        }
    }

    // =====================================================================
    // Getter / Setter（維持既有 API）
    // =====================================================================

    /** 傳回帳號暫存資料 */
    public L1Account getAccount() { return _account; }

    /** 設置登入帳號 */
    public void setAccount(final L1Account account) { _account = account; }

    /** 傳回登入帳號 */
    public String getAccountName() { return _account == null ? null : _account.get_login(); }

    /** 傳回目前登入人物 */
    public L1PcInstance getActiveChar() { return _activeChar; }

    /** 設置目前登入人物 */
    public void setActiveChar(final L1PcInstance pc) { _activeChar = pc; }

    /** 傳回IP位置 */
    public StringBuilder getIp() { return _ip; }
    
    /**
     * 獲取IP地址字串
     */
    public String getIpString() {
        return (_ip != null) ? _ip.toString() : "unknown";
    }

    /** 傳回MAC位置 */
    public StringBuilder getMac() { return _mac; }

    /**
     * 設置MAC位置
     * @return true:允許登入 false:禁止登入（沿用原回傳）
     */
    public boolean setMac(final StringBuilder mac) {
        _mac = mac;
        return true;
    }

    /** 傳回 Socket */
    public Socket get_socket() { return _csocket; }

    /** 傳回封包加密解密管理接口 */
    public EncryptExecutor out() { return _encrypt; }

    /** 傳回加密與解密金鑰 */
    public Cipher get_keys() { return _keys; }

    /** 加密與解密金鑰 */
    public void set_keys(final Cipher keys) { _keys = keys; }

    /** 傳回錯誤次數 */
    public int get_error() { return _error; }

    /** 設置錯誤次數（>=2 直接踢掉，沿用你的規則） */
    public void set_error(final int error) {
        _error = error;
        if (_error >= 2) {
            kick();
        }
    }

    /** 傳回自動存檔背包物件時間 */
    public int get_saveInventory() { return _saveInventory; }

    /** 設置自動存檔背包物件時間 */
    public void set_saveInventory(final int saveInventory) { _saveInventory = saveInventory; }

    /** 傳回自動存檔人物資料時間 */
    public int get_savePc() { return _savePc; }

    /** 設置自動存檔人物資料時間 */
    public void set_savePc(final int savePc) { _savePc = savePc; }

    // 輔助：除錯時好看
    @Override
    public String toString() {
        final String ip = (_ip != null) ? _ip.toString() : "unknown";
        final String acc = (_account != null) ? _account.get_login() : "-";
        return "ClientExecutor{" + acc + "@" + ip + "}";
    }
}