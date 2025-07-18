package com.lineage.data.item_weapon.proficiency;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 玩家武器熟練度資料記錄（待優化版本）<br>
 * 建議增加 ReentrantLock 同步處理<br>
 * 以最大限度避免讀取寫入的意外(檢測延遲)情況
 * @author 聖子默默
 */
public class CharacterWeaponProficiencyTable {
    private static final Log _log = LogFactory.getLog(CharacterWeaponProficiencyTable.class);
    private static final HashMap<Integer, HashMap<Integer, PlayerWeaponProficiency>> PROFICIENCY_MAP = new HashMap<>();
    private static CharacterWeaponProficiencyTable _instance;

    public static CharacterWeaponProficiencyTable getInstance() {
        if (_instance == null) {
            _instance = new CharacterWeaponProficiencyTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `character_武器經驗`");
            rs = pm.executeQuery();
            while (rs.next()) {
                final int char_id = rs.getInt("char_id");
                if (CharObjidTable.get().isChar(char_id) != null) {
                    final int type = rs.getInt("type");
                    final int level = rs.getInt("level");
                    final int exp = rs.getInt("exp");
                    PlayerWeaponProficiency proficiency = new PlayerWeaponProficiency();
                    proficiency.setCharId(char_id);
                    proficiency.setType(type);
                    proficiency.setLevel(level);
                    proficiency.setExp(exp);
                    HashMap<Integer, PlayerWeaponProficiency> _dateMap = PROFICIENCY_MAP.get(char_id);
                    if (_dateMap == null) {
                        _dateMap = new HashMap<>();
                        _dateMap.put(type, proficiency);
                        PROFICIENCY_MAP.put(char_id, _dateMap);
                    } else {
                        _dateMap.put(type, proficiency);
                    }
                } else {
                    deleteProficiency(char_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pm, co);
        }
        _log.info("載入->[人物武器熟練度設定]記錄資料數量: " + PROFICIENCY_MAP.size() + "(" + timer.get() + ")ms");
    }

    /**
     * 新增記錄（僅增加武器類型記錄 熟練度默認為0）
     *
     * @param char_id 玩家編號
     * @param type 武器類型
     * @param proficiency 熟練度設定
     */
    public void storeProficiency(final int char_id, final int type, final PlayerWeaponProficiency proficiency) {
        int realType = L1WeaponProficiency.normalizeWeaponType(type);
        proficiency.setType(realType);
        HashMap<Integer, PlayerWeaponProficiency> dateMap = PROFICIENCY_MAP.get(char_id);
        if (dateMap == null) {
            dateMap = Maps.newHashMap();
            PROFICIENCY_MAP.put(char_id, dateMap);
        }
        dateMap.put(realType, proficiency);

        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement(
                    "INSERT INTO `character_武器經驗` (`char_id`, `type`, `level`, `exp`) VALUES (?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE `level`=?, `exp`=?"
            );
            int i = 0;
            pm.setInt(++i, char_id);
            pm.setInt(++i, realType);
            pm.setInt(++i, proficiency.getLevel());
            pm.setInt(++i, proficiency.getExp());
            pm.setInt(++i, proficiency.getLevel());
            pm.setInt(++i, proficiency.getExp());
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }



    /**
     * 更新記錄
     *
     * @param char_id 玩家編號
     * @param type 武器類型
     * @param proficiency 熟練度設定
     */
    public void updateProficiency(final int char_id, final int type, final PlayerWeaponProficiency proficiency) {
        int realType = L1WeaponProficiency.normalizeWeaponType(type);
        HashMap<Integer, PlayerWeaponProficiency> dateMap = PROFICIENCY_MAP.get(char_id);
        if (dateMap == null) {
            dateMap = Maps.newHashMap();
            dateMap.put(realType, proficiency);
            PROFICIENCY_MAP.put(char_id, dateMap);
        } else {
            dateMap.put(realType, proficiency);
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `character_武器經驗` SET `level`=?,`exp`=? WHERE `char_id`=? AND `type`=?");
            int i = 0;
            pm.setInt(++i, proficiency.getLevel());
            pm.setInt(++i, proficiency.getExp());
            pm.setInt(++i, char_id);
            pm.setInt(++i, realType);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }


    private static void deleteProficiency(final int objectId) {
        PROFICIENCY_MAP.remove(objectId);
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE FROM `character_武器經驗` WHERE `char_id`=?");
            pm.setInt(1, objectId);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    public HashMap<Integer, PlayerWeaponProficiency> getProficiency(final int char_id) {
        return PROFICIENCY_MAP.get(char_id);
    }
}
