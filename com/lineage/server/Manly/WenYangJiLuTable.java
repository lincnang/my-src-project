package com.lineage.server.Manly;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 紋樣紀錄（單筆覆蓋 + 成功次數累加）
 * 表：character_紋樣記錄
 * 欄位：玩家ID, 類型, 強化等級, 成功次數, created_at
 * 唯一鍵：uniq_pcid_type(玩家ID, 類型)
 */
public class WenYangJiLuTable {

    private static final Log _log = LogFactory.getLog(WenYangJiLuTable.class);
    private static WenYangJiLuTable _instance;

    /** 依玩家ID暫存當次執行的紀錄（僅供「記憶體累加」用，非最終權威） */
    private final Map<Integer, List<L1WenYangJiLu>> _wenyangj = new ConcurrentHashMap<>();

    /** 最近樣本（相容舊 getTemplate 行為） */
    private final Map<Integer, L1WenYangJiLu> _lastRecordByPcid = new ConcurrentHashMap<>();

    /** 成功次數快取：key = "pcid#type" → 次數（以 DB 為準，load 時載入） */
    private final Map<String, Integer> _successCount = new ConcurrentHashMap<>();

    public static WenYangJiLuTable getInstance() {
        if (_instance == null) {
            _instance = new WenYangJiLuTable();
        }
        return _instance;
    }

    /** 快取用 key */
    private static String key(int pcid, int type) {
        return pcid + "#" + type;
    }

    /** 啟動/重載：載入 DB → 快取 */
    public void loadWenYangTable() {
        PerformanceTimer timer = new PerformanceTimer();
        _lastRecordByPcid.clear();
        _successCount.clear();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "SELECT `玩家ID`,`類型`,`強化等級`,`成功次數` FROM `character_紋樣記錄`"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                int pcid = rs.getInt("玩家ID");
                int type = rs.getInt("類型");
                int level = rs.getInt("強化等級");
                int cnt   = rs.getInt("成功次數");

                L1WenYangJiLu row = new L1WenYangJiLu();
                row.setPcid(pcid);
                row.setType(type);
                row.setLevel(level);

                // 只保留「最近樣本」：若一玩家多類型，這裡最後一次覆蓋即可（相容舊行為）
                _lastRecordByPcid.put(pcid, row);
                _successCount.put(key(pcid, type), cnt);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
        _log.info("讀取->character_紋樣記錄 準備完成 (" + timer.get() + "ms), 組數=" + _successCount.size());
    }

    // ------------------------------------------------------------
    // 相容舊 API（不會改變成功次數，僅插入/更新等級）
    // ------------------------------------------------------------

    /** 舊版：單純插入一筆（若有唯一鍵可能衝突，僅供舊程式碼相容） */
    public void storeItem(L1PcInstance pc, final L1WenYangJiLu wenyang) {
        List<L1WenYangJiLu> list = _wenyangj.computeIfAbsent(pc.getId(), k -> new ArrayList<>());
        list.add(wenyang);

        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement(
                    "INSERT INTO `character_紋樣記錄` (`玩家ID`,`類型`,`強化等級`,`成功次數`,`created_at`) VALUES (?,?,?,0,NOW())"
            );
            pm.setInt(1, pc.getId());
            pm.setInt(2, wenyang.getType());
            pm.setInt(3, wenyang.getLevel());
            pm.execute();

            // 樣本快取
            _lastRecordByPcid.put(pc.getId(), wenyang);
            // 成功次數快取維持原狀（此方法不累加）

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    /** 舊版：只覆蓋強化等級（成功次數不動） */
    public void updateItem(final L1PcInstance pc, final L1WenYangJiLu wenyang) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "UPDATE `character_紋樣記錄` SET `強化等級`=?, `created_at`=NOW() WHERE `玩家ID`=? AND `類型`=?"
            );
            int i = 0;
            ps.setInt(++i, wenyang.getLevel());
            ps.setInt(++i, pc.getId());
            ps.setInt(++i, wenyang.getType());
            ps.execute();

            _lastRecordByPcid.put(pc.getId(), wenyang);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
    }

    /** 相容舊邏輯（僅回傳最近樣本，非權威資料） */
    public L1WenYangJiLu getTemplate(final int pcid) {
        return _lastRecordByPcid.get(pcid);
    }

    // ------------------------------------------------------------
    // 新 API：帶「成功次數累加」語意
    // ------------------------------------------------------------

    /**
     * UPSERT 成功一次：
     * - 新玩家/類型：插入(成功次數=1)
     * - 已存在：強化等級覆蓋為最新、成功次數 +1、時間更新
     */
    public void storeOrUpdateSuccess(final L1PcInstance pc, final L1WenYangJiLu wenyang) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "INSERT INTO `character_紋樣記錄` (`玩家ID`,`類型`,`強化等級`,`成功次數`,`created_at`) " +
                            "VALUES (?,?,?,1,NOW()) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "  `強化等級`=VALUES(`強化等級`), " +
                            "  `成功次數`=`成功次數`+1, " +
                            "  `created_at`=NOW()"
            );
            int i = 0;
            ps.setInt(++i, pc.getId());
            ps.setInt(++i, wenyang.getType());
            ps.setInt(++i, wenyang.getLevel());
            ps.execute();

            // 同步樣本與成功次數快取
            _lastRecordByPcid.put(pc.getId(), wenyang);
            String k = key(pc.getId(), wenyang.getType());
            _successCount.put(k, _successCount.getOrDefault(k, 0) + 1);

        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
    }

    /** 從快取取成功次數（若快取沒有，退回 0；注意：可搭配 getSuccessCountFromDB 做校正） */
    public int getSuccessCount(int pcid, int type) {
        // 先讀快取（以 load / storeOrUpdateSuccess 維護）
        Integer v = _successCount.get(key(pcid, type));
        if (v != null) return v;

        // 快取沒有 → 退回記憶體內累加（僅限同開機期）
        List<L1WenYangJiLu> list = _wenyangj.get(pcid);
        if (list == null) return 0;
        int cnt = 0;
        for (L1WenYangJiLu row : list) {
            if (row.getType() == type) cnt++;
        }
        return cnt;
    }

    /** 直接查 DB 的成功次數（權威值）。可在關鍵流程用它來校正快取。 */
    public int getSuccessCountFromDB(final int pcid, final int type) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "SELECT `成功次數` FROM `character_紋樣記錄` WHERE `玩家ID`=? AND `類型`=?"
            );
            ps.setInt(1, pcid);
            ps.setInt(2, type);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
        return 0;
    }

    // ------------------------------------------------------------
    // 重置 API（道具 / 指令 用）
    // ------------------------------------------------------------

    /** 重置：單一類型 → 將強化等級、成功次數歸 0 */
    public boolean resetSingle(final int pcid, final int type) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "UPDATE `character_紋樣記錄` " +
                            "SET `強化等級`=0, `成功次數`=0, `created_at`=NOW() " +
                            "WHERE `玩家ID`=? AND `類型`=?"
            );
            ps.setInt(1, pcid);
            ps.setInt(2, type);
            int rows = ps.executeUpdate();

            // 快取同步
            _successCount.put(key(pcid, type), 0);
            List<L1WenYangJiLu> list = _wenyangj.get(pcid);
            if (list != null) {
                list.removeIf(row -> row.getType() == type);
            }
            L1WenYangJiLu last = _lastRecordByPcid.get(pcid);
            if (last != null && last.getType() == type) {
                last.setLevel(0);
                _lastRecordByPcid.put(pcid, last);
            }

            return rows > 0;
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
    }

    /** 重置：全部類型 → 該玩家所有記錄歸 0 */
    public int resetAll(final int pcid) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement(
                    "UPDATE `character_紋樣記錄` " +
                            "SET `強化等級`=0, `成功次數`=0, `created_at`=NOW() " +
                            "WHERE `玩家ID`=?"
            );
            ps.setInt(1, pcid);
            int rows = ps.executeUpdate();

            // 快取同步
            String prefix = pcid + "#";
            _successCount.entrySet().removeIf(e -> e.getKey().startsWith(prefix));
            _wenyangj.remove(pcid);

            L1WenYangJiLu last = _lastRecordByPcid.get(pcid);
            if (last != null) {
                last.setLevel(0);
                _lastRecordByPcid.put(pcid, last);
            }

            return rows;
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return 0;
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
    }

    /** 提供方便呼叫（玩家物件 + 單一類型） */
    public boolean resetSingle(final L1PcInstance pc, final int type) {
        return resetSingle(pc.getId(), type);
    }

    /** 提供方便呼叫（玩家物件 + 全部類型） */
    public int resetAll(final L1PcInstance pc) {
        return resetAll(pc.getId());
    }
}
