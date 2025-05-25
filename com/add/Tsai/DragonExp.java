package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 龍之祝福
 *
 * @author hero
 */
public class DragonExp {
    private static final Log _log = LogFactory.getLog(DragonExp.class);
    private static final Map<Integer, DragonExp> _DragonExp = Maps.newHashMap();
    private static DragonExp _instance;
    private int _outtime;// 登出時間
    private int _storeexp = 0;// 殷海薩的祝福-休息系統(EXP)

    public static DragonExp get() {
        if (_instance == null) {
            _instance = new DragonExp();
        }
        return _instance;
    }

    private static void delete(final int objid) {
        // 清空資料庫紀錄
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `日誌_龍之祝福` WHERE `ChaObjid`=?");
            ps.setInt(1, objid);
            ps.execute();
            _DragonExp.remove(objid);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `日誌_龍之祝福`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int Objid = rs.getInt("ChaObjid");
                if (CharObjidTable.get().isChar(Objid) != null) {
                    final int LastOutTime = rs.getInt("LastOutTime");
                    final int StoreExp = rs.getInt("StoreExp");
                    final DragonExp Dragon = new DragonExp();
                    Dragon.setLastLoginOutTime(LastOutTime);
                    Dragon.setStoreExp(StoreExp);
                    _DragonExp.put(Objid, Dragon);
                    i++;
                } else {
                    delete(Objid);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->[系統_龍之祝福]記錄檔: " + i + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回上次登出時間(殷海薩祝福計算用)
     *
     */
    public int getLastLoginOutTime() {
        return _outtime;
    }

    /**
     * 設定上次登出時間(殷海薩祝福計算用)
     *
     */
    public void setLastLoginOutTime(int out_time) {
        _outtime = out_time;
    }

    /**
     * 殷海薩的祝福-休息系統(EXP)
     *
     */
    public int getStoreExp() {
        return _storeexp;
    }

    /**
     * 殷海薩的祝福-休息系統(EXP)
     *
     */
    public void setStoreExp(final int exp) {
        _storeexp = exp;
    }

    public void addStoreExp(final L1PcInstance pc) {
        if (getDragonExp(pc.getId()) != null) {
            Update(pc);
        } else {
            adddate(pc);
        }
    }

    private void Update(final L1PcInstance pc) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            final int NowTime = (int) (System.currentTimeMillis() / 60L / 1000L);//目前時間換算為(分)
            final int Exp = pc.getDragonExp();
            final DragonExp Dragon = new DragonExp();
            Dragon.setLastLoginOutTime(NowTime);
            Dragon.setStoreExp(Exp);
            _DragonExp.put(pc.getId(), Dragon);
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `日誌_龍之祝福` SET " + "`LastOutTime`=?,`StoreExp`=?" + " WHERE `ChaObjid`=?");
            int i = 0;
            ps.setInt(++i, NowTime);
            ps.setInt(++i, Exp);
            ps.setInt(++i, pc.getId());
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void adddate(final L1PcInstance pc) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            final int objid = pc.getId();
            final int NowTime = (int) (System.currentTimeMillis() / 60L / 1000L);//目前時間換算為(分)
            final int Exp = pc.getDragonExp();
            final DragonExp Dragon = new DragonExp();
            Dragon.setLastLoginOutTime(NowTime);
            Dragon.setStoreExp(Exp);
            _DragonExp.put(objid, Dragon);
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `日誌_龍之祝福` SET `ChaObjid`=?,`LastOutTime`=?," + "`StoreExp`=?");
            int i = 0;
            ps.setInt(++i, objid);
            ps.setInt(++i, NowTime);
            ps.setInt(++i, Exp);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public DragonExp getDragonExp(final int objid) {
        return _DragonExp.get(objid);
    }
}