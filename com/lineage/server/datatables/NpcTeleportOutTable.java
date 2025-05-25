package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1NpcTeleportOut;
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

/**
 * 指定地圖指定時間傳走玩家
 *
 * @author dexc
 */
public class NpcTeleportOutTable {
    private static final Log _log = LogFactory.getLog(NpcTeleportOutTable.class);
    private static NpcTeleportOutTable _notice;
    private Map<Integer, L1NpcTeleportOut> list = new HashMap<>();

    public static NpcTeleportOutTable get() {
        if (_notice == null) {
            _notice = new NpcTeleportOutTable();
        }
        return _notice;
    }

    public Map<Integer, L1NpcTeleportOut> getAllNpcTeleportOut() {
        return list;
    }

    public L1NpcTeleportOut getNpcTeleportOut(final int id) {
        return list.get(id);
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM npcaction_teleport_out");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final L1NpcTeleportOut data = new L1NpcTeleportOut();
                data.setId(rs.getInt("指定地圖"));
                data.setweek(rs.getInt("星期幾"));
                data.sethour(rs.getInt("小時"));
                data.setminute(rs.getInt("分鐘"));
                data.setsecond(rs.getInt("秒"));
                data.setLocx(rs.getInt("傳出座標_X"));
                data.setLocy(rs.getInt("傳出座標_Y"));
                data.setMapid(rs.getInt("傳出地圖編號"));
                data.setMsg(rs.getString("顯示文字"));
                list.put(data.getId(), data);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->指定地圖指定時間傳走玩家設置數量: " + list.size() + "(" + timer.get() + "ms)");
    }
}
