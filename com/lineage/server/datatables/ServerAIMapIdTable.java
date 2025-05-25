package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 特效驗證系統
 */
public class ServerAIMapIdTable {
    private static final Log _log = LogFactory.getLog(ServerAIMapIdTable.class);
    private static final ArrayList<Integer> _can_ai = new ArrayList<>();
    private static final ArrayList<Integer> _cant_ai = new ArrayList<>();
    private static ServerAIMapIdTable _instance;

    private ServerAIMapIdTable() {
        load();
    }

    public static ServerAIMapIdTable get() {
        if (_instance == null) {
            _instance = new ServerAIMapIdTable();
        }
        return _instance;
    }

    public static void reload() {
        _can_ai.clear();
        _cant_ai.clear();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `驗證AI地圖設置`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int map_id = rs.getInt("驗證地圖編號");
                boolean can_ai = rs.getBoolean("安全區域是否驗證");
                boolean cant_ai = rs.getBoolean("驗證開關");
                if (can_ai) {
                    _can_ai.add(map_id);
                } else if (cant_ai) {
                    _cant_ai.add(map_id);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->【AI地圖】編號設置->安區偵測:" + _can_ai.size() + " /不偵測:" + _cant_ai.size());
    }

    private void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `驗證AI地圖設置`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int map_id = rs.getInt("驗證地圖編號");
                boolean can_ai = rs.getBoolean("安全區域是否驗證");
                boolean cant_ai = rs.getBoolean("驗證開關");
                if (can_ai) {
                    _can_ai.add(map_id);
                } else if (cant_ai) {
                    _cant_ai.add(map_id);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->【驗證AI地圖】編號設置->安區偵測:" + _can_ai.size() + " /不偵測:" + _cant_ai.size());
    }

    public boolean checkCanAI(int map_id) {
        return _can_ai.contains(map_id);
    }

    public boolean checkCantAI(int map_id) {
        return _cant_ai.contains(map_id);
    }
}