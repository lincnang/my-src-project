package com.lineage.server.timecontroller.pc;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class PcAutoSaveTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PcAutoSaveTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleWithFixedDelay(this, 60000L, 60000L);
    }

    public void run() {
        try {
            Collection<?> allClien = OnlineUser.get().all();
            if (allClien.isEmpty()) {
                return;
            }
            for (Object o : allClien) {
                ClientExecutor client = (ClientExecutor) o;
                int time = client.get_savePc();
                if (time != -1) {
                    time--;
                    save(client, time);
                }
            }
        } catch (Exception e) {
            _log.error("人物資料自動保存時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            PcAutoSaveTimer restart = new PcAutoSaveTimer();
            restart.start();
        }
    }

    private void save(ClientExecutor client, Integer time) {
        try {
            if (client.get_socket() == null) {
                return;
            }
            if (time > 0) {
                client.set_savePc(time);
            } else {
                client.set_savePc(Config.AUTOSAVE_INTERVAL);
                L1PcInstance pc = client.getActiveChar();
                if (pc != null) {
                    pc.save();
                }
            }
        } catch (Exception e) {
            _log.debug("執行人物資料存檔處理異常 帳號:" + client.getAccountName());
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.PcAutoSaveTimer JD-Core Version: 0.6.2
 */