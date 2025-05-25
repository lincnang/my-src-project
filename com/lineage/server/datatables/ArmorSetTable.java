package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.templates.L1ArmorSets;
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
import java.util.StringTokenizer;

public class ArmorSetTable {
    private static final Log _log = LogFactory.getLog(ArmorSetTable.class);
    private static final ArrayList<L1ArmorSets> _armorSetList = new ArrayList<>();
    private static ArmorSetTable _instance;
    private static HashMap<Integer, Integer> _armor_id_list = new HashMap<>();

    public static ArmorSetTable get() {
        if (_instance == null) {
            _instance = new ArmorSetTable();
        }
        return _instance;
    }

    private static int[] getArray(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        String sTemp = null;
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    private static int[] getArray(String s, String sToken) {
        StringTokenizer st = new StringTokenizer(s, sToken);
        int size = st.countTokens();
        String temp = null;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            temp = st.nextToken();
            array[i] = Integer.parseInt(temp);
        }
        return array;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `armor_set`");
            rs = pstm.executeQuery();
            fillTable(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->套裝設置數量: " + _armorSetList.size() + "(" + timer.get() + "ms)");
        ArmorSet.load();
    }

    private void fillTable(ResultSet rs) throws SQLException {
        while (rs.next()) {
            L1ArmorSets as = new L1ArmorSets();
            as.setId(rs.getInt("id"));
            String sets = rs.getString("sets");
            as.setSets(rs.getString("sets"));
            for (int key : getArray(sets)) {
                _armor_id_list.put(key, rs.getInt("id"));
            }
            as.setPolyId(rs.getInt("polyid"));
            as.setPolyDesc(rs.getInt("poly_desc")); // 變身名字編號
            as.setAc(rs.getInt("ac"));
            as.setHp(rs.getInt("hp"));
            as.setMp(rs.getInt("mp"));
            as.setHpr(rs.getInt("hpr"));
            as.setMpr(rs.getInt("mpr"));
            as.setMr(rs.getInt("mr"));
            as.setStr(rs.getInt("str"));
            as.setDex(rs.getInt("dex"));
            as.setCon(rs.getInt("con"));
            as.setWis(rs.getInt("wis"));
            as.setCha(rs.getInt("cha"));
            as.setIntl(rs.getInt("intl"));
            as.setDefenseWater(rs.getInt("defense_water"));
            as.setDefenseWind(rs.getInt("defense_wind"));
            as.setDefenseFire(rs.getInt("defense_fire"));
            as.setDefenseEarth(rs.getInt("defense_earth"));
            as.set_regist_stun(rs.getInt("regist_stun"));
            as.set_regist_stone(rs.getInt("regist_stone"));
            as.set_regist_sleep(rs.getInt("regist_sleep"));
            as.set_regist_freeze(rs.getInt("regist_freeze"));
            as.set_regist_sustain(rs.getInt("regist_sustain"));
            as.set_regist_blind(rs.getInt("regist_blind"));
            as.set_modifier_dmg(rs.getInt("modifier_dmg"));
            as.set_reduction_dmg(rs.getInt("reduction_dmg"));
            as.set_magic_modifier_dmg(rs.getInt("magic_modifier_dmg"));
            as.set_magic_reduction_dmg(rs.getInt("magic_reduction_dmg"));
            as.set_bow_modifier_dmg(rs.getInt("bow_modifier_dmg"));
            as.set_haste(rs.getInt("haste"));
            as.set_sp(rs.getInt("sp"));
            as.set_hit_modifier(rs.getInt("hit_modifier"));
            as.set_bow_hit_modifier(rs.getInt("bow_hit_modifier"));
            as.set_magiccritical_chance(rs.getInt("magiccritical_chance"));
            String gfx = rs.getString("gfx");
            if ((gfx != null) && (!gfx.equals(""))) {
                String[] gfxs = gfx.replaceAll(" ", "").split(",");
                int[] out = new int[gfxs.length];
                for (int i = 0; i < gfxs.length; i++) {
                    out[i] = Integer.parseInt(gfxs[i]);
                }
                as.set_gfxs(out);
            }
            as.setEffectId(rs.getInt("effect_id"));
            as.setInterval(rs.getInt("interval"));
            as.setQuality1(rs.getString("quality1"));//src008
            as.setQuality2(rs.getString("quality2"));
            as.setQuality3(rs.getString("quality3"));
            as.setQuality4(rs.getString("quality4"));
            as.setQuality5(rs.getString("quality5"));
            as.setQuality6(rs.getString("quality6"));
            as.setQuality7(rs.getString("quality7"));
            as.setQuality8(rs.getString("quality8"));
            as.setQuality9(rs.getString("quality9"));
            _armorSetList.add(as);
        }
    }

    public L1ArmorSets[] getAllList() {
        return (L1ArmorSets[]) _armorSetList.toArray(new L1ArmorSets[0]);
    }

    public boolean checkArmorSet(int item_id) {
        return _armor_id_list.containsKey(item_id);
    }

    public String getQuality1(int item_id) {//src008
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality1();
                }
            }
        }
        return null;
    }

    public String getQuality2(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality2();
                }
            }
        }
        return null;
    }

    public String getQuality3(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality3();
                }
            }
        }
        return null;
    }

    public String getQuality4(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality4();
                }
            }
        }
        return null;
    }

    public String getQuality5(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality5();
                }
            }
        }
        return null;
    }

    public String getQuality6(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality6();
                }
            }
        }
        return null;
    }

    public String getQuality7(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality7();
                }
            }
        }
        return null;
    }

    public String getQuality8(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality8();
                }
            }
        }
        return null;
    }

    public String getQuality9(int item_id) {
        for (L1ArmorSets armorSets : get().getAllList()) {
            int[] tgItemId = getArray(armorSets.getSets(), ",");
            for (int j : tgItemId) {
                if (j == item_id) {
                    return armorSets.getQuality9();
                }
            }
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.ArmorSetTable JD-Core Version: 0.6.2
 */