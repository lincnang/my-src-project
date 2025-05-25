package com.lineage.server.timecontroller.event;

import com.lineage.server.datatables.UBTable;
import com.lineage.server.model.L1UltimateBattle;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class UbTime extends TimerTask {
    private static final Log _log = LogFactory.getLog(UbTime.class);
    private ScheduledFuture<?> _timer;

    private static void checkUbTime() {
        for (L1UltimateBattle ub : UBTable.getInstance().getAllUb()) {
            if ((ub.checkUbTime()) && (!ub.isActive())) {
                ub.start();
                /** [原碼] 無限大賽廣播 */
                World.get().broadcastPacketToAll(new S_SystemMessage("【" + ub.getName() + "】５分鐘後即將開始，想參賽者請趕快入場。"));
            }
        }
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 16500L, 16500L);
    }

    public void run() {
        try {
            checkUbTime();
        } catch (Exception e) {
            _log.error("無線大賽時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            UbTime timer = new UbTime();
            timer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.UbTime JD-Core Version: 0.6.2
 */