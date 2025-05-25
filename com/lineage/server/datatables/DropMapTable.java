package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.templates.L1DropMap;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 掉落物品資料(指定地圖)
 *
 * @author dexc
 */
public class DropMapTable {
    private static final Log _log = LogFactory.getLog(DropMapTable.class);
    private static DropMapTable _instance;

    public static DropMapTable get() {
        if (_instance == null) {
            _instance = new DropMapTable();
        }
        return _instance;
    }

    /**
     * 更新物品註記
     *
     * @param npcId
     * @param itemId
     */
    private static void updata_name(int mapid, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String mapname = MapsTable.get().getMapName(mapid);
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `droplist_map` SET `note`=? WHERE `地圖編號`=? AND `物品編號`=?");
            int i = 0;
            ps.setString(++i, mapname + "=>" + itemname);
            ps.setInt(++i, mapid);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 刪除錯誤物品資料
     *
     * @param objid
     */
    private static void errorItem(int itemid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `droplist_map` WHERE `物品編號`=?");
            pstm.setInt(1, itemid);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void load() {
        final Map<Integer, ArrayList<L1DropMap>> droplists = this.allDropList();
        final SetDropExecutor setDropExecutor = new SetDrop();
        setDropExecutor.addDropMapX(droplists);
    }

    private Map<Integer, ArrayList<L1DropMap>> allDropList() {
        final PerformanceTimer timer = new PerformanceTimer();
        final Map<Integer, ArrayList<L1DropMap>> droplistMap = new HashMap<Integer, ArrayList<L1DropMap>>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `droplist_map`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int mapid = rs.getInt("地圖編號");
                final int itemId = rs.getInt("物品編號");
                final int min = rs.getInt("最小掉落數量");
                final int max = rs.getInt("最大掉落數量");
                final int chance = rs.getInt("機率(1000000)");
                final int enchant_min = rs.getInt("最小強化值"); // 最小強化值 by
                // terry0412
                final int enchant_max = rs.getInt("最大強化值"); // 最大強化值 by
                // terry0412
                if (check_item(itemId)) {
                    final L1DropMap drop = new L1DropMap(mapid, itemId, enchant_min, enchant_max, min, max, chance);
                    // dorplist 物品列表
                    ArrayList<L1DropMap> dropList = droplistMap.get(drop.get_mapid());
                    if (dropList == null) {
                        dropList = new ArrayList<L1DropMap>();
                        droplistMap.put(new Integer(drop.get_mapid()), dropList);
                    }
                    dropList.add(drop);
                }
                String note = rs.getString("note");//物品註記
                if (!note.contains("=>")) {
                    updata_name(mapid, itemId);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->掉落物品資料數量(指定地圖): " + droplistMap.size() + "(" + timer.get() + "ms)");
        return droplistMap;
    }

    private boolean check_item(int itemId) {
        final L1Item itemTemplate = ItemTable.get().getTemplate(itemId);
        if (itemTemplate == null) {
            // 無該物品資料 移除
            errorItem(itemId);
            return false;
        }
        return true;
    }
}