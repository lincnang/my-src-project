package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1WeaponSkill;
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

public class WeaponSkillTable {
    private static final Log _log = LogFactory.getLog(WeaponSkillTable.class);
    private static final Map<Integer, L1WeaponSkill> _weaponIdIndex = new HashMap<>();
    private static WeaponSkillTable _instance;

    public static WeaponSkillTable get() {
        if (_instance == null) {
            _instance = new WeaponSkillTable();
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
            pstm = con.prepareStatement("SELECT * FROM `weapon_skill`");
            rs = pstm.executeQuery();
            fillWeaponSkillTable(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->技能武器資料數量: " + _weaponIdIndex.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 填充武器技能表的方法。
     * 此方法從資料庫的 ResultSet 中讀取武器技能相關資料，
     * 並將其轉換為 L1WeaponSkill 物件，然後存入 _weaponIdIndex 映射中。
     *
     * @param rs 從資料庫查詢得到的結果集
     * @throws SQLException 如果在讀取資料時發生 SQL 錯誤
     */
    private void fillWeaponSkillTable(ResultSet rs) throws SQLException {
        // 遍歷 ResultSet 中的每一行資料
        while (rs.next()) {
            // 取得武器的唯一識別碼
            int weaponId = rs.getInt("weapon_id");

            // 取得技能名稱，註解說明這是魔法武器發動的技能名稱
            final String skillName = rs.getString("skill_name"); // 魔法武器發動的技能名稱

            // 取得技能的觸發概率
            int probability = rs.getInt("probability");

            // 取得固定傷害值
            int fixDamage = rs.getInt("fix_damage");

            // 取得隨機傷害值範圍
            int randomDamage = rs.getInt("random_damage");

            // 取得技能影響的範圍
            int area = rs.getInt("area");

            // 取得技能的唯一識別碼
            int skillId = rs.getInt("skill_id");

            // 取得技能持續時間
            int skillTime = rs.getInt("skill_time");

            // 取得效果的唯一識別碼
            int effectId = rs.getInt("effect_id");

            // 取得效果的目標類型
            int effectTarget = rs.getInt("effect_target");

            // 判斷是否為箭矢類型
            boolean isArrowType = rs.getBoolean("arrow_type");

            // 取得屬性值
            int attr = rs.getInt("attr");

            // 取得第一個效果的唯一識別碼及相關目標和坐標
            int effectId1 = rs.getInt("effect_id1");
            int effectTarget1 = rs.getInt("effect_target1");
            int effect_xy1 = rs.getInt("effect_xy1");

            // 取得第二個效果的唯一識別碼及相關目標和坐標
            int effectId2 = rs.getInt("effect_id2");
            int effectTarget2 = rs.getInt("effect_target2");
            int effect_xy2 = rs.getInt("effect_xy2");

            // 取得第三個效果的唯一識別碼及相關目標和坐標
            int effectId3 = rs.getInt("effect_id3");
            int effectTarget3 = rs.getInt("effect_target3");
            int effect_xy3 = rs.getInt("effect_xy3");

            // 取得第四個效果的唯一識別碼及相關目標和坐標
            int effectId4 = rs.getInt("effect_id4");
            int effectTarget4 = rs.getInt("effect_target4");
            int effect_xy4 = rs.getInt("effect_xy4");

            // 創建一個新的 L1WeaponSkill 物件
            L1WeaponSkill weaponSkill = new L1WeaponSkill();

            // 設置武器ID
            weaponSkill.setWeaponId(weaponId);

            // 設置技能名稱，註解說明這是魔法武器發動的技能名稱
            weaponSkill.setSkillName(skillName); // 魔法武器發動的技能名稱

            // 設置技能觸發概率
            weaponSkill.setProbability(probability);

            // 設置固定傷害值
            weaponSkill.setFixDamage(fixDamage);

            // 設置隨機傷害值範圍
            weaponSkill.setRandomDamage(randomDamage);

            // 設置技能影響的範圍
            weaponSkill.setArea(area);

            // 設置技能ID
            weaponSkill.setSkillId(skillId);

            // 設置技能持續時間
            weaponSkill.setSkillTime(skillTime);

            // 設置效果ID
            weaponSkill.setEffectId(effectId);

            // 設置效果的目標類型
            weaponSkill.setEffectTarget(effectTarget);

            // 設置是否為箭矢類型
            weaponSkill.setArrowType(isArrowType);

            // 設置屬性值
            weaponSkill.setAttr(attr);

            // 設置第一個效果的ID
            weaponSkill.setEffectId1(effectId1);

            // 設置第二個效果的ID
            weaponSkill.setEffectId2(effectId2);

            // 設置第三個效果的ID
            weaponSkill.setEffectId3(effectId3);

            // 設置第四個效果的ID
            weaponSkill.setEffectId4(effectId4);

            // 設置第一個效果的目標類型
            weaponSkill.setEffectTarget1(effectTarget1);

            // 設置第二個效果的目標類型
            weaponSkill.setEffectTarget2(effectTarget2);

            // 設置第三個效果的目標類型
            weaponSkill.setEffectTarget3(effectTarget3);

            // 設置第四個效果的目標類型
            weaponSkill.setEffectTarget4(effectTarget4);

            // 設置第一個效果的坐標
            weaponSkill.setEffectXY1(effect_xy1);

            // 設置第二個效果的坐標
            weaponSkill.setEffectXY2(effect_xy2);

            // 設置第三個效果的坐標
            weaponSkill.setEffectXY3(effect_xy3);

            // 設置第四個效果的坐標
            weaponSkill.setEffectXY4(effect_xy4);

            // 將武器技能物件存入 _weaponIdIndex 映射中，以 weaponId 為鍵，weaponSkill 為值
            _weaponIdIndex.put(weaponId, weaponSkill);
        }
    }


    public L1WeaponSkill getTemplate(int weaponId) {
        return (L1WeaponSkill) _weaponIdIndex.get(weaponId);
    }
}
