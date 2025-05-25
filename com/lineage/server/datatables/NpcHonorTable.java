package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * NPC積分設置資料 2025/05/02 by 台灣JAVA技術老爹更新積分狩獵改變(MIN~MAX)
 *
 * @author dexc
 */
public class NpcHonorTable {

    private static final Log _log = LogFactory.getLog(NpcHonorTable.class);
    private static final Map<Integer, Integer> _scoremaxList = new TreeMap<>();
    private static final Map<Integer, Integer> _scoreminList = new TreeMap<>();
    private static final Map<Integer, Integer> _randomList = new TreeMap<>();
    private static final ArrayList<Integer> _scorenpcList = new ArrayList<>();
    private static final Set<Integer> _targetNpcSet = new HashSet<>();
    private static final Map<Integer, Integer> _npcMapIdList = new HashMap<>();

    private static NpcHonorTable _instance;
    private static final Random _random = new Random();

    public static NpcHonorTable get() {
        if (_instance == null) {
            _instance = new NpcHonorTable();
        }
        return _instance;
    }

    public void reload() {
        _scoremaxList.clear();
        _scoreminList.clear();
        _scorenpcList.clear();
        _randomList.clear();
        _targetNpcSet.clear();
        _npcMapIdList.clear();
        load();
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_爵位獵取設置` WHERE 獲取機率 > 0");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int npcId = rs.getInt("怪物編號");
                final int score_min = rs.getInt("積分最小值");
                final int score_max = rs.getInt("積分最大值");
                final int rand = rs.getInt("獲取機率");
                final int mapId = rs.getInt("地圖ID");

                _scoremaxList.put(npcId, score_max);
                _scoreminList.put(npcId, score_min);
                _randomList.put(npcId, rand);
                _scorenpcList.add(npcId);
                _targetNpcSet.add(npcId);
                _npcMapIdList.put(npcId, mapId);
            }

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->系統_爵位獵取設置資料數量: " + _scoremaxList.size() + "(" + timer.get() + "ms)");
    }

    public ArrayList<Integer> get_scoreList() {
        return _scorenpcList;
    }

    public int get_score(final int npcid) {
        if (_scoreminList.containsKey(npcid)) {
            final int max = _scoremaxList.get(npcid);
            final int min = _scoreminList.get(npcid);
            return min + _random.nextInt(max - min + 1);
        }
        return 0;
    }

    public int get_random(final int npcid) {
        return _randomList.getOrDefault(npcid, 0);
    }

    public boolean isTargetNpc(final int npcId) {
        return _targetNpcSet.contains(npcId);
    }
    public int getMapId(int npcId) {
        return _npcMapIdList.getOrDefault(npcId, 0); // 若找不到就回傳 0
    }
    public List<Integer> getNpcIdListByMap(int mapId) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : _npcMapIdList.entrySet()) {
            int npcId = entry.getKey();
            int assignedMapId = entry.getValue();

            if (assignedMapId == mapId) { // ✅ 僅限當前地圖任務怪
                result.add(npcId);
            }
        }
        return result;
    }

}
