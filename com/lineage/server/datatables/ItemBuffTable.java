package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.sql.CharBuffTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1ItemBuff;
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
 * 道具狀態系統
 */
public class ItemBuffTable {
    public static final Map<Integer, L1ItemBuff> _list = new HashMap<>();
    public static final Map<Integer, ArrayList<Integer>> _type = new HashMap<>();
    private static final Log _log = LogFactory.getLog(ItemBuffTable.class);
    private static ItemBuffTable _instance;

    private ItemBuffTable() {
        load();
    }

    public static ItemBuffTable get() {
        if (_instance == null) {
            _instance = new ItemBuffTable();
        }
        return _instance;
    }

    private void load() {
        Connection co = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pstm = co.prepareStatement("SELECT * FROM `其他_道具給予狀態`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("道具編號");
                final String startmsg = rs.getString("使用顯示文字");
                final String stopmsg = rs.getString("結束顯示文字");
                final int viplevel = rs.getInt("判斷VIP等級");
                final int type = rs.getInt("type");
                final int type_mod = rs.getInt("type_mod");
                final int buff_time = rs.getInt("狀態時間");
                final int buff_gfx = rs.getInt("特效編號");
                final int buff_iconid = rs.getInt("偵測圖檔tbl編號");
                final int buff_stringid = rs.getInt("偵測圖檔string編號");
                final int open_string = rs.getInt("登入是否顯示");
                final boolean buff_save = rs.getBoolean("是否登出保存");
                final int poly = rs.getInt("變身");
                final int str = rs.getInt("力量上限值");
                final int dex = rs.getInt("敏捷上限值");
                final int con = rs.getInt("體質上限值");
                final int intel = rs.getInt("智力上限值");
                final int wis = rs.getInt("精神上限值");
                final int cha = rs.getInt("魅力上限值");
                final int ac = rs.getInt("防禦上限值");
                final int hp = rs.getInt("血量上限值");
                final int mp = rs.getInt("魔量上限值");
                final int hpr = rs.getInt("回血量上限值");
                final int mpr = rs.getInt("回魔量上限值");
                final int mr = rs.getInt("抗魔上限值");
                final int sp = rs.getInt("魔攻上限值");
                final int dmg = rs.getInt("近戰傷害上限值");
                final int bow_dmg = rs.getInt("遠攻傷害上限值");
                final int hit = rs.getInt("近戰命中上限值");
                final int bow_hit = rs.getInt("遠攻命中上限值");
                final int dmg_r = rs.getInt("減免物理傷害上限值");
                final int magic_r = rs.getInt("減免魔法傷害上限值");
                final int fire = rs.getInt("抗火屬性上限值");
                final int wind = rs.getInt("抗風屬性上限值");
                final int earth = rs.getInt("抗地屬性上限值");
                final int water = rs.getInt("抗水屬性上限值");
                final int stun = rs.getInt("昏迷耐性上限值");
                final int stone = rs.getInt("石化耐性上限值");
                final int sleep = rs.getInt("睡眠耐性上限值");
                final int freeze = rs.getInt("寒冰耐性上限值");
                final int sustain = rs.getInt("支撑耐性上限值");
                final int blind = rs.getInt("暗黑耐性上限值");
                final int pvpdmg = rs.getInt("PVP傷害上限值");
                final int pvpdmg_r = rs.getInt("減免PVP傷害上限值");
                final int exp = rs.getInt("增加經驗");
                final int gf = rs.getInt("增加金幣倍數");
                boolean death_exp = rs.getBoolean("死亡不噴經驗"); // 死亡不噴經驗
                boolean death_item = rs.getBoolean("死亡不噴道具"); // 死亡不噴道具
                boolean death_skill = rs.getBoolean("死亡不噴技能"); // 死亡不噴技能
                boolean death_score = rs.getBoolean("死亡不噴積分"); // 死亡不噴積分
                final L1ItemBuff vip = new L1ItemBuff();
                vip.setStartMsg(startmsg);
                vip.setStopMsg(stopmsg);
                vip.setVipLevel(viplevel);
                vip.set_type(type);
                vip.set_type_mod(type_mod);
                vip.set_buff_time(buff_time);
                vip.set_buff_gfx(buff_gfx);
                vip.set_buff_iconid(buff_iconid);
                vip.set_buff_stringid(buff_stringid);
                vip.set_open_string(open_string);
                vip.set_buff_save(buff_save);
                vip.set_poly(poly);
                vip.set_type(type);
                vip.set_str(str);
                vip.set_dex(dex);
                vip.set_con(con);
                vip.set_intel(intel);
                vip.set_wis(wis);
                vip.set_cha(cha);
                vip.set_ac(ac);
                vip.set_hp(hp);
                vip.set_mp(mp);
                vip.set_hpr(hpr);
                vip.set_mpr(mpr);
                vip.set_mr(mr);
                vip.set_sp(sp);
                vip.set_dmg(dmg);
                vip.set_bow_dmg(bow_dmg);
                vip.set_hit(hit);
                vip.set_bow_hit(bow_hit);
                vip.set_dmg_r(dmg_r);
                vip.set_magic_r(magic_r);
                vip.set_fire(fire);
                vip.set_wind(wind);
                vip.set_earth(earth);
                vip.set_water(water);
                vip.set_stun(stun);
                vip.set_stone(stone);
                vip.set_sleep(sleep);
                vip.set_freeze(freeze);
                vip.set_sustain(sustain);
                vip.set_blind(blind);
                vip.set_exp(exp);
                vip.set_gf(gf);
                vip.set_pvpdmg(pvpdmg);
                vip.set_pvpdmg_r(pvpdmg_r);
                vip.set_death_exp(death_exp);
                vip.set_death_item(death_item);
                vip.set_death_skill(death_skill);
                vip.set_death_score(death_score);
                _list.put(item_id, vip);
                ArrayList<Integer> map = _type.get(type);
                if (map == null) {
                    map = new ArrayList<>();
                    map.add(item_id);
                    _type.put(type, map);
                } else {
                    map.add(item_id);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(co);
        }
        _log.info("讀取->[系統]_道具給予狀態系統數量: " + _list.size());
    }

    public boolean checkItem(final int item_id) {
        return _list.containsKey(item_id);
    }

    /**
     * 道具顯示信息
     *
     */
    public L1ItemBuff getUseEX(int item_id) {
        if (_list.isEmpty()) {
            return null;
        }
        if (_list.containsKey(item_id)) {
            return (L1ItemBuff) _list.get(item_id);
        } else {
            return null;
        }
    }

    public void checkBuffSave(final L1PcInstance pc) {
        for (final Integer skillId : _list.keySet()) {
            if (_list.get(skillId).is_buff_save()) {
                final int timeSec = pc.getSkillEffectTimeSec(skillId);
                if (timeSec > 0) {
                    final int poly_id = _list.get(skillId).get_poly();
                    CharBuffTable.storeBuff(pc.getId(), skillId, timeSec, poly_id);
                }
            }
        }
    }

    /**
     * 增加效果
     *
     */
    public boolean add(final L1PcInstance pc, final int item_id, final int buff_time) {
        if (!_list.containsKey(item_id)) {
            return false;
        }
        final L1ItemBuff value = _list.get(item_id);
        if (value.getVipLevel() != 0) { // 0不判斷VIP等級，0以上判斷VIP等級
            if (pc.get_vipLevel() < value.getVipLevel()) {
                pc.sendPackets(new S_ServerMessage("\\aDVIP[" + value.getVipLevel() + "]以上玩家才能使用此道具。"));
                return false;
            }
        }
        /*if (pc.hasSkillEffect(item_id)) {
            pc.sendPackets(new S_ServerMessage("\\aD道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(item_id)));
            return false;
        }*/
        if (pc.hasSkillEffect(item_id)) {
            if (value.get_type_mod() == 0) { // 設0提示剩餘時間 其它為覆蓋效果
                pc.sendPackets(new S_ServerMessage("\\aD道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(item_id)));
                return false;
            }
            pc.removeSkillEffect(item_id);
        }
        if (value.get_type() != 0) { // 0不判斷類別，0以上才判斷
            for (final Integer buff_id : _type.get(value.get_type())) {
                if (pc.hasSkillEffect(buff_id)) {
                    if (value.get_type_mod() == 0) { // 設0提示剩餘時間  其它為覆蓋效果
                        pc.sendPackets(new S_ServerMessage("\\aD同類道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(buff_id)));
                        return false;
                    }
                    pc.removeSkillEffect(buff_id);
                }
            }
        }
        if (buff_time != 0) {
            pc.setSkillEffect(item_id, buff_time * 1000);
            if (value.get_buff_iconid() == 0 && value.get_buff_stringid() == 0) { // 有自訂的狀態圖示和訊息時不顯示
                pc.sendPackets(new S_ServerMessage("\\aD道具狀態剩餘時間(秒): \\aE" + pc.getSkillEffectTimeSec(item_id))); // 重登時
            }
        } else {
            if (value.get_buff_gfx() != 0) { // 特效
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), value.get_buff_gfx()));
            }
            pc.setSkillEffect(item_id, value.get_buff_time() * 1000);
        }
        /*if (value.get_buff_iconid() != 0 && value.get_buff_stringid() != 0) { // 自訂狀態圖示和訊息
			pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), value.get_buff_stringid(), pc.getSkillEffectTimeSec(item_id)));
        }*/
        if (value.get_buff_iconid() != 0 && value.get_buff_stringid() != 0) { // 自訂狀態圖示和訊息
            if (value.get_open_string() == 0) { // 有設狀態圖示和訊息的使用和登入是否顯示圖示裡面的訊息內容
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), pc.getSkillEffectTimeSec(item_id))); // 重登不會有訊息
            } else {
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), value.get_buff_stringid(), pc.getSkillEffectTimeSec(item_id))); // 重登有訊息
            }
        }
        final boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;
        final StringBuilder name = new StringBuilder();
        if (value.get_poly() != -1) { // 變身
            L1PolyMorph.doPoly(pc, value.get_poly(), value.get_buff_time(), 1);
        }
        if (value.get_hp() != 0) { // 體力上限
            pc.addMaxHp(value.get_hp());
            name.append(" 體力上限+");
            name.append(value.get_hp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
        if (value.get_mp() != 0) { // 魔力上限
            pc.addMaxMp(value.get_mp());
            name.append(" 魔力上限+");
            name.append(value.get_mp());
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (value.get_hpr() != 0) { // 體力回復量
            pc.addHpr(value.get_hpr());
            name.append(" 體力回復量+");
            name.append(value.get_hpr());
        }
        if (value.get_mpr() != 0) { // 魔力回復量
            pc.addMpr(value.get_mpr());
            name.append(" 魔力回復量+");
            name.append(value.get_mpr());
        }
        if (value.get_ac() != 0) { // 防禦
            pc.addAc(-value.get_ac());
            name.append(" 防禦+");
            name.append(value.get_ac());
            attr = true;
        }
        if (value.get_sp() != 0) { // 魔法攻擊力
            pc.addSp(value.get_sp());
            name.append(" 魔攻+");
            name.append(value.get_sp());
            spmr = true;
        }
        if (value.get_mr() != 0) { // 額外魔法防禦
            pc.addMr(value.get_mr());
            name.append(" 額外魔法防禦+");
            name.append(value.get_mr());
            spmr = true;
        }
        if (value.get_str() != 0) { // 力量
            pc.addStr(value.get_str());
            name.append(" 力量+");
            name.append(value.get_str());
            status2 = true;
        }
        if (value.get_dex() != 0) { // 敏捷
            pc.addDex(value.get_dex());
            name.append(" 敏捷+");
            name.append(value.get_dex());
            status2 = true;
        }
        if (value.get_con() != 0) { // 體質
            pc.addCon(value.get_con());
            name.append(" 體質+");
            name.append(value.get_con());
            status2 = true;
        }
        if (value.get_wis() != 0) { // 精神
            pc.addWis(value.get_wis());
            name.append(" 精神+");
            name.append(value.get_wis());
            status2 = true;
        }
        if (value.get_intel() != 0) { // 智力
            pc.addInt(value.get_intel());
            name.append(" 智力+");
            name.append(value.get_intel());
            status2 = true;
        }
        if (value.get_cha() != 0) { // 魅力
            pc.addCha(value.get_cha());
            name.append(" 魅力+");
            name.append(value.get_cha());
            status2 = true;
        }
        if (value.get_dmg() != 0) { // 近遠距離攻擊
            pc.addDmgup(value.get_dmg());
            name.append(" 近遠距離攻擊+");
            name.append(value.get_dmg());
        }
        if (value.get_bow_dmg() != 0) { // 遠距離攻擊力
            pc.addBowDmgup(value.get_bow_dmg());
            name.append(" 遠距離攻擊力+");
            name.append(value.get_bow_dmg());
        }
        if (value.get_hit() != 0) { // 近距離命中
            pc.addHitup(value.get_hit());
            name.append(" 近距離命中+");
            name.append(value.get_hit());
        }
        if (value.get_bow_hit() != 0) { // 遠距離命中
            pc.addBowHitup(value.get_bow_hit());
            name.append(" 遠距離命中+");
            name.append(value.get_bow_hit());
        }
        if (value.get_dmg_r() != 0) { // 物理傷害減免
            pc.addDamageReductionByArmor(value.get_dmg_r());
            name.append(" 物理傷害減免+");
            name.append(value.get_dmg_r());
        }
        if (value.get_magic_r() != 0) { // 魔法傷害減免
            pc.add_magic_reduction_dmg(value.get_magic_r());
            name.append(" 魔法傷害減免+");
            name.append(value.get_magic_r());
        }
        if (value.get_water() != 0) { // 水屬性抗性
            pc.addWater(value.get_water());
            name.append(" 水屬性抗性+");
            name.append(value.get_water());
            attr = true;
        }
        if (value.get_fire() != 0) { // 火屬性抗性
            pc.addFire(value.get_fire());
            name.append(" 火屬性抗性+");
            name.append(value.get_fire());
            attr = true;
        }
        if (value.get_wind() != 0) { // 風屬性抗性
            pc.addWind(value.get_wind());
            name.append(" 風屬性抗性+");
            name.append(value.get_wind());
            attr = true;
        }
        if (value.get_earth() != 0) { // 地屬性抗性
            pc.addEarth(value.get_earth());
            name.append(" 地屬性抗性+");
            name.append(value.get_earth());
            attr = true;
        }
        if (value.get_stun() != 0) { // 昏迷耐性
            pc.addRegistStun(value.get_stun());
            name.append(" 昏迷耐性+");
            name.append(value.get_stun());
        }
        if (value.get_stone() != 0) { // 石化耐性
            pc.addRegistStone(value.get_stone());
            name.append(" 石化耐性+");
            name.append(value.get_stone());
        }
        if (value.get_sleep() != 0) { // 睡眠耐性
            pc.addRegistSleep(value.get_sleep());
            name.append(" 睡眠耐性+");
            name.append(value.get_sleep());
        }
        if (value.get_freeze() != 0) { // 寒冰耐性
            pc.add_regist_freeze(value.get_freeze());
            name.append(" 寒冰耐性+");
            name.append(value.get_freeze());
        }
        if (value.get_sustain() != 0) { // 支撐耐性
            pc.addRegistSustain(value.get_sustain());
            name.append(" 支撐耐性+");
            name.append(value.get_sustain());
        }
        if (value.get_blind() != 0) { // 暗黑耐性
            pc.addRegistBlind(value.get_blind());
            name.append(" 暗黑耐性+");
            name.append(value.get_blind());
        }
        if (value.get_pvpdmg() != 0) { // 增加PVP傷害
            pc.setPvpDmg(value.get_pvpdmg());
            name.append(" PVP傷害+");
            name.append(value.get_pvpdmg());
        }
        if (value.get_pvpdmg_r() != 0) { // 減免PVP傷害
            pc.setPvpDmg_R(value.get_pvpdmg_r());
            name.append(" PVP減免+");
            name.append(value.get_pvpdmg_r());
        }
        if (value.get_exp() != 0) { // 狩獵經驗值
            pc.addExpRateToPc(value.get_exp());
            name.append(" 狩獵經驗值+");
            name.append(value.get_exp());
            name.append("% ");
        }
        if (value.get_gf() != 0) { // 怪物金幣掉落量
            pc.addGF(value.get_gf());
            name.append(" 怪物金幣掉落量+");
            name.append(value.get_gf());
            name.append("% ");
        }
        boolean death_exp = value.get_death_exp();
        if (death_exp) { // 死亡不噴經驗
            pc.set_death_exp(true);
            name.append(" 死亡不噴經驗");
        }
        boolean death_item = value.get_death_item();
        if (death_item) { // 死亡不噴道具
            pc.set_death_item(true);
            name.append(" 死亡不噴道具");
        }
        boolean death_skill = value.get_death_skill();
        if (death_skill) { // 死亡不噴技能
            pc.set_death_skill(true);
            name.append(" 死亡不噴技能");
        }
        boolean death_score = value.get_death_score();
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
        if (value.getStartMsg() != null) { // 空值時什麼都不顯示
            pc.sendPackets(new S_ServerMessage(value.getStartMsg())); // 自設訊息
        } else {
            pc.sendPackets(new S_ServerMessage(name.toString())); // 預設訊息
        }
        return true;
    }

    /**
     * 移除效果
     *
     */
    public void remove(final L1PcInstance pc, final int item_id) {
        final L1ItemBuff value = _list.get(item_id);
        final boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;
        final StringBuilder name = new StringBuilder();
        if (value.get_hp() != 0) { // 體力上限
            pc.addMaxHp(-value.get_hp());
            name.append(" 體力上限-");
            name.append(value.get_hp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
        if (value.get_mp() != 0) { // 魔力上限
            pc.addMaxMp(-value.get_mp());
            name.append(" 魔力上限-");
            name.append(value.get_mp());
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (value.get_hpr() != 0) { // 體力回復量
            pc.addHpr(-value.get_hpr());
            name.append(" 體力回復量-");
            name.append(value.get_hpr());
        }
        if (value.get_mpr() != 0) { // 魔力回復量
            pc.addMpr(-value.get_mpr());
            name.append(" 魔力回復量-");
            name.append(value.get_mpr());
        }
        if (value.get_ac() != 0) { // 防禦
            pc.addAc(value.get_ac());
            name.append(" 防禦-");
            name.append(value.get_ac());
            attr = true;
        }
        if (value.get_sp() != 0) { // 魔法攻擊力
            pc.addSp(-value.get_sp());
            name.append(" 魔攻-");
            name.append(value.get_sp());
            spmr = true;
        }
        if (value.get_mr() != 0) { // 額外魔法防禦
            pc.addMr(-value.get_mr());
            name.append(" 魔防-");
            name.append(value.get_mr());
            spmr = true;
        }
        if (value.get_str() != 0) { // 力量
            pc.addStr(-value.get_str());
            name.append(" 力量-");
            name.append(value.get_str());
            status2 = true;
        }
        if (value.get_dex() != 0) { // 敏捷
            pc.addDex(-value.get_dex());
            name.append(" 敏捷-");
            name.append(value.get_dex());
            status2 = true;
        }
        if (value.get_con() != 0) { // 體質
            pc.addCon(-value.get_con());
            name.append(" 體質-");
            name.append(value.get_con());
            status2 = true;
        }
        if (value.get_wis() != 0) { // 精神
            pc.addWis(-value.get_wis());
            name.append(" 精神-");
            name.append(value.get_wis());
            status2 = true;
        }
        if (value.get_intel() != 0) { // 智力
            pc.addInt(-value.get_intel());
            name.append(" 智力-");
            name.append(value.get_intel());
            status2 = true;
        }
        if (value.get_cha() != 0) { // 魅力
            pc.addCha(-value.get_cha());
            name.append(" 魅力-");
            name.append(value.get_cha());
            status2 = true;
        }
        if (value.get_dmg() != 0) { // 近遠距離攻擊
            pc.addDmgup(-value.get_dmg());
            name.append(" 近遠距離攻擊-");
            name.append(value.get_dmg());
        }
        if (value.get_bow_dmg() != 0) { // 遠距離攻擊力
            pc.addBowDmgup(-value.get_bow_dmg());
            name.append(" 遠距離攻擊力-");
            name.append(value.get_bow_dmg());
        }
        if (value.get_hit() != 0) { // 近距離命中
            pc.addHitup(-value.get_hit());
            name.append(" 近距離命中-");
            name.append(value.get_hit());
        }
        if (value.get_bow_hit() != 0) { // 遠距離命中
            pc.addBowHitup(-value.get_bow_hit());
            name.append(" 遠距離命中-");
            name.append(value.get_bow_hit());
        }
        if (value.get_dmg_r() != 0) { // 物理傷害減免
            pc.addDamageReductionByArmor(-value.get_dmg_r());
            name.append(" 物理傷害減免-");
            name.append(value.get_dmg_r());
        }
        if (value.get_magic_r() != 0) { // 魔法傷害減免
            pc.add_magic_reduction_dmg(-value.get_magic_r());
            name.append(" 魔法傷害減免-");
            name.append(value.get_magic_r());
        }
        if (value.get_water() != 0) { // 水屬性抗性
            pc.addWater(-value.get_water());
            name.append(" 水屬性抗性-");
            name.append(value.get_water());
            attr = true;
        }
        if (value.get_fire() != 0) { // 火屬性抗性
            pc.addFire(-value.get_fire());
            name.append(" 火屬性抗性-");
            name.append(value.get_fire());
            attr = true;
        }
        if (value.get_wind() != 0) { // 風屬性抗性
            pc.addWind(-value.get_wind());
            name.append(" 風屬性抗性-");
            name.append(value.get_wind());
            attr = true;
        }
        if (value.get_earth() != 0) { // 地屬性抗性
            pc.addEarth(-value.get_earth());
            name.append(" 地屬性抗性-");
            name.append(value.get_earth());
            attr = true;
        }
        if (value.get_stun() != 0) { // 昏迷耐性
            pc.addRegistStun(-value.get_stun());
            name.append(" 昏迷耐性-");
            name.append(value.get_stun());
        }
        if (value.get_stone() != 0) { // 石化耐性
            pc.addRegistStone(-value.get_stone());
            name.append(" 石化耐性-");
            name.append(value.get_stone());
        }
        if (value.get_sleep() != 0) { // 睡眠耐性
            pc.addRegistSleep(-value.get_sleep());
            name.append(" 睡眠耐性-");
            name.append(value.get_sleep());
        }
        if (value.get_freeze() != 0) { // 寒冰耐性
            pc.add_regist_freeze(-value.get_freeze());
            name.append(" 寒冰耐性-");
            name.append(value.get_freeze());
        }
        if (value.get_sustain() != 0) { // 支撐耐性
            pc.addRegistSustain(-value.get_sustain());
            name.append(" 支撐耐性-");
            name.append(value.get_sustain());
        }
        if (value.get_blind() != 0) { // 暗黑耐性
            pc.addRegistBlind(-value.get_blind());
            name.append(" 暗黑耐性-");
            name.append(value.get_blind());
        }
        if (value.get_pvpdmg() != 0) { // 增加PVP傷害
            pc.setPvpDmg(-value.get_pvpdmg());
            name.append(" PVP傷害-");
            name.append(value.get_pvpdmg());
        }
        if (value.get_pvpdmg_r() != 0) { // 減免PVP傷害
            pc.setPvpDmg_R(-value.get_pvpdmg_r());
            name.append(" PVP減免-");
            name.append(value.get_pvpdmg_r());
        }
        if (value.get_exp() != 0) { // 狩獵經驗值
            pc.addExpRateToPc(-value.get_exp());
            name.append(" 狩獵經驗值-");
            name.append(value.get_exp());
            name.append("% ");
        }
        if (value.get_gf() != 0) { // 怪物金幣掉落量
            pc.addGF(-value.get_gf());
            name.append(" 怪物金幣掉落量-");
            name.append(value.get_gf());
            name.append("% ");
        }
        boolean death_exp = value.get_death_exp();
        if (death_exp) { // 死亡不噴經驗
            pc.set_death_exp(false);
            name.append(" 死亡不噴經驗效果消失");
        }
        boolean death_item = value.get_death_item();
        if (death_item) { // 死亡不噴道具
            pc.set_death_item(false);
            name.append(" 死亡不噴道具效果消失");
        }
        boolean death_skill = value.get_death_skill();
        if (death_skill) { // 死亡不噴技能
            pc.set_death_skill(false);
            name.append(" 死亡不噴技能效果消失");
        }
        boolean death_score = value.get_death_score();
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
        if (value.getStopMsg() != null) { // 空值時什麼都不顯示
            pc.sendPackets(new S_ServerMessage(value.getStopMsg())); // 自設訊息
        } else {
            pc.sendPackets(new S_ServerMessage(name.toString())); // 預設訊息
        }
    }
}
