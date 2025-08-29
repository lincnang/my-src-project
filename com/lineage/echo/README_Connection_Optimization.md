# 連線穩定性優化說明

## 🔍 問題分析

`ClientExecutor` 和 `DecryptExecutor` 造成玩家斷線的主要問題：

### 1. **超時設定過於嚴格**
- 握手期間讀取超時：15秒 → 60秒
- 握手後讀取超時：2分鐘 → 10分鐘  
- 閒置殺手：2分鐘 → 30分鐘
- Watchdog週期：10秒 → 1分鐘

### 2. **Socket參數不夠優化**
- 接收緩衝區：64K → 512K
- 發送緩衝區：64K → 512K
- 啟用TCP KeepAlive

### 3. **封包長度限制過嚴**
- 最大封包長度：1440 → 8192 bytes
- 異常封包容忍次數：5次 → 10次

### 4. **缺乏超時保護**
- `DecryptExecutor.readByte()` 沒有超時保護
- 可能導致連線卡死

## 🛠️ 改進方案

### 1. **調整連線參數**
```java
// 更寬鬆的超時設定
private static final int READ_TIMEOUT_MS_HANDSHAKE = 60_000;    // 60秒
private static final int READ_TIMEOUT_MS_AFTER = 600_000;       // 10分鐘
private static final int IDLE_KILL_MS = 1_800_000;             // 30分鐘
private static final int WATCHDOG_PERIOD_MS = 60_000;          // 1分鐘
```

### 2. **優化Socket設定**
```java
// 更大的緩衝區
_csocket.setReceiveBufferSize(512 * 1024);  // 512K
_csocket.setSendBufferSize(512 * 1024);     // 512K
_csocket.setKeepAlive(true);                // TCP KeepAlive
```

### 3. **改善封包處理**
```java
// 更寬鬆的封包限制
private static final int MAX_PACKET_LEN = 8192;           // 8KB
private static final int BAD_PACKET_THRESHOLD = 10;       // 10次容忍
```

### 4. **新增重試機制**
```java
// DecryptExecutor 重試機制
while (retryCount < MAX_RETRY) {
    try {
        // 讀取封包邏輯
    } catch (SocketTimeoutException e) {
        retryCount++;
        if (retryCount >= MAX_RETRY) {
            throw e;
        }
        continue;
    }
}
```

### 5. **連線監控系統**
新增 `ConnectionMonitor` 類別：
- 統計連線數量
- 監控斷線原因
- 提供診斷信息
- 自動清理過期數據

## 📊 監控功能

### 連線統計
```java
ConnectionMonitor monitor = ConnectionMonitor.getInstance();
String stats = monitor.getConnectionStats();
String diagnostic = monitor.getDiagnosticInfo();
```

### 斷線原因分析
- 正常關閉
- 系統踢出
- 錯誤次數過多
- 網路超時
- 封包異常

## 🚀 預期效果

1. **減少斷線率**：超時設定更寬鬆，適應不同網路環境
2. **提高穩定性**：更大的緩衝區和TCP KeepAlive
3. **更好的診斷**：詳細的連線監控和統計
4. **自動恢復**：重試機制處理暫時性網路問題

## ⚠️ 注意事項

1. **記憶體使用**：更大的緩衝區會增加記憶體使用
2. **超時容忍**：更長的超時可能掩蓋真正的網路問題
3. **監控開銷**：新增的監控功能會增加少量CPU開銷

## 🔧 使用方法

1. 重新編譯專案
2. 重啟伺服器
3. 觀察日誌中的連線信息
4. 使用監控器查看統計數據

## 📝 日誌範例

```
[INFO] 客戶端連線建立: ClientExecutor{test@192.168.1.100}
[DEBUG] 新連線建立: ClientExecutor{test@192.168.1.100}, 當前連線數: 45
[WARN] 短暫連線斷開: ClientExecutor{test@192.168.1.100}, 原因: 網路超時, 持續時間: 8秒
[DEBUG] 連線斷開: ClientExecutor{test@192.168.1.100}, 原因: 網路超時, 當前連線數: 44
```

## 🎯 後續優化建議

1. **網路品質檢測**：自動調整超時參數
2. **負載均衡**：連線數過多時自動限制
3. **異常封包分析**：記錄和分析異常封包內容
4. **效能監控**：監控封包處理效能
