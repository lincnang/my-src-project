/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.templates;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L1DoorGfx {
    private static Logger _log = Logger.getLogger(L1DoorGfx.class.getName());
    private final int _gfxId;
    private final int _direction;
    private final int _rightEdgeOffset;
    private final int _leftEdgeOffset;

    public L1DoorGfx(int gfxId, int direction, int rightEdgeOffset, int leftEdgeOffset) {
        _gfxId = gfxId;
        _direction = direction;
        _rightEdgeOffset = rightEdgeOffset;
        _leftEdgeOffset = leftEdgeOffset;
    }

    /**
     * door_gfxs指定gfxid主行返。<br>
     * 常最新返。
     *
     * @param gfxId
     * @return
     */
    public static L1DoorGfx findByGfxId(int gfxId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM door_gfxs WHERE gfxid = ?");
            pstm.setInt(1, gfxId);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            int id = rs.getInt("gfxid");
            int dir = rs.getInt("direction");
            int rEdge = rs.getInt("right_edge_offset");
            int lEdge = rs.getInt("left_edge_offset");
            return new L1DoorGfx(id, dir, rEdge, lEdge);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return null;
    }

    public int getGfxId() {
        return _gfxId;
    }

    public int getDirection() {
        return _direction;
    }

    public int getRightEdgeOffset() {
        return _rightEdgeOffset;
    }

    public int getLeftEdgeOffset() {
        return _leftEdgeOffset;
    }
}
