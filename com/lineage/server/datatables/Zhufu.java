/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Zhufu;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static com.lineage.server.datatables.ItemTable.ArmorTypes;
import static com.lineage.server.datatables.ItemTable.WeaponTypes;

/**
 * 祝福化系統
 * By 台灣JAVA技術老爹
 *
 * @author Administrator
 */
public class Zhufu {
    private static final Log _log = LogFactory.getLog(Zhufu.class);

    private static Zhufu _instance;

    private final HashMap<Integer, L1Zhufu> _itemidIndex
            = new HashMap<>();

    private Zhufu() {
        loadItemIdOrginal();
    }

    public static Zhufu getInstance() {
        if (_instance == null) {
            _instance = new Zhufu();
        }
        return _instance;
    }

    private void loadItemIdOrginal() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {

            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_祝福化能力設定");
            rs = pstm.executeQuery();
            fillbessSkill(rs);
        } catch (SQLException e) {

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->系統_祝福化能力設定: " + "(" + timer.get() + "ms)");
    }

    private void fillbessSkill(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int id = rs.getInt("流水編號");
            int kind = rs.getInt("種類");
            int itemId = rs.getInt("itemid");    //道具ID
            int type = GetItemType(itemId, rs.getString("Type"));    //類型
            boolean zhufu = rs.getBoolean("是否會爆");
            boolean zhufu2 = rs.getBoolean("是否可點");
            byte addStr = rs.getByte("addStr");
            byte addDex = rs.getByte("addDex");
            byte addCon = rs.getByte("addCon");
            byte addInt = rs.getByte("addInt");
            byte addWis = rs.getByte("addWis");
            byte addCha = rs.getByte("addCha");
            int addAc = rs.getInt("addAc");
            int addMaxHp = rs.getInt("增加血量");
            int addMaxMp = rs.getInt("增加魔量");
            int addHpr = rs.getInt("增加回血量");
            int addMpr = rs.getInt("增加回魔量");
            int addDmg = rs.getInt("增加近距離攻擊");
            int addBowDmg = rs.getInt("增加遠距離攻擊");
            int addHit = rs.getInt("增加近距離命中");
            int addBowHit = rs.getInt("增加遠距離命中");
            int reduction_dmg = rs.getInt("增加減免傷害");
            int reduction_magic_dmg = rs.getInt("增加減免魔法傷害");
            int addMr = rs.getInt("增加魔防");
            int addSp = rs.getInt("增加魔攻");
            int addFire = rs.getInt("addFire");
            int addWind = rs.getInt("addWind");
            int addEarth = rs.getInt("addEarth");
            int addWater = rs.getInt("addWater");
            int add_pvpjianmian = rs.getInt("PVP物理傷害減免");//PVP物理傷害減免
            int add_pvpjianmianbai = rs.getInt("PVP物理傷害減免%");//PVP物理傷害減免百分比
            int add_pvpmojianmian = rs.getInt("PVP魔法傷害減免");//PVP魔法傷害減免
            int add_pvpmojianmianbai = rs.getInt("PVP魔法傷害減免%");//PVP魔法傷害減免百分比

            L1Zhufu itemidOrginal = new L1Zhufu(id, kind, itemId, type, zhufu, zhufu2, addStr, addDex, addCon, addInt, addWis, addCha,
                    addAc, addMaxHp, addMaxMp, addHpr, addMpr, addDmg, addBowDmg, addHit, addBowHit, reduction_dmg, reduction_magic_dmg,
                    addMr, addSp, addFire, addWind, addEarth, addWater, addWater, add_pvpjianmian,
                    add_pvpjianmianbai, add_pvpmojianmian, add_pvpmojianmianbai);
            _itemidIndex.put(id, itemidOrginal);
        }
    }

    private int GetItemType(int itemid, String type) {
        L1Item item = ItemTable.get().getTemplate(itemid);
        if (item == null) {
            if (WeaponTypes().get(type) != null) {
                return WeaponTypes().get(type);
            }

            if (ArmorTypes().get(type) != null) {
                return ArmorTypes().get(type);
            }

            return 0;
        }

        if (item.getType2() == 1) {            // 武器
            if (WeaponTypes().get(type) == null)
                return 0;

            return WeaponTypes().get(type);
        } else if (item.getType2() == 2) {    // 防具,飾品
            if (ArmorTypes().get(type) == null)
                return 0;

            return ArmorTypes().get(type);
        }

        return 0;
    }

    public L1Zhufu getTemplate(int itemid, int kind) {
        for (L1Zhufu item : _itemidIndex.values()) {
            if (item.getItemid() == itemid) {
                return item;
            }
        }

        L1Item item = ItemTable.get().getTemplate(itemid);
        if (item == null) {
            return null;
        }

        for (L1Zhufu item2 : _itemidIndex.values()) {
            if (item2.getType() == item.getType() && item2.getItemid() == 0 && item2.getKind() == kind) {
                return item2;
            }
        }

        return null;
    }

    public L1Zhufu getTemplateByType(int type, int kind) {
        for (L1Zhufu item : _itemidIndex.values()) {
            if (item.getType() == type && item.getItemid() == 0 && item.getKind() == kind) {
                return item;
            }
        }
        return null;
    }

    public L1Zhufu[] getItemIdList() {
        return _itemidIndex.values().toArray(new L1Zhufu[0]);
    }
}
