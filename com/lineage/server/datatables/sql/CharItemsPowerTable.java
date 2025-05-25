package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ExtraBossWeaponTable;
import com.lineage.server.datatables.ExtraCriticalHitStoneTable;
import com.lineage.server.datatables.ExtraMagicWeaponTable;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.storage.CharItemsPowerStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1BossWeapon;
import com.lineage.server.templates.L1CriticalHitStone;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 人物古文字物品資料
 *
 * @author dexc
 */
public class CharItemsPowerTable implements CharItemsPowerStorage {
    private static final Log _log = LogFactory.getLog(CharItemsPowerTable.class);
    private static final CopyOnWriteArrayList<Integer> _objList = new CopyOnWriteArrayList<>();

    /**
     * 初始化建立資料
     *
     */
    private static void addValue(final int item_obj_id, final L1ItemPower_name power) {
        L1ItemInstance item = WorldItem.get().getItem(item_obj_id);
        boolean isError = true;
        if (item != null) {
            if (item.get_power_name() == null) {
                _objList.add(item_obj_id);
                item.set_power_name(power);
            }
            isError = false;
        }
        if (isError) {
            errorItem(item_obj_id);
        }
    }

    /**
     * 刪除 錯誤/遺失 物品資料
     *
     */
    private static void errorItem(int item_obj_id) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `character_item_power` WHERE `item_obj_id`=?");
            pstm.setInt(1, item_obj_id);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 資料預先載入
     */
    @Override
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_item_power`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int item_obj_id = rs.getInt("item_obj_id");
                final int item_power = rs.getInt("item_power");
                // 魔法武器DIY系統(附魔石類型) by terry0412
                final int magic_weapon = rs.getInt("magic_weapon");
                final Timestamp date_time = rs.getTimestamp("date_time");
                final int boss_weapon = rs.getInt("boss_weapon");
                final int boss_lv = rs.getInt("boss_lv");
                final Timestamp boss_date_time = rs.getTimestamp("boss_date_time");
                // 魔法武器DIY系統(使用期限) by terry0412
                L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(item_power);
                if (power == null) {
                    power = new L1ItemPower_name();
                }
                // 魔法武器DIY系統(附魔石類型) by terry0412
                if (magic_weapon > 0) {
                    final L1MagicWeapon magicWeapon = ExtraMagicWeaponTable.getInstance().get(magic_weapon);
                    power.set_magic_weapon(magicWeapon);
                }
                power.set_date_time(date_time);
                power.set_boss_lv(boss_lv);
                if (boss_weapon > 0) {
                    L1BossWeapon bossWeapon = ExtraBossWeaponTable.getInstance().get(boss_weapon, power.get_boss_lv());
                    power.set_boss_weapon(bossWeapon);
                }
                // 魔法武器DIY系統(使用期限) by terry0412
                power.set_boss_date_time(boss_date_time);
                final int _super_rune_1 = rs.getInt("super_rune_1");
                final int _super_rune_2 = rs.getInt("super_rune_2");
                final int _super_rune_3 = rs.getInt("super_rune_3");
                final int _super_rune_4 = rs.getInt("super_rune_4");
                power.set_super_rune_1(_super_rune_1);
                power.set_super_rune_2(_super_rune_2);
                power.set_super_rune_3(_super_rune_3);
                power.set_super_rune_4(_super_rune_4);
                final int critical_hit_stone = rs.getInt("critical_hit_stone");
                if (critical_hit_stone > 0) {
                    final L1CriticalHitStone criticalHitStone = ExtraCriticalHitStoneTable.getInstance().get(critical_hit_stone);
                    power.set_critical_hit_stone(criticalHitStone);
                }
                addValue(item_obj_id, power);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->人物古文字物件清單資料數量: " + i + "(" + timer.get() + "ms)");
    }

    /**
     * 增加古文字物品資料
     *
     */
    @Override
    public void storeItem(final int item_obj_id, final L1ItemPower_name power) throws Exception {
        if (_objList.contains(item_obj_id)) {
            return;
        }
        _objList.add(item_obj_id);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `character_item_power` SET " + "`item_obj_id`=?,`item_power`=?,`note`=?,`magic_weapon`=?,`date_time`=?," + "`boss_weapon`=?,`boss_lv`=?,`boss_date_time`=?," + "`super_rune_1`=?,`super_rune_2`=?,`super_rune_3`=?,`super_rune_4`=?,`critical_hit_stone`=?");
            int i = 0;
            pstm.setInt(++i, item_obj_id);
            pstm.setInt(++i, power.get_power_id());
            pstm.setString(++i, power.get_power_name());
            // 魔法武器DIY系統(附魔石類型) by terry0412
            pstm.setInt(++i, power.get_magic_weapon() != null ? power.get_magic_weapon().getItemId() : 0);
            pstm.setTimestamp(++i, power.get_date_time());
            pstm.setInt(++i, power.get_boss_weapon() != null ? power.get_boss_weapon().getItemId() : 0);
            pstm.setInt(++i, power.get_boss_lv());
            pstm.setTimestamp(++i, power.get_boss_date_time());
            //超能
            pstm.setInt(++i, power.get_super_rune_1());
            pstm.setInt(++i, power.get_super_rune_2());
            pstm.setInt(++i, power.get_super_rune_3());
            pstm.setInt(++i, power.get_super_rune_4());
            //爆擊
            pstm.setInt(++i, power.get_critical_hit_stone() != null ? power.get_critical_hit_stone().getItemId() : 0);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public void delItem(int item_obj_id) {
        if (_objList.contains(item_obj_id)) {
            _objList.remove(new Integer(item_obj_id));
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `character_item_power` WHERE `item_obj_id`=?");
            pstm.setInt(1, item_obj_id);
            pstm.execute();
        } catch (final java.lang.ArrayIndexOutOfBoundsException e) {
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 更新凹槽資料
     *
     */
    @Override
    public void updateItem(final int item_obj_id, final L1ItemPower_name power) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `character_item_power` SET " + "`magic_weapon`=?,`date_time`=?,`boss_weapon`=?,`boss_lv`=?,`boss_date_time`=?," + "`super_rune_1`=?,`super_rune_2`=?,`super_rune_3`=?,`super_rune_4`=?,`critical_hit_stone`=? WHERE `item_obj_id`=?");
            int i = 0;
            // 魔法武器DIY系統(附魔石類型) by terry0412
            pm.setInt(++i, power.get_magic_weapon() != null ? power.get_magic_weapon().getItemId() : 0);
            // 魔法武器DIY系統(使用期限) by terry0412
            pm.setTimestamp(++i, power.get_date_time());
            pm.setInt(++i, power.get_boss_weapon() != null ? power.get_boss_weapon().getItemId() : 0);
            pm.setInt(++i, power.get_boss_lv());
            pm.setTimestamp(++i, power.get_boss_date_time());
            //超能
            pm.setInt(++i, power.get_super_rune_1());
            pm.setInt(++i, power.get_super_rune_2());
            pm.setInt(++i, power.get_super_rune_3());
            pm.setInt(++i, power.get_super_rune_4());
            //爆擊
            pm.setInt(++i, power.get_critical_hit_stone() != null ? power.get_critical_hit_stone().getItemId() : 0);
            pm.setInt(++i, item_obj_id);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }
}
