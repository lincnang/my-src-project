package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MobGroup;
import com.lineage.server.templates.L1NpcCount;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobGroupTable {
    private static final Log _log = LogFactory.getLog(MobGroupTable.class);
    private static final Map<Integer, L1MobGroup> _mobGroupIndex = new HashMap<Integer, L1MobGroup>();
    private static MobGroupTable _instance;

    public static MobGroupTable get() {
        if (_instance == null) {
            _instance = new MobGroupTable();
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
            pstm = con.prepareStatement("SELECT * FROM `mobgroup`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int mobGroupId = rs.getInt("id");
                boolean isRemoveGroup = rs.getBoolean("remove_group_if_leader_die");
                int leaderId = rs.getInt("leader_id");
                List<L1NpcCount> minions = Lists.newArrayList();
                for (int i = 1; i <= 7; i++) {
                    int id = rs.getInt("minion" + i + "_id");
                    int count = rs.getInt("minion" + i + "_count");
                    minions.add(new L1NpcCount(id, count));
                }
                L1MobGroup mobGroup = new L1MobGroup(mobGroupId, leaderId, minions, isRemoveGroup);
                _mobGroupIndex.put(Integer.valueOf(mobGroupId), mobGroup);
            }
            _log.info("讀取->MOB隊伍資料數量: " + _mobGroupIndex.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public L1MobGroup getTemplate(int mobGroupId) {
        return (L1MobGroup) _mobGroupIndex.get(Integer.valueOf(mobGroupId));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.MobGroupTable JD-Core Version: 0.6.2
 */