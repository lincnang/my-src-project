package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.data.event.LeavesSet;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
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

    /** 定期保存任務是否已啟動 */
    private static boolean _autoSaveStarted = false;

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

    // ====== 啟動定期保存任務 ======
    public void startAutoSave() {
        if (_autoSaveStarted) {
            return;
        }
        _autoSaveStarted = true;

        // 使用 GeneralThreadPool 執行定期任務
        GeneralThreadPool.get().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    saveAllCachedData();
                } catch (Exception e) {
                    _log.error("定期保存龍之祝福失敗", e);
                }
            }
        }, 5 * 60 * 1000L, 5 * 60 * 1000L); // 5分鐘後開始，每5分鐘執行一次
        _log.info("龍之祝福定期保存任務已啟動（每5分鐘保存一次）");
    }

    // ====== 保存所有快取資料到資料庫 ======
    private void saveAllCachedData() {
        if (_CACHE.isEmpty()) {
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<Integer, DragonExp> entry : _CACHE.entrySet()) {
            try {
                DragonExp data = entry.getValue();
                saveToDB(entry.getKey(), data.getLastLoginOutTime(), data.getStoreExp());
                successCount++;
            } catch (Exception e) {
                failCount++;
                _log.error("保存快取資料失敗: objId=" + entry.getKey(), e);
            }
        }

        if (successCount > 0 || failCount > 0) {
//            _log.info("定期保存完成: 成功=" + successCount + ", 失敗=" + failCount);
        }
    }

    // ====== 直接寫入資料庫（不更新快取） ======
    private void saveToDB(final int objId, final int lastOutTime, final int storeExp) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement(
                    "REPLACE INTO `日誌_龍之祝福` (`ChaObjid`,`LastOutTime`,`StoreExp`) " +
                            "VALUES (?, ?, ?)"
            );
            int i = 0;
            ps.setInt(++i, objId);
            ps.setInt(++i, lastOutTime);
            ps.setInt(++i, storeExp);
            ps.executeUpdate();
        } catch (SQLException e) {
            _log.error("保存到資料庫失敗: objId=" + objId, e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
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
                // 移除 CharObjidTable 依賴，避免因 CharObjidTable 載入失敗導致資料被誤刪或略過
                // if (CharObjidTable.get().isChar(objId) != null) {
                final int lastOutTime = rs.getInt("LastOutTime");
                final int storeExp = rs.getInt("StoreExp");
                final DragonExp rec = new DragonExp();
                rec.setLastLoginOutTime(lastOutTime);
                rec.setStoreExp(storeExp);
                _CACHE.put(objId, rec);
                count++;
                // } else {
                //    delete(objId); // 角色不存在，清掉遺孤 (風險過高，改為手動清理或忽略)
                // }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->[系統_龍之祝福]記錄檔: " + count + " (" + timer.get() + "ms)");

        // 啟動定期保存任務
        startAutoSave();
    }

    // ====== 登入：把 DB/快取數值灌回 pc（避免重開後變 0） ======
    public void restoreToPcOnLogin(final L1PcInstance pc) {
        if (pc == null) {
            _log.error("DragonExp: pc is null");
            return;
        }

        _log.info("開始恢復龍之祝福: " + pc.getName() + " (ID: " + pc.getId() + ")");

        DragonExp rec = _CACHE.get(pc.getId());
        int nowMin = nowMinutes();

        // 防呆機制：如果快取沒有，嘗試直接從 DB 讀取單筆 (Double Check)
        if (rec == null) {
            _log.warn("快取中沒有找到龍之祝福資料，嘗試從 DB 讀取: " + pc.getName());
            rec = loadFromDB(pc.getId());
            if (rec != null) {
                _CACHE.put(pc.getId(), rec);
                _log.info("從 DB 補回龍之祝福快取: " + pc.getName() + ", Exp: " + rec.getStoreExp());
            } else {
                _log.warn("DB 中也沒有找到龍之祝福資料: " + pc.getName());
            }
        }

        // 計算離線累積 (比照 LeavesSet 邏輯，但對象是 DragonExp)
        if (rec != null) {
            if (com.lineage.data.event.LeavesSet.START) {
                int lastOut = rec.getLastLoginOutTime();
                if (lastOut > 0 && nowMin > lastOut) {
                    int deltaMin = nowMin - lastOut;
                    int timeStep = com.lineage.data.event.LeavesSet.TIME;
                    int expStep = com.lineage.data.event.LeavesSet.EXP;
                    int maxExp = com.lineage.data.event.LeavesSet.MAXEXP;

                    if (timeStep > 0) {
                        int addN = deltaMin / timeStep;
                        if (addN > 0) {
                            int newExp = rec.getStoreExp() + (addN * expStep);
                            if (newExp > maxExp) {
                                newExp = maxExp;
                            }
                            rec.setStoreExp(newExp);
                            _log.info("龍之祝福離線累計: " + pc.getName() + " +" + (addN * expStep) + " (總額: " + newExp + ")");
                        }
                    }
                }
            }
            pc.setDragonExp(rec.getStoreExp());
            _log.info("成功恢復龍之祝福: " + pc.getName() + ", Exp: " + rec.getStoreExp());
        } else {
            // 真的沒紀錄就視為 0，並建一筆（讓後續 flush 直接 upsert）
            pc.setDragonExp(0);
            DragonExp fresh = new DragonExp();
            fresh.setLastLoginOutTime(nowMin);
            fresh.setStoreExp(0);
            _CACHE.put(pc.getId(), fresh);
            _log.info("創建新的龍之祝福記錄: " + pc.getName());
        }
    }

    // ====== 內部：單筆讀取 (防呆用) ======
    private DragonExp loadFromDB(int objId) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DragonExp rec = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT `LastOutTime`,`StoreExp` FROM `日誌_龍之祝福` WHERE `ChaObjid`=?");
            ps.setInt(1, objId);
            rs = ps.executeQuery();
            if (rs.next()) {
                rec = new DragonExp();
                rec.setLastLoginOutTime(rs.getInt("LastOutTime"));
                rec.setStoreExp(rs.getInt("StoreExp"));
            }
        } catch (SQLException e) {
            _log.error("讀取單筆龍之祝福失敗: " + objId, e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        return rec;
    }

    // ====== 對外：把 pc 當前值落庫（登出/定時/關機都叫這個） ======
    public void flushFromPc(final L1PcInstance pc) {
        if (pc == null) {
            _log.error("DragonExp.flushFromPc: pc is null");
            return;
        }

        int dragonExp = pc.getDragonExp();
        int objId = pc.getId();
        String name = pc.getName();

        try {
            upsert(objId, nowMinutes(), dragonExp);
            _log.info("保存龍之祝福: " + name + ", Exp: " + dragonExp);
        } catch (Exception e) {
            _log.error("保存龍之祝福失敗: " + name + ", Exp: " + dragonExp, e);
        }
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
            // 嘗試使用 REPLACE 語法（相容性更好）
            ps = cn.prepareStatement(
                    "REPLACE INTO `日誌_龍之祝福` (`ChaObjid`,`LastOutTime`,`StoreExp`) " +
                            "VALUES (?, ?, ?)"
            );
            int i = 0;
            ps.setInt(++i, objId);
            ps.setInt(++i, lastOutTime);
            ps.setInt(++i, storeExp);
            int result = ps.executeUpdate();
            if (result > 0) {
                _log.debug("UPSERT 成功: objId=" + objId + ", exp=" + storeExp);
            }
        } catch (SQLException e) {
            _log.error("UPSERT 失敗: objId=" + objId + ", exp=" + storeExp, e);
            // 嘗試使用 INSERT + UPDATE 作為備用方案
            try {
                if (ps != null) ps.close();
                ps = cn.prepareStatement(
                        "INSERT IGNORE INTO `日誌_龍之祝福` (`ChaObjid`,`LastOutTime`,`StoreExp`) " +
                                "VALUES (?, ?, ?)"
                );
                int i = 0;
                ps.setInt(++i, objId);
                ps.setInt(++i, lastOutTime);
                ps.setInt(++i, storeExp);
                ps.execute();

                if (ps != null) ps.close();
                ps = cn.prepareStatement(
                        "UPDATE `日誌_龍之祝福` SET `LastOutTime`=?, `StoreExp`=? WHERE `ChaObjid`=?"
                );
                i = 0;
                ps.setInt(++i, lastOutTime);
                ps.setInt(++i, storeExp);
                ps.setInt(++i, objId);
                ps.executeUpdate();
                _log.info("使用 INSERT+UPDATE 備用方案成功: objId=" + objId);
            } catch (SQLException e2) {
                _log.error("備用方案也失敗: objId=" + objId, e2);
            }
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
