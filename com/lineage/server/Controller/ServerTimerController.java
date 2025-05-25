package com.lineage.server.Controller;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.datatables.ActivityNoticeTable;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.NpcTeleportOutTable;
import com.lineage.server.datatables.lock.CharMapTimeReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.ActivityNotice;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.templates.L1NpcTeleportOut;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 各種時間控制
 */
public class ServerTimerController implements Runnable {
    private static final Log _log = LogFactory.getLog(ServerTimerController.class);
    private static ServerTimerController _instance;

    public ServerTimerController() {
        GeneralThreadPool.get().execute(this);
    }

    public static ServerTimerController getInstance() {
        if (_instance == null) {
            _instance = new ServerTimerController();
        }
        return _instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                TeleportOutChack(); // 指定地圖指定時間傳走玩家
                MapRestartChack(); // 重置地圖使用時間
                ActivityNoticeBossChack(); // 活動召喚BOSS
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 指定地圖指定時間傳走玩家
     */
    private void TeleportOutChack() {
        for (final L1NpcTeleportOut notic : NpcTeleportOutTable.get().getAllNpcTeleportOut().values()) {
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // 星期
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); // 時
            int minute = Calendar.getInstance().get(Calendar.MINUTE); // 分
            int second = Calendar.getInstance().get(Calendar.SECOND); // 秒
            int startWeek = notic.getweek();
            if (startWeek == 1) {
                startWeek = 2;
            } else if (startWeek == 2) {
                startWeek = 3;
            } else if (startWeek == 3) {
                startWeek = 4;
            } else if (startWeek == 4) {
                startWeek = 5;
            } else if (startWeek == 5) {
                startWeek = 6;
            } else if (startWeek == 6) {
                startWeek = 7;
            } else if (startWeek == 7) {
                startWeek = 1;
            }
            for (Object obj : World.get().getVisibleObjects(notic.getId()).values()) {
                if (obj instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) obj;
                    if (pc.getMapId() == notic.getId()) {
                        if (notic.getweek() == 0) {
                            if (hour == notic.gethour() && minute == notic.getminute() && second == notic.getsecond()) {
                                L1Teleport.teleport(pc, notic.getLocx(), notic.getLocy(), (short) notic.getMapid(), pc.getHeading(), true); // 傳回
                                if (notic.getMsg() != null) {
                                    pc.sendPackets(new S_ServerMessage(notic.getMsg()));
                                }
                            }
                        } else {
                            if (week == startWeek && hour == notic.gethour() && minute == notic.getminute() && second == notic.getsecond()) {
                                L1Teleport.teleport(pc, notic.getLocx(), notic.getLocy(), (short) notic.getMapid(), pc.getHeading(), true); // 傳回
                                if (notic.getMsg() != null) {
                                    pc.sendPackets(new S_ServerMessage(notic.getMsg()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 重置地圖使用時間
     */
    private void MapRestartChack() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); // 時
        int minute = Calendar.getInstance().get(Calendar.MINUTE); // 分
        int second = Calendar.getInstance().get(Calendar.SECOND); // 秒
        if (hour == ConfigOtherSet2.Reset_Map_Time[0] && minute == ConfigOtherSet2.Reset_Map_Time[1] && second == ConfigOtherSet2.Reset_Map_Time[2]) {
            final Collection<L1PcInstance> allpc = World.get().getAllPlayers();
            if (allpc.isEmpty()) {
                return;
            }
			/*for (final Iterator<L1PcInstance> iter = allpc.iterator(); iter.hasNext();) {
				final L1PcInstance pc = iter.next();
				final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(pc.getMapId());
				if (mapsLimitTime != null) {
					L1Teleport.teleport(pc, mapsLimitTime.getOutMapX(), mapsLimitTime.getOutMapY(), mapsLimitTime.getOutMapId(), pc.getHeading(), true);
				}
			}*/
            for (final L1PcInstance pc : allpc) {
                final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(pc.getMapId());
                if (mapsLimitTime != null) {
                    L1Teleport.teleport(pc, mapsLimitTime.getOutMapX(), mapsLimitTime.getOutMapY(), mapsLimitTime.getOutMapId(), pc.getHeading(), true);
                }
            }
            // 清除全部地圖入場時間紀錄
            CharMapTimeReading.get().clearAllTime();
            _log.info("------- 目前所有地圖使用時間已重置完畢 -------");
            // 發送系統提示訊息
            World.get().broadcastPacketToAll(new S_SystemMessage("目前所有地圖使用時間已重置完畢。"));
        }
    }

    /**
     * 活動召喚BOSS
     */
    private void ActivityNoticeBossChack() {
        for (final ActivityNotice notic : ActivityNoticeTable.get().getAllActivityNotice().values()) {
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // 星期
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); // 時
            int minute = Calendar.getInstance().get(Calendar.MINUTE); // 分
            int second = Calendar.getInstance().get(Calendar.SECOND); // 秒
            if (notic.getisShow() == 1 && notic.getboss_spawnid() != 0) {
                if (notic.getweek() == 0) {
                    if (hour == notic.gethour() && minute == notic.getminute() && second == notic.getsecond()) {
                        L1SpawnUtil.spawnT(notic.getboss_spawnid(), notic.getLocx(), notic.getLocy(), (short) notic.getMapid(), 5, notic.getendtime() * 60 * 1000);
                    }
                } else {
                    if (week == notic.getweek() && hour == notic.gethour() && minute == notic.getminute() && second == notic.getsecond()) {
                        L1SpawnUtil.spawnT(notic.getboss_spawnid(), notic.getLocx(), notic.getLocy(), (short) notic.getMapid(), 5, notic.getendtime() * 60 * 1000);
                    }
                }
            }
        }
    }
}
