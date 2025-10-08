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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 載入並管理「守護星盤_格立特」資料
 */
public class GritAstrologyTable {
    private static final Log _log = LogFactory.getLog(GritAstrologyTable.class);
    private static GritAstrologyTable _instance;
    private static final Map<Integer, GritAstrologyData> _index = new ConcurrentHashMap<>();

    private GritAstrologyTable() {}

    public static GritAstrologyTable get() {
        if (_instance == null) {
            synchronized (GritAstrologyTable.class) {
                if (_instance == null) {
                    _instance = new GritAstrologyTable();
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
            ps = cn.prepareStatement("SELECT * FROM `守護星盤_格立特`");
            rs = ps.executeQuery();
            while (rs.next()) {
                GritAstrologyData data = new GritAstrologyData(
                        rs.getInt("按鈕排序"),
                        rs.getString("備註"),
                        rs.getInt("任務編號"),
                        rs.getInt("牌組數"),
                        rs.getInt("技能編號"),
                        rs.getInt("未完成Img圖檔編號"),
                        rs.getInt("完成Img圖檔編號"),
                        rs.getString("需求道具編號"),
                        rs.getString("需求道具數量"),
                        rs.getInt("AddAc"),
                        getIntSafe(rs, "AddHP"),
                        rs.getInt("近距離攻擊"),
                        rs.getInt("遠距離攻擊"),
                        rs.getInt("近距離命中"),
                        rs.getInt("遠距離命中"),
                        rs.getInt("物理傷害減免"),
                        rs.getInt("魔法傷害減免"),
                        rs.getInt("魔法傷害"),
                        rs.getInt("PVP傷害提升"),
                        rs.getInt("PVP傷害減免"),
                        getIntSafe(rs, "遠近距離爆擊抵抗+%"),
                        rs.getInt("爆擊時吸收mp"),
                        rs.getInt("爆擊時吸收hp"),
                        rs.getInt("爆擊傷害減免"),
                        rs.getInt("爆擊傷害提升"),
                        getDoubleSafe(rs, "格立特觸發機率"),
                        getDoubleSafe(rs, "格立特技能傷害"),
                        getIntSafe(rs, "技能觸發GFX"),
                        getIntSafe(rs, "近距離爆擊+%"),
                        getIntSafe(rs, "遠距離爆擊+%")
                );
                _index.put(data.getButtonOrder(), data);
                count++;
            }
            _log.info("讀取->[守護星盤_格立特]: " + count + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
    }

    public static void effectBuff(L1PcInstance pc, GritAstrologyData value, int negative) {
        if (negative == 0 || value == null) return;
        pc.addAc(value.getAddAc() * negative);
        pc.addMaxHp(value.getAddHp() * negative);
        pc.addDmgup(value.getAddMeleeDmg() * negative);
        pc.addBowDmgup(value.getAddMissileDmg() * negative);
        pc.addHitup(value.getAddMeleeHit() * negative);
        pc.addBowHit(value.getAddMissileHit() * negative);
        pc.addDamageReductionByArmor(value.getAddDmgReduction() * negative);
        pc.addMagicDmgReduction(value.getAddMagicDmgReduction() * negative);
        pc.addMagicDmgModifier(value.getAddMagicDmg() * negative);
        if (value.getPvpDamageUp() != 0) pc.setPvpDmg(value.getPvpDamageUp() * negative);
        // PVP傷害減免（統一一致性：使用 setPvpDmg_R 注入到 PVP 減免機制）
        if (value.getPveDmgReduction() != 0) pc.setPvpDmg_R(value.getPveDmgReduction() * negative);

        // 暴擊相關：分開設定近距/遠距
        if (value.getCritChanceClose() != 0) {
            pc.addCloseCritical(value.getCritChanceClose() * negative);
        }
        if (value.getCritChanceBow() != 0) {
            pc.addBowCritical(value.getCritChanceBow() * negative);
        }
        // 被動暴擊傷害提升%（持續）：記錄到玩家
        if (value.getCritDmgUp() != 0) {
            pc.addGritCritDmgUpPassivePercent(value.getCritDmgUp() * negative);
        }
        // 被動暴擊傷害減免%（持續，作為被攻擊者減免）：記錄到玩家
        if (value.getCritDmgReduction() != 0) {
            pc.addGritCritDmgReductionPercent(value.getCritDmgReduction() * negative);
        }
        // 暴擊抗性%（降低對方暴擊率）：記錄到玩家
        if (value.getCritResistPercent() != 0) {
            pc.addGritCritResistPercent(value.getCritResistPercent() * negative);
        }

        // 爆擊時吸收 HP/MP：改為累加所有「非技能節點」已解鎖值
        // 這裡僅在新增/移除單一節點後觸發重新統計，避免覆蓋/遺漏
        refreshPassiveAbsorb(pc);
        // 技能節點專屬：機率/技能暴擊傷害%/技能觸發GFX
        if (value.getSkillId() > 0) {
            if (negative > 0) {
                pc.setGritSkillProcChance(Math.max(0.0D, Math.min(100.0D, value.getProcChance())));
                pc.setGritSkillCritDmgPercent(Math.max(0.0D, value.getSkillCritDmg()));
                if (value.getSkillProcGfxId() > 0) pc.setGritSkillGfxId(value.getSkillProcGfxId());
            } else {
                pc.setGritSkillProcChance(0.0D);
                pc.setGritSkillCritDmgPercent(0.0D);
                if (value.getSkillProcGfxId() > 0) pc.setGritSkillGfxId(0);
            }
        }

        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        if (pc.isInParty()) pc.getParty().updateMiniHP(pc);
    }

    public GritAstrologyData getData(int buttonOrder) { return _index.get(buttonOrder); }
    public Integer[] getIndexArray() { return _index.keySet().toArray(new Integer[0]); }
    public int size() { return _index.size(); }

    /**
     * 重新計算並注入：所有已解鎖的非技能節點之爆擊吸收 HP/MP（不影響其他屬性，避免重複疊加）。
     */
    public static void refreshPassiveAbsorb(final L1PcInstance pc) {
        if (pc == null) return;
        int sumHp = 0;
        int sumMp = 0;
        try {
            Integer[] orders = get().getIndexArray();
            java.util.Arrays.sort(orders);
            for (Integer order : orders) {
                GritAstrologyData d = get().getData(order);
                if (d == null) continue;
                if (d.getSkillId() != 0) continue; // 僅統計非技能節點
                if (pc.getQuest() == null) continue;
                if (!pc.getQuest().isEnd(d.getQuestId())) continue; // 僅統計已解鎖
                if (d.getCritAbsorbHp() > 0) sumHp += d.getCritAbsorbHp();
                if (d.getCritAbsorbMp() > 0) sumMp += d.getCritAbsorbMp();
            }
        } catch (Throwable ignore) {}
        pc.setGritSkillAbsorbHp(sumHp);
        pc.setGritSkillAbsorbMp(sumMp);
    }

    private static int getIntSafe(ResultSet rs, String col) {
        try { return rs.getInt(col); } catch (Exception ignored) { return 0; }
    }

    private static double getDoubleSafe(ResultSet rs, String col) {
        try { return rs.getDouble(col); } catch (Exception ignored) { return 0.0D; }
    }

    // 移除相容讀取：僅保留 getIntSafe/getDoubleSafe
}


