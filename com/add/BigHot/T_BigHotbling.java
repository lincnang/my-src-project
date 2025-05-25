package com.add.BigHot;

import com.lineage.server.model.TimeInform;
import com.lineage.server.thread.GeneralThreadPool;

import java.util.Timer;
import java.util.TimerTask;

public class T_BigHotbling extends TimerTask {
    private static T_BigHotbling _instance;
    private Timer _timeHandler = new Timer(true);
    private boolean _isBigHotta = false;

    private T_BigHotbling() {
        this._timeHandler.schedule(this, 5000L, 10000L);
        GeneralThreadPool.get().execute(this);
    }

    public static T_BigHotbling getStart() {
        if (_instance == null) {
            _instance = new T_BigHotbling();
        }
        return _instance;
    }

    public void run() {
        String mTime = TimeInform.time().getNow_YMDHMS(3);
        String hTime = TimeInform.time().getNow_YMDHMS(4);
        int mm = Integer.parseInt(mTime);
        int hh = Integer.parseInt(hTime);
        switch (hh) {
            case 2:
            case 6:
            case 10:
            case 14:
            case 18:
            case 22:
                if ((mm == 1) && (!this._isBigHotta)) {
                    BigHotblingTime BigHot = new BigHotblingTime();
                    BigHot.startBigHotbling();
                    this._isBigHotta = true;
                }
                if ((mm == 3) && (this._isBigHotta)) {
                    this._isBigHotta = false;
                }
                break;
        }
    }

    public void startT_BigHotbling() {
        getStart();
    }
}
