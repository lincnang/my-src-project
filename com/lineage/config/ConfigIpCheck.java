package com.lineage.config;

import com.lineage.commons.system.LanSecurityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * IP防禦
 *
 * @author loli
 */
public final class ConfigIpCheck {
    private static final String _ipcheck = "./config/ipcheck.properties";
    /**
     * IP封包驗證策略
     */
    public static boolean IPCHECKPACK;
    /**
     * IP防禦策略
     */
    public static boolean IPCHECK;
    /**
     * 相同IP指定秒數內重複連結列入計算(毫秒)
     */
    public static int TIMEMILLIS;
    /**
     * 達到指定次數列入IP封鎖
     */
    public static int COUNT;
    /**
     * 封鎖IP是否寫入資料庫(true:是 false:否)
     */
    public static boolean SETDB;
    /**
     * 如果是LINUX系統 是否加入ufw 封鎖清單(true:是 false:否)
     */
    public static boolean UFW;
    /**
     * 1個IP僅允許一個連線
     */
    public static boolean ISONEIP;
    /**
     * 1個IP只定時間內僅允許連限一個(毫秒) 0:不啟用
     */
    public static int ONETIMEMILLIS;
    /**
     * 相同帳號重複登入的的間隔時間列入計算
     */
    public static int ACCLOGINTIMEMILLIS;
    /**
     * 如果帳號登入間隔異常是否鎖定IP
     */
    public static boolean ACCLOGINBANIP;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(_ipcheck).toPath());
            set.load(is);
            is.close();
            IPCHECKPACK = Boolean.parseBoolean(set.getProperty("IPCHECKPACK", "false"));
            if (IPCHECKPACK) {
                final LanSecurityManager manager = new LanSecurityManager();
                manager.stsrt_cmd_tmp();
            }
            IPCHECK = Boolean.parseBoolean(set.getProperty("IPCHECK", "false"));
            TIMEMILLIS = Integer.parseInt(set.getProperty("TIMEMILLIS", "1000"));
            COUNT = Integer.parseInt(set.getProperty("COUNT", "10"));
            SETDB = Boolean.parseBoolean(set.getProperty("SETDB", "false"));
            UFW = Boolean.parseBoolean(set.getProperty("UFW", "true"));
            ISONEIP = Boolean.parseBoolean(set.getProperty("ISONEIP", "false"));
            ONETIMEMILLIS = Integer.parseInt(set.getProperty("ONETIMEMILLIS", "20000"));
            if (ONETIMEMILLIS != 0) {
                final LanSecurityManager manager = new LanSecurityManager();
                manager.stsrt_cmd();
            }
            ACCLOGINTIMEMILLIS = Integer.parseInt(set.getProperty("ACCLOGINTIMEMILLIS", "10"));
            ACCLOGINBANIP = Boolean.parseBoolean(set.getProperty("ACCLOGINBANIP", "false"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + _ipcheck);
        } finally {
            set.clear();
        }
    }
}