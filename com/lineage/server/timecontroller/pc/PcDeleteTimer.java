package com.lineage.server.timecontroller.pc;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_PacketBoxSelect;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class PcDeleteTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PcDeleteTimer.class);
    private ScheduledFuture<?> _timer;

    private static void outPc(L1PcInstance tgpc) {
        try {
            if (tgpc.isDead()) {
                ClientExecutor client = tgpc.getNetConnection();
                tgpc.sendPacketsAll(new S_ChangeName(tgpc, false));
                client.quitGame();
                _log.info("角色死亡登出: " + tgpc.getName());
                client.out().encrypt(new S_PacketBoxSelect());
            }
        } catch (Exception e) {
            _log.error("登出死亡角色時發生異常!", e);
        }
    }

    public void start() {
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, 1100L, 1100L);
    }

    public void run() {
        try {
            Collection<?> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (Object o : all) {
                L1PcInstance tgpc = (L1PcInstance) o;
                if (tgpc.isDead()) {
                    if (tgpc.get_delete_time() > 0) {
                        int newtime = tgpc.get_delete_time() - 1;
                        tgpc.set_delete_time(newtime);
                        if (tgpc.get_delete_time() <= 0) {
                            outPc(tgpc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("PC 死亡刪除處理時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            PcDeleteTimer pcHprTimer = new PcDeleteTimer();
            pcHprTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.PcDeleteTimer JD-Core Version: 0.6.2
 */