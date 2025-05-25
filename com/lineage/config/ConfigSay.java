package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Properties;

public final class ConfigSay {
    private static final String Say = "./config/☆服務器設定表☆/顯示公告設置.properties";
    /**
     * 寶箱公告是否顯示螢幕
     */
    public static boolean BOX_Position;
    /**
     * 掉寶公告是否顯示螢幕
     */
    public static boolean DROP_Position;
    /**
     * 殺人公告是否顯示螢幕
     */
    public static boolean KILL_Position;

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            //InputStream is = new FileInputStream(new File("./config/superdiy-A+/顯示公告設置.properties"));
            final InputStream is = Files.newInputStream(new File(Say).toPath());
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            /** 掉寶公告是否顯示螢幕 */
            BOX_Position = Boolean.parseBoolean(set.getProperty("BOX_Position", "true"));
            /** 掉寶公告是否顯示螢幕 */
            DROP_Position = Boolean.parseBoolean(set.getProperty("DROP_Position", "true"));
            /** 殺人公告是否顯示螢幕 */
            KILL_Position = Boolean.parseBoolean(set.getProperty("KILL_Position", "true"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: ./config/☆服務器設定表☆/顯示公告設置.properties");
        } finally {
            set.clear();
        }
    }
}