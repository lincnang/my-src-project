package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1BossWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 類名稱：boss武器資料<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public final class ExtraBossWeaponTable {
    private static final Log _log = LogFactory.getLog(ExtraBossWeaponTable.class);
    private static final Map<Integer, L1BossWeapon> _BossList = new LinkedHashMap<>();
    private static ExtraBossWeaponTable _instance;

    public static ExtraBossWeaponTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraBossWeaponTable();
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
            pstm = con.prepareStatement("SELECT * FROM 其他_武器靈魂水晶系統 ORDER BY 道具編號");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int item_id = rs.getInt("道具編號");
                int boss_lv = rs.getInt("階級");
                String boss_name = rs.getString("顯示武器名稱");
                int success_random = rs.getInt("成功機率");
                int max_use_time = rs.getInt("使用時間");
                String success_msg = rs.getString("成功顯示");
                String failure_msg = rs.getString("失敗顯示");
                int probability = rs.getInt("效果發動機率");
                boolean isLongRange = rs.getBoolean("遠距離開關");
                int fixDamage = rs.getInt("固定傷害");
                int randomDamage = rs.getInt("隨機傷害");
                double doubleDmgValue = rs.getInt("暴擊倍率");
                int gfxId = rs.getInt("攻擊特效編號");
                boolean gfxIdTarget = rs.getBoolean("目標對象");
                boolean arrowType = rs.getBoolean("飛行效果開關");
                int effectId = rs.getInt("額外技能編號");
                int effectTime = rs.getInt("技能持續時間");
                final int negativeId = rs.getInt("額外負面技能類型");
                final int negativeTime = rs.getInt("額外負面技能時間");
                int attr = rs.getInt("傷害附加屬性");
                int hpAbsorb = rs.getInt("吸血量");
                int mpAbsorb = rs.getInt("吸魔量");
                boolean type_remove_weapon = rs.getBoolean("type_remove_weapon");
                int type_remove_armor = rs.getInt("type_remove_armor");
                L1BossWeapon bossStone = new L1BossWeapon(item_id, boss_lv, boss_name, success_random, max_use_time, success_msg, failure_msg, probability, isLongRange, fixDamage, randomDamage, doubleDmgValue, gfxId, gfxIdTarget, arrowType, effectId, effectTime, negativeId, negativeTime, attr, hpAbsorb, mpAbsorb, type_remove_weapon, type_remove_armor);
                int index = item_id * 100 + boss_lv;
                _BossList.put(index, bossStone);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統]_武器Boss靈魂水晶系統資料數量: " + _BossList.size() + "(" + timer.get() + "ms)");
    }

    public L1BossWeapon get(int id, int boss_lv) {
        int index = id * 100 + boss_lv;
        return (L1BossWeapon) _BossList.get(index);
    }

    public int BossWeaponMax() {
        int max = 0;
        for (Integer key : _BossList.keySet()) {
            L1BossWeapon bossWeapon = (L1BossWeapon) _BossList.get(key);
            if (bossWeapon.getItemId() > max) {
                max = bossWeapon.getItemId();
            }
        }
        return max;
    }

    public L1BossWeapon get(int itemId) {
        // TODO Auto-generated method stub
        return null;
    }
}
