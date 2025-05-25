package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1PolyPower;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExtraPolyPowerTable//src014
{
    private static final Log _log = LogFactory.getLog(ExtraPolyPowerTable.class);
    private static final Map<Integer, L1PolyPower> _polyList = new LinkedHashMap<Integer, L1PolyPower>();
    private static ExtraPolyPowerTable _instance;

    public static ExtraPolyPowerTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraPolyPowerTable();
        }
        return _instance;
    }

    public static void effectBuff(L1PcInstance pc, L1PolyPower value, int negative) {
        pc.addAc(value.getAc() * negative); // 增加護甲等級
        pc.addMaxHp(value.getHp() * negative); // 增加最大生命值
        pc.addMaxMp(value.getMp() * negative); // 增加最大魔法值
        pc.addHpr(value.getHpr() * negative); // 增加生命再生率
        pc.addMpr(value.getMpr() * negative); // 增加魔法再生率
        pc.addStr(value.getStr() * negative); // 增加力量屬性
        pc.addCon(value.getCon() * negative); // 增加體質屬性
        pc.addDex(value.getDex() * negative); // 增加敏捷屬性
        pc.addWis(value.getWis() * negative); // 增加智慧屬性
        pc.addCha(value.getCha() * negative); // 增加魅力屬性
        pc.addInt(value.getInt() * negative); // 增加智力屬性
        pc.addSp(value.getSp() * negative); // 增加技能點數
        pc.addMr(value.getMr() * negative); // 增加魔法抗性
        pc.addHitup(value.getHitModifier() * negative); // 增加近戰命中率
        pc.addDmgup(value.getDmgModifier() * negative); // 增加近戰傷害
        pc.addBowHitup(value.getBowHitModifier() * negative); // 增加弓箭命中率
        pc.addBowDmgup(value.getBowDmgModifier() * negative); // 增加弓箭傷害
        pc.addMagicDmgModifier(value.getMagicDmgModifier() * negative); // 增加魔法傷害
        pc.addMagicDmgReduction(value.getMagicDmgReduction() * negative); // 增加魔法傷害減免
        pc.addDamageReductionByArmor(value.getReductionDmg() * negative); // 增加傷害的減免
        pc.addWater(value.getDefenseWater() * negative); // 增加水元素防禦
        pc.addWind(value.getDefenseWind() * negative); // 增加風元素防禦
        pc.addFire(value.getDefenseFire() * negative); // 增加火元素防禦
        pc.addEarth(value.getDefenseEarth() * negative); // 增加土元素防禦
        pc.addRegistStun(value.getRegistStun() * negative); // 增加眩暈抗性
        pc.addRegistStone(value.getRegistStone() * negative); // 增加石化抗性
        pc.addRegistSleep(value.getRegistSleep() * negative); // 增加睡眠抗性
        pc.add_regist_freeze(value.getRegistFreeze() * negative); // 增加冰凍抗性
        pc.addRegistSustain(value.getRegistSustain() * negative); // 增加持續性異常抗性
        pc.addRegistBlind(value.getRegistBlind() * negative); // 增加失明抗性
        pc.set_expadd(value.getExpPoint() * negative); // 增加經驗
        pc.add_up_hp_potion(value.get_up_hp_potion() * negative); // 藥水回復
        pc.setPvpDmg(value.getPvpDmg() * negative); // PVP攻擊
        pc.setPvpDmg_R(value.getPvpDmg_R() * negative); // PVP減免傷害
        pc.addOriginalMagicHit(value.getOriginalMagicHit() * negative); // 魔法命中
        pc.addStunLevel(value.getStunLevel() * negative); // 昏迷命中
        pc.addCloseCritical(value.getCloseCritical() * negative); // 近距離爆擊率
        pc.addBowCritical(value.getBowCritical() * negative); // 遠距離爆擊率

        pc.sendPackets(new S_OwnCharStatus(pc));
        //pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        if (pc.isInParty()) {
            pc.getParty().updateMiniHP(pc);
        }
        if (negative == 1) {
            // 设定一个对话档，玩家自行设定 pc.getloginpoly() == 1 时才显示
            powerInfos(pc, value, pc.getloginpoly() == 0);
        }
    }

    /**
     * 視窗展示變身額外能力值<br>
     * <font color="ff0000">自已檢查一下是否有遺漏的屬性</font><br>
     * <font color="00ff00">理论上是没有遗漏的</font>
     *
     * @param pc    展示對象
     * @param value 能力值
     * @param show  true = 不顯示
     */
    public static void powerInfos(L1PcInstance pc, L1PolyPower value, boolean show) {
        try {
            if (show) {
                pc.sendPackets(new S_AllChannelsChat("當前變身具備額外能力值加成", 44));
                return;
            }
            final List<String> powers = World.get().getHtmlString();// 任意需要內容說明的位置直接照抄此條 1
            powers.add("---- 變身附加能力值 ----");// 然後使用 add 添加說明內容 2
            if (value.getAc() != 0) {
                powers.add("防禦: " + value.getAc());
            }
            if (value.getHp() != 0) {
                powers.add("體力: " + value.getHp());
            }
            if (value.getMp() != 0) {
                powers.add("魔力: " + value.getMp());
            }
            if (value.getHpr() != 0) {
                powers.add("體力恢復: " + value.getHpr());
            }
            if (value.getMpr() != 0) {
                powers.add("魔力恢復: " + value.getMpr());
            }
            if (value.getStr() != 0) {
                powers.add("力量: " + value.getStr());
            }
            if (value.getCon() != 0) {
                powers.add("體質: " + value.getCon());
            }
            if (value.getDex() != 0) {
                powers.add("敏捷: " + value.getDex());
            }
            if (value.getWis() != 0) {
                powers.add("精神: " + value.getWis());
            }
            if (value.getCha() != 0) {
                powers.add("魅力: " + value.getCha());
            }
            if (value.getInt() != 0) {
                powers.add("智力: " + value.getInt());
            }
            if (value.getSp() != 0) {
                powers.add("魔攻: " + value.getSp());
            }
            if (value.getMr() != 0) {
                powers.add("魔防: " + value.getMr());
            }
            if (value.getHitModifier() != 0) {
                powers.add("近戰命中: " + value.getHitModifier());
            }
            if (value.getDmgModifier() != 0) {
                powers.add("近戰傷害: " + value.getDmgModifier());
            }
            if (value.getBowHitModifier() != 0) {
                powers.add("遠程命中: " + value.getBowHitModifier());
            }
            if (value.getBowDmgModifier() != 0) {
                powers.add("遠程傷害: " + value.getBowDmgModifier());
            }
            if (value.getMagicDmgModifier() != 0) {
                powers.add("魔法傷害: " + value.getMagicDmgModifier());
            }
            if (value.getMagicDmgReduction() != 0) {
                powers.add("魔法減傷: " + value.getMagicDmgReduction());
            }
            if (value.getReductionDmg() != 0) {
                powers.add("傷害減免: " + value.getReductionDmg());
            }
            if (value.getDefenseWater() != 0) {
                powers.add("水屬性防禦: " + value.getDefenseWater());
            }
            if (value.getDefenseWind() != 0) {
                powers.add("風屬性防禦: " + value.getDefenseWind());
            }
            if (value.getDefenseFire() != 0) {
                powers.add("火屬性防禦: " + value.getDefenseFire());
            }
            if (value.getDefenseEarth() != 0) {
                powers.add("地屬性防禦: " + value.getDefenseEarth());
            }
            if (value.getRegistStun() != 0) {
                powers.add("昏迷耐性: " + value.getRegistStun());
            }
            if (value.getRegistStone() != 0) {
                powers.add("石化耐性: " + value.getRegistStone());
            }
            if (value.getRegistSleep() != 0) {
                powers.add("睡眠耐性: " + value.getRegistSleep());
            }
            if (value.getRegistFreeze() != 0) {
                powers.add("冰凍耐性: " + value.getRegistFreeze());
            }
            if (value.getRegistSustain() != 0) {
                powers.add("支撐耐性: " + value.getRegistSustain());
            }
            if (value.getRegistBlind() != 0) {
                powers.add("暗黑耐性: " + value.getRegistBlind());
            }
            if (value.getExpPoint() != 0) {
                powers.add("經驗 + " + value.getExpPoint() + "%"); // 例如: "經驗 + 10%"
            }
            if (value.get_up_hp_potion() != 0) {
                powers.add("藥水恢復 + " + value.get_up_hp_potion() + "%"); // 藥水回復
            }
            if (value.getPvpDmg() != 0) {
                powers.add("PVP傷害 + " + value.getPvpDmg() + "攻擊"); //PVP攻擊
            }
            if (value.getPvpDmg_R() != 0) {
                powers.add("PVP減免 + " + value.getPvpDmg_R() + "減免"); //PVP減免
            }
            if (value.getOriginalMagicHit() != 0) {
                powers.add("魔法命中 + " + value.getOriginalMagicHit() + "%"); //魔法命中
            }
            if (value.getStunLevel() != 0) {
                powers.add("昏迷命中 + " + value.getStunLevel() + "%"); //魔法命中
            }
            if (value.getCloseCritical() != 0) {
                powers.add("近距離爆擊 + " + value.getCloseCritical() + "%"); //近距離爆擊
            }
            if (value.getBowCritical() != 0) {
                powers.add("遠距離爆擊 + " + value.getBowCritical() + "%"); //遠距離爆擊
            }
            if (powers.size() < 2) {// 因為第一行為標題，所以內容範圍最少要2條
                ListMapUtil.clear(powers);
                return;
            }
            powers.add(" ");
            World.showHtmlInfo(pc, "otherInfo");// 任意需要內容說明的位置直接照抄此條 3
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 系統_變身附加能力系統 ORDER BY 變身編號");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int polyId = rs.getInt("變身編號");
                int ac = rs.getInt("提升防禦力");
                int hp = rs.getInt("提升血量");
                int mp = rs.getInt("提升魔量");
                int hpr = rs.getInt("提升回血量");
                int mpr = rs.getInt("提升回魔量");
                int str = rs.getInt("提升力量");
                int con = rs.getInt("提升體質");
                int dex = rs.getInt("提升敏捷");
                int wis = rs.getInt("提升精神");
                int cha = rs.getInt("提升魅力");
                int intel = rs.getInt("提升智慧");
                int sp = rs.getInt("提升魔攻");
                int mr = rs.getInt("提升魔防");
                int hit_modifier = rs.getInt("近戰攻擊命中");
                int dmg_modifier = rs.getInt("近戰攻擊傷害");
                int bow_hit_modifier = rs.getInt("遠距攻擊命中");
                int bow_dmg_modifier = rs.getInt("遠距攻擊傷害");
                int magic_dmg_modifier = rs.getInt("額外魔法傷害");
                int magic_dmg_reduction = rs.getInt("魔法傷害減免");
                int reduction_dmg = rs.getInt("全傷害減免");
                int defense_water = rs.getInt("水屬性");
                int defense_wind = rs.getInt("風屬性");
                int defense_fire = rs.getInt("火屬性");
                int defense_earth = rs.getInt("地屬性");
                int regist_stun = rs.getInt("昏迷耐性");
                int regist_stone = rs.getInt("石化耐性");
                int regist_sleep = rs.getInt("睡眠耐性");
                int regist_freeze = rs.getInt("寒冰耐性");
                int regist_sustain = rs.getInt("支撑耐性");
                int regist_blind = rs.getInt("暗黑耐性");
                int EXP = rs.getInt("EXP");
                int potion = rs.getInt("藥水恢復");
                int PVP = rs.getInt("PVP攻擊");
                int PVP_R = rs.getInt("PVP減免");
                int magic_hit = rs.getInt("魔法命中率");
                int StunLv = rs.getInt("昏迷命中");
                int CloseCritical = rs.getInt("近距離爆擊");
                int BowCritical = rs.getInt("遠距離爆擊");
                L1PolyPower polyPower = new L1PolyPower(polyId, ac, hp, mp, hpr, mpr, str, con, dex, wis, cha, intel, sp, mr, hit_modifier, dmg_modifier, bow_hit_modifier, bow_dmg_modifier, magic_dmg_modifier, magic_dmg_reduction, reduction_dmg, defense_water, defense_wind, defense_fire, defense_earth, regist_stun, regist_stone, regist_sleep, regist_freeze, regist_sustain, regist_blind, EXP, potion, PVP, PVP_R, magic_hit, StunLv, CloseCritical, BowCritical);
                _polyList.put(Integer.valueOf(polyId), polyPower);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->變身附加能力資料數量: " + _polyList.size() + "(" + timer.get() + "ms)");
    }

    public final L1PolyPower get(int polyId) {
        L1PolyPower temp = (L1PolyPower) _polyList.get(Integer.valueOf(polyId));
        if (temp != null) {
            return temp;
        }
        return (L1PolyPower) _polyList.get(Integer.valueOf(polyId));
    }
}
