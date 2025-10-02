package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ChooseBoxOption;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 快取 etcitem_box寶箱自訂選擇 表資料
 * 表結構：
 *  - 寶箱編號 int
 *  - 獲得道具 varchar(逗號分隔)
 *  - 道具強化值 varchar(逗號分隔)
 *  - 道具數量 varchar(逗號分隔)
 *  - bless varchar(逗號分隔)
 *  - out int
 *  - note varchar
 */
public class ChooseBoxCache {
    private static final Log _log = LogFactory.getLog(ChooseBoxCache.class);
    private static final ChooseBoxCache _instance = new ChooseBoxCache();

    public static ChooseBoxCache get() { return _instance; }

    private final Map<Integer, List<L1ChooseBoxOption>> boxOptions = new HashMap<>();
    private final Map<Integer, Boolean> boxOut = new HashMap<>();

    public void clear() {
        boxOptions.clear();
        boxOut.clear();
    }

    public void loadCustomSelect() {
        clear();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `寶箱編號`,`獲得道具`,`道具強化值`,`道具數量`,`bless`,`out` FROM `etcitem_box寶箱自訂選擇`");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int boxId = rs.getInt("寶箱編號");
                String items = nvl(rs.getString("獲得道具"));
                String enchants = nvl(rs.getString("道具強化值"));
                String counts = nvl(rs.getString("道具數量"));
                String bless = nvl(rs.getString("bless"));
                boolean out = rs.getInt("out") == 1;

                int[] itemIds = parseIntArray(items);
                int[] enchantVals = parseIntArray(enchants);
                int[] countVals = parseIntArray(counts);
                int[] blessVals = parseIntArray(bless);

                int n = itemIds.length;
                if (enchantVals.length != n || countVals.length != n || blessVals.length != n) {
                    _log.error("etcitem_box寶箱自訂選擇 欄位長度不一致: boxId=" + boxId);
                    continue;
                }
                List<L1ChooseBoxOption> list = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    // 過濾不存在的道具
                    L1Item t = ItemTable.get().getTemplate(itemIds[i]);
                    if (t == null) {
                        continue;
                    }
                    int cnt = Math.max(1, countVals[i]);
                    int enc = Math.max(0, enchantVals[i]);
                    int bls = Math.max(0, blessVals[i]);
                    list.add(new L1ChooseBoxOption(itemIds[i], enc, cnt, bls));
                }
                if (!list.isEmpty()) {
                    boxOptions.put(boxId, list);
                    boxOut.put(boxId, out);
                }
            }
            _log.info("讀取->etcitem_box寶箱自訂選擇: " + boxOptions.size());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public List<L1ChooseBoxOption> getOptions(int boxItemId) {
        return boxOptions.get(boxItemId);
    }

    public boolean isOut(int boxItemId) {
        Boolean v = boxOut.get(boxItemId);
        return v != null && v;
    }

    private static String nvl(String s) { return s == null ? "" : s.trim(); }

    private static int[] parseIntArray(String csv) {
        if (csv.isEmpty()) return new int[0];
        String[] parts = csv.split(",");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try { out[i] = Integer.parseInt(parts[i].trim()); }
            catch (Exception ignore) { out[i] = 0; }
        }
        return out;
    }
}


