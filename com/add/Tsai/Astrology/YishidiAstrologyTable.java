package com.add.Tsai.Astrology;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
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
 * 載入並管理「守護星盤_依詩蒂」資料
 * 結構參照 GritAstrologyTable，欄位定義依據 SQL: 守護星盤_依詩蒂
 */
public class YishidiAstrologyTable {
    private static final Log _log = LogFactory.getLog(YishidiAstrologyTable.class);
    private static YishidiAstrologyTable _instance;
    private static final Map<Integer, YishidiAstrologyData> _index = new HashMap<>();

    private YishidiAstrologyTable() {}

    public static YishidiAstrologyTable get() {
        if (_instance == null) {
            synchronized (YishidiAstrologyTable.class) {
                if (_instance == null) {
                    _instance = new YishidiAstrologyTable();
                }
            }
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            _index.clear();
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `守護星盤_依詩蒂`");
            rs = ps.executeQuery();
            while (rs.next()) {
                YishidiAstrologyData data = new YishidiAstrologyData(
                        rs.getInt("按鈕排序"),
                        rs.getString("備註"),
                        rs.getInt("任務編號"),
                        rs.getInt("牌組數"),
                        rs.getInt("技能編號"),
                        rs.getInt("未完成Img圖檔編號"),
                        rs.getInt("完成Img圖檔編號"),
                        rs.getString("需求道具編號"),
                        rs.getString("需求道具數量"),
                        getIntSafe(rs, "AddAc"),
                        getIntSafe(rs, "AddHP"),
                        getIntSafe(rs, "AddMP"),
                        getIntSafe(rs, "近距離攻擊"),
                        getIntSafe(rs, "遠距離攻擊"),
                        getIntSafe(rs, "近距離命中"),
                        getIntSafe(rs, "遠距離命中"),
                        getIntSafe(rs, "魔法命中"),
                        getIntSafe(rs, "物理傷害減免"),
                        getIntSafe(rs, "魔法傷害減免"),
                        getIntSafe(rs, "魔法傷害"),
                        getIntSafe(rs, "PVP傷害提升"),
                        getIntSafe(rs, "PVP傷害減免"),
                        getIntSafe(rs, "遠近距離爆擊抵抗+%"),
                        getIntSafe(rs, "爆擊傷害減免"),
                        getIntSafe(rs, "爆擊傷害提升"),
                        getDoubleSafe(rs, "依詩蒂觸發機率"),
                        getIntSafe(rs, "技能觸發GFX"),
                        getIntSafe(rs, "近距離爆擊+%"),
                        getIntSafe(rs, "遠距離爆擊+%"),
                        getIntSafe(rs, "AddStr"),
                        getIntSafe(rs, "AddDex"),
                        getIntSafe(rs, "AddCon"),
                        getIntSafe(rs, "AddInt"),
                        getIntSafe(rs, "AddWis"),
                        getIntSafe(rs, "整體傷害減少%"),
                        getIntSafe(rs, "近距離傷害減免%"),
                        getIntSafe(rs, "遠距離傷害減免%"),
                        getIntSafe(rs, "阻擋武器+%"),
                        getIntSafe(rs, "技能範圍"),
                        getIntSafe(rs, "技能範圍傷害%"),
                        getIntSafe(rs, "減益狀態觸發機率%"),
                        getIntSafe(rs, "減益狀態減傷"),
                        getIntSafe(rs, "減益狀態GFX"),
                        getIntSafe(rs, "減益狀態恢復時間"),
                        getIntSafe(rs, "減益狀態ICON_ID"),
                        getIntSafe(rs, "減益狀態字串_ID")
                );
                _index.put(data.getButtonOrder(), data);
                count++;
            }
            _log.info("讀取->[守護星盤_依詩蒂]: " + count + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
    }

    public static void effectBuff(L1PcInstance pc, YishidiAstrologyData value, int negative) {
        if (negative == 0 || value == null) return;
        pc.addAc(value.getAddAc() * negative);
        pc.addMaxHp(value.getAddHp() * negative);
        pc.addMaxMp(value.getAddMp() * negative);
        pc.addDmgup(value.getAddMeleeDmg() * negative);
        pc.addBowDmgup(value.getAddMissileDmg() * negative);
        pc.addHitup(value.getAddMeleeHit() * negative);
        pc.addBowHit(value.getAddMissileHit() * negative);
        if (value.getAddMagicHit() != 0) pc.addOriginalMagicHit(value.getAddMagicHit() * negative);
        pc.addDamageReductionByArmor(value.getAddDmgReduction() * negative);
        pc.addMagicDmgReduction(value.getAddMagicDmgReduction() * negative);
        pc.addMagicDmgModifier(value.getAddMagicDmg() * negative);
        if (value.getPvpDamageUp() != 0) pc.setPvpDmg(value.getPvpDamageUp() * negative);
        if (value.getPvpDmgReduction() != 0) pc.setPvpDmg_R(value.getPvpDmgReduction() * negative);
        if (value.getCritResistPercent() != 0) {
            // 此專案已有格立特用的暴擊抗性欄位，沿用記錄方法
            pc.addGritCritResistPercent(value.getCritResistPercent() * negative);
        }
        if (value.getCritChanceClose() != 0) pc.addCloseCritical(value.getCritChanceClose() * negative);
        if (value.getCritChanceBow() != 0) pc.addBowCritical(value.getCritChanceBow() * negative);
        // 基礎五維
        if (value.getAddStr() != 0) pc.addStr(value.getAddStr() * negative);
        if (value.getAddDex() != 0) pc.addDex(value.getAddDex() * negative);
        if (value.getAddCon() != 0) pc.addCon(value.getAddCon() * negative);
        if (value.getAddInt() != 0) pc.addInt(value.getAddInt() * negative);
        if (value.getAddWis() != 0) pc.addWis(value.getAddWis() * negative);
        // 百分比減傷（全傷 & 近戰 & 遠距）
        if (value.getAllDmgReductionPercent() != 0) pc.addAllDmgReductionPercent(value.getAllDmgReductionPercent() * negative);
        if (value.getMeleeDmgReductionPercent() != 0) pc.addMeleeDmgReductionPercent(value.getMeleeDmgReductionPercent() * negative);
        if (value.getRangedDmgReductionPercent() != 0) pc.addRangedDmgReductionPercent(value.getRangedDmgReductionPercent() * negative);
        // 阻擋武器（專案內對應阻擋武器%）
        if (value.getBlockWeaponPercent() != 0) pc.addBlockWeapon(value.getBlockWeaponPercent() * negative);

        // 減益狀態參數（僅記錄在玩家身上，實際觸發在戰鬥時判斷）
        if (value.getDebuffProcPercent() != 0) pc.setYishidiDebuffProcPercent(value.getDebuffProcPercent() * negative);
        if (value.getDebuffDmgDown() != 0) pc.setYishidiDebuffDmgDown(value.getDebuffDmgDown() * negative);
        if (value.getDebuffDurationSec() != 0) pc.setYishidiDebuffDurationSec(value.getDebuffDurationSec() * negative);
        if (value.getDebuffGfxId() != 0) pc.setYishidiDebuffGfxId(value.getDebuffGfxId() * negative);

        // 依詩蒂技能節點：不再寫入格立特欄位，改為戰鬥時計算時直接讀取當前選擇節點資料（避免混用覆蓋）

        // 技能節點：僅記錄發動機率與特效，實際觸發在戰鬥邏輯中判斷（若需要）
        // 依詩蒂此處僅套用持續型屬性；天鵝系效果可於後續擴充觸發點

        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        if (pc.isInParty()) pc.getParty().updateMiniHP(pc);
    }

    public YishidiAstrologyData getData(int buttonOrder) { return _index.get(buttonOrder); }
    public Integer[] getIndexArray() { return _index.keySet().toArray(new Integer[0]); }
    public int size() { return _index.size(); }

    private static int getIntSafe(ResultSet rs, String col) {
        try { return rs.getInt(col); } catch (Exception ignored) { return 0; }
    }
    private static double getDoubleSafe(ResultSet rs, String col) {
        try { return rs.getDouble(col); } catch (Exception ignored) { return 0.0D; }
    }
}


