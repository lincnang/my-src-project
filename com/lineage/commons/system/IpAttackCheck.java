package com.lineage.commons.system;

import com.lineage.config.ConfigIpCheck;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.IpReading;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP攻擊行為檢查器
 *
 * @author loli
 */
public class IpAttackCheck {
    public static final Map<ClientExecutor, String> SOCKETLIST = new ConcurrentHashMap<>();// DaiEn
    private static final Log _log = LogFactory.getLog(IpAttackCheck.class);
    // 2012-04-25
    private static final HashMap<String, IpTemp> _ipList = new HashMap<>();
    private static IpAttackCheck _instance;

    private IpAttackCheck() {
        _ipList.clear();
    }

    public static IpAttackCheck get() {
        if (_instance == null) {
            _instance = new IpAttackCheck();
        }
        return _instance;
    }

    public boolean check(String key) {
        try {
            final long nowtime = System.currentTimeMillis();
            IpTemp value = _ipList.get(key);
            if (value == null) {// 加入IP登入時間及次數
                value = new IpTemp();
                value._time = nowtime;// 登入時間
                value._count = 1;// 登入次數
                _ipList.put(key, value);
            } else {
                // 登入間隔小於設定時間
                if (nowtime - value._time <= ConfigIpCheck.TIMEMILLIS) {
                    value._count += 1;// 登入次數+1
                    // 登入次數大於設定次數
                    if (value._count >= ConfigIpCheck.COUNT) {
                        kick(key);// 剔除相同IP所有連線
                        if (ConfigIpCheck.SETDB) {// 加入IP封鎖
                            IpReading.get().add(key, "IP類似攻擊行為");
                            System.out.println("IP類似攻擊行為" + key);
                            return false;
                        }
                    }
                } else {// 登入間隔大於設定時間
                    value._time = nowtime;// 更新登入時間
                    value._count = 1;// 更新登入次數
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    // 中斷相同IP所有連線 DaiEn 2012-04-25
    private void kick(final String key) {
        // === 診斷用: 加強日誌與判斷 ===
        if (key == null || key.isEmpty()) {
            _log.warn("[DIAG-IP-KICK] kick() 收到空的 IP,忽略");
            return;
        }
        
        try {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
            int kickCount = 0;
            int socketListSize = (SOCKETLIST != null) ? SOCKETLIST.size() : 0;
            
            _log.warn("[DIAG-IP-KICK] 準備踢除 IP: " + key + 
                     " 時間: " + timestamp + 
                     " SOCKETLIST 大小: " + socketListSize);
            
            if (SOCKETLIST == null || SOCKETLIST.isEmpty()) {
                _log.warn("[DIAG-IP-KICK] SOCKETLIST 為空,無法踢除");
                return;
            }
            
            for (final ClientExecutor socket : SOCKETLIST.keySet()) {
                if (socket == null) continue; // 跳過空值
                
                final String ip = SOCKETLIST.get(socket);
                if (ip != null && ip.equals(key)) {
                    try {
                        String accountName = (socket.getAccount() != null) ? socket.getAccount().get_login() : "未登入";
                        _log.warn("[DIAG-IP-KICK] 關閉連線 - IP: " + ip + " 帳號: " + accountName);
                        socket.close();
                        kickCount++;
                    } catch (Exception e) {
                        _log.error("[DIAG-IP-KICK] 關閉連線時發生錯誤: " + e.getMessage(), e);
                    }
                }
            }
            
            _log.warn("[DIAG-IP-KICK] IP 踢除完成 - IP: " + key + " 踢除數量: " + kickCount);
            
        } catch (final Exception e) {
            _log.error("[DIAG-IP-KICK] kick() 發生錯誤: " + e.getMessage(), e);
        }
    }

    private static class IpTemp {
        long _time;
        int _count;
    }
}
