package com.add.Mobbling;

import com.lineage.server.model.TimeInform;
import com.lineage.server.thread.GeneralThreadPool;

import java.util.Timer;
import java.util.TimerTask;

public class T_Mobbling extends TimerTask {
    private static T_Mobbling _instance;
    private Timer _timeHandler = new Timer(true);
    private boolean _isStart = false;

    private T_Mobbling() {
        this._timeHandler.schedule(this, 5000L, 10000L);
        GeneralThreadPool.get().execute(this);
    }

    public static T_Mobbling getStart() {
        if (_instance == null) {
            _instance = new T_Mobbling();
        }
        return _instance;
    }

    public void run() {
        String mTime = TimeInform.time().getNow_YMDHMS(3);
        String hTime = TimeInform.time().getNow_YMDHMS(4);
        int mm = Integer.parseInt(mTime);
        Integer.parseInt(hTime);
        switch (mm) {
            case 1:
            case 11:
            case 21:
            case 31:
            case 41:
            case 51:
                if (this._isStart) {
                    return;
                }
                MobblingTime Mob = new MobblingTime();
                Mob.startMobbling();
                this._isStart = true;
                break;
            case 0:
            case 10:
            case 20:
            case 30:
            case 40:
            case 50:
                if (!(this._isStart)) {
                    return;
                }
                this._isStart = false;
                MobblingTime Mob1 = new MobblingTime();
                Mob1.clear();
                Mob1.setMobbling(false);
        }
    }

    public void startT_Mobbling() {
        getStart();
    }
}