package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class NewAutoPractice {
    private static final Log _log = LogFactory.getLog(NewAutoPractice.class);
    private static NewAutoPractice _instance;

    public static NewAutoPractice get() {
        if (_instance == null) {
            _instance = new NewAutoPractice();
        }
        return _instance;
    }

    public void load() {
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `內掛_自動練功仇人名單`");
            rs = pm.executeQuery();
            while (rs.next()) {
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    public void load2() {
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `內掛_自動練功被殺名單`");
            rs = pm.executeQuery();
            while (rs.next()) {
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    /**
     * 設置自動練功仇人名單
     *
     */
    public void addEnemy(final L1PcInstance pc) {
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `內掛_自動練功仇人名單`");
            rs = pm.executeQuery();
            while (rs.next()) {
                if (pc.getId() == (rs.getInt("玩家Objid"))) {
                    pc.setInEnemyList((rs.getString("仇人玩家名字")));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    /**
     * 仇人名單建立
     *
     */
    public void AddEnemyList(final L1PcInstance pc, final String name) {
        int id = LoadMaxId() + 1;
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `內掛_自動練功仇人名單` SET `流水號`=?,`玩家Objid`=?,`仇人玩家名字`=?");
            pstm.setInt(1, id);
            pstm.setInt(2, pc.getId());
            pstm.setString(3, name);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        pc.setInEnemyList(name);
    }

    /**
     * 仇人名單ID檢測
     */
    public int LoadMaxId() {
        int id = 1;
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT MAX(流水號) FROM 內掛_自動練功仇人名單");
            rs = pm.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        return id;
    }

    /**
     * 仇人名單刪除
     *
     */
    public void DeleteEnemyList(final L1PcInstance pc, final String name) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `內掛_自動練功仇人名單` WHERE `玩家Objid`=? AND `仇人玩家名字`=?");
            ps.setInt(1, pc.getId());
            ps.setString(2, name);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        pc.removeInEnemyList(name);
    }

    /**
     * OK
     * 使用內掛被殺名單建立
     *
     */
    public void AddAutoList(final L1PcInstance pc, final L1Character srpc) {
        int id = LoadMaxId2() + 1;
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `內掛_自動練功被殺名單` SET `流水號`=?,`玩家Objid`=?," + "`殺人玩家名字`=?,`日期`=?");
            pstm.setInt(1, id);
            pstm.setInt(2, pc.getId());
            pstm.setString(3, srpc.getName());
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            final String times = sdf.format(System.currentTimeMillis());
            pstm.setString(4, times);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * oK
     * 使用內掛被殺名單ID檢測
     */
    public int LoadMaxId2() {
        int id = 1;
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            String sql = "SELECT MAX(Id) FROM 內掛_自動練功被殺名單";
            pm = co.prepareStatement(sql);
            rs = pm.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        return id;
    }

    /**
     * OK
     * 使用內掛被殺查詢清單
     *
     */
    public void SearchAutoLog(final L1PcInstance pc) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 內掛_自動練功被殺名單");
            rs = pstm.executeQuery();
            if (rs != null) {
                final StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                while (rs.next()) {
                    int objid = (rs.getInt("玩家Objid"));
                    String name = (rs.getString("殺人玩家名字"));
                    String time = (rs.getString("日期"));
                    if (objid == pc.getId()) {
                        stringBuilder.append(name + " [" + time + "],");
                        i++;
                    }
                }
                if (i == 0) {
                    stringBuilder.append("沒有任何被殺紀錄,");
                }
                final String[] clientStrAry = stringBuilder.toString().split(",");
                pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto", clientStrAry));
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
            SQLUtil.close(rs);
        }
    }

    /**
     * OK
     * 使用內掛被殺名單清除
     *
     */
    public void ClearAutoLog(final int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `內掛_自動練功被殺名單` WHERE `玩家Objid`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}
