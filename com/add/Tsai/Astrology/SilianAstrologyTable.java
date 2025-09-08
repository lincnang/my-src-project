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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 載入並管理「守護星盤_絲莉安」資料
 */
public class SilianAstrologyTable {
    private static final Log _log = LogFactory.getLog(SilianAstrologyTable.class);
    private static final Map<Integer, SilianAstrologyData> _index = new HashMap<>();
    private static final Set<Integer> _grantItemIds = new HashSet<>();
    private static final Map<Integer, SilianAstrologyData> _grantMap = new HashMap<>();
    private static SilianAstrologyTable _instance;

    private SilianAstrologyTable() {}

    public static SilianAstrologyTable get() {
        if (_instance == null) {
            synchronized (SilianAstrologyTable.class) {
                if (_instance == null) {
                    _instance = new SilianAstrologyTable();
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
            _grantItemIds.clear();
            _grantMap.clear();
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `守護星盤_絲莉安`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int buttonOrder = rs.getInt("按鈕排序");
                final String note = rs.getString("備註");
                final int needQuestId = 0;
                final int questId = rs.getInt("任務編號");
                final int cards = rs.getInt("牌組數");
                final int skillId = rs.getInt("技能編號");
                final int incompleteGfxId = rs.getInt("未完成Img圖檔編號");
                final int completeGfxId = rs.getInt("完成Img圖檔編號");
                final String needItemId = rs.getString("需求道具編號");
                final String needItemCount = rs.getString("需求道具數量");
                final int addHp = rs.getInt("AddHp");
                final int addWeightLimit = rs.getInt("負重上限");
                int stunResist = 0;
                try { stunResist = rs.getInt("昏迷耐性"); } catch (SQLException ignore) {}
                // 擴充欄位：三重矢減傷、遠距離減免%、HPR、MPR、gfx1/2
                int tripleArrowReduction = 0;
                int rangedReducePercent = 0;
                int hpr = 0;
                int mpr = 0;
                int gfxid1 = 0;
                int gfxid2 = 0;
                try { tripleArrowReduction = rs.getInt("被三重矢攻擊減傷"); } catch (SQLException ignore) {}
                try { rangedReducePercent = rs.getInt("遠距離傷害減免+%"); } catch (SQLException ignore) {}
                try { hpr = rs.getInt("Hpr"); } catch (SQLException ignore) {}
                try { mpr = rs.getInt("Mpr"); } catch (SQLException ignore) {}
                try { gfxid1 = rs.getInt("gfxid1"); } catch (SQLException ignore) {}
                try { gfxid2 = rs.getInt("gfxid2"); } catch (SQLException ignore) {}
                int hotTime = 0;
                try { hotTime = rs.getInt("冷卻時間"); } catch (SQLException ignore) {}
                int grantItemId = 0;
                try { grantItemId = rs.getInt("啟動道具編號"); } catch (SQLException ignore) {}
                // 其餘欄位皆 0，只保留需要的欄位
                // 技能施放成本（選用）
                int castItemId = 0;
                int castItemCount = 0;
                try { castItemId = rs.getInt("施放道具編號"); } catch (SQLException ignore) {}
                try { castItemCount = rs.getInt("施放道具數量"); } catch (SQLException ignore) {}

                SilianAstrologyData data = new SilianAstrologyData(
                        buttonOrder, note, needQuestId, questId, cards, skillId,
                        incompleteGfxId, completeGfxId, needItemId, needItemCount,
                        addHp, stunResist, addWeightLimit,
                        tripleArrowReduction, rangedReducePercent, hpr, mpr,
                        gfxid1, gfxid2, castItemId, castItemCount, hotTime, grantItemId, 0);

                _index.put(data.getButtonOrder(), data);
                if (grantItemId > 0) {
                    _grantItemIds.add(grantItemId);
                }
                count++;
            }
            _log.info("讀取->[守護星盤_絲莉安]: " + count + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
    }
    public static void effectBuff(L1PcInstance pc, SilianAstrologyData value, int negative) {
        if (negative == 0 || value == null) return;
        pc.addMaxHp(value.getAddHp() * negative);
        pc.addRegistStun(value.getStunResist() * negative);
        pc.addWeightReduction(value.getAddWeightLimit() * negative);
        if (value.getHpr() != 0) pc.addHpr(value.getHpr() * negative);
        if (value.getMpr() != 0) pc.addMpr(value.getMpr() * negative);
        if (value.getTripleArrowReduction() != 0) pc.addTripleArrowReduction(value.getTripleArrowReduction() * negative);
        if (value.getRangedDmgReductionPercent() != 0) pc.addRangedDmgReductionPercent(value.getRangedDmgReductionPercent() * negative);
        if (negative > 0) {
            if (value.getGfxid1() > 0) pc.setLeechGfx1(value.getGfxid1());
            if (value.getGfxid2() > 0) pc.setLeechGfx2(value.getGfxid2());
        } else {
            if (value.getGfxid1() > 0) pc.setLeechGfx1(0);
            if (value.getGfxid2() > 0) pc.setLeechGfx2(0);
        }

        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        if (pc.isInParty()) pc.getParty().updateMiniHP(pc);
    }

    /**
     * 回收玩家身上所有絲莉安授予道具（可排除當前要保留的道具）。
     */
    public void revokeGrantItems(final L1PcInstance pc, final int excludeItemId) {
        if (pc == null) return;
        for (Integer itemId : _grantItemIds) {
            if (itemId == null) continue;
            if (excludeItemId > 0 && itemId == excludeItemId) continue;
            try {
                pc.getInventory().consumeItem(itemId.intValue());
            } catch (Exception ignored) {
            }
        }
    }

    private static int getIntSafe(ResultSet rs, String col) {
        try { return rs.getInt(col); } catch (Exception ignored) { return 0; }
    }
    // 啟動道具改由 SilianSkillStarter 決定，不在此維護列表
    public SilianAstrologyData getData(int buttonOrder) { return _index.get(buttonOrder); }
    public Integer[] getIndexArray() { return _index.keySet().toArray(new Integer[0]); }
    public int size() { return _index.size(); }
}


