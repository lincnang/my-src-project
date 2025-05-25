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

public class ItemSummon {
    private static Logger _log = Logger.getLogger(ItemSummon.class.getName());
    private static ItemSummon _instance;
    private final HashMap<Integer, L1WilliamItemSummon> _itemIdIndex = new HashMap<Integer, L1WilliamItemSummon>();

    private ItemSummon() {
        loadItemSummon();
    }

    public static ItemSummon getInstance() {
        if (_instance == null) {
            _instance = new ItemSummon();
        }
        return _instance;
    }

    private void loadItemSummon() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM william_item_summon");
            rs = pstm.executeQuery();
            fillItemSummon(rs);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while creating william_item_summon table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillItemSummon(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int item_id = rs.getInt("item_id");
            int checkLevel = rs.getInt("checkLevel");
            int checkClass = rs.getInt("checkClass");
            int checkItem = rs.getInt("checkItem");
            int hpConsume = rs.getInt("hpConsume");
            int mpConsume = rs.getInt("mpConsume");
            int material = rs.getInt("material");
            int material_count = rs.getInt("material_count");
            int summon_id = rs.getInt("summon_id");
            int summonCost = rs.getInt("summonCost");
            int onlyOne = rs.getInt("onlyOne");
            int removeItem = rs.getInt("removeItem");
            L1WilliamItemSummon Item_Summon = new L1WilliamItemSummon(item_id, checkLevel, checkClass, checkItem, hpConsume, mpConsume, material, material_count, summon_id, summonCost, onlyOne, removeItem);
            _itemIdIndex.put(Integer.valueOf(item_id), Item_Summon);
        }
    }

    public L1WilliamItemSummon getTemplate(int itemId) {
        return (L1WilliamItemSummon) _itemIdIndex.get(Integer.valueOf(itemId));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * william.ItemSummon JD-Core Version: 0.6.2
 */