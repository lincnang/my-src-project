package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.data.event.ItemBuffSet;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemBuffTable;
import com.lineage.server.datatables.storage.CharBuffStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Cooking;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1BuffTmp;
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

import static com.lineage.server.model.skill.L1SkillId.*;

public class CharBuffTable implements CharBuffStorage {
    private static final Log _log = LogFactory.getLog(CharBuffTable.class);
    private static final Map<Integer, ArrayList<L1BuffTmp>> _buffMap = new HashMap<>();
    /**
     * 需要保留的技能
     */
    private static final int[] _buffSkill = {LIGHT, // 人物技能效果
            SHAPE_CHANGE, // 變身
            HASTE, GREATER_HASTE, PRIDE, SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN, HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, DRESS_MIGHTY, DRESS_DEXTERITY, GLOWING_AURA, SHINING_AURA, GLOWING_AURA_2, Armor_Valor, BRAVE_AURA, FIRE_WEAPON, FIRE_BLESS, BURNING_WEAPON, WIND_SHOT, STORM_EYE, STORM_SHOT, BLOODLUST, RESIST_FEAR, STATUS_BLUE_POTION, STATUS_CHAT_PROHIBITED, PHYSICAL_ENCHANT_INT_DEX_STR, STATUS_BRAVE2, // 荒神加速
            STATUS_BRAVE, // 加速效果
            STATUS_HASTE, STATUS_ELFBRAVE, STATUS_BRAVE3, STATUS_RIBRAVE, COOKING_1_0_N, // 舊版料理效果
            COOKING_1_0_S, COOKING_1_1_N, COOKING_1_1_S, COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S, COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S, COOKING_1_6_N, COOKING_1_6_S, COOKING_2_0_N, COOKING_2_0_S, COOKING_2_1_N, COOKING_2_1_S, COOKING_2_2_N, COOKING_2_2_S, COOKING_2_3_N, COOKING_2_3_S, COOKING_2_4_N, COOKING_2_4_S, COOKING_2_5_N, COOKING_2_5_S, COOKING_2_6_N, COOKING_2_6_S, COOKING_3_0_N, COOKING_3_0_S, COOKING_3_1_N, COOKING_3_1_S, COOKING_3_2_N, COOKING_3_2_S, COOKING_3_3_N, COOKING_3_3_S, COOKING_3_4_N, COOKING_3_4_S, COOKING_3_5_N, COOKING_3_5_S, COOKING_3_6_N, COOKING_3_6_S, COOKING_1_7_N, COOKING_1_7_S, COOKING_2_7_N, COOKING_2_7_S, COOKING_3_7_N, COOKING_3_7_S, COOKING_4_0_N, COOKING_4_1_N, COOKING_4_2_N, COOKING_4_3_N, // 新版料理
            // 一段經驗加倍藥水
            EXP13, EXP15, EXP17, EXP20, EXP25, EXP30, EXP35, EXP40, // 一段經驗加倍藥水
            EXP45, EXP50, EXP55, EXP60, EXP65, EXP70, EXP75, EXP80, EXP85, EXP90, EXP95, EXP100,
            // 二段經驗加倍藥水
            SEXP13, SEXP25, SEXP30, SEXP300, SEXP35, SEXP40, SEXP45, SEXP50, SEXP55, SEXP60, SEXP65, SEXP70, SEXP75, SEXP80, SEXP85, SEXP90, SEXP95, SEXP100, SEXP150, SEXP175, SEXP200, SEXP225, SEXP250, // 二段經驗加倍藥水
            5000, 5001, 5002, 5003, 5004, 5005, 5006, DRAGON1, DRAGON2, DRAGON3, DRAGON4, DRAGON5, DRAGON6, DRAGON7, // 魔眼效果
            BS_GX01, BS_GX02, BS_GX03, BS_GX04, BS_GX05, BS_GX06, BS_GX07, BS_GX08, BS_GX09, // 附魔石效果我
            BS_AX01, BS_AX02, BS_AX03, BS_AX04, BS_AX05, BS_AX06, BS_AX07, BS_AX08, BS_AX09, BS_WX01, BS_WX02, BS_WX03, BS_WX04, BS_WX05, BS_WX06, BS_WX07, BS_WX08, BS_WX09, BS_ASX01, BS_ASX02, BS_ASX03, BS_ASX04, BS_ASX05, BS_ASX06, BS_ASX07, BS_ASX08, BS_ASX09, DS_GX00, DS_GX01, DS_GX02, DS_GX03, DS_GX04, DS_GX05, DS_GX06, DS_GX07, DS_GX08, DS_GX09, // 龍印魔石效果
            DS_AX00, DS_AX01, DS_AX02, DS_AX03, DS_AX04, DS_AX05, DS_AX06, DS_AX07, DS_AX08, DS_AX09, DS_WX00, DS_WX01, DS_WX02, DS_WX03, DS_WX04, DS_WX05, DS_WX06, DS_WX07, DS_WX08, DS_WX09, DS_ASX00, DS_ASX01, DS_ASX02, DS_ASX03, DS_ASX04, DS_ASX05, DS_ASX06, DS_ASX07, DS_ASX08, DS_ASX09, SCORE02, SCORE03, CHAT_STOP, /* 自創道具效果 */
            DRAGON_BLOOD_1, DRAGON_BLOOD_2, DRAGON_BLOOD_3, DRAGON_BLOOD_4, /* 龍之血痕效果 */
            HOLYLIGHT, // 祝福之光效果
            EFFECT_ENCHANTING_BATTLE, // 強化戰鬥卷軸
            99889, 9964, 9965, 9966, 9967, 9968, 9969, 9971, 9972, 9973, 9974, 9975, 9976, LEVEL_UP_BONUS, // 升級經驗獎勵狀態
            Ancient_Secretn, // 古老秘藥
            Mazu// 媽祖狀態
    };

    /**
     * 刪除遺失資料
     *
     */
    private static void delete(int objid) {
        ArrayList<?> list = (ArrayList<?>) _buffMap.get(objid);
        if (list != null) {
            list.clear();
        }
        _buffMap.remove(objid);
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_buff` WHERE `char_obj_id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 刪除指定資料
     *
     */
    private static void delete_skill(int skillid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_buff` WHERE `skill_id`=?");
            ps.setInt(1, skillid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private static void delete_skill_buff(int objid) {
        delMap(objid);
    }

    /**
     * 加入清單
     *
     */
    private static void addMap(int objId, L1BuffTmp buffTmp) {
        ArrayList<L1BuffTmp> list = (ArrayList<L1BuffTmp>) _buffMap.get(objId);
        if (list == null) {
            ArrayList<L1BuffTmp> newlist = new ArrayList<>();
            newlist.add(buffTmp);
            _buffMap.put(objId, newlist);
        } else {
            list.add(buffTmp);
        }
    }

    /**
     * 刪除清單
     *
     */
    private static void delMap(int objid) {
        final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
        if (list != null) {
            for (L1BuffTmp buffTmp : list) {
                int skill_id = buffTmp.get_skill_id();
                if (skill_id == 40000) {
                    list.remove(buffTmp);
                    break;
                }
            }
        }
    }

    /**
     * 寫入保留技能紀錄
     *
     */
    //private void storeBuff(int objId, int skillId, int time, int polyId) {
    public static void storeBuff(int objId, int skillId, int time, int polyId) {
        L1BuffTmp buffTmp = new L1BuffTmp();
        buffTmp.set_char_obj_id(objId);
        buffTmp.set_skill_id(skillId);
        buffTmp.set_remaining_time(time);
        buffTmp.set_poly_id(polyId);
        // 加入MAP
        addMap(objId, buffTmp);
        // 寫入資料庫
        storeBuffR(buffTmp);
    }

    /**
     * 寫入保留技能紀錄
     *
     */
    private static void storeBuffR(L1BuffTmp buffTmp) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `character_buff` SET `char_obj_id`=?,`skill_id`=?,`remaining_time`=?,`poly_id`=?");
            ps.setInt(1, buffTmp.get_char_obj_id());
            ps.setInt(2, buffTmp.get_skill_id());
            ps.setInt(3, buffTmp.get_remaining_time());
            ps.setInt(4, buffTmp.get_poly_id());
            ps.execute();
        } catch (SQLException localSQLException) {
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 初始化載入
     */
    @Override
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_buff`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int char_obj_id = rs.getInt("char_obj_id");
                if (CharObjidTable.get().isChar(char_obj_id) != null) {
                    int skill_id = rs.getInt("skill_id");
                    int remaining_time = rs.getInt("remaining_time");
                    int poly_id = rs.getInt("poly_id");
                    L1BuffTmp buffTmp = new L1BuffTmp();
                    buffTmp.set_char_obj_id(char_obj_id);
                    buffTmp.set_skill_id(skill_id);
                    buffTmp.set_remaining_time(remaining_time);
                    buffTmp.set_poly_id(poly_id);
                    addMap(char_obj_id, buffTmp);
                } else {
                    delete(char_obj_id);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->保留技能紀錄資料數量: " + _buffMap.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 增加保留技能紀錄
     *
     */
    @Override
    public void saveBuff(L1PcInstance pc) {
        for (int skillId : _buffSkill) {
            int timeSec = pc.getSkillEffectTimeSec(skillId);
            if (timeSec > 0) {
                int polyId = -1;
                if (skillId == SHAPE_CHANGE) {
                    polyId = pc.getTempCharGfx();
                }
                storeBuff(pc.getId(), skillId, timeSec, polyId);
            }
        }
        if (ItemBuffSet.START) {
            ItemBuffTable.get().checkBuffSave(pc);
        }
        pc.clearSkillEffectTimer();
    }

    /**
     * 給予保留技能
     */
    @Override
    public void buff(L1PcInstance pc) {
        int objid = pc.getId();
        final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
        if (list != null) {
            for (L1BuffTmp buffTmp : list) {
                int skill_id = buffTmp.get_skill_id();
                int remaining_time = buffTmp.get_remaining_time();
                int poly_id = buffTmp.get_poly_id();
                if (remaining_time > 0) {// 還有剩餘時間
                    if (skill_id == SHAPE_CHANGE) {// 變身
                        L1PolyMorph.doPoly(pc, poly_id, remaining_time, L1PolyMorph.MORPH_BY_LOGIN);
                    } else {// 除了變身以外的其他技能
                        switch (skill_id) {
                            case STATUS_BRAVE3:
                                pc.sendPackets(new S_PacketBoxThirdSpeed(remaining_time));
                                pc.sendPacketsAll(new S_Liquor(pc.getId(), 8));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case Ancient_Secretn: // 古老秘藥
                                pc.addMaxHp(100);   // 體力上限+100
                                pc.addMaxMp(100);   // 魔力上限+100
                                pc.addHitup(10);    // 近距離命中+10
                                pc.addDmgup(5);     // 近距離傷害+5
                                pc.addBowHitup(10); // 遠距離命中+10
                                pc.addBowDmgup(5);  // 遠距離傷害+5
                                pc.addSp(5);        // 魔攻+5
                                pc.addAc(-10);      // 防禦-10
                                pc.addMr(10);       // 魔法防禦+10
                                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp())); // 體力更新
                                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                                pc.sendPackets(new S_OwnCharStatus(pc)); // 防禦更新
                                pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("古老秘藥剩餘(秒):" + remaining_time));
                                break;
                            case STATUS_BRAVE2:
                                pc.sendPackets(new S_SkillBrave(pc.getId(), 5, remaining_time));
                                pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 5, 0));
                                pc.setBraveSpeed(5);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 1000:
                                if (pc.hasSkillEffect(STATUS_BRAVE3)) {
                                    break;
                                }
                                pc.sendPackets(new S_SkillBrave(pc.getId(), 1, remaining_time));
                                pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
                                pc.setBraveSpeed(1);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 1016:
                                pc.sendPackets(new S_SkillBrave(pc.getId(), 3, remaining_time));
                                pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 3, 0));
                                pc.setBraveSpeed(3);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 1001:
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, remaining_time));
                                pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
                                pc.setMoveSpeed(1);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 1002:
                                pc.sendPackets(new S_PacketBox(32, remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 4002:
                            case 7004:
                                pc.sendPackets(new S_PacketBox(36, remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                            case 6666:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段1.3倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6667:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段1.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6668:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段1.7倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6669:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段2.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6670:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段2.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6671:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段3.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6672:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段3.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6673:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段4.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6674:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段4.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6675:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段5.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6676:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段5.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6677:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段6.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6678:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段6.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6679:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段7.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6680:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段7.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case 6681:
                                pc.sendPackets(new S_ServerMessage("\\fX第一段8.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case EXP85: // 第一段8.0倍經驗
                                // 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
                                pc.sendPackets(new S_ServerMessage("\\fY第一段8.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case EXP90: // 第一段8.0倍經驗
                                // 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
                                pc.sendPackets(new S_ServerMessage("\\fY第一段9.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case EXP95: // 第一段8.0倍經驗
                                // 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
                                pc.sendPackets(new S_ServerMessage("\\fY第一段9.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case EXP100: // 第一段8.0倍經驗
                                // 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
                                pc.sendPackets(new S_ServerMessage("\\fY第一段10.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                // 狩獵的經驗職將會增加
                                pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
                                break;
                            case SEXP13: // 第二段1.3倍經驗
                                // 3083 第二段經驗1.3倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段1.3倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP150: // 第二段1.5倍經驗
                                // 3084 第二段經驗1.5倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段1.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP175: // 第二段1.7倍經驗
                                // 3085 第二段經驗1.7倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段1.75倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP200: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段2.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP225: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段2.25倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP250: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段2.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP300: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段3.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP500: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段5.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP45: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段4.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP50: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段5.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP55: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段5.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP60: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段6.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP65: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段6.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP70: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段7.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP75: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段7.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP80: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段8.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP85: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段8.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP90: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段9.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP95: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段9.5倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case SEXP100: // 第二段2.0倍經驗
                                // 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
                                pc.sendPackets(new S_ServerMessage("\\fY第二段10.0倍經驗 剩餘時間(秒):" + remaining_time));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, remaining_time)); // 2565(一階段經驗料理圖示編號)
                                break;
                            case 7002:
                                pc.sendPackets(new S_ServerMessage("\\fR積分加倍(2倍)作用中"));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 7003:
                                pc.sendPackets(new S_ServerMessage("\\fR積分加倍(3倍)作用中"));
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 99889:
                            case 9971:
                            case 9972:
                            case 9973:
                            case 9974:
                            case 9975:
                            case 9976:
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case 9964:
                                pc.addStr(6);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[力量+6]剩餘(秒):" + remaining_time));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                break;
                            case 9965:
                                pc.addDex(6);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[敏捷+6]剩餘(秒):" + remaining_time));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                break;
                            case 9966:
                                pc.addInt(6);
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[智力+6]剩餘(秒):" + remaining_time));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                break;
                            case 9967:
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[攻擊+15]剩餘(秒):" + remaining_time));
                                pc.addDmgup(15);
                                pc.addBowDmgup(12);
                                break;
                            case 9968:
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[命中+10]剩餘(秒):" + remaining_time));
                                pc.addHitup(10);
                                pc.addBowHitup(7);
                                break;
                            case 9969:
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                pc.sendPackets(new S_ServerMessage("魔法占卜[魔法攻擊+5]剩餘(秒):" + remaining_time));
                                pc.addSp(5);
                                pc.sendPackets(new S_SPMR(pc));
                                break;
                            case 3000:
                            case 3001:
                            case 3002:
                            case 3003:
                            case 3004:
                            case 3005:
                            case 3006:
                            case 3007:
                            case 3008:
                            case 3009:
                            case 3010:
                            case 3011:
                            case 3012:
                            case 3013:
                            case 3014:
                            case 3015:
                            case 3016:
                            case 3017:
                            case 3018:
                            case 3019:
                            case 3020:
                            case 3021:
                            case 3022:
                            case 3023:
                            case 3024:
                            case 3025:
                            case 3026:
                            case 3027:
                            case 3028:
                            case 3029:
                            case 3030:
                            case 3031:
                            case 3032:
                            case 3033:
                            case 3034:
                            case 3035:
                            case 3036:
                            case 3037:
                            case 3038:
                            case 3039:
                            case 3040:
                            case 3041:
                            case 3042:
                            case 3043:
                            case 3044:
                            case 3045:
                            case 3046:
                            case 3047:
                            case 3048:
                            case 3049:
                            case 3050:
                            case 3051:
                                L1Cooking.eatCooking(pc, skill_id, remaining_time);
                                break;
                            case 8000: // 祝福之光
                                pc.setSkillEffect(skill_id, remaining_time * 1000);
                                break;
                            case EFFECT_ENCHANTING_BATTLE: // 強化戰鬥卷軸
                                pc.setSkillEffect(EFFECT_ENCHANTING_BATTLE, remaining_time * 1000);
                                pc.addHitup(3); // 攻擊成功 +3
                                pc.addDmgup(3); // 額外攻擊點數 +3
                                pc.addBowHitup(3); // 遠距離命中率 +3
                                pc.addBowDmgup(3); // 遠距離攻擊力 +3
                                pc.addSp(3); // 魔攻 +3
                                pc.sendPackets(new S_SPMR(pc));
                                break;
                            case Mazu:
                                pc.set_mazu(true);
                                pc.setSkillEffect(Mazu, remaining_time * 1000);
                                pc.set_mazu_time(remaining_time);
                                pc.sendPackets(new S_SystemMessage("媽祖狀態還有" + remaining_time + "秒。"));
                                break;
                            case LEVEL_UP_BONUS: // 升級經驗獎勵狀態
                                pc.setSkillEffect(LEVEL_UP_BONUS, remaining_time * 1000);
                                pc.sendPackets(new S_PacketBox(remaining_time, true, true));
                                break;
                            default:
                                SkillMode mode = L1SkillMode.get().getSkill(skill_id);
                                if (mode != null) {// 具有SkillMode
                                    try {
                                        mode.start(pc, pc, null, remaining_time);// SkillMode移轉
                                        switch (skill_id) {
                                            case 4401:// 附魔石
                                            case 4402:
                                            case 4403:
                                            case 4404:
                                            case 4405:
                                            case 4406:
                                            case 4407:
                                            case 4408:
                                            case 4409:
                                                pc.sendPackets(new S_SkillSound(pc.getId(), skill_id + 4538));
                                                break;
                                            case 4411:// 附魔石
                                            case 4412:
                                            case 4413:
                                            case 4414:
                                            case 4415:
                                            case 4416:
                                            case 4417:
                                            case 4418:
                                            case 4419:
                                                pc.sendPackets(new S_SkillSound(pc.getId(), skill_id + 4537));
                                                break;
                                            case 4421:// 附魔石
                                            case 4422:
                                            case 4423:
                                            case 4424:
                                            case 4425:
                                            case 4426:
                                            case 4427:
                                            case 4428:
                                            case 4429:
                                                pc.sendPackets(new S_SkillSound(pc.getId(), skill_id + 4536));
                                                break;
                                            case 4431:// 附魔石
                                            case 4432:
                                            case 4433:
                                            case 4434:
                                            case 4435:
                                            case 4436:
                                            case 4437:
                                            case 4438:
                                            case 4439:
                                                pc.sendPackets(new S_SkillSound(pc.getId(), skill_id + 4535));
                                                break;
                                            case 4500:
                                            case 4501:
                                            case 4502:
                                            case 4503:
                                            case 4504:
                                            case 4505:
                                            case 4506:
                                            case 4507:
                                            case 4508:
                                            case 4509:// 9階 鬥士
                                            case 4510:
                                            case 4511:
                                            case 4512:
                                            case 4513:
                                            case 4514:
                                            case 4515:
                                            case 4516:
                                            case 4517:
                                            case 4518:
                                            case 4519:// 9階 弓手
                                            case 4520:
                                            case 4521:
                                            case 4522:
                                            case 4523:
                                            case 4524:
                                            case 4525:
                                            case 4526:
                                            case 4527:
                                            case 4528:
                                            case 4529:// 9階 賢者
                                            case 4530:
                                            case 4531:
                                            case 4532:
                                            case 4533:
                                            case 4534:
                                            case 4535:
                                            case 4536:
                                            case 4537:
                                            case 4538:
                                            case 4539:// 9階 衝鋒
                                                pc.sendPackets(new S_SkillSound(pc.getId(), skill_id + 6311));
                                                break;
                                        }
                                    } catch (Exception e) {
                                        _log.error(e.getLocalizedMessage(), e);
                                    }
                                } else if (ItemBuffSet.START && ItemBuffTable.get().checkItem(skill_id)) {
                                    ItemBuffTable.get().add(pc, skill_id, remaining_time);
                                } else {// 沒有SkillMode
                                    L1SkillUse l1skilluse = new L1SkillUse();
                                    l1skilluse.handleCommands(pc, skill_id, pc.getId(), pc.getX(), pc.getY(), remaining_time, L1SkillUse.TYPE_LOGIN);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 刪除全部保留技能紀錄
     *
     */
    @Override
    public void deleteBuff(final L1PcInstance pc) {
        delete(pc.getId());
    }

    /**
     * 刪除全部保留技能紀錄
     *
     */
    @Override
    public void deleteBuff(final int objid) {
        delete(objid);
    }

    /**
     * 刪除指定保留技能紀錄
     *
     */
    @Override
    public void deleteBuff_skill(int skillid) {
        delete_skill(skillid);
    }

    /**
     * 刪除指定保留技能紀錄buff
     *
     */
    @Override
    public void deleteBuff_skill_buff(int objid) {
        delete_skill_buff(objid);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.CharBuffTable JD-Core Version: 0.6.2
 */