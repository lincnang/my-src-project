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
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 進階溶解規則(原架構保留)
 * - 同一 itemId 可在 resolvent_ex 放多筆：
 *   * 強化值 > 0 ：視為「精準加值規則」（itemId + enchant 精準命中）
 *   * 強化值 = 0 ：視為「一般/主規則」（舊義：最低門檻，或無門檻）
 * - 以實際道具分解時：先找精準規則，找不到才回退到一般規則
 * - min/max 改為「區間上下限（含上限）」的語意（min=10、max=100 → 隨機 10~100）
 */
public final class ResolventEXTable {

    private static final Log _log = LogFactory.getLog(ResolventEXTable.class);
    private static ResolventEXTable _instance;

    /** 舊：每個 itemId 對應一筆「一般/主規則」（最低門檻或無門檻） */
    private final Map<Integer, Gift> _resolvent = new HashMap<>();
    /** 新：每個 itemId 底下的「精準加值規則」（enchant -> Gift） */
    private final Map<Integer, Map<Integer, Gift>> _rulesByEnchant = new HashMap<>();

    /**
     * 舊API（getCrystalCount(pc,int)）是否阻擋帶門檻或有精準規則的條目，
     * 以避免以舊API繞過加值處理或最低門檻。
     */
    private static final boolean LEGACY_BLOCK_ENCHANT_RULES = true;

    // ------------------------------------------------------------
    // 單例
    // ------------------------------------------------------------

    private ResolventEXTable() {
        load();
    }

    public static ResolventEXTable get() {
        if (_instance == null) {
            _instance = new ResolventEXTable();
        }
        return _instance;
    }

    public static void reload() {
        ResolventEXTable old = _instance;
        _instance = new ResolventEXTable();
        if (old != null) {
            old._resolvent.clear();
            old._rulesByEnchant.clear();
        }
    }

    // ------------------------------------------------------------
    // 公開方法
    // ------------------------------------------------------------

    /** 查是否至少存在一種規則（精準或一般） */
    public boolean hasRule(final int itemId) {
        return _resolvent.containsKey(itemId) || _rulesByEnchant.containsKey(itemId);
    }

    /**
     * 依「實際道具實例」分解與發放：
     * 1) 先找精準加值規則（itemId+enchant 完全命中）
     * 2) 找不到再回退一般/主規則（舊義：最低門檻）
     *
     * @return true=成功發放；false=無規則或不符合門檻/負重欄位不足
     */
    public boolean getCrystalCount(final L1PcInstance pc, final L1ItemInstance srcItem) {
        if (pc == null || srcItem == null) return false;

        final int itemId = srcItem.getItemId();
        final int enchant = srcItem.getEnchantLevel();
        final int type2 = srcItem.getItem().getType2(); // 1:武器 2:防具 0:其他

        // ① 精準加值規則
        final Map<Integer, Gift> byEnchant = _rulesByEnchant.get(itemId);
        if (byEnchant != null) {
            final Gift exact = byEnchant.get(enchant);
            if (exact != null) {
                return grantRewards(pc, exact);
            }
        }

        // ② 一般/主規則（最低門檻語意）
        final Gift general = _resolvent.get(itemId);
        if (general == null) return false;

        // 舊義的門檻判斷：僅武器/防具才看最低強化需求
        if ((type2 == 1 || type2 == 2) && general._requireEnchant > 0) {
            if (enchant < general._requireEnchant) {
                pc.sendPackets(new S_SystemMessage(
                        String.format("【溶解】此物品需要至少 +%d 才能分解（目前：+%d）。",
                                general._requireEnchant, enchant)));
                return false;
            }
        }
        return grantRewards(pc, general);
    }

    /**
     * 舊版相容：僅以 itemId 分解（可能繞過加值/門檻，視開關阻擋）
     */
    public boolean getCrystalCount(final L1PcInstance pc, final int itemId) {
        if (LEGACY_BLOCK_ENCHANT_RULES && _rulesByEnchant.containsKey(itemId)) {
            _log.warn("[ResolventEXTable] getCrystalCount(pc,int) 被呼叫，但 itemId=" + itemId +
                    " 存在精準加值規則，已依設定拒絕以免繞規。");
            return false;
        }
        final Gift g = _resolvent.get(itemId);
        if (g == null) return false;

        if (LEGACY_BLOCK_ENCHANT_RULES && g._requireEnchant > 0) {
            _log.warn("[ResolventEXTable] getCrystalCount(pc,int) 被呼叫，但 itemId=" + itemId +
                    " 需要最低強化 +" + g._requireEnchant + "，已依設定拒絕以免繞門檻。");
            return false;
        }
        return grantRewards(pc, g);
    }

    // ------------------------------------------------------------
    // 載入資料表
    // ------------------------------------------------------------

    /**
     * 載入 resolvent_ex
     *
     * 需要的欄位（中文）：
     * - 道具編號 (int)
     * - 分解後的道具編號 (varchar，逗號分隔)
     * - 分解後最小值 (varchar，逗號分隔)
     * - 分解後最大值 (varchar，逗號分隔)
     * - 強化值 (int，可無；>0=精準規則，=0=一般規則)
     */
    private void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        int generalCount = 0;
        int preciseCount = 0;

        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM resolvent_ex");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int itemId = rs.getInt("道具編號");
                final int[] ids  = parseIntArray(rs.getString("分解後的道具編號"));
                final int[] mins = parseIntArray(rs.getString("分解後最小值"));
                final int[] maxs = parseIntArray(rs.getString("分解後最大值"));

                int requireEnchant = 0;
                try {
                    // 欄位可能不存在，吞掉例外視為 0
                    requireEnchant = rs.getInt("強化值");
                } catch (SQLException ignore) {}

                final Gift g = new Gift();
                g._crystal_id       = ids;
                g._crystalMincount  = mins;
                g._crystalMaxcount  = maxs;
                g._requireEnchant   = Math.max(0, requireEnchant);

                // 基本防呆：三欄位長度需一致
                if (!sameLength(ids, mins, maxs)) {
                    _log.warn("resolvent_ex 欄位長度不一致，itemId=" + itemId +
                            "；請檢查「分解後的道具編號 / 最小值 / 最大值」。此筆將跳過。");
                    continue;
                }

                // 分流：精準 vs 一般
                if (g._requireEnchant > 0) {
                    _rulesByEnchant
                            .computeIfAbsent(itemId, k -> new HashMap<>(2))
                            .put(g._requireEnchant, g);
                    preciseCount++;
                } else {
                    // 同 itemId 多筆 requireEnchant=0 以最後一筆覆蓋（維持舊行為）
                    _resolvent.put(itemId, g);
                    generalCount++;
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }

        _log.info(String.format(
                "讀取->進階溶解規則：一般規則=%d，精準加值規則=%d（耗時 %d ms）",
                generalCount, preciseCount, timer.get()));
    }

    // ------------------------------------------------------------
    // 內部共用
    // ------------------------------------------------------------

    /** 「1,2,3」→ int[]；null/空字串回傳 null */
    private static int[] parseIntArray(final String s) {
        if (s == null || s.isEmpty()) return null;
        final StringTokenizer st = new StringTokenizer(s, ",");
        final int[] arr = new int[st.countTokens()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(st.nextToken().trim());
        }
        return arr;
    }

    private static boolean sameLength(final int[] a, final int[] b, final int[] c) {
        if (a == null || b == null || c == null) return false;
        return a.length == b.length && a.length == c.length;
    }

    /**
     * 實際發獎（沿用你原本的新增背包邏輯；數量計算改為 min~max「含上限」）
     */
    private boolean grantRewards(final L1PcInstance pc, final Gift g) {
        final int[] itemIds  = g._crystal_id;
        final int[] minArr   = g._crystalMincount;
        final int[] maxArr   = g._crystalMaxcount;

        if (!sameLength(itemIds, minArr, maxArr)) {
            _log.warn("grantRewards: 欄位長度不一致，取消發獎。");
            return false;
        }

        for (int i = 0; i < itemIds.length; i++) {
            final int id = itemIds[i];

            // 取出區間上下限，防負值
            int min = Math.max(0, minArr[i]);
            int max = Math.max(0, maxArr[i]);

            // 若填反，做交換並警告一次
            if (max < min) {
                final int t = min;
                min = max;
                max = t;
                _log.warn("resolvent_ex: 最大值 < 最小值，已自動交換（itemId=" + id + "）。");
            }

            // 取數量：固定 or 隨機（含上限）
            final int cnt;
            if (max == min) {
                cnt = min;
            } else {
                // ThreadLocalRandom 的上限排他，所以用 max+1 讓上限被包含
                // 避免 max+1 溢位：實務上 max 不會到 Integer.MAX_VALUE；若真遇到，改用 double 近似
                if (max == Integer.MAX_VALUE) {
                    final long span = ((long) max - (long) min) + 1L;
                    final long r = (long) (ThreadLocalRandom.current().nextDouble() * span);
                    cnt = (int) (min + r);
                } else {
                    cnt = ThreadLocalRandom.current().nextInt(min, max + 1);
                }
            }

            final L1ItemInstance reward = ItemTable.get().createItem(id);
            if (reward == null) {
                _log.warn("createItem 失敗, itemId=" + id);
                continue;
            }
            reward.setCount(cnt);

            // 以「實際數量」檢查負重與欄位
            if (pc.getInventory().checkAddItem(reward, reward.getCount()) == 0) {
                pc.getInventory().storeItem(reward);
                pc.sendPackets(new S_ServerMessage(403, reward.getLogName())); // 你獲得%d(%s)。
            } else {
                pc.sendPackets(new S_SystemMessage("負重或欄位不足，無法領取分解獎勵。"));
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------
    // 資料結構
    // ------------------------------------------------------------

    /** resolvent_ex 一筆規則 */
    private static class Gift {
        private int[] _crystal_id;        // 分解產出道具編號（多筆）
        private int[] _crystalMincount;   // 對應最小值
        private int[] _crystalMaxcount;   // 對應最大值（含上限）
        /**
         * 對「一般規則」：最低門檻強化值（只對武器/防具生效）
         * 對「精準規則」：觸發的精準加值（==實際 enchant）
         */
        private int _requireEnchant;
    }

    // ------------------------------------------------------------
    // 相容：提供唯讀取出舊主表（除非有舊碼需要）
    // ------------------------------------------------------------

    /** 僅供相容舊碼：唯讀主規則表 */
    @Deprecated
    public Map<Integer, ?> getInternalMap() {
        return java.util.Collections.unmodifiableMap(_resolvent);
    }
}
