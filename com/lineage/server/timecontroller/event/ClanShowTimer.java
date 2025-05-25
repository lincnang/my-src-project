package com.lineage.server.timecontroller.event;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClanShowTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ClanShowTimer.class);
    private ScheduledFuture<?> _timer;

    private static boolean check(L1PcInstance tgpc) {
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
            if (tgpc.isDead()) {
                return false;
            }
            if (tgpc.getCurrentHp() <= 0) {
                return false;
            }
            switch (tgpc.getMapId()) {
                case 340:
                case 350:
                case 360:
                case 370:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean checkC(L1PcInstance tgpc) {
        try {
            if (tgpc.getClan() == null) {
                return false;
            }
            if (!tgpc.getClan().isClanskill()) {
                return false;
            }
            int count = tgpc.getClan().getOnlineClanMemberSize();
            if (count < 16) {
                return false;
            }
            if (tgpc.get_other().get_clanskill() == 0) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    private static void showClan(L1PcInstance tgpc) {
        try {
            int count = tgpc.getClan().getOnlineClanMemberSize();
            if (count >= 30) {
                tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), 5201));
            } else {
                tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), 5263));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 15000L, 15000L);
    }

    public void run() {
        try {
            Collection<?> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (Iterator<?> iter = all.iterator(); iter.hasNext(); ) {
                L1PcInstance tgpc = (L1PcInstance) iter.next();
                if (check(tgpc)) {
                    if ((ServerWarExecutor.get().checkCastleWar() <= 0) && (checkC(tgpc))) {
                        showClan(tgpc);
                    }
                    TimeUnit.MILLISECONDS.sleep(10L);
                }
            }
        } catch (Exception e) {
            _log.error("血盟技能光環 顯示處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ClanShowTimer clanShowTimer = new ClanShowTimer();
            clanShowTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.ClanShowTimer JD-Core Version: 0.6.2
 */