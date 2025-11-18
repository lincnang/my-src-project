package com.lineage;

import com.lineage.config.ConfigSQL;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFactoryLogin {
    private static final Log _log = LogFactory.getLog(DatabaseFactoryLogin.class);
    private static DatabaseFactoryLogin _instance;
    private static String _driver;
    private static String _url;
    private static String _user;
    private static String _password;
    private ComboPooledDataSource _source;

    public DatabaseFactoryLogin() throws SQLException {
        try {
            // 使用 c3p0 默認配置
            _source = new ComboPooledDataSource();
            _source.setDriverClass(_driver);
            _source.setJdbcUrl(_url);
            _source.setUser(_user);
            _source.setPassword(_password);
            applyResilienceSettings(_source);
            _source.getConnection().close();
        } catch (Exception e) {
            _log.fatal("資料庫讀取錯誤!", e);
        }
    }

    private void applyResilienceSettings(ComboPooledDataSource source) {
        // 以程式方式套用與 c3p0-config.xml 同步的連線耐久設定
        source.setInitialPoolSize(10);
        source.setMinPoolSize(10);
        source.setMaxPoolSize(100);
        source.setAcquireIncrement(5);
        source.setAcquireRetryAttempts(0);
        source.setAcquireRetryDelay(500);
    source.setCheckoutTimeout(0);
    // 僅使用 SQL 測試查詢，避免 c3p0 建立自動測試資料表
    source.setPreferredTestQuery("SELECT 1");
        source.setTestConnectionOnCheckout(true);
        source.setTestConnectionOnCheckin(true);
        source.setIdleConnectionTestPeriod(60);
        source.setMaxConnectionAge(14400);
        source.setMaxIdleTime(900);
        source.setMaxStatementsPerConnection(100);
    }

    public static void setDatabaseSettings() {
        _driver = ConfigSQL.DB_DRIVER;
        _url = ConfigSQL.DB_URL1_LOGIN + ConfigSQL.DB_URL2_LOGIN + ConfigSQL.DB_URL3_LOGIN;
        _user = ConfigSQL.DB_LOGIN_LOGIN;
        _password = ConfigSQL.DB_PASSWORD_LOGIN;
    }

    public static DatabaseFactoryLogin get() throws SQLException {
        if (_instance == null) {
            _instance = new DatabaseFactoryLogin();
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

    public Connection getConnection() throws SQLException {
        int retryCount = 0;
        final int MAX_RETRY = 5;
        final int BASE_DELAY_MS = 2000;
        
        while (retryCount < MAX_RETRY) {
            try {
                Connection con = _source.getConnection();
                if (con != null && !con.isClosed()) {
                    // 測試連接有效性
                    try {
                        con.setAutoCommit(true); // 測試連接是否真的可用
                        return con;
                    } catch (SQLException testEx) {
                        _log.warn("獲取到的連接無效，關閉並重試: " + testEx.getMessage());
                        try {
                            con.close();
                        } catch (Exception closeEx) {
                            _log.debug("關閉無效連接時發生錯誤", closeEx);
                        }
                        throw testEx; // 拋出異常進入重試邏輯
                    }
                }
            } catch (SQLException e) {
                retryCount++;
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                
                // 根據錯誤類型決定處理策略
                if (errorMsg.contains("communications link failure") || 
                    errorMsg.contains("connection reset") ||
                    errorMsg.contains("broken pipe") ||
                    errorMsg.contains("connection timed out")) {
                    
                    _log.warn("檢測到連接問題，嘗試重試第 " + retryCount + " 次: " + e.getMessage());
                } else {
                    _log.error("獲取登錄數據庫連接失敗，重試第 " + retryCount + " 次", e);
                }
                
                if (retryCount >= MAX_RETRY) {
                    _log.fatal("無法獲取登錄數據庫連接，重試次數已達上限 " + MAX_RETRY + " 次");
                    throw new SQLException("登錄數據庫連接池耗盡，無法獲取連接", e);
                }
                
                try {
                    // 指數退避策略：每次重試延遲時間遞增
                    long delayMs = BASE_DELAY_MS * retryCount;
                    _log.info("等待 " + delayMs + " 毫秒後重試獲取登錄數據庫連接...");
                    Thread.sleep(Math.min(delayMs, 30000)); // 最大延遲30秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    _log.error("獲取登錄連接時被中斷", ie);
                    throw new SQLException("獲取登錄數據庫連接時被中斷", ie);
                }
            }
        }
        
        // 如果所有重試都失敗，拋出SQLException
        throw new SQLException("無法獲取登錄數據庫連接，已達最大重試次數 " + MAX_RETRY + " 次");
    }
}
