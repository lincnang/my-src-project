package com.lineage.server.utils;

import com.lineage.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class L1QueryUtil {

    // 設置 PreparedStatement 的參數
    private static void setupPrepareStatement(PreparedStatement pstm, Object[] args) throws SQLException {
        int i = 0;
        while (i < args.length) {
            pstm.setObject(i + 1, args[i]); // 設置第 i+1 個參數為 args[i]
            i++;
        }
    }

    // 選取查詢結果的第一個實體，如果結果為空則返回 null
    public static <T> T selectFirst(EntityFactory<T> factory, String sql, Object... args) {
        List<T> result = selectAll(factory, sql, args); // 獲取所有符合條件的實體
        return result.isEmpty() ? null : result.get(0); // 返回第一個實體或 null
    }

    // 選取所有符合條件的實體並返回列表
    public static <T> List<T> selectAll(EntityFactory<T> factory, String sql, Object... args) {
        ArrayList<T> result = new ArrayList<>(); // 創建結果列表
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection(); // 獲取資料庫連接
            pstm = con.prepareStatement(sql); // 準備 SQL 語句
            setupPrepareStatement(pstm, args); // 設置參數
            rs = pstm.executeQuery(); // 執行查詢
            while (rs.next()) { // 遍歷結果集
                T entity = factory.fromResultSet(rs); // 從結果集中創建實體
                if (entity == null)
                    throw new NullPointerException(
                            String.valueOf(String.valueOf(factory.getClass().getSimpleName())) + " returned null."); // 如果實體為 null，拋出異常
                result.add(entity); // 添加到結果列表
            }
        } catch (SQLException e) {
            throw new SecurityException(e); // 捕捉 SQL 異常並轉換為安全異常
        } finally {
            SQLUtil.close(rs); // 關閉結果集
            SQLUtil.close(pstm); // 關閉 PreparedStatement
            SQLUtil.close(con); // 關閉資料庫連接
        }
        return result; // 返回結果列表
    }

    // 執行不需要返回結果的 SQL 語句，使用已存在的連接
    public static boolean execute(Connection con, String sql, Object[] args) {
        PreparedStatement pstm = null;
        try {
            pstm = con.prepareStatement(sql); // 準備 SQL 語句
            setupPrepareStatement(pstm, args); // 設置參數
            boolean bool = pstm.execute(); // 執行 SQL 語句
            return bool; // 返回執行結果
        } catch (SQLException e) {
            throw new SecurityException(e); // 捕捉 SQL 異常並轉換為安全異常
        } finally {
            SQLUtil.close(pstm); // 關閉 PreparedStatement
        }
    }

    // 執行不需要返回結果的 SQL 語句，並自行管理連接
    public static boolean execute(String sql, Object[] args) {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection(); // 獲取資料庫連接
            boolean bool = execute(con, sql, args); // 使用已有的 execute 方法執行
            return bool; // 返回執行結果
        } catch (SQLException e) {
            throw new SecurityException(e); // 捕捉 SQL 異常並轉換為安全異常
        } finally {
            SQLUtil.close(con); // 關閉資料庫連接
        }
    }

    // 定義一個接口，用於從 ResultSet 創建實體
    public static interface EntityFactory<T> {
        T fromResultSet(ResultSet param1ResultSet) throws SQLException; // 從結果集中創建實體的方法
    }
}
