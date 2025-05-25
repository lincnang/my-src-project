package com.lineage.server.timecontroller.event;

import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.templates.L1ShopS;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ShopXTime extends TimerTask {
    private static final Log _log = LogFactory.getLog(ShopXTime.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 3600000L, 3600000L);
    }

    public void run() {
        try {
            Timestamp overTime = new Timestamp(System.currentTimeMillis());
            HashMap<Integer, L1ShopS> allShopS = DwarfShopReading.get().allShopS();
            for (L1ShopS shopS : allShopS.values()) {
                if ((shopS.get_end() == 0) && (overTime.after(shopS.get_overtime()))) {
                    shopS.set_end(3);
                    shopS.set_item(null);
                    DwarfShopReading.get().updateShopS(shopS);
                }
                TimeUnit.MILLISECONDS.sleep(1L);
            }
            allShopS.clear();
        } catch (Exception e) {
            _log.error("托售物件時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ShopXTime timerTask = new ShopXTime();
            timerTask.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.ShopXTime JD-Core Version: 0.6.2
 */