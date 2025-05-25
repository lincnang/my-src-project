package com.lineage.server.serverpackets;

import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1MapsLimitTime;

import java.util.Collection;

/**
 * 地圖剩餘時間(ctrl+q)
 *
 * @author Psnnwe
 */
public class S_MapTimerOut extends ServerBasePacket {
    /**
     * ctrl+Q中地圖剩餘時間
     */
    public static final int DISPLAY_MAP_TIME = 159;
    private static final String S_MAP_TIMER_OUT = "[S] S_MapTimerOut";
    private byte[] _byte = null;

    /**
     * ctrl+Q顯示剩餘時間
     *
     */
    public S_MapTimerOut(final L1PcInstance pc) {
        final Collection<L1MapsLimitTime> mapLimitList = MapsGroupTable.get().getGroupMaps().values();
        writeC(S_EVENT);
        writeC(DISPLAY_MAP_TIME);
        writeD(mapLimitList.size());
        for (final L1MapsLimitTime mapLimit : mapLimitList) {
            final int order_id = mapLimit.getOrderId();
            final int used_time = pc.getMapsTime(order_id);
            final int time_str = (mapLimit.getLimitTime() - used_time) / 60;
            // write
            writeD(order_id);
            writeS(mapLimit.getMapName());
            writeD(time_str);
        }
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return S_MAP_TIMER_OUT;
    }
}