package com.lineage;

import com.lineage.config.ConfigSQL;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactory {
    private static final Log _log = LogFactory.getLog(DatabaseFactory.class);
    private static DatabaseFactory _instance;
    private static String _driver;
    private static String _url;
    private static String _user;
    private static String _password;
    private ComboPooledDataSource _source;

    public DatabaseFactory() throws SQLException {
        try {
            _source = new ComboPooledDataSource();
            _source.setDriverClass(_driver);
            _source.setJdbcUrl(_url);
            _source.setUser(_user);
            _source.setPassword(_password);
            _source.getConnection().close();
        } catch (Exception e) {
            _log.fatal("資料庫讀取錯誤!", e);
        }
    }

    public static void setDatabaseSettings() {
        _driver = ConfigSQL.DB_DRIVER;
        _url = ConfigSQL.DB_URL1 + ConfigSQL.DB_URL2 + ConfigSQL.DB_URL3;
        _user = ConfigSQL.DB_LOGIN;
        _password = ConfigSQL.DB_PASSWORD;
    }

    public static DatabaseFactory get() throws SQLException {
        if (_instance == null) {
            _instance = new DatabaseFactory();
        }
        return _instance;
    }

    /**
     * 找
     */
    public static DatabaseFactory getInstance() throws SQLException {
        if (_instance == null) {
            synchronized (DatabaseFactory.class) {
                if (_instance == null) {
                    _instance = new DatabaseFactory();
                }
            }
        }
        return _instance;
    }

    public void shutdown() {
        try {
            _source.close();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            _source = null;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public Connection getConnection() {
        int retryCount = 0;
        final int MAX_RETRY = 5;
        final int BASE_DELAY_MS = 1000;
        
        while (retryCount < MAX_RETRY) {
            try {
                Connection con = _source.getConnection();
                if (con != null && !con.isClosed()) {
                    return con;
                }
            } catch (SQLException e) {
                retryCount++;
                _log.error("獲取數據庫連接失敗，重試第 " + retryCount + " 次", e);
                
                if (retryCount >= MAX_RETRY) {
                    _log.fatal("無法獲取數據庫連接，重試次數已達上限 " + MAX_RETRY + " 次");
                    throw new RuntimeException("數據庫連接池耗盡，無法獲取連接", e);
                }
                
                try {
                    // 指數退避策略：每次重試延遲時間翻倍
                    long delayMs = BASE_DELAY_MS * (long) Math.pow(2, retryCount - 1);
                    Thread.sleep(Math.min(delayMs, 30000)); // 最大延遲30秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    _log.error("獲取連接時被中斷", ie);
                    throw new RuntimeException("獲取數據庫連接時被中斷", ie);
                }
            }
        }
        
        throw new RuntimeException("無法獲取數據庫連接，已達最大重試次數");
    }
}
