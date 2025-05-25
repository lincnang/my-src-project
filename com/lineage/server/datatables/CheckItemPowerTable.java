package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1CheckItemPower;
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

/**
 * 身上持有道具給予能力系統
 */
public class CheckItemPowerTable {
    private static final Log _log = LogFactory.getLog(CheckItemPowerTable.class);
    private static final Map<Integer, L1CheckItemPower> _list = new HashMap<Integer, L1CheckItemPower>();
    private static CheckItemPowerTable _instance;

    private CheckItemPowerTable() {
        load();
    }

    public static CheckItemPowerTable get() {
        if (_instance == null) {
            _instance = new CheckItemPowerTable();
        }
        return _instance;
    }

    private void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pstm = co.prepareStatement("SELECT * FROM `其他_偵測身上道具能力加乘`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("item_id");
                final int powermsg = rs.getInt("power_msg");
                final int box_icon = rs.getInt("box_icon");
                final int box_stringid = rs.getInt("box_stringid");
                final int open_string = rs.getInt("open_string");
                final int str = rs.getInt("add_str");
                final int dex = rs.getInt("add_dex");
                final int con = rs.getInt("add_con");
                final int intel = rs.getInt("add_int");
                final int wis = rs.getInt("add_wis");
                final int cha = rs.getInt("add_cha");
                final int ac = rs.getInt("add_ac");
                final int hp = rs.getInt("add_hp");
                final int mp = rs.getInt("add_mp");
                final int hpr = rs.getInt("add_hpr");
                final int mpr = rs.getInt("add_mpr");
                final int mr = rs.getInt("add_mr");
                final int sp = rs.getInt("add_sp");
                final int dmg = rs.getInt("add_dmg");
                final int bow_dmg = rs.getInt("add_bow_dmg");
                final int hit = rs.getInt("add_hit");
                final int bow_hit = rs.getInt("add_bow_hit");
                final int dmg_r = rs.getInt("add_dmg_r");
                final int magic_r = rs.getInt("add_magic_r");
                final int fire = rs.getInt("add_fire");
                final int wind = rs.getInt("add_wind");
                final int earth = rs.getInt("add_earth");
                final int water = rs.getInt("add_water");
                final int stun = rs.getInt("add_stun");
                final int stone = rs.getInt("add_stone");
                final int sleep = rs.getInt("add_sleep");
                final int freeze = rs.getInt("add_freeze");
                final int sustain = rs.getInt("add_sustain");
                final int blind = rs.getInt("add_blind");
                final int exp = rs.getInt("add_exp");
                final int gf = rs.getInt("add_gf");
                boolean death_exp = rs.getBoolean("death_exp"); // 死亡不噴經驗
                boolean death_item = rs.getBoolean("death_item"); // 死亡不噴道具
                boolean death_skill = rs.getBoolean("death_skill"); // 死亡不噴技能
                boolean death_score = rs.getBoolean("death_score"); // 死亡不噴積分
                final L1CheckItemPower power = new L1CheckItemPower();
                power.set_powermsg(powermsg);
                power.set_box_icon(box_icon);
                power.set_box_stringid(box_stringid);
                power.set_open_string(open_string);
                power.set_str(str);
                power.set_dex(dex);
                power.set_con(con);
                power.set_intel(intel);
                power.set_wis(wis);
                power.set_cha(cha);
                power.set_ac(ac);
                power.set_hp(hp);
                power.set_mp(mp);
                power.set_hpr(hpr);
                power.set_mpr(mpr);
                power.set_mr(mr);
                power.set_sp(sp);
                power.set_dmg(dmg);
                power.set_bow_dmg(bow_dmg);
                power.set_hit(hit);
                power.set_bow_hit(bow_hit);
                power.set_dmg_r(dmg_r);
                power.set_magic_r(magic_r);
                power.set_fire(fire);
                power.set_wind(wind);
                power.set_earth(earth);
                power.set_water(water);
                power.set_stun(stun);
                power.set_stone(stone);
                power.set_sleep(sleep);
                power.set_freeze(freeze);
                power.set_sustain(sustain);
                power.set_blind(blind);
                power.set_exp(exp);
                power.set_gf(gf);
                power.set_death_exp(death_exp);
                power.set_death_item(death_item);
                power.set_death_skill(death_skill);
                power.set_death_score(death_score);
                _list.put(item_id, power);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(co);
        }
        _log.info("讀取->其他_偵測身上道具能力設置數量: " + _list.size() + "(" + timer.get() + "ms)");
    }

    public L1CheckItemPower getItem(int item_id) {
        if (_list.isEmpty()) {
            return null;
        }
        if (_list.containsKey(item_id)) {
            return _list.get(item_id);
        } else {
            return null;
        }
    }

    public boolean checkItem(final int item_id) {
        return _list.containsKey(item_id);
    }

    /**
     * 增加效果
     *
     * @return
     * @return
     */
    public void givepower(L1PcInstance pc, int item_id) {
        if (_list.isEmpty()) {
            return;
        }
        if (!_list.containsKey(item_id)) {
            return;
        }
        final L1CheckItemPower power = _list.get(item_id);
        final boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;
        final StringBuilder name = new StringBuilder();
        if (power.get_hp() != 0) { // 體力上限
            pc.addMaxHp(power.get_hp());
            name.append(" 體力上限+");
            name.append(power.get_hp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
        if (power.get_mp() != 0) { // 魔力上限
            pc.addMaxMp(power.get_mp());
            name.append(" 魔力上限+");
            name.append(power.get_mp());
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (power.get_hpr() != 0) { // 體力回復量
            pc.addHpr(power.get_hpr());
            name.append(" 體力回復量+");
            name.append(power.get_hpr());
        }
        if (power.get_mpr() != 0) { // 魔力回復量
            pc.addMpr(power.get_mpr());
            name.append(" 魔力回復量+");
            name.append(power.get_mpr());
        }
        if (power.get_ac() != 0) { // 防禦
            pc.addAc(-power.get_ac());
            name.append(" 防禦+");
            name.append(power.get_ac());
            attr = true;
        }
        if (power.get_sp() != 0) { // 魔法攻擊力
            pc.addSp(power.get_sp());
            name.append(" 魔攻+");
            name.append(power.get_sp());
            spmr = true;
        }
        if (power.get_mr() != 0) { // 額外魔法防禦
            pc.addMr(power.get_mr());
            name.append(" 額外魔法防禦+");
            name.append(power.get_mr());
            spmr = true;
        }
        if (power.get_str() != 0) { // 力量
            pc.addStr(power.get_str());
            name.append(" 力量+");
            name.append(power.get_str());
            status2 = true;
        }
        if (power.get_dex() != 0) { // 敏捷
            pc.addDex(power.get_dex());
            name.append(" 敏捷+");
            name.append(power.get_dex());
            status2 = true;
        }
        if (power.get_con() != 0) { // 體質
            pc.addCon(power.get_con());
            name.append(" 體質+");
            name.append(power.get_con());
            status2 = true;
        }
        if (power.get_wis() != 0) { // 精神
            pc.addWis(power.get_wis());
            name.append(" 精神+");
            name.append(power.get_wis());
            status2 = true;
        }
        if (power.get_intel() != 0) { // 智力
            pc.addInt(power.get_intel());
            name.append(" 智力+");
            name.append(power.get_intel());
            status2 = true;
        }
        if (power.get_cha() != 0) { // 魅力
            pc.addCha(power.get_cha());
            name.append(" 魅力+");
            name.append(power.get_cha());
            status2 = true;
        }
        if (power.get_dmg() != 0) { // 近遠距離攻擊
            pc.addDmgup(power.get_dmg());
            name.append(" 近遠距離攻擊+");
            name.append(power.get_dmg());
        }
        if (power.get_bow_dmg() != 0) { // 遠距離攻擊力
            pc.addBowDmgup(power.get_bow_dmg());
            name.append(" 遠距離攻擊力+");
            name.append(power.get_bow_dmg());
        }
        if (power.get_hit() != 0) { // 近距離命中
            pc.addHitup(power.get_hit());
            name.append(" 近距離命中+");
            name.append(power.get_hit());
        }
        if (power.get_bow_hit() != 0) { // 遠距離命中
            pc.addBowHitup(power.get_bow_hit());
            name.append(" 遠距離命中+");
            name.append(power.get_bow_hit());
        }
        if (power.get_dmg_r() != 0) { // 物理傷害減免
            pc.addDamageReductionByArmor(power.get_dmg_r());
            name.append(" 物理傷害減免+");
            name.append(power.get_dmg_r());
        }
        if (power.get_magic_r() != 0) { // 魔法傷害減免
            pc.add_magic_reduction_dmg(power.get_magic_r());
            name.append(" 魔法傷害減免+");
            name.append(power.get_magic_r());
        }
        if (power.get_water() != 0) { // 水屬性抗性
            pc.addWater(power.get_water());
            name.append(" 水屬性抗性+");
            name.append(power.get_water());
            attr = true;
        }
        if (power.get_fire() != 0) { // 火屬性抗性
            pc.addFire(power.get_fire());
            name.append(" 火屬性抗性+");
            name.append(power.get_fire());
            attr = true;
        }
        if (power.get_wind() != 0) { // 風屬性抗性
            pc.addWind(power.get_wind());
            name.append(" 風屬性抗性+");
            name.append(power.get_wind());
            attr = true;
        }
        if (power.get_earth() != 0) { // 地屬性抗性
            pc.addEarth(power.get_earth());
            name.append(" 地屬性抗性+");
            name.append(power.get_earth());
            attr = true;
        }
        if (power.get_stun() != 0) { // 昏迷耐性
            pc.addRegistStun(power.get_stun());
            name.append(" 昏迷耐性+");
            name.append(power.get_stun());
        }
        if (power.get_stone() != 0) { // 石化耐性
            pc.addRegistStone(power.get_stone());
            name.append(" 石化耐性+");
            name.append(power.get_stone());
        }
        if (power.get_sleep() != 0) { // 睡眠耐性
            pc.addRegistSleep(power.get_sleep());
            name.append(" 睡眠耐性+");
            name.append(power.get_sleep());
        }
        if (power.get_freeze() != 0) { // 寒冰耐性
            pc.add_regist_freeze(power.get_freeze());
            name.append(" 寒冰耐性+");
            name.append(power.get_freeze());
        }
        if (power.get_sustain() != 0) { // 支撐耐性
            pc.addRegistSustain(power.get_sustain());
            name.append(" 支撐耐性+");
            name.append(power.get_sustain());
        }
        if (power.get_blind() != 0) { // 暗黑耐性
            pc.addRegistBlind(power.get_blind());
            name.append(" 暗黑耐性+");
            name.append(power.get_blind());
        }
        if (power.get_exp() != 0) { // 狩獵經驗值
            pc.addExpRateToPc(power.get_exp());
            name.append(" 狩獵經驗值+");
            name.append(power.get_exp());
            name.append("% ");
        }
        if (power.get_gf() != 0) { // 怪物金幣掉落量
            pc.addGF(power.get_gf());
            name.append(" 怪物金幣掉落量+");
            name.append(power.get_gf());
            name.append("% ");
        }
        boolean death_exp = power.get_death_exp();
        if (death_exp) { // 死亡不噴經驗
            pc.set_death_exp(true);
            name.append(" 死亡不噴經驗");
        }
        boolean death_item = power.get_death_item();
        if (death_item) { // 死亡不噴道具
            pc.set_death_item(true);
            name.append(" 死亡不噴道具");
        }
        boolean death_skill = power.get_death_skill();
        if (death_skill) { // 死亡不噴技能
            pc.set_death_skill(true);
            name.append(" 死亡不噴技能");
        }
        boolean death_score = power.get_death_score();
        if (death_score) { // 死亡不噴積分
            pc.set_death_score(true);
            name.append(" 死亡不噴積分");
        }
        if (status) {
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else {
            if (status2) {
                pc.sendDetails();
                pc.sendPackets(new S_OwnCharStatus2(pc));
            }
            if (attr) {
                pc.sendPackets(new S_OwnCharAttrDef(pc));
            }
        }
        if (spmr) {
            pc.sendPackets(new S_SPMR(pc));
        }
        if (power.get_powermsg() != 0) {
            pc.sendPackets(new S_ServerMessage(name.toString()));
        }
        // 開啟圖示
        if (power.get_box_icon() != 0 && power.get_box_stringid() != 0) { // 自訂狀態圖示和訊息
            if (power.get_open_string() == 0) { // 有設狀態圖示和訊息的使用和登入是否顯示圖示裡面的訊息內容
                pc.sendPackets(new S_InventoryIcon(power.get_box_icon(), true, power.get_box_stringid(), -1)); // 重登不會有訊息
            } else {
                pc.sendPackets(new S_InventoryIcon(power.get_box_icon(), true, power.get_box_stringid(), power.get_box_stringid(), -1)); // 重登有訊息
            }
        }
    }

    /**
     * 移除效果
     */
    public void delepower(L1PcInstance pc, int item_id) {
        if (!_list.containsKey(item_id)) {
            return;
        }
        L1CheckItemPower power = _list.get(item_id);
        final boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;
        final StringBuilder name = new StringBuilder();
        if (power.get_hp() != 0) { // 體力上限
            pc.addMaxHp(-power.get_hp());
            name.append(" 體力上限-");
            name.append(power.get_hp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
        if (power.get_mp() != 0) { // 魔力上限
            pc.addMaxMp(-power.get_mp());
            name.append(" 魔力上限-");
            name.append(power.get_mp());
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (power.get_hpr() != 0) { // 體力回復量
            pc.addHpr(-power.get_hpr());
            name.append(" 體力回復量-");
            name.append(power.get_hpr());
        }
        if (power.get_mpr() != 0) { // 魔力回復量
            pc.addMpr(-power.get_mpr());
            name.append(" 魔力回復量-");
            name.append(power.get_mpr());
        }
        if (power.get_ac() != 0) { // 防禦
            pc.addAc(power.get_ac());
            name.append(" 防禦-");
            name.append(power.get_ac());
            attr = true;
        }
        if (power.get_sp() != 0) { // 魔法攻擊力
            pc.addSp(-power.get_sp());
            name.append(" 魔攻-");
            name.append(power.get_sp());
            spmr = true;
        }
        if (power.get_mr() != 0) { // 額外魔法防禦
            pc.addMr(-power.get_mr());
            name.append(" 魔防-");
            name.append(power.get_mr());
            spmr = true;
        }
        if (power.get_str() != 0) { // 力量
            pc.addStr(-power.get_str());
            name.append(" 力量-");
            name.append(power.get_str());
            status2 = true;
        }
        if (power.get_dex() != 0) { // 敏捷
            pc.addDex(-power.get_dex());
            name.append(" 敏捷-");
            name.append(power.get_dex());
            status2 = true;
        }
        if (power.get_con() != 0) { // 體質
            pc.addCon(-power.get_con());
            name.append(" 體質-");
            name.append(power.get_con());
            status2 = true;
        }
        if (power.get_wis() != 0) { // 精神
            pc.addWis(-power.get_wis());
            name.append(" 精神-");
            name.append(power.get_wis());
            status2 = true;
        }
        if (power.get_intel() != 0) { // 智力
            pc.addInt(-power.get_intel());
            name.append(" 智力-");
            name.append(power.get_intel());
            status2 = true;
        }
        if (power.get_cha() != 0) { // 魅力
            pc.addCha(-power.get_cha());
            name.append(" 魅力-");
            name.append(power.get_cha());
            status2 = true;
        }
        if (power.get_dmg() != 0) { // 近遠距離攻擊
            pc.addDmgup(-power.get_dmg());
            name.append(" 近遠距離攻擊-");
            name.append(power.get_dmg());
        }
        if (power.get_bow_dmg() != 0) { // 遠距離攻擊力
            pc.addBowDmgup(-power.get_bow_dmg());
            name.append(" 遠距離攻擊力-");
            name.append(power.get_bow_dmg());
        }
        if (power.get_hit() != 0) { // 近距離命中
            pc.addHitup(-power.get_hit());
            name.append(" 近距離命中-");
            name.append(power.get_hit());
        }
        if (power.get_bow_hit() != 0) { // 遠距離命中
            pc.addBowHitup(-power.get_bow_hit());
            name.append(" 遠距離命中-");
            name.append(power.get_bow_hit());
        }
        if (power.get_dmg_r() != 0) { // 物理傷害減免
            pc.addDamageReductionByArmor(-power.get_dmg_r());
            name.append(" 物理傷害減免-");
            name.append(power.get_dmg_r());
        }
        if (power.get_magic_r() != 0) { // 魔法傷害減免
            pc.add_magic_reduction_dmg(-power.get_magic_r());
            name.append(" 魔法傷害減免-");
            name.append(power.get_magic_r());
        }
        if (power.get_water() != 0) { // 水屬性抗性
            pc.addWater(-power.get_water());
            name.append(" 水屬性抗性-");
            name.append(power.get_water());
            attr = true;
        }
        if (power.get_fire() != 0) { // 火屬性抗性
            pc.addFire(-power.get_fire());
            name.append(" 火屬性抗性-");
            name.append(power.get_fire());
            attr = true;
        }
        if (power.get_wind() != 0) { // 風屬性抗性
            pc.addWind(-power.get_wind());
            name.append(" 風屬性抗性-");
            name.append(power.get_wind());
            attr = true;
        }
        if (power.get_earth() != 0) { // 地屬性抗性
            pc.addEarth(-power.get_earth());
            name.append(" 地屬性抗性-");
            name.append(power.get_earth());
            attr = true;
        }
        if (power.get_stun() != 0) { // 昏迷耐性
            pc.addRegistStun(-power.get_stun());
            name.append(" 昏迷耐性-");
            name.append(power.get_stun());
        }
        if (power.get_stone() != 0) { // 石化耐性
            pc.addRegistStone(-power.get_stone());
            name.append(" 石化耐性-");
            name.append(power.get_stone());
        }
        if (power.get_sleep() != 0) { // 睡眠耐性
            pc.addRegistSleep(-power.get_sleep());
            name.append(" 睡眠耐性-");
            name.append(power.get_sleep());
        }
        if (power.get_freeze() != 0) { // 寒冰耐性
            pc.add_regist_freeze(-power.get_freeze());
            name.append(" 寒冰耐性-");
            name.append(power.get_freeze());
        }
        if (power.get_sustain() != 0) { // 支撐耐性
            pc.addRegistSustain(-power.get_sustain());
            name.append(" 支撐耐性-");
            name.append(power.get_sustain());
        }
        if (power.get_blind() != 0) { // 暗黑耐性
            pc.addRegistBlind(-power.get_blind());
            name.append(" 暗黑耐性-");
            name.append(power.get_blind());
        }
        if (power.get_exp() != 0) { // 狩獵經驗值
            pc.addExpRateToPc(-power.get_exp());
            name.append(" 狩獵經驗值-");
            name.append(power.get_exp());
            name.append("% ");
        }
        if (power.get_gf() != 0) { // 怪物金幣掉落量
            pc.addGF(-power.get_gf());
            name.append(" 怪物金幣掉落量-");
            name.append(power.get_gf());
            name.append("% ");
        }
        boolean death_exp = power.get_death_exp();
        if (death_exp) { // 死亡不噴經驗
            pc.set_death_exp(false);
            name.append(" 死亡不噴經驗效果消失");
        }
        boolean death_item = power.get_death_item();
        if (death_item) { // 死亡不噴道具
            pc.set_death_item(false);
            name.append(" 死亡不噴道具效果消失");
        }
        boolean death_skill = power.get_death_skill();
        if (death_skill) { // 死亡不噴技能
            pc.set_death_skill(false);
            name.append(" 死亡不噴技能效果消失");
        }
        boolean death_score = power.get_death_score();
        if (death_score) { // 死亡不噴積分
            pc.set_death_score(false);
            name.append(" 死亡不噴積分效果消失");
        }
        if (status) {
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else {
            if (status2) {
                pc.sendDetails();
                pc.sendPackets(new S_OwnCharStatus2(pc));
            }
            if (attr) {
                pc.sendPackets(new S_OwnCharAttrDef(pc));
            }
        }
        if (spmr) {
            pc.sendPackets(new S_SPMR(pc));
        }
        if (power.get_powermsg() != 0) {
            pc.sendPackets(new S_ServerMessage(name.toString()));
        }
        // 關閉圖示
        if (power.get_box_icon() != 0 && power.get_box_stringid() != 0) { // 自訂狀態圖示和訊息
            if (power.get_open_string() == 0) { // 有設狀態圖示和訊息的使用和登入是否顯示圖示裡面的訊息內容
                pc.sendPackets(new S_InventoryIcon(power.get_box_icon(), false, power.get_box_stringid(), -1)); // 重登不會有訊息
            } else {
                pc.sendPackets(new S_InventoryIcon(power.get_box_icon(), false, power.get_box_stringid(), power.get_box_stringid(), -1)); // 重登有訊息
            }
        }
    }
}
