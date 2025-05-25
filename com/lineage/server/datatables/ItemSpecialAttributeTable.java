package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemSpecialAttribute;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ItemSpecialAttributeTable {
    private static final Log _log = LogFactory.getLog(ItemSpecialAttributeTable.class);
    private static final HashMap<Integer, L1ItemSpecialAttribute> _atrrList = new HashMap<>();
    private static ItemSpecialAttributeTable _instance;

    public static ItemSpecialAttributeTable get() {
        if (_instance == null) {
            _instance = new ItemSpecialAttributeTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `Manly_炫色素質設定`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("流水號");
                String colour = rs.getString("顏色代號");
                String name = rs.getString("頭銜");
                int add_rand = rs.getInt("獲取的機率");
                int dmg_small = rs.getInt("最小攻擊力");
                int dmg_large = rs.getInt("最大攻擊力");
                int hitmodifier = rs.getInt("近戰命中");
                int dmgmodifier = rs.getInt("額外攻擊力");
                int add_str = rs.getInt("力量");
                int add_con = rs.getInt("體質");
                int add_dex = rs.getInt("敏捷");
                int add_int = rs.getInt("智力");
                int add_wis = rs.getInt("精神");
                int add_cha = rs.getInt("魅力");
                int add_hp = rs.getInt("血量");
                int add_mp = rs.getInt("魔力");
                int add_hpr = rs.getInt("回血量");
                int add_mpr = rs.getInt("回魔量");
                int add_sp = rs.getInt("魔攻");
                int add_m_def = rs.getInt("魔防");
                int drain_min_hp = rs.getInt("最小吸血量");
                int drain_max_hp = rs.getInt("最大吸血量");
                int drain_hp_rand = rs.getInt("發動吸血的機率");
                int drain_min_mp = rs.getInt("最小吸魔量");
                int drain_max_mp = rs.getInt("最大吸魔量");
                int drain_mp_rand = rs.getInt("吸魔機率");
                int skill_rand = rs.getInt("魔法施展機率");
                int skill_gfxid = rs.getInt("施展魔法gfxid");
                boolean skill_arrow = rs.getBoolean("遠程魔法");
                int skill_dmg = rs.getInt("魔法傷害");
                int Special_magic = rs.getInt("Special_magic");
                int Special_magic_rand = rs.getInt("Special_magic_rand");
                int add物理格檔 = rs.getInt("物理格檔");
                int add魔法格檔 = rs.getInt("魔法格檔");
                int addDamageReductionByArmor = rs.getInt("傷害減免");
                L1ItemSpecialAttribute attr = new L1ItemSpecialAttribute();
                attr.set_id(id);
                attr.set_colour(colour);
                attr.set_name(name);
                attr.set_dmg_small(dmg_small);
                attr.set_dmg_large(dmg_large);
                attr.set_hitmodifier(hitmodifier);
                attr.set_dmgmodifier(dmgmodifier);
                attr.set_add_str(add_str);
                attr.set_add_con(add_con);
                attr.set_add_dex(add_dex);
                attr.set_add_int(add_int);
                attr.set_add_wis(add_wis);
                attr.set_add_cha(add_cha);
                attr.set_add_hp(add_hp);
                attr.set_add_mp(add_mp);
                attr.set_add_hpr(add_hpr);
                attr.set_add_mpr(add_mpr);
                attr.set_add_sp(add_sp);
                attr.set_add_m_def(add_m_def);
                attr.set_add_rand(add_rand);
                attr.set_add_drain_min_hp(drain_min_hp);
                attr.set_add_drain_max_hp(drain_max_hp);
                attr.set_drain_hp_rand(drain_hp_rand);
                attr.set_add_drain_min_mp(drain_min_mp);
                attr.set_add_drain_max_mp(drain_max_mp);
                attr.set_drain_mp_rand(drain_mp_rand);
                attr.set_add_skill_rand(skill_rand);
                attr.set_add_skill_gfxid(skill_gfxid);
                attr.set_add_skill_arrow(skill_arrow);
                attr.set_add_skill_dmg(skill_dmg);
                attr.set_add_Special_magic(Special_magic);
                attr.set_Special_magic_rand(Special_magic_rand);
                attr.add物理格檔(add物理格檔);
                attr.add魔法格檔(add魔法格檔);
                attr.addShanghaijianmian(addDamageReductionByArmor);
                _atrrList.put(Integer.valueOf(id), attr);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->Manly_炫色素質設定數量: " + _atrrList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回指定編號特殊屬性
     *
     */
    public L1ItemSpecialAttribute getAttrId(final int id) {
        return _atrrList.get(id);
    }
}
