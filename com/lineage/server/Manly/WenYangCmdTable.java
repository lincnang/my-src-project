//package com.lineage.server.Manly;
//
//import com.lineage.DatabaseFactory;
//import com.lineage.server.utils.PerformanceTimer;
//import com.lineage.server.utils.SQLUtil;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 紋樣系統
// *
// * @author Administrator
// */
//public class WenYangCmdTable {
//    private static final Log _log = LogFactory.getLog(WenYangCmdTable.class);
//    private static WenYangCmdTable _instance;
//    private final Map<String, L1WenYangCmd> _wenyangcmd = new HashMap<>();
//
//    public static WenYangCmdTable getInstance() {
//        if (_instance == null) {
//            _instance = new WenYangCmdTable();
//        }
//        return _instance;
//    }
//
//    public void loadWenYangTable() {
//        PerformanceTimer timer = new PerformanceTimer();
//        Connection con = null;
//        PreparedStatement pstm = null;
//        ResultSet rs = null;
//        try {
//            con = DatabaseFactory.get().getConnection();
//            pstm = con.prepareStatement("SELECT * FROM 系統_紋樣屬性設定");
//            rs = pstm.executeQuery();
//            wenyangTable(rs);
//        } catch (SQLException e) {
//            _log.error(e.getLocalizedMessage(), e);
//        } finally {
//            SQLUtil.close(rs);
//            SQLUtil.close(pstm);
//            SQLUtil.close(con);
//        }
//        _log.info("讀取->[系統_紋樣屬性設定]Npc資料數量: " + _wenyangcmd.size() + "(" + timer.get() + "ms)");
//    }
//
//    private void wenyangTable(ResultSet rs) throws SQLException {
//        while (rs.next()) {
//            int npcid = rs.getInt("流水編號");// npc編號
//            String action = rs.getString("最高機率");// 對應的命令
//            int type = rs.getInt("分類");// 判斷等級
//            int wenyanglevel = rs.getInt("強化等級");// 判斷職業
//            int run = rs.getInt("機率");
//            L1WenYangCmd wenYangCmd = new L1WenYangCmd();
//            wenYangCmd.setNpcId(npcid);
//            wenYangCmd.setAction(action);
//            wenYangCmd.setType(type);
//            wenYangCmd.setLevel(wenyanglevel);
//            wenYangCmd.setRun(run);
//            String key = npcid + action;
//            _wenyangcmd.put(key, wenYangCmd);
//        }
//    }
//
//    public L1WenYangCmd getTemplate(String wenyang) {
//        return _wenyangcmd.get(wenyang);
//    }
//}
