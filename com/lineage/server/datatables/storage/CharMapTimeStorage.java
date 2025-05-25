package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Map;

public interface CharMapTimeStorage {
    /**
     * 初始化載入
     */
    public void load();

    /**
     * 新增地圖入場時間紀錄
     *
     * @param objId
     * @param order_id
     * @param used_time
     * @return
     */
    public Map<Integer, Integer> addTime(int objId, int order_id, int used_time);

    /**
     * 取回地圖入場時間紀錄
     *
     * @param pc
     */
    public void getTime(L1PcInstance pc);

    /**
     * 刪除全部地圖入場時間紀錄
     *
     * @param objid
     */
    public void deleteTime(int objid);

    /**
     * 刪除並儲存全部地圖入場時間紀錄
     */
    public void saveAllTime();

    /**
     * 清除全部地圖入場時間紀錄 (重置用)
     */
    public void clearAllTime();

    /**
     * 更新人物記時地圖信息
     */
    public abstract void update(int objid, int mapid, int time);
}
