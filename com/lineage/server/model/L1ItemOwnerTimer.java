package com.lineage.server.model;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.TimerTask;

public class L1ItemOwnerTimer extends TimerTask {
    private final L1ItemInstance _item;
    private final int _timeMillis;

    public L1ItemOwnerTimer(L1ItemInstance item, int timeMillis) {
        _item = item;
        _timeMillis = timeMillis;
    }

    public void run() {
        _item.setItemOwnerId(0);
        cancel();
    }

    public void begin() {
        com.lineage.server.thread.GeneralThreadPool.get().schedule(this, _timeMillis);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1ItemOwnerTimer JD-Core Version: 0.6.2
 */