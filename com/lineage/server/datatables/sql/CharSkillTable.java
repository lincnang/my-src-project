package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharSkillStorage;
import com.lineage.server.templates.L1UserSkillTmp;
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
import java.util.Map;

/**
 * 人物技能紀錄(整合 skill_level)
 */
public class CharSkillTable implements CharSkillStorage {
    private static final Log _log = LogFactory.getLog(CharSkillTable.class);
    /**
     * 紀錄: objId -> 該角色的所有技能資訊(包含 skill_level)
     */
    private static final Map<Integer, ArrayList<L1UserSkillTmp>> _skillMap = new HashMap<>();
    private static CharSkillTable _instance;


    public static CharSkillTable get() {
        if (_instance == null) {
            _instance = new CharSkillTable();
        }
        return _instance;
    }

    /**
     * 刪除遺失資料(角色不存在)
     */
    private static void deleteBuff(final int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_skills` WHERE `char_obj_id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 加入Map
     */
    private static void addMap(final int objId, final L1UserSkillTmp skillTmp) {
        ArrayList<L1UserSkillTmp> list = _skillMap.computeIfAbsent(objId, k -> new ArrayList<>());
        list.add(skillTmp);
    }

    @Override
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `character_skills`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int char_obj_id = rs.getInt("char_obj_id");
                // 檢查該角色是否存在
                if (CharObjidTable.get().isChar(char_obj_id) != null) {
                    final int skill_id = rs.getInt("skill_id");
                    final String skill_name = rs.getString("skill_name");
                    final int is_active = rs.getInt("is_active");
                    final int activetimeleft = rs.getInt("activetimeleft");
                    final int skill_level = rs.getInt("skill_level");

                    L1UserSkillTmp userSkill = new L1UserSkillTmp();
                    userSkill.set_char_obj_id(char_obj_id);
                    userSkill.set_skill_id(skill_id);
                    userSkill.set_skill_name(skill_name);
                    userSkill.set_is_active(is_active);
                    userSkill.set_activetimeleft(activetimeleft);
                    userSkill.set_skill_level(skill_level);

                    addMap(char_obj_id, userSkill);
                } else {
                    // 角色不存在 → 刪除紀錄
                    deleteBuff(char_obj_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->人物技能紀錄資料筆數(角色數): " + _skillMap.size() + " (" + timer.get() + "ms)");
    }

    @Override
    public ArrayList<L1UserSkillTmp> skills(final int playerobjid) {
        return _skillMap.get(playerobjid);
    }

    /**
     * 依照介面要求的5參數: 不帶 skillLevel
     * 你可自行在這裡決定給預設skillLevel=0 or 1
     */
    @Override
    public void spellMastery(final int playerobjid,
                             final int skillid,
                             final String skillname,
                             final int active,
                             final int time) {
        // 檢查是否重複
        if (this.spellCheck(playerobjid, skillid)) {
            return;
        }
        // 預設把skill_level=0 (或1) 當作初始
        int initLevel = 0;

        L1UserSkillTmp userSkill = new L1UserSkillTmp();
        userSkill.set_char_obj_id(playerobjid);
        userSkill.set_skill_id(skillid);
        userSkill.set_skill_name(skillname);
        userSkill.set_is_active(active);
        userSkill.set_activetimeleft(time);
        userSkill.set_skill_level(initLevel);

        addMap(playerobjid, userSkill);

        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement(
                    "INSERT INTO `character_skills` SET "
                            + "`char_obj_id`=?, `skill_id`=?, `skill_name`=?, "
                            + "`is_active`=?, `activetimeleft`=?, `skill_level`=?"
            );
            ps.setInt(1, userSkill.get_char_obj_id());
            ps.setInt(2, userSkill.get_skill_id());
            ps.setString(3, userSkill.get_skill_name());
            ps.setInt(4, userSkill.get_is_active());
            ps.setInt(5, userSkill.get_activetimeleft());
            ps.setInt(6, userSkill.get_skill_level());
            ps.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    @Override
    public void spellLost(final int playerobjid, final int skillid) {
        ArrayList<L1UserSkillTmp> list = _skillMap.get(playerobjid);
        if (list == null) {
            return;
        }
        L1UserSkillTmp del = null;
        for (L1UserSkillTmp userSkillTmp : list) {
            if (userSkillTmp.get_skill_id() == skillid) {
                del = userSkillTmp;
                break;
            }
        }
        if (del == null) {
            return;
        }
        // 移除
        list.remove(del);

        // 同步資料庫
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement(
                    "DELETE FROM `character_skills` WHERE `char_obj_id`=? AND `skill_id`=?"
            );
            ps.setInt(1, playerobjid);
            ps.setInt(2, skillid);
            ps.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    @Override
    public boolean spellCheck(final int playerobjid, final int skillid) {
        ArrayList<L1UserSkillTmp> list = _skillMap.get(playerobjid);
        if (list != null) {
            for (L1UserSkillTmp userSkillTmp : list) {
                if (userSkillTmp.get_skill_id() == skillid) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setAuto(final int mode, final int objid, final int skillid) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement(
                    "UPDATE `character_skills` SET `is_active`=? "
                            + "WHERE `char_obj_id`=? AND `skill_id`=?"
            );
            ps.setInt(1, mode);
            ps.setInt(2, objid);
            ps.setInt(3, skillid);
            ps.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 新增: 更新技能等級
     * 方便你在玩家吃書後, +1等, 再寫回DB & Map
     */
    @Override
    public void updateSkillLevel(int playerObjId, int skillId, int newLevel) {
        // (a) 更新記憶體
        ArrayList<L1UserSkillTmp> list = _skillMap.get(playerObjId);
        if (list != null) {
            for (L1UserSkillTmp skill : list) {
                if (skill.get_skill_id() == skillId) {
                    skill.set_skill_level(newLevel);
                    break;
                }
            }
        }
        // 更新資料庫
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("UPDATE `character_skills` SET `skill_level`=? "
                    + "WHERE `char_obj_id`=? AND `skill_id`=?");
            ps.setInt(1, newLevel);
            ps.setInt(2, playerObjId);
            ps.setInt(3, skillId);
            ps.executeUpdate();
        } catch (Exception e) {
            // logging ...
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }
}