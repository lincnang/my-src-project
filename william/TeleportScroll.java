package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeleportScroll {
    private static Logger _log = Logger.getLogger(TeleportScroll.class.getName());
    private static TeleportScroll _instance;
    private final HashMap<Integer, L1WilliamTeleportScroll> _itemIdIndex = new HashMap<Integer, L1WilliamTeleportScroll>();

    private TeleportScroll() {
        loadTeleportScroll();
    }

    public static TeleportScroll getInstance() {
        if (_instance == null) {
            _instance = new TeleportScroll();
        }
        return _instance;
    }

    private void loadTeleportScroll() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM william_teleport_scroll");
            rs = pstm.executeQuery();
            fillTeleportScroll(rs);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while creating william_teleport_scroll table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillTeleportScroll(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            int tpLocX = rs.getInt("tpLocX");
            int tpLocY = rs.getInt("tpLocY");
            short tpMapId = rs.getShort("tpMapId");
            int check_minLocX = rs.getInt("check_minLocX");
            int check_minLocY = rs.getInt("check_minLocY");
            int check_maxLocX = rs.getInt("check_maxLocX");
            int check_maxLocY = rs.getInt("check_maxLocY");
            short check_MapId = rs.getShort("check_MapId");
            int removeItem = rs.getInt("removeItem");
            L1WilliamTeleportScroll teleport_scroll = new L1WilliamTeleportScroll(itemId, tpLocX, tpLocY, tpMapId, check_minLocX, check_minLocY, check_maxLocX, check_maxLocY, check_MapId, removeItem);
            _itemIdIndex.put(Integer.valueOf(itemId), teleport_scroll);
        }
    }

    public L1WilliamTeleportScroll getTemplate(int itemId) {
        return (L1WilliamTeleportScroll) _itemIdIndex.get(Integer.valueOf(itemId));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * william.TeleportScroll JD-Core Version: 0.6.2
 */