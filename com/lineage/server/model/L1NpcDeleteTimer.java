package com.lineage.server.model;

import com.lineage.server.model.Instance.L1NpcInstance;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 怪物回收
 */
public class L1NpcDeleteTimer extends TimerTask {
    private final L1NpcInstance _npc;
    private final int _timeMillis;

    public L1NpcDeleteTimer(L1NpcInstance npc, int timeMillis) {
        _npc = npc;
        _timeMillis = timeMillis;
    }

    @Override
    public void run() {
        if (_npc.getCurrentHp() == _npc.getMaxHp()) { // 滿血才回收
            _npc.deleteMe();
        }
        this.cancel();
    }

    public void begin() {
        Timer timer = new Timer();
        timer.schedule(this, _timeMillis);
    }
}
