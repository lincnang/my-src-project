package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharMapTimeStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物記時地圖信息
 *
 * @author sudawei
 */
public class CharMapTimeTable implements CharMapTimeStorage {
    private static final Log _log = LogFactory.getLog(CharMapTimeTable.class);
    private static final Map<Integer, HashMap<Integer, Integer>> _timeMap = new ConcurrentHashMap<>();
    private static final Map<Integer, HashMap<Integer, Integer>> _bonusMap = new ConcurrentHashMap<>();

    /**
     * 初始化載入
     */
    @Override
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_maps_time` ORDER BY `char_obj_id`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int char_obj_id = rs.getInt("char_obj_id");
                if (CharObjidTable.get().isChar(char_obj_id) != null) {
                    final int order_id = rs.getInt("order_id");
                    final int used_time = rs.getInt("used_time");
                    final int bonus_time = rs.getInt("bonus_time");
                    // 加入清單
                    addTime(char_obj_id, order_id, used_time);
                    addBonus(char_obj_id, order_id, bonus_time);
                } else {
                    deleteTime(char_obj_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->地圖入場時間紀錄資料數量: " + _timeMap.size() + "(" + timer.get() + "ms)");
    }

    @Override
    public void update(int objid, int mapid, int time) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `character_maps_time` SET `used_time`=? WHERE `char_obj_id`=? AND `order_id`=?");
            ps.setInt(1, time);
            ps.setInt(2, objid);
            ps.setInt(3, mapid);
            final int rows = ps.executeUpdate();
            SQLUtil.close(ps);
            if (rows == 0) {
                ps = cn.prepareStatement("INSERT INTO `character_maps_time` (`char_obj_id`,`order_id`,`used_time`,`bonus_time`) VALUES (?,?,?,0)");
                ps.setInt(1, objid);
                ps.setInt(2, mapid);
                ps.setInt(3, time);
                ps.execute();
            }
            addTime(objid, mapid, time);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 新增地圖入場時間紀錄
     *
     */
    @Override
    public Map<Integer, Integer> addTime(final int objId, final int order_id, final int used_time) {
        final HashMap<Integer, Integer> list = _timeMap.get(objId);
        if (list == null) {
            final HashMap<Integer, Integer> newlist = new HashMap<>();
            newlist.put(order_id, used_time);
            // put
            _timeMap.put(objId, newlist);
            return newlist;
        } else {
            list.put(order_id, used_time);
        }
        return list;
    }

    /**
     * 取回地圖入場時間紀錄
     *
     */
    @Override
    public void getTime(final L1PcInstance pc) {
        final HashMap<Integer, Integer> list = _timeMap.get(pc.getId());
        if (list != null) {
            pc.setMapsList(list);
        }
        final HashMap<Integer, Integer> bonusList = _bonusMap.get(pc.getId());
        if (bonusList != null) {
            pc.setMapsBonusList(bonusList);
        }
    }

    /**
     * 刪除地圖入場時間紀錄
     *
     */
    @Override
    public void deleteTime(final int objid) {
        final HashMap<Integer, Integer> list = _timeMap.get(objid);
        if (list != null) {
            list.clear();
        }
        _timeMap.remove(objid);
        final HashMap<Integer, Integer> bonusList = _bonusMap.get(objid);
        if (bonusList != null) {
            bonusList.clear();
        }
        _bonusMap.remove(objid);
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_maps_time` WHERE `char_obj_id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 刪除並儲存全部地圖入場時間紀錄
     */
    @Override
    public void saveAllTime() {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            cn.setAutoCommit(false);
            ps = cn.prepareStatement("DELETE FROM `character_maps_time`");
            ps.execute();
            final String sql = "INSERT INTO `character_maps_time`" + "SET `char_obj_id`=?,`order_id`=?,`used_time`=?,`bonus_time`=?";
            for (final Entry<Integer, HashMap<Integer, Integer>> entryList : _timeMap.entrySet()) {
                for (final Entry<Integer, Integer> entryList2 : entryList.getValue().entrySet()) {
                    try (PreparedStatement ps_each = cn.prepareStatement(sql)) {
                        ps_each.setInt(1, entryList.getKey());
                        ps_each.setInt(2, entryList2.getKey());
                        ps_each.setInt(3, entryList2.getValue());
                        final HashMap<Integer, Integer> bonusList = _bonusMap.get(entryList.getKey());
                        int bonus = 0;
                        if (bonusList != null) {
                            final Integer bonusValue = bonusList.get(entryList2.getKey());
                            if (bonusValue != null) {
                                bonus = bonusValue;
                            }
                        }
                        ps_each.setInt(4, bonus);
                        ps_each.execute();
                    }
                }
            }
            cn.commit();
            cn.setAutoCommit(true); // reset
        } catch (final SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignore) {
                // ignore
            }
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 清除全部地圖入場時間紀錄 (重置用)
     */
    @Override
    public void clearAllTime() {
        // 清除記憶體中的時間記錄
        int clearedMapCount = _timeMap.size();
        for (final HashMap<Integer, Integer> list : _timeMap.values()) {
            if (list != null) {
                list.clear();
            }
        }
        _timeMap.clear();

        // 清除記憶體中的獎勵時間記錄
        int clearedBonusCount = _bonusMap.size();
        for (final HashMap<Integer, Integer> list : _bonusMap.values()) {
            if (list != null) {
                list.clear();
            }
        }
        _bonusMap.clear();

        Connection cn = null;
        PreparedStatement ps = null;
        int deletedRows = 0;

        try {
            cn = DatabaseFactory.get().getConnection();
            if (cn == null || cn.isClosed()) {
                _log.error("無法取得資料庫連線進行地圖時間清除！");
                throw new SQLException("資料庫連線失敗");
            }

            ps = cn.prepareStatement("DELETE FROM `character_maps_time`");
            deletedRows = ps.executeUpdate();

            _log.info("地圖時間重置完成，已清除 " + deletedRows + " 筆記錄");

        } catch (final SQLException e) {
            _log.error("清除地圖時間紀錄失敗! 錯誤訊息: " + e.getMessage(), e);
            // 重新拋出例外，讓上層知道失敗
            throw new RuntimeException("資料庫清除地圖時間失敗", e);
        } catch (final Exception e) {
            _log.error("清除地圖時間紀錄時發生未預期的錯誤", e);
            throw new RuntimeException("清除地圖時間時發生錯誤", e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    @Override
    public Map<Integer, Integer> addBonus(final int objId, final int order_id, final int bonus_time) {
        final HashMap<Integer, Integer> list = _bonusMap.get(objId);
        if (list == null) {
            final HashMap<Integer, Integer> newlist = new HashMap<>();
            newlist.put(order_id, bonus_time);
            _bonusMap.put(objId, newlist);
            return newlist;
        } else {
            list.put(order_id, bonus_time);
        }
        return list;
    }

    @Override
    public void updateBonus(int objid, int mapid, int bonus_time) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `character_maps_time` SET `bonus_time`=? WHERE `char_obj_id`=? AND `order_id`=?");
            ps.setInt(1, bonus_time);
            ps.setInt(2, objid);
            ps.setInt(3, mapid);
            final int rows = ps.executeUpdate();
            SQLUtil.close(ps);
            if (rows == 0) {
                ps = cn.prepareStatement("INSERT INTO `character_maps_time` (`char_obj_id`,`order_id`,`used_time`,`bonus_time`) VALUES (?,?,0,?)");
                ps.setInt(1, objid);
                ps.setInt(2, mapid);
                ps.setInt(3, bonus_time);
                ps.execute();
            }
            addBonus(objid, mapid, bonus_time);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}
