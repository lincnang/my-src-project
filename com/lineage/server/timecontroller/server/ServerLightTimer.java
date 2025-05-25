package com.lineage.server.timecontroller.server;

import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class ServerLightTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerLightTimer.class);
    //static L1PcInstance pc;
    private static boolean isSpawn = false;
    private ScheduledFuture<?> _timer;

    private static void checkLightTime() {
        try {
            int serverTime = L1GameTimeClock.getInstance().currentTime().getSeconds();
            int nowTime = serverTime % 86400;
            if (nowTime >= 21300 && nowTime < 64500) {
                if (isSpawn) {
                    isSpawn = false;
                    /*  實際無任何作用 因此移除 by 聖子默默
                    for (L1NpcInstance npc : WorldNpc.get().all()) {
                        if (npc instanceof L1FieldObjectInstance) {
                            switch (npc.getNpcTemplate().get_npcId()) {
                                case 81177:
                                case 81178:
                                case 81179:
                                case 81180:
                                case 81181:
                                    if (npc.getMapId() == 0 || npc.getMapId() == 4) {
                                        npc.deleteMe();
                                    }
                                    break;
                            }
                        }
                    }
                    */
                }
            } else if (nowTime >= 64500 || nowTime >= 0 && !isSpawn) {
                isSpawn = true;
                //LightSpawnTable.getInstance(); // 實際無任何動作 因此移除 by 聖子默默
                // 新手任務
                //QuestNewTable.get().mo4757b(pc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        int timeMillis = 60000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            //checkLightTime(); // 實際無任何動作 因此移除 by 聖子默默
            NpcSpawnTable.get().checkMaps();
        } catch (Exception e) {
            _log.error("照明物件召喚時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ServerLightTimer lightTimer = new ServerLightTimer();
            lightTimer.start();
        }
    }
}