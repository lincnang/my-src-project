package com.lineage.data.event.SoulQueen;

import com.lineage.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 噬魂塔副本
 *
 * @author l1j-jp
 */
public class SoulTowerSpawnTable {
    private static Logger _log = Logger.getLogger(SoulTowerSpawnTable.class.getName());
    private static SoulTowerSpawnTable _instance;
    private final HashMap<Integer, ArrayList<SoulTowerNpcModel>> npcList = new HashMap<>();

    public static SoulTowerSpawnTable get() {
        if (_instance == null) {
            _instance = new SoulTowerSpawnTable();
        }
        return _instance;
    }

    public ArrayList<SoulTowerNpcModel> getType(final int type) {
        return npcList.get(type);
    }

    public void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 任務_屍魂塔副本設置");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final SoulTowerNpcModel model = new SoulTowerNpcModel();
                model.setType(rs.getInt("type"));
                //model.setPoint_index(rs.getInt("point_index"));
                model.setPoint_index(rs.getInt("count"));
                model.setNpcid(rs.getInt("npc_id"));
                model.setLocx(rs.getInt("locx"));
                model.setLocy(rs.getInt("locy"));
                model.setHeading(rs.getInt("heading"));
                if (npcList.get(model.getType()) == null) {
                    final ArrayList<SoulTowerNpcModel> test = new ArrayList<>();
                    test.add(model);
                    npcList.put(model.getType(), test);
                } else {
                    npcList.get(model.getType()).add(model);
                }
            }
        } catch (final Exception e) {
            _log.warning("加载噬魂塔召唤怪物出错：" + e);
        }
    }
}
