package com.add.Crack;

import com.add.L1Config;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 時空裂痕啟動控制項
 *
 * @author dexc
 */
public class L1CrackStart implements Runnable {
    private static final Log _log = LogFactory.getLog(L1CrackStart.class);
    private static L1CrackStart _instance;

    public L1CrackStart() {
        GeneralThreadPool.get().execute(this);
    }

    public static L1CrackStart getInstance() {
        if (_instance == null) {
            _instance = new L1CrackStart();
        }
        return _instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                CrackStartChack();
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private void CrackStartChack() {
        Calendar Crack_StartTime = null;
        final String tmp = L1Config._2219;
        if (!tmp.equalsIgnoreCase("null")) {
            final String[] temp = tmp.split(":");
            if (temp.length == 3) {
                final Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                Crack_StartTime = cal;
            } else {
                _log.info("[時空裂痕]啟動時間有誤, 請重新設置!");
            }
        }
        final Calendar cals = Calendar.getInstance();
        if (Crack_StartTime != null && Crack_StartTime.before(cals)) {
            L1CrackTime.getStart(); // 啟動
            Crack_StartTime = null;
        }
    }
}