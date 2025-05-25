package com.lineage.server.timecontroller.pet;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DollGetTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollGetTimer.class);
    private ScheduledFuture<?> _timer;

    private static boolean checkErr(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                return false;
            }
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            if (tgpc.getNetConnection() == null) {
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
            int newtime = tgpc.get_doll_get_time() - 1;
            tgpc.set_doll_get_time(newtime);
            if (newtime <= 0) {
                tgpc.set_doll_get_time(tgpc.get_doll_get_time_src());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (Iterator<?> iter = all.iterator(); iter.hasNext(); ) {
                L1PcInstance tgpc = (L1PcInstance) iter.next();
                if (tgpc.get_doll_get_time_src() > 0) {
                    if (checkErr(tgpc)) {
                        int itemid = tgpc.get_doll_get()[0];
                        int count = tgpc.get_doll_get()[1];
                        L1ItemInstance item = ItemTable.get().createItem(itemid);
                        if (item != null) {
                            item.setCount(count);
                            if (tgpc.getInventory().checkAddItem(item, count) == 0) {
                                tgpc.getInventory().storeItem(item);
                                tgpc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            }
                        }
                        TimeUnit.MILLISECONDS.sleep(50L);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("魔法娃娃處理時間軸(贈送道具)異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            DollGetTimer dollTimer = new DollGetTimer();
            dollTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.DollGetTimer JD-Core Version: 0.6.2
 */