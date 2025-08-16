package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
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
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public final class ResolventEXTable {
    private static final Log _log = LogFactory.getLog(ResolventEXTable.class);
    private static ResolventEXTable _instance;
    private final Map<Integer, Gift> _resolvent = new HashMap<>();

    private ResolventEXTable() {
        load();
    }
    // === 新增：查規則是否存在 ===
    public boolean hasRule(int itemId) {
        return _resolvent.containsKey(itemId);
    }
    public static ResolventEXTable get() {
        if (_instance == null) {
            _instance = new ResolventEXTable();
        }
        return _instance;
    }

    public static void reload() {
        ResolventEXTable oldInstance = _instance;
        _instance = new ResolventEXTable();
        oldInstance._resolvent.clear();
    }

    /** 將 "1,2,3" 轉為 int[] */
    private static int[] getArray(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, ",");
        int[] arr = new int[st.countTokens()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(st.nextToken().trim());
        }
        return arr;
    }

    /** 載入資料表 resolvent_ex */
    private void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM resolvent_ex");
            rs = ps.executeQuery();
            while (rs.next()) {
                int itemId = rs.getInt("道具編號");
                int[] crystalId = getArray(rs.getString("分解後的道具編號"));
                int[] crystalMin = getArray(rs.getString("分解後最小值"));
                int[] crystalMax = getArray(rs.getString("分解後最大值"));
                // 「強化最大」在本版定義為最低需求強化門檻
                int requireEnchant = 0;
                try {
                    requireEnchant = rs.getInt("強化值");
                } catch (SQLException ignore) {
                    // 若欄位不存在就當作 0（不限制）
                }

                Gift g = new Gift();
                g._crystal_id = crystalId;
                g._crystalMincount = crystalMin;
                g._crystalMaxcount = crystalMax;
                g._requireEnchant = Math.max(0, requireEnchant);

                _resolvent.put(itemId, g);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->進階版溶解物品設置資料數量: " + _resolvent.size() + " (" + timer.get() + "ms)");
    }

    // ===================== 對外 API（建議使用） =====================

    /**
     * 分解並發放物品（傳入被分解的實際道具實例，會檢查強化門檻）
     * @return true=成功發放 / false=條件不符或無規則
     */
    public boolean getCrystalCount(L1PcInstance pc, L1ItemInstance srcItem) {
        if (pc == null || srcItem == null) {
            return false;
        }
        final int itemId = srcItem.getItemId();
        final Gift g = _resolvent.get(itemId);
        if (g == null) {
            return false;
        }

        // 只對武器(type2=1)與防具(type2=2)檢查強化門檻；其他類型不檢查
        final int type2 = srcItem.getItem().getType2(); // 1:武器 2:防具 0:其他
        if ((type2 == 1 || type2 == 2) && g._requireEnchant > 0) {
            int nowEnchant = srcItem.getEnchantLevel();
            if (nowEnchant < g._requireEnchant) {
                pc.sendPackets(new S_SystemMessage(
                        String.format("【溶解】此物品需要至少 +%d 才能分解（目前：+%d）。", g._requireEnchant, nowEnchant)));
                return false;
            }
        }

        return grantRewards(pc, g);
    }

    // ===================== 舊有 API（保留相容） =====================

    /**
     * 舊版：只以 itemId 發放，不檢查強化門檻（保留相容，不建議使用）
     */
// === 修改舊介面：避免繞過強化門檻 ===
    public boolean getCrystalCount(L1PcInstance pc, int itemId) {
        Gift g = _resolvent.get(itemId);
        if (g == null) {
            return false;
        }
        // ★若該規則有最低強化門檻，舊介面拿不到實例強化值，為避免繞過，直接拒絕
        if (g._requireEnchant > 0) {
            _log.warn("[ResolventEXTable] getCrystalCount(pc,int) 被呼叫，但 itemId="
                    + itemId + " 需要最低強化 +" + g._requireEnchant + "，已拒絕以免繞過門檻。");
            return false;
        }
        return grantRewards(pc, g);
    }

    // ===================== 內部共用 =====================

    /** 實際發放獎勵（含欄位與長度防呆） */
    private boolean grantRewards(L1PcInstance pc, Gift g) {
        int[] item_id = g._crystal_id;
        int[] min_count = g._crystalMincount;
        int[] max_count = g._crystalMaxcount;

        if (item_id == null || min_count == null || max_count == null) {
            return false;
        }
        if (item_id.length != min_count.length || item_id.length != max_count.length) {
            _log.warn("resolvent_ex 欄位長度不一致，請檢查資料。");
            return false;
        }

        for (int i = 0; i < item_id.length; i++) {
            L1ItemInstance reward = ItemTable.get().createItem(item_id[i]);
            if (reward == null) {
                _log.warn("createItem 失敗, itemId=" + item_id[i]);
                continue;
            }

            int min = Math.max(0, min_count[i]);
            int max = max_count[i]; // 舊義：視為「範圍大小」，非上限

            int cnt;
            if (max <= 0 || max == min) {
                // 版本B：max<=0 或 min==max -> 固定給最小值
                cnt = min;
            } else if (max < min) {
                // 防呆：資料填反就用最小值
                cnt = min;
            } else {
                // 維持舊義：min + rand(0..max-1)
                cnt = min + ThreadLocalRandom.current().nextInt(max);
            }
            reward.setCount(cnt);

            // 用實際數量做負重/欄位檢查
            if (pc.getInventory().checkAddItem(reward, reward.getCount()) == 0) {
                pc.getInventory().storeItem(reward);
                pc.sendPackets(new S_ServerMessage(403, reward.getLogName())); // 你獲得%d(%s)。
            } else {
                // 若無法加入背包，視需求可直接 return false 或改為 continue
                pc.sendPackets(new S_SystemMessage("負重或欄位不足，無法領取分解獎勵。"));
                return false;
            }
        }
        return true;
    }


    /** 資料表一筆規則 */
    private static class Gift {
        private int[] _crystal_id = null;
        private int[] _crystalMincount = null;
        private int[] _crystalMaxcount = null;
        private int _requireEnchant = 0; // 最低需求強化(只對武器/防具檢查)
    }
}
