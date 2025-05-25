package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1AttrWeapon;
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
 * 類名稱：特殊屬性武器系統  [屬性卷軸功能設定]<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public final class ExtraAttrWeaponTable {
    private static final Log _log = LogFactory.getLog(ExtraAttrWeaponTable.class);
    private static final Map<Integer, L1AttrWeapon> _attrList = new LinkedHashMap<Integer, L1AttrWeapon>();
    private static ExtraAttrWeaponTable _instance;

    public static ExtraAttrWeaponTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraAttrWeaponTable();
        }
        return _instance;
    }

    public final void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 系統_屬性DB設定 ORDER BY id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");//屬性代碼
                String name = rs.getString("顯示名子");//顯示名子
                int stage = rs.getInt("屬性階段");//屬性階段
                int chance = rs.getInt("升階機率(1/1000)");//升階機率(1/1000)
                int probability = rs.getInt("技能機率 (1/1000)");//技能機率 (1/1000)
                double type_bind = rs.getDouble("束縛時間(秒)");//束縛時間(秒)
                double type_drain_hp = rs.getDouble("吸血量");//吸血量
                int type_drain_mp = rs.getInt("吸魔量");//吸魔量
                double type_dmgup = rs.getDouble("傷害倍率");//傷害倍率
                int type_range = rs.getInt("範圍");//範圍
                int type_range_dmg = rs.getInt("範圍傷害");//範圍傷害
                int type_light_dmg = rs.getInt("光裂術傷害");//光裂術傷害
                boolean type_skill_1 = rs.getBoolean("闇盲咒術");//闇盲咒術
                boolean type_skill_2 = rs.getBoolean("魔法封印");//魔法封印
                boolean type_skill_3 = rs.getBoolean("變形術");//變形術
                double type_skill_time = rs.getDouble("技能時間");//技能時間
                String temp_poly_list = rs.getString("變型編號");//變型編號
                String[] type_poly_list = (String[]) null;
                if ((temp_poly_list != null) && (!temp_poly_list.isEmpty())) {
                    type_poly_list = temp_poly_list.replace(" ", "").split(",");
                }
                boolean type_remove_weapon = rs.getBoolean("卸除對方武器");//卸除對方武器
                boolean type_remove_doll = rs.getBoolean("卸除對方魔法娃娃");//卸除對方魔法娃娃
                int type_remove_armor = rs.getInt("卸除對方防具");//卸除對方防具
                int extradmg = rs.getInt("額外近距離傷害");
                int extrabowdmg = rs.getInt("額外遠距離傷害");
                L1AttrWeapon attrWeapon = new L1AttrWeapon(name, stage, chance, probability, type_bind, type_drain_hp, type_drain_mp, type_dmgup, type_range, type_range_dmg, type_light_dmg, type_skill_1, type_skill_2, type_skill_3, type_skill_time, type_poly_list, type_remove_weapon, type_remove_doll, type_remove_armor, extradmg, extrabowdmg);
                int index = id * 100 + stage;
                _attrList.put(Integer.valueOf(index), attrWeapon);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->特殊屬性武器[DB化]系統數量: " + _attrList.size() + "(" + timer.get() + "ms)");
    }

    public final L1AttrWeapon get(int id, int stage) {
        int index = id * 100 + stage;
        return (L1AttrWeapon) _attrList.get(Integer.valueOf(index));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.ExtraAttrWeaponTable JD-Core Version: 0.6.2
 */