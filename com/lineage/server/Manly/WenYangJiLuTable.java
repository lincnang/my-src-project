package com.lineage.server.Manly;

import com.lineage.DatabaseFactory;
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

public class WenYangJiLuTable {
    private static final Log _log = LogFactory.getLog(WenYangCmdTable.class);
    private static WenYangJiLuTable _instance;
    private final HashMap<Integer, L1WenYangJiLu> _wenyangj = new HashMap<>();

    public static WenYangJiLuTable getInstance() {
        if (_instance == null) {
            _instance = new WenYangJiLuTable();
        }
        return _instance;
    }

    public void loadWenYangTable() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_紋樣記錄");
            rs = pstm.executeQuery();
            wenyangTable(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統_紋樣記錄]資料數量: " + _wenyangj.size() + "(" + timer.get() + "ms)");
    }

    private void wenyangTable(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int pcid = rs.getInt("玩家ID");// npc編號
            int type = rs.getInt("類型");// 判斷等級
            int wenyanglevel = rs.getInt("強化等級");// 判斷職業
            L1WenYangJiLu wenYangJiLu = new L1WenYangJiLu();
            wenYangJiLu.setPcid(pcid);
            wenYangJiLu.setType(type);
            wenYangJiLu.setLevel(wenyanglevel);
            _wenyangj.put(pcid, wenYangJiLu);
        }
    }

    /**
     * 更新資料
     *
     */
    public void updateItem(L1PcInstance pc, final L1WenYangJiLu wenyang) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            int i = 0;
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `character_紋樣記錄` SET `類型`=?,`強化等級`=? WHERE `玩家ID`=?");
            pm.setInt(++i, wenyang.getType());
            pm.setInt(++i, wenyang.getLevel());
            pm.setInt(++i, pc.getId());
            pm.execute();
            loadWenYangTable();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    /**
     * 寫入資料
     *
     */
    public void storeItem(L1PcInstance pc, final L1WenYangJiLu wenyang) {
        if (_wenyangj.get(pc.getId()) != null) {
            return;
        }
        _wenyangj.put(pc.getId(), wenyang);
        Connection co = null;
        PreparedStatement pm = null;
        try {
            int i = 0;
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_紋樣記錄` SET `玩家ID`=?,`類型`=?,`強化等級`=?");
            pm.setInt(++i, pc.getId());
            pm.setInt(++i, wenyang.getType());
            pm.setInt(++i, wenyang.getLevel());
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    public L1WenYangJiLu getTemplate(int pcid) {
        return _wenyangj.get(pcid);
    }
}
