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
    public static final Map<ClientExecutor, String> SOCKETLIST = new ConcurrentHashMap<ClientExecutor, String>();// DaiEn
    private static final Log _log = LogFactory.getLog(IpAttackCheck.class);
    // 2012-04-25
    private static final HashMap<String, IpTemp> _ipList = new HashMap<String, IpTemp>();
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
        try {
            for (final ClientExecutor socket : SOCKETLIST.keySet()) {
                final String ip = SOCKETLIST.get(socket);
                if (ip != null) {
                    if (ip.equals(key)) {
                        socket.close();
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static class IpTemp {
        long _time;
        int _count;
    }
}
