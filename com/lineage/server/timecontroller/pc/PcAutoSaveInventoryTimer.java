package com.lineage.server.timecontroller.pc;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.PcOtherThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class PcAutoSaveInventoryTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PcAutoSaveInventoryTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = PcOtherThreadPool.get().scheduleWithFixedDelay(this, 60000L, 60000L);
    }

    public void run() {
        try {
            Collection<?> allClien = OnlineUser.get().all();
            if (allClien.isEmpty()) {
                return;
            }
            for (Iterator<?> iter = allClien.iterator(); iter.hasNext(); ) {
                ClientExecutor client = (ClientExecutor) iter.next();
                int time = client.get_saveInventory();
                if (time != -1) {
                    time--;
                    save(client, Integer.valueOf(time));
                }
            }
        } catch (Exception e) {
            _log.error("背包物品自動保存時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            PcAutoSaveInventoryTimer restart = new PcAutoSaveInventoryTimer();
            restart.start();
        }
    }

    private void save(ClientExecutor client, Integer time) {
        try {
            if (client.get_socket() == null) {
                return;
            }
            if (time.intValue() > 0) {
                client.set_saveInventory(time.intValue());
            } else {
                client.set_saveInventory(Config.AUTOSAVE_INTERVAL_INVENTORY);
                L1PcInstance pc = client.getActiveChar();
                if (pc != null) {
                    pc.saveInventory();
                }
            }
        } catch (Exception e) {
            _log.debug("執行背包資料存檔處理異常 帳號:" + client.getAccountName());
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.PcAutoSaveInventoryTimer JD-Core
 * Version: 0.6.2
 */