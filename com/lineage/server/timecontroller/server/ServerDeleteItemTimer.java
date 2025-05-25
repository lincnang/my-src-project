package com.lineage.server.timecontroller.server;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * 自動清除地面物件時間軸
 *
 * @author dexc
 */
public class ServerDeleteItemTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerDeleteItemTimer.class);
    private static final ArrayList<L1ItemInstance> _itemList = new ArrayList<>();
    private ScheduledFuture<?> _timer;

    public static void add(final L1ItemInstance item) {
        _itemList.add(item);
    }

    public static boolean contains(final L1ItemInstance item) {
        return _itemList.contains(item);
    }

    public static void remove(final L1ItemInstance item) {
        if (!_itemList.remove(item)) {
            _log.error("地面物件刪除失敗 OBJID:" + item.getId());
        }
        final int x = item.getX();
        final int y = item.getY();
        final short m = item.getMapId();
        // 取回物件點背包資料
        final L1Inventory inventory = World.get().getInventory(x, y, m);
        if (inventory.getItem(item.getId()) != null) {
            inventory.removeItem(item);
        }
    }

    /**
     * 檢查物品
     *
     */
    private static boolean checkItem(final L1ItemInstance item) {
        World.get().getVisiblePlayer(item);
        /*
         * if (!players.isEmpty()) { // 指定範圍內有PC return false; }
         */
        if (item.getItem().getItemId() == 40515) { // 元素石
            return false;
        }
        if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item.getMapId())) { // 盟屋內
            return false;
        }
        final int x = item.getX();
        final int y = item.getY();
        final short m = item.getMapId();
        // 取回物件點背包資料
        final L1Inventory inventory = World.get().getInventory(x, y, m);
        if (inventory.getItem(item.getId()) == null) {
            remove(item);
            return false;
        }
        return true;
    }

    public void start() {
        final int timeMillis = ConfigAlt.ALT_ITEM_DELETION_TIME * 60 * 1000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            if (_itemList.isEmpty()) {
                return;
            }
            for (final Object object : _itemList.toArray()) {
                final L1ItemInstance e = (L1ItemInstance) object;
                if (e == null) {
                    continue;
                }
                if (checkItem(e)) {
                    remove(e);
                }
            }
        } catch (final Exception e) {
            _log.error("自動清除地面物件時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final ServerDeleteItemTimer deleteItemTimer = new ServerDeleteItemTimer();
            deleteItemTimer.start();
        }
    }
}
