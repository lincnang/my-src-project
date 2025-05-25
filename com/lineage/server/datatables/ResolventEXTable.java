package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
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
import java.util.Random;
import java.util.StringTokenizer;

public final class ResolventEXTable {
    private static final Log _log = LogFactory.getLog(ResolventEXTable.class);
    private static ResolventEXTable _instance;
    private final Map<Integer, Gift> _resolvent = new HashMap<>();

    private ResolventEXTable() {
        load();
    }

    public static ResolventEXTable get() {
        if (_instance == null) {
            _instance = new ResolventEXTable();
        }
        return _instance;
    }

    public static void reload() {
        ResolventEXTable oldInstance = _instance;
        _instance = new ResolventEXTable();
        oldInstance._resolvent.clear();
    }

    private static int[] getArray(String s) {
        if ((s == null) || (s.equalsIgnoreCase("")) || (s.isEmpty())) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        String sTemp = null;
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    private void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM resolvent_ex");
            for (rs = ps.executeQuery(); rs.next(); ) {
                int itemId = rs.getInt("道具編號");
                int[] crystalId = getArray(rs.getString("分解後的道具編號"));
                int[] crystalMincount = getArray(rs.getString("分解後最小值"));
                int[] crystalMaxcount = getArray(rs.getString("分解後最大值"));
                Gift g = new Gift();
                g._crystal_id = crystalId;
                g._crystalMincount = crystalMincount;
                g._crystalMaxcount = crystalMaxcount;
                this._resolvent.put(itemId, g);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->進階版溶解物品設置資料數量: " + this._resolvent.size() + "(" + timer.get() + "ms)");
    }

    public boolean getCrystalCount(L1PcInstance pc, int itemId) {
        if (this._resolvent.containsKey(itemId)) {
            Gift g = (Gift) this._resolvent.get(itemId);
            int[] item_id = g._crystal_id;
            int[] min_count = g._crystalMincount;
            int[] max_count = g._crystalMaxcount;
            if ((item_id != null) && (min_count != null) && (max_count != null)) {
                for (int i = 0; i < item_id.length; i++) {
                    L1ItemInstance item = ItemTable.get().createItem(item_id[i]);
                    if (max_count[i] <= 0) {
                        item.setCount(min_count[i]);
                    } else {
                        Random r = new Random();
                        item.setCount(min_count[i] + r.nextInt(max_count[i]));
                    }
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1L) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static class Gift {
        private int[] _crystal_id = null;
        private int[] _crystalMincount = null;
        private int[] _crystalMaxcount = null;

        private Gift() {
        }
    }
}
