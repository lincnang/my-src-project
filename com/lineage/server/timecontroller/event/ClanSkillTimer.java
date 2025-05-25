package com.lineage.server.timecontroller.event;

import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClanSkillTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ClanSkillTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 3600 * 1000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<?> allClan = WorldClan.get().getAllClans();
            if (allClan.isEmpty()) {
                return;
            }
            for (Iterator<?> iter = allClan.iterator(); iter.hasNext(); ) {
                L1Clan clan = (L1Clan) iter.next();
                if (clan.isClanskill()) {
                    Timestamp skilltime = clan.get_skilltime();
                    if (skilltime == null) {
                        clan.set_clanskill(false);
                    } else {
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        if (skilltime.before(ts)) {
                            clan.set_clanskill(false);
                            clan.set_skilltime(null);
                            ClanReading.get().updateClan(clan);
                        }
                        TimeUnit.MILLISECONDS.sleep(1L);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("血盟技能計時時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ClanSkillTimer skillTimer = new ClanSkillTimer();
            skillTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.ClanSkillTimer JD-Core Version: 0.6.2
 */