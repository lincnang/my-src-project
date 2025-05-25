package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MagicWeapon;
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
import java.util.List;
import java.util.Map;

/**
 * 類名稱：武器魔法DIY資料數量<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public final class ExtraMagicWeaponTable {
    private static final Log _log = LogFactory.getLog(ExtraMagicWeaponTable.class);
    private static final Map<Integer, L1MagicWeapon> _magicList = new HashMap<>();
    private static ExtraMagicWeaponTable _instance;

    public static ExtraMagicWeaponTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraMagicWeaponTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 其他_武器附魔系統 ORDER BY 道具編號");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("道具編號");
                final String skill_name = rs.getString("顯示武器名稱");
                final int success_random = rs.getInt("機率");
                final int max_use_time = rs.getInt("使用時間");
                final String success_msg = rs.getString("成功顯示");
                final String failure_msg = rs.getString("失敗顯示");
                final int probability = rs.getInt("效果發動機率");
                final boolean isLongRange = rs.getBoolean("遠距離開關");
                final int fixDamage = rs.getInt("固定傷害");
                final int randomDamage = rs.getInt("隨機傷害");
                final double doubleDmgValue = rs.getInt("暴擊倍率");
                final int gfxId = rs.getInt("特效編號");
                final boolean gfxIdTarget = rs.getBoolean("目標對象");
                String gfxIdOtherLocStr = rs.getString("額外發動特效座標");
                List<int[]> gfxIdOtherLoc = null;
                if (gfxIdOtherLocStr != null && !gfxIdOtherLocStr.isEmpty()) {
                    gfxIdOtherLoc = new ArrayList<>();
                    gfxIdOtherLocStr = gfxIdOtherLocStr.replace(" ", "");
                    for (final String value : gfxIdOtherLocStr.split(",")) {
                        final String[] value2 = value.split("/");
                        gfxIdOtherLoc.add(new int[]{Integer.parseInt(value2[0]), Integer.parseInt(value2[1])});
                    }
                }
                final int area = rs.getInt("範圍傷害");
                final boolean arrowType = rs.getBoolean("飛行效果開關");
                final int effectId = rs.getInt("額外技能(skill)");
                final int effectTime = rs.getInt("額外技能時間");
                final int negativeId = rs.getInt("額外負面技能類型");
                final int negativeTime = rs.getInt("額外負面技能時間");
                final int attr = rs.getInt("傷害附加屬性");
                final int hpAbsorb = rs.getInt("吸血量");
                final int mpAbsorb = rs.getInt("吸魔量");
                final L1MagicWeapon magicStone = new L1MagicWeapon(item_id, skill_name, success_random, max_use_time, success_msg, failure_msg, probability, isLongRange, fixDamage, randomDamage, doubleDmgValue, gfxId, gfxIdTarget, gfxIdOtherLoc, area, arrowType, effectId, effectTime, negativeId, negativeTime, attr, hpAbsorb, mpAbsorb);
                _magicList.put(item_id, magicStone);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統]_武器附魔資料數量: " + _magicList.size() + "(" + timer.get() + "ms)");
    }

    public L1MagicWeapon get(final int id) {
        return _magicList.get(id);
    }
}
