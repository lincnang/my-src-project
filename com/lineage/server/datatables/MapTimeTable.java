package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.timecontroller.server.TimeMap;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加載計時地圖信息
 *
 * @author sudaw
 */
public class MapTimeTable {
    private static final Log _log = LogFactory.getLog(MapTimeTable.class);
    static MapTimeTable timemap;
    final int INTERVAL = 1000;
    ArrayList<TimeMap> _map = new ArrayList<>();
    ConcurrentHashMap<Integer, TimeMap> timermap = new ConcurrentHashMap<>();
    Timer _regenTimer5;

    private MapTimeTable() {
    }

    /**
     * 刪除地圖計時器裡的玩家
     */
    //	public void deluser(L1PcInstance pc) {
    //		TimeMap temp = timermap.get((int) pc.getMapId());
    //		if (temp != null) {
    //			temp.deluser(pc);
    //		}
    //	}
    public static MapTimeTable get() {
        if (timemap == null) {
            timemap = new MapTimeTable();
        }
        return timemap;
    }

    public void start() {
        _regenTimer5 = new Timer(true);
        for (TimeMap temp : _map) {
            _regenTimer5.scheduleAtFixedRate(temp, INTERVAL, INTERVAL);
            timermap.put(temp.getMapid(), temp);
        }
    }

    /**
     * 向地圖計時器添加玩家
     */
    public void adduser(L1PcInstance pc) {
        int mapid;
        if (pc.getMapId() >= 4001 && pc.getMapId() <= 4050) {
            mapid = 4001;
        } else {
            mapid = pc.getMapId();
        }
        TimeMap temp = timermap.get(mapid);
        if (temp != null) {
            temp.adduser(pc);
        }
    }

    // TODO 加載記時地圖信息
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `maptime`");
            rs = ps.executeQuery();
            while (rs.next()) {
                TimeMap map = new TimeMap();
                //map.setId(rs.getInt("id"));
                map.setMapid(rs.getInt("mapid"));
                map.setMaptime(rs.getInt("time") * 60);
                map.setLocx(rs.getInt("locx"));
                map.setLocy(rs.getInt("locy"));
                //map.setMapName(rs.getString("name"));
                map.setTeleportmapid(rs.getInt("teleportmapid"));
                _map.add(map);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->計時地圖資料數量: " + _map.size() + "(" + timer.get() + "ms)");
    }

    public ArrayList<TimeMap> getAllMap() {
        return _map;
    }
}
