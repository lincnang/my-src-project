package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SkinInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.*;
import com.lineage.server.templates.L1ItemVIP;
import com.lineage.server.utils.L1SpawnUtil;
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
 * 道具VIP系統管理類別。
 * 此類負責從資料庫載入VIP道具資料，並對玩家角色應用或移除VIP道具的效果。
 */
public class ItemVIPTable {
    // 日誌記錄器，用於記錄錯誤和資訊訊息
    private static final Log _log = LogFactory.getLog(ItemVIPTable.class);

    // 用於儲存VIP道具資料，鍵為道具ID，值為L1ItemVIP物件
    private static final Map<Integer, L1ItemVIP> _VIPList = new HashMap<>();

    // 單例實例
    private static ItemVIPTable _instance;

    /**
     * 私有建構子，防止外部實例化。
     * 在建構時自動載入VIP道具資料。
     */
    private ItemVIPTable() {
        load();
    }

    /**
     * 獲取ItemVIPTable的單例實例。
     *
     * @return ItemVIPTable的單例實例
     */
    public static ItemVIPTable get() {
        if (_instance == null) {
            _instance = new ItemVIPTable();
        }
        return _instance;
    }

    /**
     * 從資料庫載入所有VIP道具資料並存入_VIPList。
     */
    private void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `其他_vip道具系統`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("道具編號");
                final int type = rs.getInt("屬性");
                final int gif = rs.getInt("特效編號");
                final int gif_time = rs.getInt("特效秒數");
                final int add_wmd = rs.getInt("魔武傷害%");
                final int add_wmc = rs.getInt("魔武發動機率");
                final int add_str = rs.getInt("力量");
                final int add_dex = rs.getInt("敏捷");
                final int add_con = rs.getInt("體質");
                final int add_int = rs.getInt("智力");
                final int add_wis = rs.getInt("精神");
                final int add_cha = rs.getInt("魅力");
                final int add_ac = rs.getInt("防禦");
                final int add_hp = rs.getInt("血量");
                final int add_mp = rs.getInt("魔量");
                final int add_hpr = rs.getInt("回血量");
                final int add_mpr = rs.getInt("回魔量");
                final int add_dmg = rs.getInt("近戰傷害");
                final int add_hit = rs.getInt("近戰命中率");
                final int add_bow_dmg = rs.getInt("遠攻傷害");
                final int add_bow_hit = rs.getInt("遠攻命中率");
                final int add_dmg_r = rs.getInt("物理傷害減免");
                final int add_magic_r = rs.getInt("魔法傷害減免");
                final int add_mr = rs.getInt("魔防");
                final int add_sp = rs.getInt("魔攻");
                final int add_fire = rs.getInt("火屬性");
                final int add_wind = rs.getInt("風屬性");
                final int add_earth = rs.getInt("地屬性");
                final int add_water = rs.getInt("水屬性");
                final int add_stun = rs.getInt("昏迷耐性");
                final int add_stone = rs.getInt("石化耐性");
                final int add_sleep = rs.getInt("睡眠耐性");
                final int add_freeze = rs.getInt("寒冰耐性");
                final int add_sustain = rs.getInt("支撑耐性");
                final int add_blind = rs.getInt("暗黑耐性");
                final int add_exp = rs.getInt("經驗%");
                final int add_adena = rs.getInt("金幣倍數%");
                final int skin_id = rs.getInt("光環編號");
                final boolean death_exp = rs.getBoolean("死亡不噴經驗");
                final boolean death_item = rs.getBoolean("死亡不噴道具");
                final boolean death_skill = rs.getBoolean("死亡不噴技能");
                final boolean death_score = rs.getBoolean("死亡不噴積分");
                final int add_potion = rs.getInt("藥水恢復");
                final int add_PVP = rs.getInt("PVP攻擊");
                final int add_PVP_R = rs.getInt("PVP減免");
                final int add_magic_hit = rs.getInt("魔法命中率");
                final int add_StunLv = rs.getInt("昏迷命中");
                final int add_CloseCritical = rs.getInt("近距離爆擊");
                final int add_BowCritical = rs.getInt("遠距離爆擊");
                final int add_DiceDmg = rs.getInt("決勝打擊_機率");
                final int add_DiceDmg_dmherm = rs.getInt("決勝打擊_傷害");
                final int add_PVE = rs.getInt("PVE_攻擊");
                final int add_PVE_R = rs.getInt("PVE_減免");
                final int add_dice_hp = rs.getInt("HP吸收");
                final int add_sucking_hp = rs.getInt("HP吸收機率");
                final int add_dice_mp = rs.getInt("MP吸收");
                final int add_sucking_mp = rs.getInt("MP吸收機率");

                final L1ItemVIP vip = new L1ItemVIP();
                vip.set_type(type);
                vip.set_gif(gif);
                vip.set_gif_time(gif_time);
                vip.set_add_wmd(add_wmd);
                vip.set_add_wmc(add_wmc);
                vip.set_add_str(add_str);
                vip.set_add_dex(add_dex);
                vip.set_add_con(add_con);
                vip.set_add_int(add_int);
                vip.set_add_wis(add_wis);
                vip.set_add_cha(add_cha);
                vip.set_add_ac(add_ac);
                vip.set_add_hp(add_hp);
                vip.set_add_mp(add_mp);
                vip.set_add_hpr(add_hpr);
                vip.set_add_mpr(add_mpr);
                vip.set_add_dmg(add_dmg);
                vip.set_add_hit(add_hit);
                vip.set_add_bow_dmg(add_bow_dmg);
                vip.set_add_bow_hit(add_bow_hit);
                vip.set_add_dmg_r(add_dmg_r);
                vip.set_add_magic_r(add_magic_r);
                vip.set_add_mr(add_mr);
                vip.set_add_sp(add_sp);
                vip.set_add_fire(add_fire);
                vip.set_add_wind(add_wind);
                vip.set_add_earth(add_earth);
                vip.set_add_water(add_water);
                vip.set_add_stun(add_stun);
                vip.set_add_stone(add_stone);
                vip.set_add_sleep(add_sleep);
                vip.set_add_freeze(add_freeze);
                vip.set_add_sustain(add_sustain);
                vip.set_add_blind(add_blind);
                vip.set_add_exp(add_exp);
                vip.set_add_adena(add_adena);
                vip.set_skin_id(skin_id);
                vip.set_death_exp(death_exp);
                vip.set_death_item(death_item);
                vip.set_death_skill(death_skill);
                vip.set_death_score(death_score);
                vip.set_add_potion(add_potion);
                vip.set_add_PVP(add_PVP);
                vip.set_add_PVP_R(add_PVP_R);
                vip.set_magic_hit(add_magic_hit);
                vip.set_StunLevel(add_StunLv);
                vip.set_CloseCritical(add_CloseCritical);
                vip.set_BowCritical(add_BowCritical);
                vip.set_DiceDmg(add_DiceDmg);
                vip.set_DiceDmg_dmherm(add_DiceDmg_dmherm);
                vip.set_DamageReductionPVE(add_PVE);
                vip.set_DamageReductionPVE_R(add_PVE_R);
                vip.set_dice_hp(add_dice_hp);
                vip.set_sucking_hp(add_sucking_hp);
                vip.set_dice_mp(add_dice_mp);
                vip.set_sucking_mp(add_sucking_mp);

                _VIPList.put(item_id, vip);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->_vip道具系統數量: " + _VIPList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 根據道具ID取得對應的L1ItemVIP物件。
     *
     * @param item_id 道具ID
     * @return 對應的L1ItemVIP物件，若不存在則返回null
     */
    public L1ItemVIP getVIP(final int item_id) {
        if (_VIPList.isEmpty()) {
            return null;
        }
        return _VIPList.get(item_id);
    }

    /**
     * 檢查指定道具ID是否為VIP道具。
     *
     * @param item_id 道具ID
     * @return 若是VIP道具則返回true，否則返回false
     */
    public boolean checkVIP(final int item_id) {
        return _VIPList.containsKey(item_id);
    }

    /**
     * 為玩家角色添加VIP道具的效果。
     * 根據VIP道具的屬性，修改玩家角色的各項能力值，並發送相應的狀態更新封包。
     *
     * @param pc      玩家角色實例
     * @param item_id 道具ID
     */
    public void addItemVIP(final L1PcInstance pc, final int item_id) {
        if (_VIPList.isEmpty() || !_VIPList.containsKey(item_id)) {
            return;
        }
        final L1ItemVIP vip = _VIPList.get(item_id);
        boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;

        // 根據VIP道具的各項屬性，修改玩家角色的能力值
        final int add_wmd = vip.get_add_wmd();
        if (add_wmd != 0) {
            pc.addweaponMD(add_wmd);
            status2 = true;
        }

        final int add_wmc = vip.get_add_wmc();
        if (add_wmc != 0) {
            pc.addweaponMDC(add_wmc);
            status2 = true;
        }

        final int add_str = vip.get_add_str();
        if (add_str != 0) {
            pc.addStr(add_str);
            status2 = true;
        }

        final int add_dex = vip.get_add_dex();
        if (add_dex != 0) {
            pc.addDex(add_dex);
            status2 = true;
        }

        final int add_con = vip.get_add_con();
        if (add_con != 0) {
            pc.addCon(add_con);
            status2 = true;
        }

        final int add_int = vip.get_add_int();
        if (add_int != 0) {
            pc.addInt(add_int);
            status2 = true;
        }

        final int add_wis = vip.get_add_wis();
        if (add_wis != 0) {
            pc.addWis(add_wis);
            status2 = true;
        }

        final int add_cha = vip.get_add_cha();
        if (add_cha != 0) {
            pc.addCha(add_cha);
            status2 = true;
        }

        final int add_ac = vip.get_add_ac();
        if (add_ac != 0) {
            pc.addAc(-add_ac);
            attr = true;
        }

        final int add_hp = vip.get_add_hp();
        if (add_hp != 0) {
            pc.addMaxHp(add_hp);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }

        final int add_mp = vip.get_add_mp();
        if (add_mp != 0) {
            pc.addMaxMp(add_mp);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }

        final int add_hpr = vip.get_add_hpr();
        if (add_hpr != 0) {
            pc.addHpr(add_hpr);
        }

        final int add_mpr = vip.get_add_mpr();
        if (add_mpr != 0) {
            pc.addMpr(add_mpr);
        }

        final int add_dmg = vip.get_add_dmg();
        if (add_dmg != 0) {
            pc.addDmgup(add_dmg);
        }

        final int add_hit = vip.get_add_hit();
        if (add_hit != 0) {
            pc.addHitup(add_hit);
        }

        final int add_bow_dmg = vip.get_add_bow_dmg();
        if (add_bow_dmg != 0) {
            pc.addBowDmgup(add_bow_dmg);
        }

        final int add_bow_hit = vip.get_add_bow_hit();
        if (add_bow_hit != 0) {
            pc.addBowHitup(add_bow_hit);
        }

        final int add_dmg_r = vip.get_add_dmg_r();
        if (add_dmg_r != 0) {
            pc.addDamageReductionByArmor(add_dmg_r);
        }

        final int add_magic_r = vip.get_add_magic_r();
        if (add_magic_r != 0) {
            pc.add_magic_reduction_dmg(add_magic_r);
        }

        final int add_mr = vip.get_add_mr();
        if (add_mr != 0) {
            pc.addMr(add_mr);
            spmr = true;
        }

        final int add_sp = vip.get_add_sp();
        if (add_sp != 0) {
            pc.addSp(add_sp);
            spmr = true;
        }

        final int add_fire = vip.get_add_fire();
        if (add_fire != 0) {
            pc.addFire(add_fire);
            attr = true;
        }

        final int add_wind = vip.get_add_wind();
        if (add_wind != 0) {
            pc.addWind(add_wind);
            attr = true;
        }

        final int add_earth = vip.get_add_earth();
        if (add_earth != 0) {
            pc.addEarth(add_earth);
            attr = true;
        }

        final int add_water = vip.get_add_water();
        if (add_water != 0) {
            pc.addWater(add_water);
            attr = true;
        }

        final int add_stun = vip.get_add_stun();
        if (add_stun != 0) {
            pc.addRegistStun(add_stun);
        }

        final int add_stone = vip.get_add_stone();
        if (add_stone != 0) {
            pc.addRegistStone(add_stone);
        }

        final int add_sleep = vip.get_add_sleep();
        if (add_sleep != 0) {
            pc.addRegistSleep(add_sleep);
        }

        final int add_freeze = vip.get_add_freeze();
        if (add_freeze != 0) {
            pc.add_regist_freeze(add_freeze);
        }

        final int add_sustain = vip.get_add_sustain();
        if (add_sustain != 0) {
            pc.addRegistSustain(add_sustain);
        }

        final int add_blind = vip.get_add_blind();
        if (add_blind != 0) {
            pc.addRegistBlind(add_blind);
        }

        if (vip.get_skin_id() != 0) {
            final L1SkinInstance skin = L1SpawnUtil.spawnSkin(pc, vip.get_skin_id());
            if (skin != null) {
                skin.setMoveType(1);
                pc.addSkin(skin, vip.get_skin_id());
            }
        }

        final boolean death_exp = vip.get_death_exp();
        if (death_exp) {
            pc.set_death_exp(true);
        }
        final boolean death_item = vip.get_death_item();
        if (death_item) {
            pc.set_death_item(true);
        }
        final boolean death_skill = vip.get_death_skill();
        if (death_skill) {
            pc.set_death_skill(true);
        }
        final boolean death_score = vip.get_death_score();
        if (death_score) {
            pc.set_death_score(true);
        }

        final int add_exp = vip.get_add_exp();
        if (add_exp != 0) {
            pc.addExpRateToPc(add_exp);
        }

        final int add_adena = vip.get_add_adena();
        if (add_adena != 0) {
            pc.addGF(add_adena);
        }

        final int add_potion = vip.get_up_hp_potion();
        if (add_potion != 0) {
            pc.add_up_hp_potion(add_potion);
        }
        final int add_PVP = vip.getPvpDmg();
        if (add_PVP != 0) {
            pc.setPvpDmg(add_PVP);
        }
        final int add_PVP_R = vip.getPvpDmg_R();
        if (add_PVP_R != 0) {
            pc.setPvpDmg_R(add_PVP_R);
        }
        final int add_magic_hit = vip.getOriginalMagicHit();
        if (add_magic_hit != 0) {
            pc.addOriginalMagicHit(add_magic_hit);
        }
        final int add_StunLv = vip.getStunLevel();
        if (add_StunLv != 0) {
            pc.addStunLevel(add_StunLv);
        }
        final int add_CloseCritical = vip.getCloseCritical();
        if (add_CloseCritical != 0) {
            pc.addCloseCritical(add_CloseCritical);
        }
        final int add_BowCritical = vip.getBowCritical();
        if (add_BowCritical != 0) {
            pc.addBowCritical(add_BowCritical);
        }

        final int add_DiceDmg = vip.getDiceDmg();
        final int add_DiceDmg_dmherm = vip.getDmg();
        pc.set_dmgAdd(add_DiceDmg_dmherm, add_DiceDmg);

        final int add_DamageReductionPVE = vip.get_DamageReductionPVE();
        if (add_DamageReductionPVE != 0) {
            pc.addDamageReductionPVE(add_DamageReductionPVE);
        }
        final int add_DamageReductionPVE_R = vip.get_DamageReductionPVE_R();
        if (add_DamageReductionPVE_R != 0) {
            pc.adddollDamageReductionByArmor(add_DamageReductionPVE_R);
        }
        final int add_dice_hp = vip.get_dice_hp();
        final int _sucking_hp = vip.get_sucking_hp();
        pc.add_dice_hp(add_dice_hp, _sucking_hp);

        final int add_dice_mp = vip.get_dice_mp();
        final int _sucking_mp = vip.get_sucking_mp();
        pc.add_dice_mp(add_dice_mp, _sucking_mp);
        pc.setVipDiceChance(vip.getDiceDmg());
        pc.setVipDiceDamage(vip.getDmg());

        // 1. 取得角色的總屬性 (基礎屬性 + 裝備/道具加成)
        // 這些 getter 方法已經返回了所有加成後的總值
        final int totalStr = pc.getStr();
        final int totalDex = pc.getDex();
        final int totalInt = pc.getInt();
        final int totalWis = pc.getWis();
        final int totalCon = pc.getCon();

        // 2. 根據總屬性計算衍生屬性 (力量)
        // 這裡直接使用 L1ClassFeature 的方法，該方法需要總屬性和基礎屬性作為參數
        final int calculatedMeleeDmg = L1ClassFeature.calcStrDmg(totalStr, pc.getBaseStr());
        final int calculatedMeleeHit = L1ClassFeature.calcStrHit(totalStr, pc.getBaseStr());
        final int calculatedMeleeCritical = L1ClassFeature.calcStrDmgCritical(totalStr, pc.getBaseStr());
        final int calculatedMaxWeight = (int) L1ClassFeature.calcAbilityMaxWeight(totalStr, totalCon);

        // 3. 根據總屬性計算衍生屬性 (敏捷)
        final int calculatedBowDmg = L1ClassFeature.calcDexDmg(totalDex, pc.getBaseDex());
        final int calculatedBowHit = L1ClassFeature.calcDexHit(totalDex, pc.getBaseDex());
        final int calculatedBowCritical = L1ClassFeature.calcDexDmgCritical(totalDex, pc.getBaseDex());
        final int calculatedAc = L1ClassFeature.calcDexAc(totalDex);
        final int calculatedEr = L1ClassFeature.calcDexEr(totalDex);

        // 4. 根據總屬性計算衍生屬性 (智力)
        final int calculatedMagicDmg = L1ClassFeature.calcIntMagicDmg(totalInt, pc.getBaseInt());
        final int calculatedMagicHit = L1ClassFeature.calcIntMagicHit(totalInt, pc.getBaseInt());
        final int calculatedMagicCritical = L1ClassFeature.calcIntMagicCritical(totalInt, pc.getBaseInt());
        final int calculatedMagicBonus = L1ClassFeature.calcIntMagicBonus(pc.getType(), totalInt);
        final int calculatedMagicConsumeReduction = L1ClassFeature.calcIntMagicConsumeReduction(totalInt);

        // 5. 根據總屬性計算衍生屬性 (體質 & 精神)
        final int calculatedHPRecovery = L1ClassFeature.calcConHpr(totalCon, pc.getBaseCon());
        final int calculatedManaMpr = L1ClassFeature.calcWisMpr(totalWis, pc.getBaseWis());
        final int calculatedMr = L1ClassFeature.calcStatMr(totalWis) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr();

        // 升等HP/MP增加 (這部分通常是固定的，或者與等級有關，與當前屬性無關)
        final int calculatedLevelUpHP = L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon());
        final int calculatedLevelUpMP = L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())[0];
        final int[] mpUpRange = L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis());

        // 6. 發送封包更新介面
        pc.sendPackets(new S_StrDetails(2, calculatedMeleeDmg, calculatedMeleeHit, calculatedMeleeCritical, calculatedMaxWeight));

        pc.sendPackets(new S_DexDetails(2, calculatedBowDmg, calculatedBowHit, calculatedBowCritical, calculatedAc, calculatedEr));

        pc.sendPackets(new S_IntDetails(2, calculatedMagicDmg, calculatedMagicHit, calculatedMagicCritical, calculatedMagicBonus, calculatedMagicConsumeReduction));

        final int calculatedPotionHpr = pc.get_up_hp_potion(); // 這裡的值是直接加成，不需計算
        pc.sendPackets(new S_ConDetails(2, calculatedHPRecovery, calculatedPotionHpr, calculatedMaxWeight, calculatedLevelUpHP, pc.getMaxHp()));

        pc.sendPackets(new S_WisDetails(2, calculatedManaMpr, calculatedManaMpr, calculatedMr, mpUpRange, pc.getMaxMp()));

        // 7. 發送原始的通用封包來觸發介面更新
        if (status) {
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else {
            if (status2) {
                pc.sendPackets(new S_OwnCharStatus2(pc));
            }
            if (attr) {
                pc.sendPackets(new S_OwnCharAttrDef(pc));
            }
        }
        if (spmr) {
            pc.sendPackets(new S_SPMR(pc));
        }

        pc.sendPackets(new S_BaseAbilityDetails(25));
        pc.sendPackets(new S_BaseAbilityDetails(35));
        pc.sendPackets(new S_BaseAbilityDetails(45));
    }

    /**
     * 為玩家角色移除VIP道具的效果。
     * 根據VIP道具的屬性，恢復玩家角色的各項能力值，並發送相應的狀態更新封包。
     *
     * @param pc      玩家角色實例
     * @param item_id 道具ID
     */
    public void deleItemVIP(final L1PcInstance pc, final int item_id) {
        if (!_VIPList.containsKey(item_id)) {
            return;
        }
        final L1ItemVIP vip = _VIPList.get(item_id);
        boolean status = false;
        boolean status2 = false;
        boolean spmr = false;
        boolean attr = false;

        // 根據VIP道具的各項屬性，恢復玩家角色的能力值
        final int add_wmd = vip.get_add_wmd();
        if (add_wmd != 0) {
            pc.addweaponMD(-add_wmd);
            status2 = true;
        }

        final int add_wmc = vip.get_add_wmc();
        if (add_wmc != 0) {
            pc.addweaponMDC(-add_wmc);
            status2 = true;
        }

        final int add_str = vip.get_add_str();
        if (add_str != 0) {
            pc.addStr(-add_str);
            status2 = true;
        }

        final int add_dex = vip.get_add_dex();
        if (add_dex != 0) {
            pc.addDex(-add_dex);
            status2 = true;
        }

        final int add_con = vip.get_add_con();
        if (add_con != 0) {
            pc.addCon(-add_con);
            status2 = true;
        }

        final int add_int = vip.get_add_int();
        if (add_int != 0) {
            pc.addInt(-add_int);
            status2 = true;
        }

        final int add_wis = vip.get_add_wis();
        if (add_wis != 0) {
            pc.addWis(-add_wis);
            status2 = true;
        }

        final int add_cha = vip.get_add_cha();
        if (add_cha != 0) {
            pc.addCha(-add_cha);
            status2 = true;
        }

        final int add_ac = vip.get_add_ac();
        if (add_ac != 0) {
            pc.addAc(add_ac);
            attr = true;
        }

        final int add_hp = vip.get_add_hp();
        if (add_hp != 0) {
            pc.addMaxHp(-add_hp);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }

        final int add_mp = vip.get_add_mp();
        if (add_mp != 0) {
            pc.addMaxMp(-add_mp);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }

        final int add_hpr = vip.get_add_hpr();
        if (add_hpr != 0) {
            pc.addHpr(-add_hpr);
        }

        final int add_mpr = vip.get_add_mpr();
        if (add_mpr != 0) {
            pc.addMpr(-add_mpr);
        }

        final int add_dmg = vip.get_add_dmg();
        if (add_dmg != 0) {
            pc.addDmgup(-add_dmg);
        }

        final int add_hit = vip.get_add_hit();
        if (add_hit != 0) {
            pc.addHitup(-add_hit);
        }

        final int add_bow_dmg = vip.get_add_bow_dmg();
        if (add_bow_dmg != 0) {
            pc.addBowDmgup(-add_bow_dmg);
        }

        final int add_bow_hit = vip.get_add_bow_hit();
        if (add_bow_hit != 0) {
            pc.addBowHitup(-add_bow_hit);
        }

        final int add_dmg_r = vip.get_add_dmg_r();
        if (add_dmg_r != 0) {
            pc.addDamageReductionByArmor(-add_dmg_r);
        }

        final int add_magic_r = vip.get_add_magic_r();
        if (add_magic_r != 0) {
            pc.add_magic_reduction_dmg(-add_magic_r);
        }

        final int add_mr = vip.get_add_mr();
        if (add_mr != 0) {
            pc.addMr(-add_mr);
            spmr = true;
        }

        final int add_sp = vip.get_add_sp();
        if (add_sp != 0) {
            pc.addSp(-add_sp);
            spmr = true;
        }

        final int add_fire = vip.get_add_fire();
        if (add_fire != 0) {
            pc.addFire(-add_fire);
            attr = true;
        }

        final int add_wind = vip.get_add_wind();
        if (add_wind != 0) {
            pc.addWind(-add_wind);
            attr = true;
        }

        final int add_earth = vip.get_add_earth();
        if (add_earth != 0) {
            pc.addEarth(-add_earth);
            attr = true;
        }

        final int add_water = vip.get_add_water();
        if (add_water != 0) {
            pc.addWater(-add_water);
            attr = true;
        }

        final int add_stun = vip.get_add_stun();
        if (add_stun != 0) {
            pc.addRegistStun(-add_stun);
        }

        final int add_stone = vip.get_add_stone();
        if (add_stone != 0) {
            pc.addRegistStone(-add_stone);
        }

        final int add_sleep = vip.get_add_sleep();
        if (add_sleep != 0) {
            pc.addRegistSleep(-add_sleep);
        }

        final int add_freeze = vip.get_add_freeze();
        if (add_freeze != 0) {
            pc.add_regist_freeze(-add_freeze);
        }

        final int add_sustain = vip.get_add_sustain();
        if (add_sustain != 0) {
            pc.addRegistSustain(-add_sustain);
        }

        final int add_blind = vip.get_add_blind();
        if (add_blind != 0) {
            pc.addRegistBlind(-add_blind);
        }

        if (vip.get_skin_id() != 0 && pc.getSkin(vip.get_skin_id()) != null) {
            pc.getSkin(vip.get_skin_id()).deleteMe();
            pc.removeSkin(vip.get_skin_id());
        }

        final boolean death_exp = vip.get_death_exp();
        if (death_exp) {
            pc.set_death_exp(false);
        }
        final boolean death_item = vip.get_death_item();
        if (death_item) {
            pc.set_death_item(false);
        }
        final boolean death_skill = vip.get_death_skill();
        if (death_skill) {
            pc.set_death_skill(false);
        }
        final boolean death_score = vip.get_death_score();
        if (death_score) {
            pc.set_death_score(false);
        }

        final int add_exp = vip.get_add_exp();
        if (add_exp != 0) {
            pc.addExpRateToPc(-add_exp);
        }

        final int add_adena = vip.get_add_adena();
        if (add_adena != 0) {
            pc.addGF(-add_adena);
        }
        final int add_potion = vip.get_up_hp_potion();
        if (add_potion != 0) {
            pc.add_up_hp_potion(-add_potion);
        }
        final int add_PVP = vip.getPvpDmg();
        if (add_PVP != 0) {
            pc.setPvpDmg(-add_PVP);
        }
        final int add_PVP_R = vip.getPvpDmg_R();
        if (add_PVP_R != 0) {
            pc.setPvpDmg_R(-add_PVP_R);
        }
        final int add_magic_hit = vip.getOriginalMagicHit();
        if (add_magic_hit != 0) {
            pc.addOriginalMagicHit(-add_magic_hit);
        }
        final int add_StunLv = vip.getStunLevel();
        if (add_StunLv != 0) {
            pc.addStunLevel(-add_StunLv);
        }
        final int add_CloseCritical = vip.getCloseCritical();
        if (add_CloseCritical != 0) {
            pc.addCloseCritical(-add_CloseCritical);
        }
        final int add_BowCritical = vip.getBowCritical();
        if (add_BowCritical != 0) {
            pc.addBowCritical(-add_BowCritical);
        }
        final int add_DiceDmg = vip.getDiceDmg();
        final int add_DiceDmg_dmherm = vip.getDmg();
        pc.set_dmgAdd(-add_DiceDmg_dmherm, -add_DiceDmg);

        final int add_DamageReductionPVE = vip.get_DamageReductionPVE();
        if (add_DamageReductionPVE != 0) {
            pc.addDamageReductionPVE(-add_DamageReductionPVE);
        }
        final int add_DamageReductionPVE_R = vip.get_DamageReductionPVE_R();
        if (add_DamageReductionPVE_R != 0) {
            pc.adddollDamageReductionByArmor(-add_DamageReductionPVE_R);
        }
        final int add_dice_hp = vip.get_dice_hp();
        final int _sucking_hp = vip.get_sucking_hp();
        pc.add_dice_hp(-add_dice_hp, -_sucking_hp);

        final int add_dice_mp = vip.get_dice_mp();
        final int _sucking_mp = vip.get_sucking_mp();
        pc.add_dice_mp(-add_dice_mp, -_sucking_mp);
        pc.setVipDiceChance(0);
        pc.setVipDiceDamage(0);
        // 1. 取得角色的總屬性 (基礎屬性 + 裝備/道具加成)
        // 這些 getter 方法已經返回了所有加成後的總值
        final int totalStr = pc.getStr();
        final int totalDex = pc.getDex();
        final int totalInt = pc.getInt();
        final int totalWis = pc.getWis();
        final int totalCon = pc.getCon();
        // 2. 根據總屬性計算衍生屬性 (力量)
        final int calculatedMeleeDmg = L1ClassFeature.calcStrDmg(totalStr, pc.getBaseStr());
        final int calculatedMeleeHit = L1ClassFeature.calcStrHit(totalStr, pc.getBaseStr());
        final int calculatedMeleeCritical = L1ClassFeature.calcStrDmgCritical(totalStr, pc.getBaseStr());
        final int calculatedMaxWeight = (int) L1ClassFeature.calcAbilityMaxWeight(totalStr, totalCon);
        // 3. 根據總屬性計算衍生屬性 (敏捷)
        final int calculatedBowDmg = L1ClassFeature.calcDexDmg(totalDex, pc.getBaseDex());
        final int calculatedBowHit = L1ClassFeature.calcDexHit(totalDex, pc.getBaseDex());
        final int calculatedBowCritical = L1ClassFeature.calcDexDmgCritical(totalDex, pc.getBaseDex());
        final int calculatedAc = L1ClassFeature.calcDexAc(totalDex);
        final int calculatedEr = L1ClassFeature.calcDexEr(totalDex);
        // 4. 根據總屬性計算衍生屬性 (智力)
        final int calculatedMagicDmg = L1ClassFeature.calcIntMagicDmg(totalInt, pc.getBaseInt());
        final int calculatedMagicHit = L1ClassFeature.calcIntMagicHit(totalInt, pc.getBaseInt());
        final int calculatedMagicCritical = L1ClassFeature.calcIntMagicCritical(totalInt, pc.getBaseInt());
        final int calculatedMagicBonus = L1ClassFeature.calcIntMagicBonus(pc.getType(), totalInt);
        final int calculatedMagicConsumeReduction = L1ClassFeature.calcIntMagicConsumeReduction(totalInt);

        // 5. 根據總屬性計算衍生屬性 (體質 & 精神)
        final int calculatedHPRecovery = L1ClassFeature.calcConHpr(totalCon, pc.getBaseCon());
        final int calculatedManaMpr = L1ClassFeature.calcWisMpr(totalWis, pc.getBaseWis());
        final int calculatedMr = L1ClassFeature.calcStatMr(totalWis) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr();

        // 升等HP/MP增加 (這部分通常是固定的，或者與等級有關，與當前屬性無關)
        final int calculatedLevelUpHP = L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon());
        final int calculatedLevelUpMP = L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())[0];
        final int[] mpUpRange = L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis());

        // 6. 發送封包更新介面
        pc.sendPackets(new S_StrDetails(2, calculatedMeleeDmg, calculatedMeleeHit, calculatedMeleeCritical, calculatedMaxWeight));

        pc.sendPackets(new S_DexDetails(2, calculatedBowDmg, calculatedBowHit, calculatedBowCritical, calculatedAc, calculatedEr));

        pc.sendPackets(new S_IntDetails(2, calculatedMagicDmg, calculatedMagicHit, calculatedMagicCritical, calculatedMagicBonus, calculatedMagicConsumeReduction));

        final int calculatedPotionHpr = pc.get_up_hp_potion(); // HP藥水恢復增加
        pc.sendPackets(new S_ConDetails(2, calculatedHPRecovery, calculatedPotionHpr, calculatedMaxWeight, calculatedLevelUpHP, pc.getMaxHp()));

        pc.sendPackets(new S_WisDetails(2, calculatedManaMpr, pc.getMpr(), calculatedMr, mpUpRange, pc.getMaxMp()));

        // 7. 發送原始的通用封包來觸發介面更新
        if (status) {
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else {
            if (status2) {
                pc.sendPackets(new S_OwnCharStatus2(pc));
            }
            if (attr) {
                pc.sendPackets(new S_OwnCharAttrDef(pc));
            }
        }
        if (spmr) {
            pc.sendPackets(new S_SPMR(pc));
        }

        pc.sendPackets(new S_BaseAbilityDetails(25));
        pc.sendPackets(new S_BaseAbilityDetails(35));
        pc.sendPackets(new S_BaseAbilityDetails(45));
    }
}