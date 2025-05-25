package com.add.Tsai;

import com.lineage.DatabaseFactory;
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

//指定地圖回血魔設置
public class RevertHpMp {
    private static final Log _log = LogFactory.getLog(RevertHpMp.class);
    private static final Map<Integer, RevertHpMp> _relist = Maps.newHashMap();
    private static RevertHpMp _instance;
    private int _id;
    private int _map;
    private int _sx;
    private int _sy;
    private int _ex;
    private int _ey;
    private int _hpr;
    private int _mpr;

    public static RevertHpMp get() {
        if (_instance == null) {
            _instance = new RevertHpMp();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_地圖回血魔設置`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("Id");
                final int map = rs.getInt("地圖編號");
                final int sx = rs.getInt("範圍MinX");
                final int sy = rs.getInt("範圍MinY");
                final int ex = rs.getInt("範圍MaxX");
                final int ey = rs.getInt("範圍MaxY");
                final int hpr = rs.getInt("HP回復量");
                final int mpr = rs.getInt("MP回復量");
                final RevertHpMp re = new RevertHpMp();
                re.setId(id);
                re.setMapId(map);
                re.setSx(sx);
                re.setSy(sy);
                re.setEx(ex);
                re.setEy(ey);
                re.setHpr(hpr);
                re.setMpr(mpr);
                _relist.put(id, re);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->[系統_地圖回血魔設置]: " + i + "(" + timer.get() + "ms)");
    }

    public int getId() {
        return _id;
    }

    private void setId(final int i) {
        _id = i;
    }

    public int getMapId() {
        return _map;
    }

    private void setMapId(final int i) {
        _map = i;
    }

    public int getSx() {
        return _sx;
    }

    private void setSx(final int i) {
        _sx = i;
    }

    public int getSy() {
        return _sy;
    }

    private void setSy(final int i) {
        _sy = i;
    }

    public int getEx() {
        return _ex;
    }

    private void setEx(final int i) {
        _ex = i;
    }

    public int getEy() {
        return _ey;
    }

    private void setEy(final int i) {
        _ey = i;
    }

    public int getHpr() {
        return _hpr;
    }

    private void setHpr(final int i) {
        _hpr = i;
    }

    public int getMpr() {
        return _mpr;
    }

    private void setMpr(final int i) {
        _mpr = i;
    }

    /**
     * 回傳地圖回血魔size
     *
     * @return
     */
    public int RevertSize() {
        return _relist.size();
    }

    /**
     * 回傳地圖回血魔list
     *
     * @param id
     * @return
     */
    public RevertHpMp getRevert(final int id) {
        return _relist.get(id);
    }
}