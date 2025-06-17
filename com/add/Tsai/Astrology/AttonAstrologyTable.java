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
import java.util.List;
import java.util.Map;

/**
 * 單例：載入並管理「守護星盤_阿頓」資料，並套用效果 (已修正)
 */
public class AttonAstrologyTable {
    private static final Log _log = LogFactory.getLog(AttonAstrologyTable.class);
    private static final Map<Integer, AttonAstrologyData> _attonIndex = new HashMap<>();
    private static AttonAstrologyTable _instance;

    private AttonAstrologyTable() {}

    public static AttonAstrologyTable get() {
        if (_instance == null) {
            _instance = new AttonAstrologyTable();
        }
        return _instance;
    }

    /**
     * 從資料庫載入所有「守護星盤_阿頓」設定
     */
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `守護星盤_阿頓`");
            rs = ps.executeQuery();
            while (rs.next()) {
                // 已修正: 增加讀取 "前置編號" 並使用新的 AttonAstrologyData 物件
                AttonAstrologyData data = new AttonAstrologyData(
                        rs.getInt("按鈕排序"),
                        rs.getString("備註"),
                        rs.getInt("前置編號"), // <-- 修正點
                        rs.getInt("任務編號"),
                        rs.getInt("牌組數"),
                        rs.getInt("技能編號"),
                        rs.getInt("未完成Img圖檔編號"),
                        rs.getInt("完成Img圖檔編號"),
                        rs.getString("需求道具編號"),
                        rs.getString("需求道具數量"),
                        rs.getInt("AddStr"),
                        rs.getInt("AddDex"),
                        rs.getInt("AddCon"),
                        rs.getInt("AddInt"),
                        rs.getInt("AddWis"),
                        rs.getInt("AddCha"),
                        rs.getInt("AddAc"),
                        rs.getInt("AddSp"),
                        rs.getInt("AddHp"),
                        rs.getInt("AddMp"),
                        rs.getInt("近距離攻擊"),
                        rs.getInt("遠距離攻擊"),
                        rs.getInt("近距離命中"),
                        rs.getInt("遠距離命中"),
                        rs.getInt("物理傷害減免"),
                        rs.getInt("魔法傷害減免"),
                        rs.getInt("負重上限"),
                        rs.getInt("昏迷等級"),
                        rs.getInt("昏迷命中"),
                        rs.getInt("魔法傷害"),
                        rs.getInt("PvP傷害提升"),
                        rs.getInt("PvE傷害減免"),
                        rs.getInt("無視傷害減免+%"),
                        rs.getInt("被三重矢攻擊減傷"),
                        rs.getInt("遠近距離致命攻擊抗性+%"),
                        rs.getInt("遠近距離爆擊+%"),
                        rs.getInt("爆擊時吸收mp"),
                        rs.getInt("爆擊時吸收hp"),
                        rs.getInt("遠距離傷害減免+%"),
                        rs.getInt("昏迷時傷害減免"),
                        rs.getInt("無視遠距離傷害減免"),
                        rs.getInt("暴擊傷害減免"),
                        rs.getInt("暴擊傷害提升")
                );
                _attonIndex.put(data.getButtonOrder(), data);
                count++;
            }
            _log.info("讀取->[守護星盤_阿頓]: " + count +"(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
    }

    /**
     * 套用屬性增益
     * @param pc 玩家
     * @param value 該按鈕的資料
     * @param negative +1 加效果，-1 去效果
     */
    public static void effectBuff(L1PcInstance pc, AttonAstrologyData value, int negative) {
        if (negative == 0 || value == null) {
            return;
        }
        pc.addStr(value.getAddStr() * negative);
        pc.addDex(value.getAddDex() * negative);
        pc.addCon(value.getAddCon() * negative);
        pc.addInt(value.getAddInt() * negative);
        pc.addWis(value.getAddWis() * negative);
        pc.addCha(value.getAddCha() * negative);
        pc.addAc(value.getAddAc() * negative);
        pc.addSp(value.getAddSp() * negative);
        pc.addMaxHp(value.getAddHp() * negative);
        pc.addMaxMp(value.getAddMp() * negative);
        pc.addDmgup(value.getAddMeleeDmg() * negative);
        pc.addBowDmgup(value.getAddMissileDmg() * negative);
        pc.addHitup(value.getAddMeleeHit() * negative);
        pc.addBowHit(value.getAddMissileHit() * negative);
        pc.addDamageReductionByArmor(value.getAddDmgReduction() * negative);
        pc.addMagicDmgReduction(value.getAddMagicDmgReduction() * negative);
        pc.addWeightReduction(value.getAddWeightLimit() * negative);
        pc.addStunLevel(value.getStunLevel() * negative);
        pc.addStunLevel(value.getStunHit() * negative);
        pc.addMagicDmgModifier(value.getAddMagicDmg() * negative);
//        pc.add_pvp_reduction(value.getPveDmgReduction() * negative);
        // TODO: 根據 AttonAstrologyData 中的進階效果欄位補齊對應的 pc 新增方法

        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        if (pc.isInParty()) {
            pc.getParty().updateMiniHP(pc);
        }
        sendEffectMsg(pc, value, negative);
    }

    private static void sendEffectMsg(L1PcInstance pc, AttonAstrologyData value, int negative) {
        List<String> effects = new ArrayList<>();
        if (value.getAddStr() != 0) {
            effects.add("力量 " + value.getAddStr() * negative);
        }
        if (value.getPvpDamageUp() != 0) {
            effects.add("PvP 傷害提升 " + value.getPvpDamageUp() * negative);
        }
        // …其他欄位依序加入 effects…
        if (effects.isEmpty()) {
            return;
        }
        for (String msg : effects) {
            pc.sendPackets(new S_SystemMessage("守護星盤[阿頓]效果： " + msg, 3));
        }
    }

    /** 取得單筆資料 */
    public AttonAstrologyData getData(int buttonOrder) {
        return _attonIndex.get(buttonOrder);
    }

    /** 取得所有按鈕索引 */
    public Integer[] getIndexArray() {
        return _attonIndex.keySet().toArray(new Integer[0]);
    }

    /** 資料筆數 */
    public int size() {
        return _attonIndex.size();
    }
}
