package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Blend;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 物品融合系統(DB自製)
 *
 * @author terry0412
 */
public final class BlendTable {
    private static final Log _log = LogFactory.getLog(BlendTable.class);
    private static BlendTable _instance;
    private final HashMap<Integer, L1Blend> _itemIdIndex = new HashMap<Integer, L1Blend>();

    public static BlendTable getInstance() {
        if (_instance == null) {
            _instance = new BlendTable();
        }
        return _instance;
    }

    public final void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM 功能_道具合成update");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int item_id = rs.getInt("物品編號");// 融合道具
                int checkLevel = rs.getInt("判定等級");// 判斷等級
                int checkClass = rs.getInt("判定職業");// 判斷職業
                int rnd = rs.getInt("合成機率");// DB設定合成機率
                int checkItem = rs.getInt("觸發融合道具");// 身上要有何種道具才可執行
                int hpConsume = rs.getInt("所需血量");// 判斷所需血量
                int mpConsume = rs.getInt("所需魔力");// 判斷所需魔力
                int material = rs.getInt("合成媒介");// 判斷合成媒介
                int material_count = rs.getInt("媒介數量");// 判斷合成媒介 數量
                int material_2 = rs.getInt("合成媒介_2");// 判斷合成媒介2
                int material_2_count = rs.getInt("媒介數量_2");// 判斷合成媒介2
                // 數量
                int material_3 = rs.getInt("合成媒介_3");// 判斷合成媒介3
                int material_3_count = rs.getInt("媒介數量_3");// 判斷合成媒介3
                // 數量
                int material_4 = rs.getInt("合成媒介_4");// 判斷合成媒介3
                int material_4_count = rs.getInt("媒介數量_4");// 判斷合成媒介4
                // 數量
                int material_5 = rs.getInt("合成媒介_5");// 判斷合成媒介3
                int material_5_count = rs.getInt("媒介數量_5");// 判斷合成媒介5
                // 數量
                int new_item = rs.getInt("給予道具");// 合成後的新道具
                int new_item_counts = rs.getInt("給予道具數量");// 新道具的數量
                int new_Enchantlvl_SW = rs.getInt("強化值是否隨機的開關");// 新道具(武器或防具)強化值是否隨機的開關
                // 隨機:1
                // 固定:0
                int new_item_Enchantlvl = rs.getInt("強化值");// 新道具的強化值
                int removeItem = rs.getInt("是否刪除");// 是否刪除融合道具
                String message = rs.getString("顯示對話");// 設定顯示對話
                int item_Html = rs.getInt("是否開啟融合條件清單");// 設定顯示融合所須物品清單
                L1Blend Item_Blend = new L1Blend(item_id, checkLevel, checkClass, rnd, checkItem, hpConsume, mpConsume, material, material_count, material_2, material_2_count, material_3, material_3_count, material_4, material_4_count, material_5, material_5_count, new_item, new_item_counts, new_Enchantlvl_SW, new_item_Enchantlvl, removeItem, message, item_Html);
                _itemIdIndex.put(item_id, Item_Blend);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(conn);
        }
        _log.info("讀取->[系統]_道具合成系統設置數量: " + _itemIdIndex.size() + "(" + timer.get() + "ms)");
    }

    public final L1Blend getTemplate(int itemId) {
        return _itemIdIndex.get(itemId);
    }
}
