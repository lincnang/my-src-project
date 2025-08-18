package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 龍之祝福（殷海薩休息 EXP）持久化管理
 *
 * 重點修整：
 * 1) Map -> ConcurrentHashMap（執行緒安全）
 * 2) 新增/更新改為 UPSERT（INSERT ... ON DUPLICATE KEY UPDATE）
 * 3) 提供 restoreToPcOnLogin / flushFromPc API，登入回灌、登出/定時/關機落庫更容易
 *
 * 資料表建議（確保主鍵）：
 *   CREATE TABLE IF NOT EXISTS `日誌_龍之祝福` (
 *     `ChaObjid`    INT NOT NULL,
 *     `LastOutTime` INT NOT NULL DEFAULT 0,   -- 分鐘（System.currentTimeMillis()/1000/60）
 *     `StoreExp`    INT NOT NULL DEFAULT 0,
 *     PRIMARY KEY (`ChaObjid`)
 *   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 *
 * @author hero
 * @editor Tsai
 */
public class DragonExp {

    private static final Log _log = LogFactory.getLog(DragonExp.class);

    /** 快取（key=角色 ObjId） */
    private static final Map<Integer, DragonExp> _CACHE = new ConcurrentHashMap<>();

    /** 單例僅作為「管理器」使用（資料物件仍用本類，但存在 _CACHE 內） */
    private static DragonExp _instance;

    /** 角色最後登出時間（分鐘） */
    private int _outtime;

    /** 殷海薩休息 EXP */
    private int _storeexp;

    // ====== 管理器入口 ======
    public static DragonExp get() {
        if (_instance == null) {
            _instance = new DragonExp();
        }
        return _instance;
    }

    // ====== 資料欄位 ======
    public int getLastLoginOutTime() { return _outtime; }
    public void setLastLoginOutTime(int out_time) { _outtime = out_time; }

    public int getStoreExp() { return _storeexp; }
    public void setStoreExp(final int exp) { _storeexp = exp; }

    // ====== 內部：刪除遺孤資料 ======
    private static void delete(final int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `日誌_龍之祝福` WHERE `ChaObjid`=?");
            ps.setInt(1, objid);
            ps.execute();
            _CACHE.remove(objid);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    // ====== 啟動：載入 DB 到快取 ======
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT `ChaObjid`,`LastOutTime`,`StoreExp` FROM `日誌_龍之祝福`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int objId = rs.getInt("ChaObjid");
                if (CharObjidTable.get().isChar(objId) != null) {
                    final int lastOutTime = rs.getInt("LastOutTime");
                    final int storeExp = rs.getInt("StoreExp");
                    final DragonExp rec = new DragonExp();
                    rec.setLastLoginOutTime(lastOutTime);
                    rec.setStoreExp(storeExp);
                    _CACHE.put(objId, rec);
                    count++;
                } else {
                    delete(objId); // 角色不存在，清掉遺孤
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->[系統_龍之祝福]記錄檔: " + count + " (" + timer.get() + "ms)");
    }

    // ====== 登入：把 DB/快取數值灌回 pc（避免重開後變 0） ======
    public void restoreToPcOnLogin(final L1PcInstance pc) {
        if (pc == null) return;
        DragonExp rec = _CACHE.get(pc.getId());
        if (rec != null) {
            pc.setDragonExp(rec.getStoreExp());
        } else {
            // 沒紀錄就視為 0，並建一筆（讓後續 flush 直接 upsert）
            pc.setDragonExp(0);
            DragonExp fresh = new DragonExp();
            fresh.setLastLoginOutTime(nowMinutes());
            fresh.setStoreExp(0);
            _CACHE.put(pc.getId(), fresh);
        }
    }

    // ====== 對外：把 pc 當前值落庫（登出/定時/關機都叫這個） ======
    public void flushFromPc(final L1PcInstance pc) {
        if (pc == null) return;
        upsert(pc.getId(), nowMinutes(), pc.getDragonExp());
    }

    // ====== 對外：舊呼叫（保留相容，等於 flushFromPc） ======
    public void addStoreExp(final L1PcInstance pc) {
        flushFromPc(pc);
    }

    // ====== 查快取 ======
    public DragonExp getDragonExp(final int objid) {
        return _CACHE.get(objid);
    }

    // ====== 內部：單一 UPSERT（寫 DB + 更新快取） ======
    private void upsert(final int objId, final int lastOutTime, final int storeExp) {
        // 先更新快取（記憶體一致）
        DragonExp rec = new DragonExp();
        rec.setLastLoginOutTime(lastOutTime);
        rec.setStoreExp(storeExp);
        _CACHE.put(objId, rec);

        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            // 需要 DB 有 PRIMARY KEY (ChaObjid)
            ps = cn.prepareStatement(
                    "INSERT INTO `日誌_龍之祝福` (`ChaObjid`,`LastOutTime`,`StoreExp`) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE `LastOutTime`=VALUES(`LastOutTime`), `StoreExp`=VALUES(`StoreExp`)"
            );
            int i = 0;
            ps.setInt(++i, objId);
            ps.setInt(++i, lastOutTime);
            ps.setInt(++i, storeExp);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    // ====== 工具：現在時間（分鐘） ======
    private static int nowMinutes() {
        return (int) (System.currentTimeMillis() / 1000L / 60L);
    }
}
