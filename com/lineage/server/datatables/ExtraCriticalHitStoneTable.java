package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1CriticalHitStone;
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

/**
 * 類名稱：暴擊寶石鑲嵌資料<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public final class ExtraCriticalHitStoneTable {
    private static final Log _log = LogFactory.getLog(ExtraCriticalHitStoneTable.class);
    private static final Map<Integer, L1CriticalHitStone> _stoneList = new HashMap<Integer, L1CriticalHitStone>();
    private static ExtraCriticalHitStoneTable _instance;

    public static ExtraCriticalHitStoneTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraCriticalHitStoneTable();
        }
        return _instance;
    }

    public final void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 寶_武器爆擊寶石鑲嵌系統");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("道具編號");
                final int next_item_id = rs.getInt("合成下一階道具編號");
                final String name = rs.getString("顯示在道具狀態的名稱");
                final int chance = rs.getInt("合成機率");
                final int critical_hit_chance = rs.getInt("暴擊機率 (%)");
                final int critical_hit_damage = rs.getInt("暴擊傷害 (%)");
                final int gfxId = rs.getInt("特效編號");
                final boolean gfxIdTarget = rs.getBoolean("發動特效對象 (0=自己, 1=對方)");
                final boolean arrowType = rs.getBoolean("是否為飛行特效");
                final L1CriticalHitStone stone = new L1CriticalHitStone(item_id, next_item_id, name, chance, critical_hit_chance, critical_hit_damage, gfxId, gfxIdTarget, arrowType);
                _stoneList.put(item_id, stone);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統]_武器爆擊寶石鑲嵌系統資料數量: " + _stoneList.size() + "(" + timer.get() + "ms)");
    }

    public final L1CriticalHitStone get(final int itemId) {
        return _stoneList.get(itemId);
    }
}
