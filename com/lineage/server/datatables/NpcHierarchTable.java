package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Hierarch;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class NpcHierarchTable {
    private static final Log _log = LogFactory.getLog(NpcHierarchTable.class);
    private static final HashMap<Integer, L1Hierarch> _powerMap = new HashMap<Integer, L1Hierarch>();
    private static NpcHierarchTable _instance;

    private NpcHierarchTable() {
        load();
    }

    // private static final HashMap<Integer, L1Hierarch> _powerMap = new HashMap();
    public static NpcHierarchTable get() {
        if (_instance == null) {
            _instance = new NpcHierarchTable();
        }
        return _instance;
    }

    private void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `祭司能力設置`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int npc_id = rs.getInt("npc_id");
                String skill_id = rs.getString("skill_id").replaceAll(" ", "");
                String skill_mp = rs.getString("skill_mp").replaceAll(" ", "");
                int[] SkillId = (int[]) null;
                if ((skill_id != null) && (!skill_id.equals(""))) {
                    String[] set2 = skill_id.split(",");
                    SkillId = new int[set2.length];
                    for (int i = 0; i < set2.length; i++) {
                        SkillId[i] = Integer.parseInt(set2[i]);
                    }
                }
                int[] SkillMp = (int[]) null;
                if ((skill_mp != null) && (!skill_mp.equals(""))) {
                    String[] set2 = skill_mp.split(",");
                    SkillMp = new int[set2.length];
                    for (int i = 0; i < set2.length; i++) {
                        SkillMp[i] = Integer.parseInt(set2[i]);
                    }
                }
                L1Hierarch hierarch = new L1Hierarch();
                hierarch.setSkillId(SkillId);
                hierarch.setSkillMp(SkillMp);
                _powerMap.put(Integer.valueOf(npc_id), hierarch);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[祭司能力系統]資料數量: " + _powerMap.size() + "(" + timer.get() + "ms)");
    }

    public L1Hierarch getHierarch(int key) {
        if (_powerMap.containsKey(Integer.valueOf(key))) {
            return (L1Hierarch) _powerMap.get(Integer.valueOf(key));
        }
        return null;
    }
}
