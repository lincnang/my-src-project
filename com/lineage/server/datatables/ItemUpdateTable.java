package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemUpdate;
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

public class ItemUpdateTable {
    private static final Log _log = LogFactory.getLog(ItemUpdateTable.class);
    private static Map<Integer, ArrayList<L1ItemUpdate>> _updateMap = new HashMap<Integer, ArrayList<L1ItemUpdate>>();
    private static ItemUpdateTable _instance;

    public static ItemUpdateTable get() {
        if (_instance == null) {
            _instance = new ItemUpdateTable();
        }
        return _instance;
    }

    public static void delete(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `server_item_update` WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_item_update` ORDER BY `id`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int item_id = rs.getInt("itemid");
                if (ItemTable.get().getTemplate(item_id) == null) {
                    _log.error("物品升級資料錯誤: 沒有這個編號的道具:" + item_id);
                    delete(id);
                } else {
                    String needids_c = rs.getString("needids").replaceAll(" ", "");
                    String[] needids_tmp = needids_c.split(",");
                    String needcounts_c = rs.getString("needcounts").replaceAll(" ", "");
                    String[] needcounts_tmp = needcounts_c.split(",");
                    if (needids_tmp.length != needcounts_tmp.length) {
                        _log.error("物品升級資料錯誤: 交換物品需要道具數量不吻合" + item_id);
                    } else {
                        int toid = rs.getInt("toid");
                        if (ItemTable.get().getTemplate(toid) == null) {
                            _log.error("物品升級資料錯誤: 沒有這個編號的對象道具:" + toid);
                            delete(id);
                        } else {
                            int[] needids = new int[needids_tmp.length];
                            for (int i = 0; i < needids_tmp.length; i++) {
                                needids[i] = Integer.parseInt(needids_tmp[i]);
                            }
                            int[] needcounts = new int[needcounts_tmp.length];
                            for (int i = 0; i < needcounts_tmp.length; i++) {
                                needcounts[i] = Integer.parseInt(needcounts_tmp[i]);
                            }
                            L1ItemUpdate tmp = new L1ItemUpdate();
                            tmp.set_item_id(item_id);
                            tmp.set_toid(toid);
                            tmp.set_needids(needids);
                            tmp.set_needcounts(needcounts);
                            ArrayList<L1ItemUpdate> value = (ArrayList<L1ItemUpdate>) _updateMap.get(Integer.valueOf(item_id));
                            if (value == null) {
                                value = new ArrayList<L1ItemUpdate>();
                                value.add(tmp);
                            } else {
                                value.add(tmp);
                            }
                            _updateMap.put(Integer.valueOf(item_id), value);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->物品升級資料數量: " + _updateMap.size() + "(" + timer.get() + "ms)");
    }

    public ArrayList<L1ItemUpdate> get(int key) {
        return (ArrayList<L1ItemUpdate>) _updateMap.get(Integer.valueOf(key));
    }

    public Map<Integer, ArrayList<L1ItemUpdate>> map() {
        return _updateMap;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.ItemUpdateTable JD-Core Version: 0.6.2
 */