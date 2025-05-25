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
            _source = new ComboPooledDataSource();
            _source.setDriverClass(_driver);
            _source.setJdbcUrl(_url);
            _source.setUser(_user);
            _source.setPassword(_password);
            _source.getConnection().close();
        } catch (SQLException e) {
            _log.fatal("資料庫讀取錯誤!", e);
        } catch (Exception e) {
            _log.fatal("資料庫讀取錯誤!", e);
        }
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

    public Connection getConnection() {
        Connection con = null;
        while (con == null) {
            try {
                con = _source.getConnection();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        return con;
    }
}
