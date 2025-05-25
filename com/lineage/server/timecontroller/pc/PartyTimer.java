package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Party;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 隊伍更新時間軸(優化完成LOLI 2012-05-30)
 *
 * @author KZK
 */
public class PartyTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PartyTimer.class);
    private ScheduledFuture<?> _timer;

    /**
     * PC 執行 判斷
     *
     * @param tgpc
     * @return true:執行 false:不執行
     */
    private static boolean check(L1PcInstance tgpc) {
        try {
            // 人物為空
            if (tgpc == null) {
                return false;
            }
            // 人物登出
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            // 中斷連線
            if (tgpc.getNetConnection() == null) {
                return false;
            }
            L1Party party = tgpc.getParty();
            if (party == null) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public void start() {
        int timeMillis = 5000;
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            //Collection all = World.get().getAllPlayers();
            final Collection<L1PcInstance> all = World.get().getAllPlayers();
            // 不包含元素
            if (all.isEmpty()) {
                return;
            }
            for (final L1PcInstance tgpc : all) {
                if (check(tgpc)) {
                    tgpc.sendPackets(new S_Party(0x6e, tgpc));
                    TimeUnit.MILLISECONDS.sleep(1);
                }
            }
        } catch (Exception e) {
            _log.error("隊伍更新時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            PartyTimer partyTimer = new PartyTimer();
            partyTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.PartyTimer JD-Core Version: 0.6.2
 */