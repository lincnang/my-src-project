package com.lineage.server.Manly;

import com.lineage.DatabaseFactory;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 紋樣系統
 *
 * @author Administrator
 */
public class WenYangTable {
    private static final Log _log = LogFactory.getLog(WenYangTable.class);
    private static final HashMap<Integer, Integer> _checkMaxEnchantLevelmaps = new HashMap<>();
    private static WenYangTable _instance;
    private final Map<String, L1WenYang> _itemIdIndex = new HashMap<>();

    // 快取機制：避免重複查詢相同的紋樣資料
    private final Map<String, L1WenYang> _cache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 1000;

    private WenYangTable() {
        loadMagicCrystalItem();
    }

    public static WenYangTable getInstance() {
        if (_instance == null) {
            _instance = new WenYangTable();
        }
        return _instance;
    }

    private void loadMagicCrystalItem() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_紋樣屬性設定");
            rs = pstm.executeQuery();
            fillMagicCrystalItem(rs);
        } catch (SQLException e) {
        } finally {
            _log.info("讀取->[系統_紋樣屬性設定]道具資料數量: " + _itemIdIndex.size() + "(" + timer.get() + "ms)");
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillMagicCrystalItem(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int type = rs.getInt("分類");
            int Level = rs.getInt("強化等級");
            String not = rs.getString("紋樣名稱");
            int cost = rs.getInt("強化消耗點數");
            int costup = rs.getInt("機率提升損耗點數");
            int liliang = rs.getInt("力量");
            int minjie = rs.getInt("敏捷");
            int zhili = rs.getInt("智力");
            int jingshen = rs.getInt("精神");
            int tizhi = rs.getInt("體質");
            int meili = rs.getInt("魅力");
            int xue = rs.getInt("血");
            int mo = rs.getInt("魔");
            int huixue = rs.getInt("回血");
            int huimo = rs.getInt("回魔");
            int ewai = rs.getInt("額外傷害");
            int chenggong = rs.getInt("攻擊成功");
            int mogong = rs.getInt("魔攻");
            int mofang = rs.getInt("魔防");
            int feng = rs.getInt("風屬性防禦");
            int shui = rs.getInt("水屬性防禦");
            int tu = rs.getInt("地屬性防禦");
            int huo = rs.getInt("火屬性防禦");
            int jianmian = rs.getInt("傷害減免");
            int jingyan = rs.getInt("經驗");
            int buff_iconid = rs.getInt("偵測圖檔tbl編號");
            int buff_stringid = rs.getInt("偵測圖檔string編號");
            int addshanbi = rs.getInt("閃避提升"); //0115
            int huibi = rs.getInt("迴避提升");
            int yaoshui = rs.getInt("藥水恢復率");
            int fuzhong = rs.getInt("負重");
            int add_Ac = rs.getInt("防禦"); //0122
            int adddice_dmg = rs.getInt("機率給予爆擊"); //0122
            int adddmg = rs.getInt("機率給予爆擊質"); //0122
            int addpvpdmg = rs.getInt("PVP傷害"); //0122
            int addpvpdmg_r = rs.getInt("PVP減免"); //0122
            int plus2Cap = safeGetInt(rs, "一次加二上限", 100);
            int plus3Cap = safeGetInt(rs, "一次加三上限", 100);
            L1WenYang MagicCrystal_Item = new L1WenYang(
                    type, Level, not, cost, costup,
                    liliang, minjie, zhili, jingshen, tizhi, meili, xue, mo,
                    huixue, huimo, ewai, chenggong, mogong, mofang, feng, shui,
                    tu, huo, jianmian, jingyan, buff_iconid, buff_stringid,
                    addshanbi, huibi, yaoshui, fuzhong, add_Ac, adddice_dmg, adddmg,
                    addpvpdmg, addpvpdmg_r,
                    plus2Cap, plus3Cap // ★ 新增：放在最後兩個參數
            );
            if (_checkMaxEnchantLevelmaps.containsKey(type)) {
                final Integer checkMaxEnchanrLevel = _checkMaxEnchantLevelmaps.get(type);
                if (Level > checkMaxEnchanrLevel) {
                    _checkMaxEnchantLevelmaps.put(type, Level);
                }
            } else {
                _checkMaxEnchantLevelmaps.put(type, Level);
            }
            _itemIdIndex.put(String.valueOf(type) + Level, MagicCrystal_Item);
        }
    }
    /**
     * 從 ResultSet 安全讀取 int，若欄位不存在或為 NULL，回傳預設值
     */
    private int safeGetInt(ResultSet rs, String col, int def) {
        try {
            int v = rs.getInt(col);
            if (rs.wasNull()) {
                return def;
            }
            return v;
        } catch (SQLException e) {
            return def;
        }
    }
    public L1WenYang getTemplate(int type, int enchantLevel) {
        if (_checkMaxEnchantLevelmaps.containsKey(type)) {
            final Integer checkMaxEnchantLevel = _checkMaxEnchantLevelmaps.get(type);
            int maxEnchantLevel = enchantLevel;
            if (enchantLevel > checkMaxEnchantLevel) {
                maxEnchantLevel = checkMaxEnchantLevel;
            }
            final String cacheKey = String.valueOf(type) + maxEnchantLevel;

            // 先檢查快取
            L1WenYang cached = _cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }

            // 快取沒有，從記憶體中查找
            if (_itemIdIndex.containsKey(cacheKey)) {
                L1WenYang wenYang = _itemIdIndex.get(cacheKey);

                // 添加到快取（簡單的容量控制）
                if (_cache.size() < MAX_CACHE_SIZE) {
                    _cache.put(cacheKey, wenYang);
                }

                return wenYang;
            }
        }
        return null;
    }
}
