package com.lineage.config;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.list.Announcements;
import com.lineage.server.utils.SQLUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static com.lineage.server.model.L1EquipmentSlot._log;

/**
 * 服務器基礎設置
 *
 * @author dexc
 */
public final class Config {
    /**
     * 版本編號
     */
    public static final String VER = "815.0.00";
    /**
     * 客戶端對應
     */
    public static final String SRCVER = "Lineage8.15";
    // 8.1 TW - 更新為支援 2017/05/10 客戶端版本
    /*
     * 1b b8 a9 00 90 b5 a9 00 ba 6e cf 77 2d 3e a9 00 c3 86 1b 4f
     */
    public static final int SVer = 1705102501;  // 更新：支援新版客戶端
    // /////////////////////////////////////////////
    public static final int CVer = 1705102501;  // 更新：支援新版客戶端
    public static final int AVer = 1705102501;  // 更新：支援新版客戶端
    public static final int NVer = 1705102501;  // 更新：支援新版客戶端
    /**
     * 伺服器素質選取方式 0:玩家自選 1:骰子隨機點
     */
    // public static int POWER = 0;
    private static final String SERVER_CONFIG_FILE = "./config/server.properties";
    /**
     * 伺服器執行登入器驗證
     */
    public static boolean LOGINS_TO_AUTOENTICATION;
    public static String RSA_KEY_E;
    public static String RSA_KEY_N;
    /**
     * 除錯模式
     */
    public static boolean DEBUG = true;
    /**
     * 伺服器編號
     */
    public static int SERVERNO;
    /**
     * 作業系統是UBUNTU
     */
    public static boolean ISUBUNTU = false;
    /**
     * 伺服器位置
     */
    public static String GAME_SERVER_HOST_NAME;
    /**
     * 伺服器端口
     */// 服務器監聽端口以"-"減號分隔 允許設置多個(允許設置一個)
    public static String GAME_SERVER_PORT;
    /**
     * 服務器名稱
     */
    public static String SERVERNAME;
    /**
     * 廣播伺服器位置
     */
    public static String CHAT_SERVER_HOST_NAME;
    /**
     * 廣播伺服器端口
     */
    public static int CHAT_SERVER_PORT;
    /**
     * 時區設置
     */
    public static String TIME_ZONE;
    /**
     * 伺服器語系
     */
    public static int CLIENT_LANGUAGE;
    /**
     * 伺服器語系字串源
     */
    public static String CLIENT_LANGUAGE_CODE;
    /**
     * 伺服器語系定位陣列
     */
    public static String[] LANGUAGE_CODE_ARRAY = {"UTF8", "EUCKR", "UTF8", "BIG5", "SJIS", "GBK"};
    /**
     * 重新啟動時間設置
     */
    public static String[] AUTORESTART = null;
    /**
     * 允許自動註冊
     */
    public static boolean AUTO_CREATE_ACCOUNTS;
    /**
     * 允許最大玩家
     */
    public static short MAX_ONLINE_USERS = 1000;
    /**
     * 人物資料自動保存時間
     */
    public static int AUTOSAVE_INTERVAL;
    /**
     * 人物背包自動保存時間
     */
    public static int AUTOSAVE_INTERVAL_INVENTORY;
    /**
     * 客戶端接收信息範圍 (-1為畫面內可見)
     */
    public static int PC_RECOGNIZE_RANGE;
    /**
     * 端口重置時間(單位:分鐘)
     */
    public static int RESTART_LOGIN;
    /**
     * 是否顯示公告
     */
    public static boolean NEWS;
    /**
     * 是否顯示管理視窗
     */
    public static int UI_MODE = 0; // 0=關閉, 1=J_Main, 2=EVA，預設為關閉

    /**
     * 是否開啟絕對還原設定(開新服專用)
     */
    public static boolean DBClearAll;

    /**
     * 執行緒池容量（可由 server.properties 調整）
     */
    public static int SCHEDULER_CORE_POOL_SIZE;
    public static int PC_SCHEDULER_POOL_SIZE;
    public static int AI_SCHEDULER_POOL_SIZE;

    

    public static void load() throws ConfigErrorException {
        // TODO 伺服器捆綁
        Properties pack = new Properties();
        try {
            InputStream is = Files.newInputStream(new File("./config/pack.properties").toPath());
            pack.load(is);
            is.close();
            LOGINS_TO_AUTOENTICATION = Boolean.parseBoolean(pack.getProperty("Autoentication", "false"));
            RSA_KEY_E = pack.getProperty("RSA_KEY_E", "0");
            RSA_KEY_N = pack.getProperty("RSA_KEY_N", "0");
        } catch (final Exception e) {
            System.err.println("沒有找到登入器加密設置檔案: ./config/pack.properties");
        } finally {
            pack.clear();
        }
        // _log.info("載入服務器基礎設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(SERVER_CONFIG_FILE).toPath());
            set.load(is);
            is.close();
            SERVERNO = Integer.parseInt(set.getProperty("ServerNo", "1"));

            // ★ 加入這裡
            String uiModeStr = set.getProperty("UI_Mode", "0");
            try {
                UI_MODE = Integer.parseInt(uiModeStr);
            } catch (NumberFormatException e) {
                UI_MODE = 0;
            }

            // 伺服器編號
            SERVERNO = Integer.parseInt(set.getProperty("ServerNo", "1"));
            // 通用
            GAME_SERVER_HOST_NAME = set.getProperty("GameserverHostname", "*");
            // 服務器監聽端口以"-"減號分隔 允許設置多個(允許設置一個)
            GAME_SERVER_PORT = set.getProperty("GameserverPort", "2000-2001");
            // 語系
            CLIENT_LANGUAGE = Integer.parseInt(set.getProperty("ClientLanguage", "3"));
            CLIENT_LANGUAGE_CODE = LANGUAGE_CODE_ARRAY[CLIENT_LANGUAGE];
            String tmp = set.getProperty("AutoRestart", "");
            if (!tmp.equalsIgnoreCase("null")) {
                AUTORESTART = tmp.split(",");
            }
            TIME_ZONE = set.getProperty("TimeZone", "CST");
            AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(set.getProperty("AutoCreateAccounts", "true"));
            MAX_ONLINE_USERS = Short.parseShort(set.getProperty("MaximumOnlineUsers", "30"));
            // 執行緒池容量（提供可調整的預設值）
            // 總排程池（一般系統用）
            SCHEDULER_CORE_POOL_SIZE = Integer.parseInt(set.getProperty("SchedulerCorePool", "200"));
            // PC/AI 排程池（預設依 MAX_ONLINE_USERS 動態計算，至少 2）
            int defaultPcAi = Math.max(2, 1 + (MAX_ONLINE_USERS / 5));
            PC_SCHEDULER_POOL_SIZE = Integer.parseInt(set.getProperty("PcSchedulerPool", String.valueOf(defaultPcAi)));
            AI_SCHEDULER_POOL_SIZE = Integer.parseInt(set.getProperty("AiSchedulerPool", String.valueOf(defaultPcAi)));
            //人物資料自動保存時間
            AUTOSAVE_INTERVAL = Integer.parseInt(set.getProperty("AutosaveInterval", "1200"), 10);
            AUTOSAVE_INTERVAL /= 60;
            if (AUTOSAVE_INTERVAL <= 0) {
                AUTOSAVE_INTERVAL = 20;
            }
            AUTOSAVE_INTERVAL_INVENTORY = Integer.parseInt(set.getProperty("AutosaveIntervalOfInventory", "300"), 10);
            AUTOSAVE_INTERVAL_INVENTORY /= 60;
            if (AUTOSAVE_INTERVAL_INVENTORY <= 0) {
                AUTOSAVE_INTERVAL_INVENTORY = 5;
            }
            PC_RECOGNIZE_RANGE = Integer.parseInt(set.getProperty("PcRecognizeRange", "13"));
            // SEND_PACKET_BEFORE_TELEPORT =
            // Boolean.parseBoolean(set.getProperty("SendPacketBeforeTeleport",
            // "false"));
            RESTART_LOGIN = Integer.parseInt(set.getProperty("restartlogin", "30"));
            NEWS = Boolean.parseBoolean(set.getProperty("News", "false"));
            // POWER = Integer.parseInt(set.getProperty("power", "0"));
            if (NEWS) {
                Announcements.get().load();
            }
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + SERVER_CONFIG_FILE);
        } finally {
            set.clear();
        }
    }

    public static void loadDB() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int size = 0;
        int i = 0;
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `a_絕對還原`");
            rs = ps.executeQuery();

            while (rs.next()) {
                final String id = rs.getString("id");
                final String set = rs.getString("設置");
                boolean set_boolean = false;
                if (set.equalsIgnoreCase("false") || set.equalsIgnoreCase("true")) {
                    set_boolean = rs.getBoolean("設置");
                }
                if (id.equalsIgnoreCase("DBClearAll")) {
                    DBClearAll = set_boolean;
                    i++;
                }
                size++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("加載:是否絕對還原設置: " + size + "筆 / 處理: " + i + "筆");
    }
}
