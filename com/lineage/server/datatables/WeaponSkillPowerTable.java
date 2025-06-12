package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.weaponskill.L1WeaponSkillType;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeaponSkillPowerTable {
    private static final Log _log = LogFactory.getLog(WeaponSkillPowerTable.class);
    private static final Map<Integer, L1WeaponSkillType> _weaponIdIndex = new HashMap<>();
    private static final Map<Integer, ArrayList<L1WeaponSkillType>> _weaponSkill = new HashMap<>();
    private static WeaponSkillPowerTable _instance;

    public static WeaponSkillPowerTable get() {
        if (_instance == null) {
            _instance = new WeaponSkillPowerTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        load1();
        load2();
        _log.info("讀取->技能武器能力資料數量: " + _weaponIdIndex.size() + "(" + timer.get() + "ms)");
        _log.info("讀取->技能武器設置資料數量: " + _weaponSkill.size() + "(" + timer.get() + "ms)");
    }

    private void load1() {
        Connection cn = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            pm = cn.prepareStatement("SELECT * FROM `weapon_skill_power`");
            rs = pm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String classname = rs.getString("classname");
                int level = rs.getInt("level");
                int type1 = rs.getInt("type1");
                int type2 = rs.getInt("type2");
                int type3 = rs.getInt("type3");
                int attr = rs.getInt("attr");
                int ac_mr = rs.getInt("ac_mr");
                int gfxid1 = rs.getInt("gfxid1");
                int gfxid2 = rs.getInt("gfxid2");
                int gfxid3 = rs.getInt("gfxid3");
                int gfxid4 = rs.getInt("gfxid4");
                int gfxid4_count = rs.getInt("gfxid4_count");
                int power = rs.getInt("power");
                int srcdmg = rs.getInt("srcdmg");
                int addsrcdmg = rs.getInt("addsrcdmg");
                int random1 = rs.getInt("技能發動機率");
                int random2 = rs.getInt("random2");
                boolean boss_holdout = rs.getBoolean("boss_holdout");
                int gfxidIcon = rs.getInt("gfxid狀態圖編號");
                int gfxidString = rs.getInt("gfxid對應string");
                int gfxSec = rs.getInt("gfxid對應秒");
                L1WeaponSkillType class_name = get_class(id, classname);
                if (class_name != null) {
                    class_name.set_level(level);
                    class_name.set_type1(type1);
                    class_name.set_type2(type2);
                    class_name.set_type3(type3);
                    class_name.set_attr(attr);
                    class_name.set_ac_mr(ac_mr);
                    class_name.set_gfxid1(gfxid1);
                    class_name.set_gfxid2(gfxid2);
                    class_name.set_gfxid3(gfxid3);
                    class_name.set_gfxid4(gfxid4);
                    class_name.set_gfxid4_count(gfxid4_count);
                    class_name.set_power(power);
                    class_name.set_srcdmg(srcdmg);
                    class_name.set_addsrcdmg(addsrcdmg);
                    class_name.set_random1(random1);
                    class_name.set_random2(random2);
                    class_name.set_boss_holdout(boss_holdout);
                    class_name.set_gfxid_Icon(gfxidIcon);
                    class_name.set_gfxid_String(gfxidString);
                    class_name.set_gfx_Sec(gfxSec);
                }
                _weaponIdIndex.put(id, class_name);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(cn);
        }
    }

    private void load2() {
        Connection cn = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            pm = cn.prepareStatement("SELECT * FROM `weapon_skill_in`");
            rs = pm.executeQuery();
            while (rs.next()) {
                int item_id = rs.getInt("item_id");
                if (ItemTable.get().getTemplate(item_id) == null) {
                    _log.error("技能武器設置資料錯誤: 沒有這個編號的武器:" + item_id);
                } else {
                    String powers = rs.getString("powers").replaceAll(" ", "");
                    ArrayList<L1WeaponSkillType> list = (ArrayList<L1WeaponSkillType>) _weaponSkill.get(item_id);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    if (!powers.equals("")) {
                        String[] set = powers.split(",");
                        for (String s : set) {
                            int itemid = Integer.parseInt(s);
                            L1WeaponSkillType class_name = (L1WeaponSkillType) _weaponIdIndex.get(itemid);
                            list.add(class_name);
                        }
                    }
                    _weaponSkill.put(item_id, list);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(cn);
        }
    }

    private L1WeaponSkillType get_class(int id, String className) {
        if (className.equals("0")) {
            return null;
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.lineage.server.model.weaponskill.");
            stringBuilder.append(className);
            Class<?> cls = Class.forName(stringBuilder.toString());
            return (L1WeaponSkillType) cls.getMethod("get", new Class[0]).invoke(null, new Object[0]);
        } catch (ClassNotFoundException e) {
            String error = "發生[技能武器檔案]錯誤, 檢查檔案是否存在:" + className + " 技能武器編號:" + id;
            _log.error(error);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException |
                 IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public ArrayList<L1WeaponSkillType> getTemplate(int itemid) {
        return (ArrayList<L1WeaponSkillType>) _weaponSkill.get(itemid);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.WeaponSkillPowerTable JD-Core Version: 0.6.2
 */