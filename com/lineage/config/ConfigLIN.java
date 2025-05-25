package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 掃街系統設置
 *
 */
public final class ConfigLIN {
    private static final String ConfigLIN = "./config/官方相關系統設置.properties";
    /**
     * 是否用【beanfun】方式登錄伺服器 true=LoginTest登錄器登陸(可建帳號) false=原本
     **/
    public static boolean Bean_Fun;
    /**
     * 當Bean_Fun=false時 true=正常登陸(有專用登陸器時) false=登入核心指定帳號
     **/
    public static boolean BenJi;
    /**
     * 指定帳號(密碼一樣)
     **/
    public static String BenJi_Acc;
    /**
     * 火神系統類型 1=原本伊薇火神 2=樂天堂火神製作(DB化)
     */
    public static int Item_Craft;
    /**
     * 是否開啟周任務
     **/
    public static boolean Week_Quest;
    public static int WQ_UPDATE_TYPE;
    public static int WQ_UPDATE_WEEK;
    public static int WQ_UPDATE_TIME;
    /**
     * 是否開啟封包顯示 -C
     */
    public static boolean opcode_C;
    /**
     * 是否開啟封包顯示 -S
     */
    public static boolean opcode_S;

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigLIN).toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            // 是否用【beanfun】方式登錄伺服器 true=LoginTest登錄器登陸(可建帳號) false=原本
            Bean_Fun = Boolean.parseBoolean(set.getProperty("Bean_Fun", "false"));
            // 當Bean_Fun=false時 true=正常登陸(有專用登陸器時) false=登入核心指定帳號
            BenJi = Boolean.parseBoolean(set.getProperty("BenJi", "false"));
            // 指定帳號(密碼一樣)
            BenJi_Acc = set.getProperty("BenJi_Acc", "");
            // 是否開啟封包顯示 -C
            opcode_C = Boolean.parseBoolean(set.getProperty("opcode_C", "false"));
            // 是否開啟封包顯示 -S
            opcode_S = Boolean.parseBoolean(set.getProperty("opcode_S", "false"));
            // 火神系統類型
            Item_Craft = Integer.parseInt(set.getProperty("Item_Craft", "1"));
            // 是否開啟周任務
            Week_Quest = Boolean.parseBoolean(set.getProperty("Week_Quest", "false"));
            WQ_UPDATE_TYPE = Integer.parseInt(set.getProperty("WeekQuest_UpdateType", "1"));
            WQ_UPDATE_WEEK = Integer.parseInt(set.getProperty("WeekQuest_UpdateWeek", "4"));
            WQ_UPDATE_TIME = Integer.parseInt(set.getProperty("WeekQuest_UpdateTime", "10"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigLIN);
        } finally {
            set.clear();
        }
    }
}