package com.lineage.server.timecontroller.event;

import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.system.GetNowTime;
import com.lineage.system.TimeInfo;
import william.William_Online_Reward;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 活動狀態檢查軸
 *
 * @author dexc
 */
public class T_Special extends TimerTask {
    // TODO 資料存放區
    private static T_Special _instance;
    public boolean _William_Online_Reward = false; // 線上抽獎系統
    private Timer _timeHandler = new Timer(true);

    private T_Special() {
        // 開始執行此時間軸
        final int timeMillis = 60 * 1000; // 60秒
        _timeHandler.schedule(this, timeMillis, timeMillis);
        // 交由線程工廠執行管理
        GeneralThreadPool.get().execute(this);
    }

    public static T_Special getStart() {
        if (_instance == null) {
            _instance = new T_Special();
        }
        return _instance;
    }

    @Override
    public void run() {
        //String sTime = TimeInfo.time().getNow_YMDHMS(6); // 秒
        String mTime = TimeInfo.time().getNow_YMDHMS(3); // 分
        String hTime = TimeInfo.time().getNow_YMDHMS(4); // 時
        //String dTime = TimeInfo.time().getNow_YMDHMS(5); // 日
        //int ss = Integer.parseInt(sTime); // 秒
        int mm = Integer.parseInt(mTime); // 分
        int hh = Integer.parseInt(hTime); // 時
        //int dd = Integer.parseInt(dTime); // 日
        int dw = GetNowTime.GetNowDayWeek(); // 星期
        if (!_William_Online_Reward) { // 線上抽獎系統
            if (William_Online_Reward.isTime(dw, hh, mm)) {
                _William_Online_Reward = true;
            }
        } else if (!William_Online_Reward.isTime(dw, hh, mm)) {
            _William_Online_Reward = false;
        }
    }
}
