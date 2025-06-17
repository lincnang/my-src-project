package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CharItemSublimationStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CharItemSublimationTable implements CharItemSublimationStorage {
    private static CharItemSublimationTable _instance;
    private static final Log _log = LogFactory.getLog(CharItemSublimationTable.class);

    private static final Map<Integer, CharItemSublimation> _powerMap = new HashMap<>();

    private static void errorItem(final int item_obj_id) {
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement pstm = con.prepareStatement("DELETE FROM `character_昇華` WHERE `item_obj_id`=?")) {
            pstm.setInt(1, item_obj_id);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void insertItem(final CharItemSublimation sublimation) throws Exception {
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement pstm = con.prepareStatement(
                     "INSERT INTO `character_昇華` SET `item_obj_id`=?,`char_id`=?,`item_name`=?,`Type`=?,`Lv`=?")) {
            int i = 0;
            pstm.setInt(++i, sublimation.get_item_obj_id());
            pstm.setInt(++i, sublimation.get_char_obj_id());
            pstm.setString(++i, sublimation.get_item_name());
            pstm.setInt(++i, sublimation.getType());
            pstm.setInt(++i, sublimation.getLv());
            pstm.execute();
        }
    }

    @Override
    public void load(final boolean local) {
        final PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        try (Connection cn = local ? DatabaseFactory.get().getConnection() : null;
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM `character_昇華`");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                final CharItemSublimation sub = new CharItemSublimation();
                final int item_obj_id = rs.getInt("item_obj_id");
                final L1ItemInstance item = WorldItem.get().getItem(item_obj_id);

                if (item == null) {
                    errorItem(item_obj_id);
                } else {
                    sub.set_item_obj_id(item_obj_id);
                    sub.set_char_obj_id(rs.getInt("char_id"));
                    sub.set_item_name(rs.getString("item_name"));
                    sub.setType(rs.getInt("Type"));
                    sub.setLv(rs.getInt("Lv"));

                    if (item.getSublimation() == null) {
                        item.setSublimation(sub);
                    }
                    _powerMap.put(item_obj_id, sub);
                    i++;
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        _log.info("載入->[全部角色昇華物品數量]: " + i + "(" + timer.get() + "ms)");
    }

    @Override
    public void storeItem(final CharItemSublimation sublimation) throws Exception {
        final int item_obj_id = sublimation.get_item_obj_id();
        if (!_powerMap.containsKey(item_obj_id)) {
            insertItem(sublimation);
            _powerMap.put(item_obj_id, sublimation);
        } else {
            updateItem(sublimation);
        }
    }
    public static CharItemSublimationTable get() {
        if (_instance == null) {
            _instance = new CharItemSublimationTable();
        }
        return _instance;
    }
    @Override
    public void updateItem(final CharItemSublimation sublimation) {
        try (Connection co = DatabaseFactory.get().getConnection();
             PreparedStatement pm = co.prepareStatement(
                     "UPDATE `character_昇華` SET `Type`=?,`Lv`=?, `item_name`=? WHERE `item_obj_id`=?")) {

            int i = 0;
            pm.setInt(++i, sublimation.getType());
            pm.setInt(++i, sublimation.getLv());
            pm.setString(++i, sublimation.get_item_name());
            pm.setInt(++i, sublimation.get_item_obj_id());
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void deleteItem(final int item_obj_id) {
        _powerMap.remove(item_obj_id);
        errorItem(item_obj_id);
    }

    @Override
    public CharItemSublimation loadItem(final int item_obj_id) {
        CharItemSublimation result = null;
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM `character_昇華` WHERE `item_obj_id`=?")) {

            ps.setInt(1, item_obj_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = new CharItemSublimation();
                    result.set_item_obj_id(rs.getInt("item_obj_id"));
                    result.set_char_obj_id(rs.getInt("char_id"));
                    result.set_item_name(rs.getString("item_name"));
                    result.setType(rs.getInt("Type"));
                    result.setLv(rs.getInt("Lv"));
                }
            }
        } catch (SQLException e) {
            _log.error("讀取昇華資料失敗", e);
        }
        return result;
    }
}
