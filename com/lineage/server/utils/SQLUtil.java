package com.lineage.server.utils;

import java.sql.*;

/**
 * 增加SQL相關的close方法以避免長時間運行出現SQL連接錯誤 by 聖子默默
 */
public class SQLUtil {
    public static SQLException close(Connection cn) {
        try {
            if (cn != null) {
                cn.close();
            }
        } catch (SQLException e) {
            return e;
        }
        return null;
    }

    public static SQLException close(Statement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            return e;
        }
        return null;
    }

    public static SQLException close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            return e;
        }
        return null;
    }

    public static SQLException close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            return e;
        }
        return null;
    }

    public static void close(ResultSet rs, Statement ps, Connection cn) {
        close(rs);
        close(ps);
        close(cn);
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection cn) {
        close(rs);
        close(ps);
        close(cn);
    }

    public static void close(PreparedStatement pstm, Connection con) {
        close(pstm);
        close(con);
    }
}
