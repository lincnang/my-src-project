package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1MapsLimitTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 重讀限時地間時間
 * <p>
 * DB設定 要重設的MAPID
 */
public class ReloadMapTime extends ItemExecutor { //src022
    private static final Log _log = LogFactory.getLog(ReloadMapTime.class);
    private int _mapId;
    private int _mapId1;
    private int _mapId2;
    private int _mapId3;
    private int _mapId4;
    private int _mapId5;
    private int _mapId6;

    public static ItemExecutor get() {
        return new ReloadMapTime();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            final boolean flag = removeMapTime(pc, _mapId);
            if (_mapId1 > 0) {
                removeMapTime1(pc, _mapId1);
            }
            if (_mapId2 > 0) {
                removeMapTime2(pc, _mapId2);
            }
            if (_mapId3 > 0) {
                removeMapTime3(pc, _mapId3);
            }
            if (_mapId4 > 0) {
                removeMapTime4(pc, _mapId4);
            }
            if (_mapId5 > 0) {
                removeMapTime5(pc, _mapId5);
            }
            if (_mapId6 > 0) {
                removeMapTime6(pc, _mapId6);
            }
            //此段為控制是否刪除卷軸使用
            //如果不要刪除物品就把此段備註起來
            if (flag) {
                pc.getInventory().removeItem(item, 1L);
            }
        } catch (Exception e) {
            _log.error(e.getClass().getName() + "," + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 清除限時地圖時間
     *
     * @param mapId
     * @return
     */
    public final boolean removeMapTime(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return false;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
        return true;
    }

    public final void removeMapTime1(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    public final void removeMapTime2(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    public final void removeMapTime3(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    public final void removeMapTime4(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    public final void removeMapTime5(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    public final void removeMapTime6(final L1PcInstance pc, final int mapId) {
        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("此ID:" + mapId + "不是限時地圖。"));
            return;
        }
        final int order_id = mapsLimitTime.getOrderId();
        final String mapName = mapsLimitTime.getMapName();
        // 清除地圖限時時間
        pc.removeMapsTime(order_id);
        pc.sendPackets(new S_ServerMessage("限時地圖:" + mapName + "時間重置成功。"));
        //L1Teleport.teleport(pc, 33442, 32798, (short) 4, 5, false);
    }

    /**
     * 從DB中取得MAPID(地圖編號)
     */
    public void set_set(String[] set) {
        try {
            _mapId = Integer.parseInt(set[1]);
            //if (_mapId <= 0) {
            //_log.error("ReloadMapTime 設置錯誤:應大於0");
            //}
        } catch (Exception localException) {
        }
        try {
            _mapId1 = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
        try {
            _mapId2 = Integer.parseInt(set[3]);
        } catch (Exception localException) {
        }
        try {
            _mapId3 = Integer.parseInt(set[4]);
        } catch (Exception localException) {
        }
        try {
            _mapId4 = Integer.parseInt(set[5]);
        } catch (Exception localException) {
        }
        try {
            _mapId5 = Integer.parseInt(set[6]);
        } catch (Exception localException) {
        }
        try {
            _mapId6 = Integer.parseInt(set[7]);
        } catch (Exception localException) {
        }
    }
}
