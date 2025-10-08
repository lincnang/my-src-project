package com.lineage.server.Controller;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.datatables.ActivityNoticeTable;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.NpcTeleportOutTable;
import com.lineage.server.datatables.lock.CharMapTimeReading;
import com.lineage.server.datatables.lock.ShopLimitReading;
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
import java.util.concurrent.ScheduledFuture;

/**
 * 各種時間控制（線程安全版本）
 */
public class ServerTimerController implements Runnable {
    private static final Log _log = LogFactory.getLog(ServerTimerController.class);
    
    /**
     * 線程安全的單例 Holder
     */
    private static class Holder {
        private static final ServerTimerController INSTANCE = new ServerTimerController();
    }
    
    private volatile boolean _running = false; // 運行狀態標記
    private volatile boolean _isShopResetDone = false; // 商店重置標記
    private ScheduledFuture<?> _scheduledFuture; // 定時任務控制
    
    private ServerTimerController() {
        // 不在構造函數中啟動線程
    }

    /**
     * 獲取單例實例（線程安全）
     */
    public static ServerTimerController getInstance() {
        return Holder.INSTANCE;
    }
    
    /**
     * 啟動定時器
     */
    public synchronized void start() {
        if (_running) {
            _log.warn("ServerTimerController 已經在運行中");
            return;
        }
        
        _running = true;
        _scheduledFuture = GeneralThreadPool.get().scheduleAtFixedRate(
            this, 
            0L, 
            1000L
        );
        _log.info("ServerTimerController 已啟動");
    }
    
    /**
     * 停止定時器
     */
    public synchronized void stop() {
        if (!_running) {
            return;
        }
        
        _running = false;
        
        if (_scheduledFuture != null) {
            _scheduledFuture.cancel(false);
            _scheduledFuture = null;
        }
        
        _log.info("ServerTimerController 已停止");
    }

    @Override
    public void run() {
        if (!_running) {
            return; // 可控制的停止
        }
        
        try {
            shopLimitResetCheck();
            TeleportOutChack(); // 指定地圖指定時間傳走玩家
            MapRestartChack(); // 重置地圖使用時間
            ActivityNoticeBossChack(); // 活動召喚BOSS
        } catch (Exception e) {
            _log.error("ServerTimerController 執行時發生錯誤", e);
        }
    }
    /**
     * 每日商店限購重置檢查
     */
    private void shopLimitResetCheck() { // <-- 【修改四】新增這個完整的方法
        try {
            final Calendar cal = Calendar.getInstance();
            final int hour = cal.get(Calendar.HOUR_OF_DAY);
            final int minute = cal.get(Calendar.MINUTE);

            // 在每日凌晨 5 點整，並且當日尚未執行過重置時，執行任務
            // 您可以修改下方的數字 5 來改變重置時間 (0=凌晨0點, 1=凌晨1點, etc.)
            if (hour == 23 && minute == 30 && !this._isShopResetDone) {
                ShopLimitReading.get().resetDailyPurchases();
                _log.info("每日商店限購紀錄已成功重置。");
                this._isShopResetDone = true; // 標記今日已執行
            }

            // 當時間經過重置點（例如來到5點01分），將旗標重置，為隔天的任務做準備
            if (hour == 23 && minute == 31 && this._isShopResetDone) {
                this._isShopResetDone = false;
            }

        } catch (final Exception e) {
            _log.error("執行每日商店限購重置時發生錯誤", e);
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
