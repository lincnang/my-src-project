package com.lineage.server.timecontroller.pc;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_MapTimer;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 計時地圖執行緒
 *
 * @author Psnnwe
 */
public final class MapTimerThread extends TimerTask {
    public static final Map<L1PcInstance, Integer> TIMINGMAP = new ConcurrentHashMap<L1PcInstance, Integer>();
    /**
     * 提示信息.
     */
    private static final Log _log = LogFactory.getLog(MapTimerThread.class);
    /**
     * 實例
     */
    private static MapTimerThread _instance;
    private ScheduledFuture<?> _timer;

    /**
     * 取回實例
     *
     * @return
     */
    public static MapTimerThread getInstance() {
        if (_instance == null) {
            _instance = new MapTimerThread();
            _instance.start();
        }
        return _instance;
    }

    /**
     * 加入計時地圖物件清單<BR>
     * 同時更新地圖使用時間<BR>
     * 送出時間封包
     *
     * @param pc
     * @param time 秒
     */
    public static void put(final L1PcInstance pc, final int time) {
        pc.sendPackets(new S_MapTimer(time));
        TIMINGMAP.put(pc, new Integer(time));
    }

    public void start() {
        final int timeMillis = 1000;// 1秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    /**
     * 地圖時間檢查
     *
     * @param pc
     */
    private void MapTimeCheck(final L1PcInstance pc) {
        // 玩家所在的地圖
        final int mapid = pc.getMapId();
        // 地圖限制時間(秒數)
        final int maxMapUsetime = MapsTable.get().getMapTime(mapid) * 60;
        // 已使用秒數
        int usedtime = pc.getMapUseTime(mapid);
        if (usedtime > maxMapUsetime) {
            this.teleport(pc);
        } else {
            usedtime++;// 已使用秒數+1秒
            this.message(pc);
            pc.setMapUseTime(mapid, usedtime);
            // System.out.println("usedtime ==" + usedtime);
            // 剩餘時間(秒)
            int leftTime = (maxMapUsetime - usedtime);
            if ((leftTime % 60) == 0) {// 校正計時器顯示(1分鐘來一次就好了= = )
                pc.sendPackets(new S_MapTimer(leftTime));
            }
        }
    }

    /**
     * 時間提示訊息
     *
     * @param pc
     */
    private void message(final L1PcInstance pc) {
        // 玩家所在的地圖
        final int map = pc.getMapId();
        // 地圖限制時間(秒數)
        final int maxMapUsetime = MapsTable.get().getMapTime(map) * 60;
        final int usedtime = pc.getMapUseTime(map);// 已用時間(秒數)
        final int lefttime = maxMapUsetime - usedtime;// 剩餘秒數
        S_ServerMessage msg = null;
        switch (lefttime) {
            case 10800:
                msg = new S_ServerMessage(1526, String.valueOf(3));
                break;
            case 7200:
                msg = new S_ServerMessage(1526, String.valueOf(2));
                break;
            case 3600:
                msg = new S_ServerMessage(1526, String.valueOf(1));
                break;
            case 1800:
                msg = new S_ServerMessage(1527, String.valueOf(30));
                break;
            case 900:
                msg = new S_ServerMessage(1527, String.valueOf(15));
                break;
            case 600:
                msg = new S_ServerMessage(1527, String.valueOf(10));
                break;
            case 300:
                msg = new S_ServerMessage(1527, String.valueOf(5));
                break;
            case 120:
                msg = new S_ServerMessage(1527, String.valueOf(2));
                break;
            case 60:
                msg = new S_ServerMessage(1527, String.valueOf(1));
                break;
            case 10:
                msg = new S_ServerMessage(1528, String.valueOf(10));
                break;
            case 9:
                msg = new S_ServerMessage(1528, String.valueOf(9));
                break;
            case 8:
                msg = new S_ServerMessage(1528, String.valueOf(8));
                break;
            case 7:
                msg = new S_ServerMessage(1528, String.valueOf(7));
                break;
            case 6:
                msg = new S_ServerMessage(1528, String.valueOf(6));
                break;
            case 5:
                msg = new S_ServerMessage(1528, String.valueOf(5));
                break;
            case 4:
                msg = new S_ServerMessage(1528, String.valueOf(4));
                break;
            case 3:
                msg = new S_ServerMessage(1528, String.valueOf(3));
                break;
            case 2:
                msg = new S_ServerMessage(1528, String.valueOf(2));
                break;
            case 1:
                msg = new S_ServerMessage(1528, String.valueOf(1));
                break;
            default:
                break;
        }
        pc.sendPackets(msg);
    }

    @Override
    public void run() {
        try {
            // 包含元素
            if (!TIMINGMAP.isEmpty()) {
                for (final L1PcInstance pc : TIMINGMAP.keySet()) {
                    if ((pc == null) || (pc.getNetConnection() == null)) {// 玩家離線不計時
                        continue;
                    }
                    if (pc.isDead()) {// 死亡不計時
                        continue;
                    }
                    if (pc.isInTimeMap()) {// 正在使用計時地圖
                        this.MapTimeCheck(pc);
                    }
                }
            }
        } catch (final Throwable e) {
            _log.error("計時地圖時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final MapTimerThread MapTimer = new MapTimerThread();
            MapTimer.start();
        }
    }

    /**
     * 傳出PC的處理
     *
     * @param pc
     */
    private void teleport(final L1PcInstance pc) {
        TIMINGMAP.remove(pc);// 移出清單
        if (World.get().getPlayer(pc.getName()) == null) {
            // 人物離開世界
            return;
        }
        if (pc.isInTimeMap()) {// 在計時地圖裡
            final int locx = 33427;
            final int locy = 32822;
            final int mapid = 4;
            final int heading = 5;
            L1Teleport.teleport(pc, locx, locy, (short) mapid, heading, true);
        }
    }
}