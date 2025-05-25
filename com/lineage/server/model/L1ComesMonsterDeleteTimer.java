package com.lineage.server.model;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.concurrent.ScheduledFuture;

/**
 * 指定時間召怪召物系統
 */
public class L1ComesMonsterDeleteTimer implements Runnable {
    private final L1Object cha;
    private final int _timeMillis;
    private ScheduledFuture<?> sf;

    public L1ComesMonsterDeleteTimer(final L1Object cha, final int timeMillis) {
        this.cha = cha;
        this._timeMillis = timeMillis;
    }

    @Override
    public void run() {
        if (cha instanceof L1NpcInstance) {
            final L1NpcInstance npc = (L1NpcInstance) cha;
            npc.deleteMe();
        } else if (cha instanceof L1ItemInstance) {
            final L1ItemInstance item = (L1ItemInstance) cha;
            final L1Inventory gi = World.get().getInventory(cha.getX(), cha.getY(), cha.getMapId());
            if (gi.getItem(cha.getId()) != null) {
                gi.removeItem(item);
            }
        }
        this.sf = null;
    }

    public void begin() {
        if (this.sf != null) {
            return;
        }
        this.sf = GeneralThreadPool.get().schedule(this, this._timeMillis);
    }
}
