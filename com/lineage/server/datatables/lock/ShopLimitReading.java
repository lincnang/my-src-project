package com.lineage.server.datatables.lock;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 角色商店每日購買紀錄管理
 *
 * @author L1J-AI
 */
public class ShopLimitReading {

    private static final Log _log = LogFactory.getLog(ShopLimitReading.class);

    private static class Holder {
        private static final ShopLimitReading INSTANCE = new ShopLimitReading();
    }

    public static ShopLimitReading get() {
        return Holder.INSTANCE;
    }

    /**
     * 取得玩家今日已購買數量
     *
     * @param charId 角色OBJID
     * @param itemId 物品ID
     * @return 已購買數量
     */
    public int getPurchaseCount(final int charId, final int itemId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            // 彙總同一天的所有紀錄，避免資料表缺少唯一鍵時讀到錯誤筆數
            pstm = con.prepareStatement(
                    "SELECT COALESCE(SUM(`count`), 0) AS total FROM `character_每日限購` WHERE `char_id`=? AND `item_id`=? AND DATE(`purchase_date`)=CURDATE()");
            pstm.setInt(1, charId);
            pstm.setInt(2, itemId);
            rs = pstm.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (final SQLException e) {
            _log.error("讀取角色商店購買紀錄時發生錯誤: char_id=" + charId + ", item_id=" + itemId, e);
        } finally {
            // 這個方法的關閉是正確的，因為有三個參數
            SQLUtil.close(rs, pstm, con);
        }
        return count;
    }

    /**
     * 更新玩家購買數量
     *
     * @param charId 角色OBJID
     * @param itemId 物品ID
     * @param amount 增加的數量
     */
    public void updatePurchaseCount(final int charId, final int itemId, final int amount) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            final String sql = "INSERT INTO `character_每日限購` (`char_id`, `item_id`, `count`, `purchase_date`) " +
                    "VALUES (?, ?, ?, CURDATE()) " +
                    "ON DUPLICATE KEY UPDATE `count` = `count` + ?";
            pstm = con.prepareStatement(sql);
            int i = 0;
            pstm.setInt(++i, charId);
            pstm.setInt(++i, itemId);
            pstm.setInt(++i, amount);
            pstm.setInt(++i, amount);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error("更新角色商店購買紀錄時發生錯誤: char_id=" + charId + ", item_id=" + itemId, e);
        } finally {
            // --- ▼▼▼【修正】分開關閉資源 ▼▼▼ ---
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            // --- ▲▲▲ ---
        }
    }

    /**
     * 【修正】重置所有購買紀錄 (供每日排程呼叫)
     */
    public void resetDailyPurchases() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();

            // --- ▼▼▼ 【修正】直接刪除表內所有資料，達到每日重置效果 ▼▼▼ ---
            pstm = con.prepareStatement("DELETE FROM `character_每日限購`");
            // --- ▲▲▲ ---

            pstm.execute();
        } catch (final SQLException e) {
            _log.error("重置每日商店限購紀錄時發生錯誤", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}