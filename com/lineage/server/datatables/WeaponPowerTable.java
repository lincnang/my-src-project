package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Weapon;
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

public class WeaponPowerTable {
    private static final Log _log = LogFactory.getLog(WeaponPowerTable.class);
    private static final Map<Integer, int[]> _weaponPower = new HashMap<Integer, int[]>();
    private static WeaponPowerTable _instance;

    public static WeaponPowerTable get() {
        if (_instance == null) {
            _instance = new WeaponPowerTable();
        }
        return _instance;
    }

    private static void delete(int weapon_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `weapon_power` WHERE `weapon_id`=?");
            ps.setInt(1, weapon_id);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `weapon_power`");
            rs = pstm.executeQuery();
            fillWeaponSkillTable(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        set_weapon_power();
        _log.info("讀取->武器額外傷害資料數量: " + _weaponPower.size() + "(" + timer.get() + "ms)");
    }

    private void set_weapon_power() {
        try {
            for (Integer key : _weaponPower.keySet()) {
                L1Item item = ItemTable.get().getTemplate(key.intValue());
                if ((item instanceof L1Weapon)) {
                    int[] power = (int[]) _weaponPower.get(key);
                    L1Weapon weapon = (L1Weapon) item;
                    weapon.set_add_dmg(power[0], power[1]);
                } else {
                    _log.error("武器額外傷害資料錯誤: 這個編號不是武器:" + key);
                    delete(key.intValue());
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void fillWeaponSkillTable(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int weapon_id = rs.getInt("weapon_id");
            if (ItemTable.get().getTemplate(weapon_id) == null) {
                _log.error("武器額外傷害資料錯誤: 沒有這個編號的道具:" + weapon_id);
                delete(weapon_id);
            } else {
                int add_dmg_min = rs.getInt("add_dmg_min");
                int add_dmg_max = rs.getInt("add_dmg_max");
                if (add_dmg_min >= add_dmg_max) {
                    _log.error("武器額外傷害資料錯誤: 傷害質設置異常:" + weapon_id);
                    delete(weapon_id);
                } else {
                    _weaponPower.put(Integer.valueOf(weapon_id), new int[]{add_dmg_min, add_dmg_max});
                }
            }
        }
    }

    public int[] getTemplate(int weaponId) {
        return (int[]) _weaponPower.get(Integer.valueOf(weaponId));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.WeaponPowerTable JD-Core Version: 0.6.2
 */