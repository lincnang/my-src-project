package com.lineage.data.item_weapon.proficiency;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家武器熟練度
 * @author 聖子默默
 */
public class WeaponProficiencyTable {
    private static final Log _log = LogFactory.getLog(WeaponProficiencyTable.class);
    private static final HashMap<Integer, Proficiency> _dataMap = new HashMap<>();
    private static final HashMap<Integer, Integer> _maxProficienciesLevel = new HashMap<>();
    private static WeaponProficiencyTable _instance;

    public static WeaponProficiencyTable getInstance() {
        if (_instance == null) {
            _instance = new WeaponProficiencyTable();
        }
        return _instance;
    }

    /**
     * 此處增加不同的屬性加成資料
     */
    public void loadProficiency() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Proficiency newProf;
        _dataMap.clear();
        _maxProficienciesLevel.clear();

        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_武器經驗設定");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int type;
                try {
                    type = Integer.parseInt(rs.getString("type").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                // 建立新的 Proficiency 實例
                if (!_dataMap.containsKey(type)) {
                    newProf = new Proficiency();
                    _dataMap.put(type, newProf);
                } else {
                    newProf = _dataMap.get(type);
                }
                int level = rs.getInt("level");
                int exp = rs.getInt("exp");
                short hp = rs.getShort("血量提升");
                short mp = rs.getShort("魔力提升");
                // 設定最大等級
                if (!_maxProficienciesLevel.containsKey(type)) {
                    _maxProficienciesLevel.put(type, level);
                }
                if (level > _maxProficienciesLevel.get(type)) {
                    _maxProficienciesLevel.put(type, level);
                }

                // 放入對應等級的熟練度資料
                WeaponProficiency proficiency = new WeaponProficiency(level, exp, hp, mp);
                newProf._proficiencyMap.put(level, proficiency);
            }

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }


    public static WeaponProficiency getProficiency(final int type, final int level) {
        if (_dataMap.containsKey(type)) {
            return _dataMap.get(type)._proficiencyMap.get(level);
        }
        return null;
    }

    public static int getMaxProficienciesLevel(final int type) {
        Integer level = _maxProficienciesLevel.get(type);
        if (level == null) {
            System.out.println("⚠️ 找不到熟練度最大等級 type: " + type);
            return 0; // 預設值可視邏輯調整
        }
        return level;
    }


    private static class Proficiency {
        // 因為只是用來讀取 所以使用安全的 ConcurrentHashMap 集合
        private final ConcurrentHashMap<Integer, WeaponProficiency> _proficiencyMap = new ConcurrentHashMap<>();
    }
}
