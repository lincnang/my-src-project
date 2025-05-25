package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1PetItem;
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

public class PetItemTable {
    private static final Log _log = LogFactory.getLog(PetItemTable.class);
    private static final Map<Integer, L1PetItem> _petItemIdIndex = new HashMap<>();
    private static PetItemTable _instance;

    public static PetItemTable get() {
        if (_instance == null) {
            _instance = new PetItemTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM petitem_寵物裝備");
            rs = pstm.executeQuery();
            fillPetItemTable(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->寵物道具資料數量: " + _petItemIdIndex.size() + "(" + timer.get() + "ms)");
    }

    private void fillPetItemTable(ResultSet rs) throws SQLException {
        while (rs.next()) {
            L1PetItem petItem = new L1PetItem();
            petItem.setItemId(rs.getInt("item_id"));
            petItem.setHitModifier(rs.getInt("hitmodifier"));
            petItem.setDamageModifier(rs.getInt("dmgmodifier"));
            petItem.setAddAc(rs.getInt("ac"));
            petItem.setAddStr(rs.getInt("add_str"));
            petItem.setAddCon(rs.getInt("add_con"));
            petItem.setAddDex(rs.getInt("add_dex"));
            petItem.setAddInt(rs.getInt("add_int"));
            petItem.setAddWis(rs.getInt("add_wis"));
            petItem.setAddHp(rs.getInt("add_hp"));
            petItem.setAddMp(rs.getInt("add_mp"));
            petItem.setAddSp(rs.getInt("add_sp"));
            petItem.setAddMr(rs.getInt("m_def"));
            petItem.setAddMr(rs.getInt("m_def"));
            petItem.setWeapom(rs.getBoolean("isweapon"));
            petItem.setHigher(rs.getBoolean("ishigher"));
            _petItemIdIndex.put(petItem.getItemId(), petItem);
        }
    }

    public L1PetItem getTemplate(int itemId) {
        return (L1PetItem) _petItemIdIndex.get(itemId);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.PetItemTable JD-Core Version: 0.6.2
 */