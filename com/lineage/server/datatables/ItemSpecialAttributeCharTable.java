package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ItemSpecialAttributeChar;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;

/**
 * 人物物品特殊屬性資料表<br>
 * 類名稱：ItemSpecialAttributeCharTable<br>
 * 創建人:xljnet<br>
 * 修改時間：2018年4月25日 下午3:56:12<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 *
 * @version Rev:3.2 Bin:81222<br>
 */
public class ItemSpecialAttributeCharTable {
    private static final Log _log = LogFactory.getLog(ItemSpecialAttributeCharTable.class);
    private static final HashMap<Integer, L1ItemSpecialAttributeChar> _AtrrCharList = new HashMap<Integer, L1ItemSpecialAttributeChar>();
    private static ItemSpecialAttributeCharTable _instance;

    public static ItemSpecialAttributeCharTable get() {
        if (_instance == null) {
            _instance = new ItemSpecialAttributeCharTable();
        }
        return _instance;
    }

    /**
     * 初始化建立資料
     *
     */
    private static void addValue(final int item_obj_id, final L1ItemSpecialAttributeChar ItemAttr) {
        final L1ItemInstance item = WorldItem.get().getItem(item_obj_id);
        if (item != null) {
            if (item.get_ItemAttrName2() == null) {
                item.set_ItemAttrName(ItemAttr);
            }
        }
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `Manly_炫色記錄資料`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int itemobjid = rs.getInt("玩家流水號");
                final String item_name = rs.getString("炫色武防名稱");
                final int attr_id = rs.getInt("炫色代碼");
                final String add_pc_name = rs.getString("角色名稱");
                final Timestamp add_time = rs.getTimestamp("時間");
                final String add_mon_name = rs.getString("使用方式");
                final String mapname = rs.getString("地圖方式");
                final String Acquisition_mode = rs.getString("洗白方式");
                L1ItemSpecialAttributeChar attr_char = new L1ItemSpecialAttributeChar();
                attr_char.set_itemobjid(itemobjid);
                attr_char.set_item_name(item_name);
                attr_char.set_attr_id(attr_id);
                attr_char.set_add_pc_name(add_pc_name);
                attr_char.set_add_time(add_time);
                attr_char.set_add_mon_name(add_mon_name);
                attr_char.set_mapname(mapname);
                attr_char.set_Acquisition_mode(Acquisition_mode);
                addValue(itemobjid, attr_char);
                i++;
                _AtrrCharList.put(Integer.valueOf(itemobjid), attr_char);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->人物物品特殊屬性數量: " + i + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回指定人物物品特殊屬
     *
     */
    public L1ItemSpecialAttributeChar getAttrId(final int itemobjid) {
        return _AtrrCharList.get(itemobjid);
    }

    /**
     * 增加物品特殊屬性
     *
     */
    public void storeItem(final int itemobjid, final L1ItemSpecialAttributeChar ItemAttr) throws Exception {
        if (_AtrrCharList.get(itemobjid) != null) {
            return;
        }
        _AtrrCharList.put(itemobjid, ItemAttr);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `Manly_炫色記錄資料` SET `玩家流水號`=?," + "`炫色武防名稱`=?,`炫色代碼`=?,`角色名稱`=?,`時間`=?,`使用方式`=?,`地圖方式`=?,`洗白方式`=?");
            int i = 0;
            pstm.setInt(++i, itemobjid);
            pstm.setString(++i, ItemAttr.get_item_name());
            pstm.setInt(++i, ItemAttr.get_attr_id());
            pstm.setString(++i, ItemAttr.get_add_pc_name());
            pstm.setTimestamp(++i, ItemAttr.get_add_time());
            pstm.setString(++i, ItemAttr.get_add_mon_name());
            pstm.setString(++i, ItemAttr.get_mapname());
            pstm.setString(++i, ItemAttr.get_Acquisition_mode());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 更新物品特殊屬性
     *
     */
    public void updateItem(final int itemobjid, final L1ItemSpecialAttributeChar ItemAttr) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `Manly_炫色記錄資料` SET `炫色武防名稱`=?,`炫色代碼`=?,`角色名稱`=?," + "`時間`=?,`使用方式`=?,`地圖方式`=?,`洗白方式`=? WHERE `玩家流水號`=?");
            int i = 0;
            pm.setString(++i, ItemAttr.get_item_name());
            pm.setInt(++i, ItemAttr.get_attr_id());
            pm.setString(++i, ItemAttr.get_add_pc_name());
            pm.setTimestamp(++i, ItemAttr.get_add_time());
            pm.setString(++i, ItemAttr.get_add_mon_name());
            pm.setString(++i, ItemAttr.get_mapname());
            pm.setString(++i, ItemAttr.get_Acquisition_mode());
            pm.setInt(++i, itemobjid);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }
}
