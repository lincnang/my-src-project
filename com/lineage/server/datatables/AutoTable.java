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
import java.util.HashMap;
import java.util.Map;

/**
 * 內掛_LOG
 *
 * @author dexc
 */
public class AutoTable {
    private static final Log _log = LogFactory.getLog(AutoTable.class);
    private static AutoTable _instance;
    Map<Integer, String> _autolog = new HashMap<Integer, String>();

    public static AutoTable getInstance() {
        if (_instance == null) {
            _instance = new AutoTable();
        }
        return _instance;
    }

    /**
     * 建立清單
     *
     * @param pc
     * @param srpc
     */
    public void AddAuto(final L1PcInstance pc, final L1Character srpc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `使用內掛被玩家殺死名單` SET `玩家Objid`=?," + "`殺人玩家名字`=?,`日期`=?");
            pstm.setInt(1, pc.getId());
            pstm.setString(2, srpc.getName());
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            final String times = sdf.format(System.currentTimeMillis());
            pstm.setString(3, times);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 查詢清單
     *
     * @param pc
     */
    public void AddAutolist(final L1PcInstance pc) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 使用內掛被玩家殺死名單");
            rs = pstm.executeQuery();
            if (rs != null) {
                int x = 0;
                while (rs.next()) {
                    int objid = (rs.getInt("玩家Objid"));
                    String name = (rs.getString("殺人玩家名字"));
                    String time = (rs.getString("日期"));
                    if (objid == pc.getId()) {
                        String msg = "【" + name + "】	" + time;
                        _autolog.put(x++, msg);
                    } else {
                        continue;
                    }
                }
                String msgs2[] = new String[_autolog.size() + 1];
                for (int i = 0; i < _autolog.size(); i++) {// 將MAP還原為字串陣列
                    msgs2[i + 1] = _autolog.get(i);
                }
                pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto", msgs2));
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
            SQLUtil.close(rs);
        }
    }
}
