package com.add.Tsai.Astrology;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
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
 * 守護星盤資料表
 *
 * @author hero
 */
public class Astrology1Table {
    private static final Log _log = LogFactory.getLog(Astrology1Table.class);
    private static final HashMap<Integer, AstrologyData> _astrologyIndex = new HashMap<>();
    private static Astrology1Table _instance;

    public static Astrology1Table get() {
        if (_instance == null) {
            _instance = new Astrology1Table();
        }
        return _instance;
    }

    /**
     * 守護星盤資料庫屬性設定 <br>
     * 因與星盤對話檔衝突而無法使用對話檔顯示詳細內容
     *
     * @param pc 玩家
     * @param value 守護星盤編號
     * @param negative 變更倍率
     */
    public static void effectBuff(L1PcInstance pc, AstrologyData value, int negative) {
        if (negative == 0) {
            return;
        }
        pc.addStr(value.getAddStr() * negative);
        pc.addDex(value.getAddDex() * negative);
        pc.addCon(value.getAddCon() * negative);
        pc.addInt(value.getAddInt() * negative);
        pc.addWis(value.getAddWis() * negative);
        pc.addCha(value.getAddCha() * negative);
        pc.addAc(value.getAddAc() * negative);
        pc.addSp(value.get_sp() * negative);
        pc.addMaxHp(value.getAddHp() * negative);
        pc.addMaxMp(value.getAddMp() * negative);
        pc.addDmgup(value.getAddDmg() * negative);
        pc.addBowDmgup(value.getAddBowDmg() * negative);
        pc.addHitup(value.getAddHit() * negative);
        pc.addBowHit(value.getAddBowHit() * negative);
        pc.addDamageReductionByArmor(value.getAddDmgR() * negative);
        pc.addMagicDmgReduction(value.getAddMagicDmgR() * negative);
        pc.addWeightReduction(value.getFuzhong() * negative);
        pc.addStunLevel(value.getStunHit() * negative);
        pc.addMagicDmgModifier(value.get_addMagicDmgUp() * negative);
        pc.addDamageReductionPVE(value.get_damageReductionByArmor() * negative);
        pc.addFire(value.getAddFire() * negative);
        pc.addWater(value.getAddWater() * negative);
        pc.addWind(value.getAddWind() * negative);
        pc.addEarth(value.getAddEarth() * negative);
        pc.add_dodge(value.getShanbi() * negative);
        pc.addEr(value.getHuibi() * negative);
        pc.add_up_hp_potion(value.getYaoshui() * negative);
        pc.set_expadd(value.getExp() * negative);
        pc.addRegistStun(value.getHunmi() * negative);

        pc.sendPackets(new S_OwnCharStatus(pc));
        //pc.sendPackets(new S_OwnCharAttrDef(pc)); // 實際和 S_OwnCharStatus 內容重復
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));
        pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        if (pc.isInParty()) {
            pc.getParty().updateMiniHP(pc);
        }
        sendEffectMsg(pc, value, negative);
    }

    private static void sendEffectMsg(L1PcInstance pc, AstrologyData value, int negative) {
        ArrayList<String> effects = new ArrayList<>();
        if (value.getAddStr() != 0) {
            effects.add("力量 " + value.getAddStr() * negative);
        }
        if (value.getAddDex() != 0) {
            effects.add("敏捷 " + value.getAddDex() * negative);
        }
        if (value.getAddCon() != 0) {
            effects.add("體質 " + value.getAddCon() * negative);
        }
        if (value.getAddInt() != 0) {
            effects.add("智力" + value.getAddInt() * negative);
        }
        if (value.getAddWis() != 0) {
            effects.add("精神 " + value.getAddWis() * negative);
        }
        if (value.getAddCha() != 0) {
            effects.add("魅力 " + value.getAddCha() * negative);
        }
        if (value.getAddAc() != 0) {
            effects.add("防禦 " + value.getAddAc() * negative);
        }
        if (value.get_sp() != 0) {
            effects.add("魔攻 " + value.get_sp() * negative);
        }
        if (value.getAddHp() != 0) {
            effects.add("體力 " + value.getAddHp() * negative);
        }
        if (value.getAddMp() != 0) {
            effects.add("魔力 " + value.getAddMp() * negative);
        }
        if (value.getAddDmg() != 0) {
            effects.add("近戰傷害 " + value.getAddDmg() * negative);
        }
        if (value.getAddBowDmg() != 0) {
            effects.add("遠程傷害 " + value.getAddBowDmg() * negative);
        }
        if (value.getAddHit() != 0) {
            effects.add("近戰命中 " + value.getAddHit() * negative);
        }
        if (value.getAddBowHit() != 0) {
            effects.add("遠程命中 " + value.getAddBowHit() * negative);
        }
        if (value.getAddDmgR() != 0) {
            effects.add("物理減傷 " + value.getAddDmgR() * negative);
        }
        if (value.getAddMagicDmgR() != 0) {
            effects.add("魔法減傷 " + value.getAddMagicDmgR() * negative);
        }
        if (value.getFuzhong() != 0) {
            effects.add("負重上限 " + value.getFuzhong() * negative);
        }
        if (value.getStunHit() != 0) {
            effects.add("昏迷命中 " + value.getStunHit() * negative);
        }
        if (value.get_addMagicDmgUp() != 0) {
            effects.add("魔法傷害 " + value.get_addMagicDmgUp() * negative);
        }
        if (value.get_damageReductionByArmor() != 0) {
            effects.add("PVE減傷 " + value.get_damageReductionByArmor() * negative);
        }
        if (value.getAddFire() != 0) {
            effects.add("火屬性防禦 " + value.getAddFire() * negative);
        }
        if (value.getAddWater() != 0) {
            effects.add("水屬性防禦 " + value.getAddWater() * negative);
        }
        if (value.getAddWind() != 0) {
            effects.add("風屬性防禦 " + value.getAddWind() * negative);
        }
        if (value.getAddEarth() != 0) {
            effects.add("地屬性防禦 " + value.getAddEarth() * negative);
        }
        if (value.getShanbi() != 0) {
            effects.add("回避率 " + value.getShanbi() * negative);
        }
        if (value.getHuibi() != 0) {
            effects.add("閃避率 " + value.getHuibi() * negative);
        }
        if (value.getYaoshui() != 0) {
            effects.add("藥水恢復率 " + value.getYaoshui() * negative);
        }
        if (value.getExp() != 0) {
            effects.add("經驗 " + value.getExp() * negative);
        }
        if (value.getHunmi() != 0) {
            effects.add("昏迷耐性 " + value.getHunmi() * negative);
        }
        if (effects.isEmpty()) {
            return;
        }
        for (String effect : effects) {
//            pc.sendPackets(new S_SystemMessage("守護星盤["+ value.getName() +"]屬性加成: \\fk" + effect, 3));
        }
    }

    /**
     * 載入守護星盤資料表
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `守護星盤_宙斯`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("按鈕排序");
                final String name = rs.getString("備註");
                final int needQuestId = rs.getInt("前置編號");
                final int questId = rs.getInt("任務編號");
                final int cards = rs.getInt("牌組數");
                final int skillId = rs.getInt("技能編號");
                final int addhgfxid = rs.getInt("未完成Img圖檔編號");
                final int addcgfxid = rs.getInt("完成Img圖檔編號");
                final int needItemID = rs.getInt("需求道具編號");
                final int needItemNum = rs.getInt("需求道具數量");
                final int addstr = rs.getInt("AddStr");
                final int adddex = rs.getInt("AddDex");
                final int addcon = rs.getInt("AddCon");
                final int addint = rs.getInt("AddInt");
                final int addwis = rs.getInt("AddWis");
                final int addcha = rs.getInt("AddCha");
                final int ac = rs.getInt("AddAc");
                final int sp = rs.getInt("AddSp");
                final int addhp = rs.getInt("AddHp");
                final int addmp = rs.getInt("AddMp");
                final int adddmg = rs.getInt("近距離傷害");
                final int addbowdmg = rs.getInt("遠距離傷害");
                final int addhit = rs.getInt("近距離命中");
                final int addbowhit = rs.getInt("遠戰攻擊命中");
                final int adddmgr = rs.getInt("物理減免傷害");
                final int addmdmgr = rs.getInt("魔法減免傷害");
                final int addfuzhong = rs.getInt("負重上限");
                final int stunHit = rs.getInt("昏迷命中");
                final int addMagicDmgUp = rs.getInt("魔法傷害");
                final int pvpDmg = rs.getInt("PvP傷害提升");
                final int damageReductionByArmor = rs.getInt("PvE傷害減免");
                final int addfire = rs.getInt("火屬性防禦");
                final int addwater = rs.getInt("水屬性防禦");
                final int addwind = rs.getInt("風屬性防禦");
                final int addearth = rs.getInt("地屬性防禦");
                final int addshanbi = rs.getInt("閃避提升");
                final int addhuibi = rs.getInt("迴避提升");
                final int addyaoshui = rs.getInt("藥水恢復率");
                final int addexp = rs.getInt("經驗");
                final int addhunmi = rs.getInt("昏迷耐性");
                final AstrologyData astrologyData = new AstrologyData(id, name, needQuestId, questId, cards, skillId, addhgfxid, addcgfxid, needItemID, needItemNum, addstr, adddex, addcon, addint, addwis, addcha, ac, sp, addhp, addmp, adddmg, addbowdmg, addhit, addbowhit, adddmgr, addmdmgr, addfuzhong, stunHit, addMagicDmgUp, pvpDmg, damageReductionByArmor, addfire, addwater, addwind, addearth, addshanbi, addhuibi, addyaoshui, addexp, addhunmi);
                _astrologyIndex.put(id, astrologyData);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->[守護星盤_宙斯]: " + i + "(" + timer.get() + "ms)");
    }

    public int Size() {
        return _astrologyIndex.size();
    }

    public AstrologyData getAstrology(final int id) {
        return _astrologyIndex.get(id);
    }

    public Integer[] getAstrologyIndex() {
        return _astrologyIndex.keySet().toArray(new Integer[0]);
    }
}