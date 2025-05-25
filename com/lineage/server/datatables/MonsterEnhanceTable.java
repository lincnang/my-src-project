package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1MonsterEnhanceInstance;
import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 怪物強化系統
 *
 * <p>
 * 怪物強化公式 - (current_dc / dc_enhance) * 屬性 + 原始屬性 = 最後屬性
 * <p>
 * 屬性 - monster_enhance裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr 原始屬性 - npc裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr
 * 最後屬性 - 怪物重生後的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr
 * </p>
 */
public class MonsterEnhanceTable {
    private static final Log _log = LogFactory.getLog(MonsterEnhanceTable.class);
    private static final Map<Integer, L1MonsterEnhanceInstance> _meis = new HashMap<>();
    private static MonsterEnhanceTable _instance;
    private boolean _initialized;

    private MonsterEnhanceTable() {
        load();
        _initialized = true;
    }

    public static synchronized MonsterEnhanceTable getInstance() {
        if (_instance == null) {
            _instance = new MonsterEnhanceTable();
        }
        return _instance;
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public final void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement pstm = con.prepareStatement("SELECT * FROM 系統_怪物強化設置");
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                L1MonsterEnhanceInstance mei = new L1MonsterEnhanceInstance();
                int npcid = rs.getInt("怪物編號");
                mei.setNpcId(npcid);
                mei.setCurrentDc(rs.getInt("怪物死亡-設定每(X)次後則強化"));
                mei.setDcEnhance(rs.getInt("怪物死亡-設定(X)後強化一次"));
                mei.setLevel(rs.getInt("增加等級"));
                mei.setHp(rs.getInt("增加血量"));
                mei.setMp(rs.getInt("增加魔量"));
                mei.setAc(rs.getInt("增加防禦"));
                mei.setStr(rs.getInt("增加力量"));
                mei.setDex(rs.getInt("增加敏捷"));
                mei.setCon(rs.getInt("增加體質"));
                mei.setWis(rs.getInt("增加精神"));
                mei.setIntelligence(rs.getInt("增加智力"));
                mei.setMr(rs.getInt("增加魔防"));
                mei.setHpr(rs.getInt("增加回血"));
                synchronized (_meis) {
                    _meis.put(npcid, mei);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        _log.info("讀取->[怪物強化資料數量]: " + _meis.size() + "(" + timer.get() + "ms)");
    }

    public void save(L1MonsterEnhanceInstance mei) {
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement pstm = con.prepareStatement("UPDATE 系統_怪物強化 SET current_dc=? WHERE 怪物編號=?")) {

            pstm.setInt(1, mei.getCurrentDc());
            pstm.setInt(2, mei.getNpcId());
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public L1MonsterEnhanceInstance getTemplate(int npcId) {
        synchronized (_meis) {
            return _meis.get(npcId);
        }
    }
}
