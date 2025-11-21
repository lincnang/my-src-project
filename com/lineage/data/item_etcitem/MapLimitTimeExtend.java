package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.lock.CharMapTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1MapsLimitTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 限時地圖延長卷軸。<br>
 * 依照設定的秒數，對當前 mapids_group 增加額外可停留時間配額。
 */
public class MapLimitTimeExtend extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(MapLimitTimeExtend.class);
    private static final int DEFAULT_EXTENSION_SECONDS = 600; // 預設延長 10 分鐘

    private boolean _configReady = false;
    private int _extendSeconds = DEFAULT_EXTENSION_SECONDS;
    private java.util.Set<Integer> _allowedMapIds;

    public static ItemExecutor get() {
        return new MapLimitTimeExtend();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null || item == null) {
            return;
        }

        ensureConfig();

        final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(pc.getMapId());
        if (mapsLimitTime == null) {
            pc.sendPackets(new S_ServerMessage("目前地圖沒有停留時間限制，無法使用此道具。"));
            return;
        }

        if (_allowedMapIds != null && !_allowedMapIds.contains((int) pc.getMapId())) {
            pc.sendPackets(new S_ServerMessage("此延長道具只能在指定地圖使用。"));
            return;
        }

        final int orderId = mapsLimitTime.getOrderId();
        final int usedTime = pc.getMapsTime(orderId);
        final int limitTime = mapsLimitTime.getLimitTime();
        final int maxBonus = Math.max(0, L1PcInstance.MAX_MAP_LIMIT_SECONDS - limitTime);
        final int currentBonus = pc.getMapsBonusTime(orderId);

        if (maxBonus <= 0) {
            pc.sendPackets(new S_ServerMessage("此地圖基礎停留時間已達 8 小時上限，無法再延長。"));
            return;
        }

        if (currentBonus >= maxBonus) {
            pc.sendPackets(new S_ServerMessage("此地圖延長時間已達 8 小時上限，無法再使用。"));
            return;
        }

        final int extendSeconds = Math.min(_extendSeconds, maxBonus - currentBonus);
        if (extendSeconds <= 0) {
            pc.sendPackets(new S_ServerMessage("道具設定錯誤，請聯絡管理員。"));
            return;
        }

        final int newBonus = pc.addMapsBonusTime(orderId, extendSeconds, limitTime);
        CharMapTimeReading.get().updateBonus(pc.getId(), orderId, newBonus);
        pc.getInventory().removeItem(item, 1);

        final int remainSeconds = Math.max(0, limitTime + newBonus - usedTime);
        final int remainMinutes = remainSeconds / 60;
        pc.sendPackets(new S_ServerMessage("限時地圖剩餘時間已延長，仍可停留 " + remainMinutes + " 分鐘。"));
        pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, remainSeconds));
    }

    private void ensureConfig() {
        if (_configReady) {
            return;
        }
        _configReady = true;
        final String[] set = get_set();
        if (set == null) {
            return;
        }
        if (set.length > 1) {
            try {
                _extendSeconds = Integer.parseInt(set[1]);
            } catch (NumberFormatException e) {
                _log.error("MapLimitTimeExtend 秒數參數錯誤: " + set[1], e);
                _extendSeconds = DEFAULT_EXTENSION_SECONDS;
            }
        }
        if (set.length > 2 && set[2] != null && !set[2].isEmpty()) {
            final java.util.Set<Integer> mapIds = new java.util.HashSet<>();
            for (String token : set[2].split(",")) {
                final String trimmed = token.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    mapIds.add(Integer.parseInt(trimmed));
                } catch (NumberFormatException e) {
                    _log.error("MapLimitTimeExtend 地圖參數錯誤: " + trimmed, e);
                }
            }
            if (!mapIds.isEmpty()) {
                _allowedMapIds = mapIds;
            }
        }
    }
}
