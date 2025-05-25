package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1CharItemPower;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物品凹槽強化資料
 * 強化擴充能力
 */
public class CharItemPowerTable {
    private static final Log _log = LogFactory.getLog(CharItemPowerTable.class);
    // <ITEMOBJID, POWERSET>
    private static final Map<Integer, L1CharItemPower> _itemUpdate = new ConcurrentHashMap<Integer, L1CharItemPower>();
    private static CharItemPowerTable _instance;

    public static CharItemPowerTable get() {
        if (_instance == null) {
            _instance = new CharItemPowerTable();
        }
        return _instance;
    }

    /**
     * 初始化建立資料
     *
     */
    private static void addValue(final int item_obj_id, final L1CharItemPower power) {
        final L1ItemInstance item = WorldItem.get().getItem(item_obj_id);
        boolean isError = true;
        if (item != null) {
            item.setUpdateDmgModifier(power.getUpdateDmgModifier());
            item.setUpdateHitModifier(power.getUpdateHitModifier());
            item.setUpdateBowDmgModifier(power.getUpdateBowDmgModifier());
            item.setUpdateBowHitModifier(power.getUpdateBowHitModifier());
            item.setUpdateStr(power.getUpdateStr());
            item.setUpdateDex(power.getUpdateDex());
            item.setUpdateCon(power.getUpdateCon());
            item.setUpdateWis(power.getUpdateWis());
            item.setUpdateInt(power.getUpdateInt());
            item.setUpdateCha(power.getUpdateCha());
            item.setUpdateHp(power.getUpdateHp());
            item.setUpdateMp(power.getUpdateMp());
            item.setUpdateEarth(power.getUpdateEarth());
            item.setUpdateWind(power.getUpdateWind());
            item.setUpdateWater(power.getUpdateWater());
            item.setUpdateFire(power.getUpdateFire());
            item.setUpdateMr(power.getUpdateMr());
            item.setUpdateAc(power.getUpdateAc());
            item.setUpdateHpr(power.getUpdateHpr());
            item.setUpdateMpr(power.getUpdateMpr());
            item.setUpdateSp(power.getUpdateSp());
            item.setUpdatePVPdmg(power.getUpdatePvpDmg());
            item.setUpdatePVPdmg_R(power.getUpdatePvpDmg_R());
            item.setUpdateWeaponSoul(power.getUpdateWeaponSoul()); // 武器劍靈系統
            // 加入map資料
            _itemUpdate.put(power.getId(), power);
            isError = false;
        }
        // 刪除遺失資料
        if (isError) {
            deleteItem(item_obj_id);
        }
    }

    /**
     * 刪除遺失資料
     *
     */
    private static void deleteItem(final Integer objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM character_item_expand WHERE item_id = ?");
            ps.setInt(1, objid);
            ps.execute();
            System.out.println(">>>>>>>>>>>>>>>>>>刪除強化擴充能力遺失道具OBJ:" + objid);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 預先加載
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_item_expand ORDER BY item_id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                // 每一個次均不同
                final L1CharItemPower itemUpdate = new L1CharItemPower();
                final int item_objid = rs.getInt("item_id");
                itemUpdate.setId(item_objid);
                itemUpdate.setUpdateDmgModifier(rs.getInt("updatedmgmodifier"));
                itemUpdate.setUpdateHitModifier(rs.getInt("updatehitmodifier"));
                itemUpdate.setUpdateBowDmgModifier(rs.getInt("updatebowdmgmodifier"));
                itemUpdate.setUpdateBowHitModifier(rs.getInt("updatebowhitmodifier"));
                itemUpdate.setUpdateStr(rs.getInt("updatestr"));
                itemUpdate.setUpdateDex(rs.getInt("updatedex"));
                itemUpdate.setUpdateCon(rs.getInt("updatecon"));
                itemUpdate.setUpdateWis(rs.getInt("updatewis"));
                itemUpdate.setUpdateInt(rs.getInt("updateint"));
                itemUpdate.setUpdateCha(rs.getInt("updatecha"));
                itemUpdate.setUpdateHp(rs.getInt("updatehp"));
                itemUpdate.setUpdateMp(rs.getInt("updatemp"));
                itemUpdate.setUpdateEarth(rs.getInt("updateearth"));
                itemUpdate.setUpdateWind(rs.getInt("updatewind"));
                itemUpdate.setUpdateWater(rs.getInt("updatewater"));
                itemUpdate.setUpdateFire(rs.getInt("updatefire"));
                itemUpdate.setUpdateMr(rs.getInt("updatemr"));
                itemUpdate.setUpdateAc(rs.getInt("updateac"));
                itemUpdate.setUpdateHpr(rs.getInt("updatehpr"));
                itemUpdate.setUpdateMpr(rs.getInt("updatempr"));
                itemUpdate.setUpdateSp(rs.getInt("updatesp"));
                itemUpdate.setUpdatePvpDmg(rs.getInt("updatepvpdmg"));
                itemUpdate.setUpdatePvpDmg_R(rs.getInt("updatepvpdmg_r"));
                itemUpdate.setUpdateWeaponSoul(rs.getInt("updateweaponsoul")); // 武器劍靈系統
                // 建立資料
                addValue(item_objid, itemUpdate);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        _log.info("讀取->凹槽強化道具數量: " + _itemUpdate.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 取回凹槽強化紀錄
     *
     */
    public L1CharItemPower getPower(final L1ItemInstance item) {
        final L1CharItemPower powerTmp = _itemUpdate.get(item.getId());
        return powerTmp;
    }

    /**
     * 刪除遺失資料 道具升級用
     *
     */
    public void deleteItemUpdate(final Integer objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM character_item_expand WHERE item_id = ?");
            ps.setInt(1, objid);
            ps.execute();
            //System.out.println(">>>>>>>>>>>>>>>>>>刪除強化擴充能力遺失道具OBJ:" + objid);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 增加資料
     *
     */
    public void storeItem(final L1ItemInstance item) {
        if (_itemUpdate.get(item.getId()) != null) {
            return;
        }
        final L1CharItemPower power = new L1CharItemPower();
        power.setId(item.getId());
        power.setUpdateDmgModifier(item.getUpdateDmgModifier());
        power.setUpdateHitModifier(item.getUpdateHitModifier());
        power.setUpdateBowDmgModifier(item.getUpdateBowDmgModifier());
        power.setUpdateBowHitModifier(item.getUpdateBowHitModifier());
        power.setUpdateStr(item.getUpdateStr());
        power.setUpdateDex(item.getUpdateDex());
        power.setUpdateCon(item.getUpdateCon());
        power.setUpdateWis(item.getUpdateWis());
        power.setUpdateInt(item.getUpdateInt());
        power.setUpdateCha(item.getUpdateCha());
        power.setUpdateHp(item.getUpdateHp());
        power.setUpdateMp(item.getUpdateMp());
        power.setUpdateEarth(item.getUpdateEarth());
        power.setUpdateWind(item.getUpdateWind());
        power.setUpdateWater(item.getUpdateWater());
        power.setUpdateFire(item.getUpdateFire());
        power.setUpdateMr(item.getUpdateMr());
        power.setUpdateAc(item.getUpdateAc());
        power.setUpdateHpr(item.getUpdateHpr());
        power.setUpdateMpr(item.getUpdateMpr());
        power.setUpdateSp(item.getUpdateSp());
        power.setUpdatePvpDmg(item.getUpdatePVPdmg());
        power.setUpdatePvpDmg_R(item.getUpdatePVPdmg_R());
        power.setUpdateWeaponSoul(item.getUpdateWeaponSoul()); // 武器劍靈系統
        _itemUpdate.put(item.getId(), power);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_item_expand SET item_id=?, updatedmgmodifier=?, updatehitmodifier=?, updatebowdmgmodifier=?, updatebowhitmodifier=?, updatestr=?, updatedex=?, updatecon=?, updatewis=?, updateint=?, updatecha=?, updatehp=?, updatemp=?, updateearth=?, updatewind=?, updatewater=?, updatefire=?,updatemr=?,updateac=?,updatehpr=?,updatempr=?,updatesp=?,updatepvpdmg=?,updatepvpdmg_r=?,updateweaponsoul=?");
            int i = 0;
            pstm.setInt(++i, item.getId());
            pstm.setInt(++i, power.getUpdateDmgModifier());
            pstm.setInt(++i, power.getUpdateHitModifier());
            pstm.setInt(++i, power.getUpdateBowDmgModifier());
            pstm.setInt(++i, power.getUpdateBowHitModifier());
            pstm.setInt(++i, power.getUpdateStr());
            pstm.setInt(++i, power.getUpdateDex());
            pstm.setInt(++i, power.getUpdateCon());
            pstm.setInt(++i, power.getUpdateWis());
            pstm.setInt(++i, power.getUpdateInt());
            pstm.setInt(++i, power.getUpdateCha());
            pstm.setInt(++i, power.getUpdateHp());
            pstm.setInt(++i, power.getUpdateMp());
            pstm.setInt(++i, power.getUpdateEarth());
            pstm.setInt(++i, power.getUpdateWind());
            pstm.setInt(++i, power.getUpdateWater());
            pstm.setInt(++i, power.getUpdateFire());
            pstm.setInt(++i, power.getUpdateMr());
            pstm.setInt(++i, power.getUpdateAc());
            pstm.setInt(++i, power.getUpdateHpr());
            pstm.setInt(++i, power.getUpdateMpr());
            pstm.setInt(++i, power.getUpdateSp());
            pstm.setInt(++i, power.getUpdatePvpDmg());
            pstm.setInt(++i, power.getUpdatePvpDmg_R());
            pstm.setInt(++i, power.getUpdateWeaponSoul()); // 武器劍靈系統
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(con);
            SQLUtil.close(pstm);
        }
    }

    /**
     * 更新資料
     *
     */
    public void updateItem(final L1ItemInstance item) {
        final L1CharItemPower power = _itemUpdate.get(item.getId());
        // 更新強化TMP
        if (power != null) {
            power.setUpdateDmgModifier(item.getUpdateDmgModifier());
            power.setUpdateHitModifier(item.getUpdateHitModifier());
            power.setUpdateBowDmgModifier(item.getUpdateBowDmgModifier());
            power.setUpdateBowHitModifier(item.getUpdateBowHitModifier());
            power.setUpdateStr(item.getUpdateStr());
            power.setUpdateDex(item.getUpdateDex());
            power.setUpdateCon(item.getUpdateCon());
            power.setUpdateWis(item.getUpdateWis());
            power.setUpdateInt(item.getUpdateInt());
            power.setUpdateCha(item.getUpdateCha());
            power.setUpdateHp(item.getUpdateHp());
            power.setUpdateMp(item.getUpdateMp());
            power.setUpdateEarth(item.getUpdateEarth());
            power.setUpdateWind(item.getUpdateWind());
            power.setUpdateWater(item.getUpdateWater());
            power.setUpdateFire(item.getUpdateFire());
            power.setUpdateMr(item.getUpdateMr());
            power.setUpdateAc(item.getUpdateAc());
            power.setUpdateHpr(item.getUpdateHpr());
            power.setUpdateMpr(item.getUpdateMpr());
            power.setUpdateSp(item.getUpdateSp());
            power.setUpdatePvpDmg(item.getUpdatePVPdmg());
            power.setUpdatePvpDmg_R(item.getUpdatePVPdmg_R());
            power.setUpdateWeaponSoul(item.getUpdateWeaponSoul()); // 武器劍靈系統
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE character_item_expand SET updatedmgmodifier=?, updatehitmodifier=?, updatebowdmgmodifier=?, updatebowhitmodifier=?, updatestr=?, updatedex=?, updatecon=?, updatewis=?, updateint=?, updatecha=?, updatehp=?, updatemp=?, updateearth=?, updatewind=?, updatewater=?, updatefire=?,updatemr=?,updateac=?,updatehpr=?,updatempr=?,updatesp=?,updatepvpdmg=?,updatepvpdmg_r=?,updateweaponsoul=? WHERE item_id=?");
            int i = 0;
            pstm.setInt(++i, power.getUpdateDmgModifier());
            pstm.setInt(++i, power.getUpdateHitModifier());
            pstm.setInt(++i, power.getUpdateBowDmgModifier());
            pstm.setInt(++i, power.getUpdateBowHitModifier());
            pstm.setInt(++i, power.getUpdateStr());
            pstm.setInt(++i, power.getUpdateDex());
            pstm.setInt(++i, power.getUpdateCon());
            pstm.setInt(++i, power.getUpdateWis());
            pstm.setInt(++i, power.getUpdateInt());
            pstm.setInt(++i, power.getUpdateCha());
            pstm.setInt(++i, power.getUpdateHp());
            pstm.setInt(++i, power.getUpdateMp());
            pstm.setInt(++i, power.getUpdateEarth());
            pstm.setInt(++i, power.getUpdateWind());
            pstm.setInt(++i, power.getUpdateWater());
            pstm.setInt(++i, power.getUpdateFire());
            pstm.setInt(++i, power.getUpdateMr());
            pstm.setInt(++i, power.getUpdateAc());
            pstm.setInt(++i, power.getUpdateHpr());
            pstm.setInt(++i, power.getUpdateMpr());
            pstm.setInt(++i, power.getUpdateSp());
            pstm.setInt(++i, power.getUpdatePvpDmg());
            pstm.setInt(++i, power.getUpdatePvpDmg_R());
            pstm.setInt(++i, power.getUpdateWeaponSoul());
            pstm.setInt(++i, item.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(con);
            SQLUtil.close(pstm);
        }
    }
}
