package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 服務器限制設置
 *
 * @author dexc
 */
public final class ConfigPRO {
    private static final String ALT_SETTINGS_FILE = "./config/☆老爹專屬製作☆遊戲設定☆/加速器偵測系統設置.properties";
    public static int INJUSTICE_COUNT;
    public static int JUSTICE_COUNT;
    public static int CHECK_STRICTNESS;
    public static int CHECK_MOVE_STRICTNESS;
    public static int PUNISHMENT_TYPE;
    public static int PUNISHMENT_TIME;
    public static int PUNISHMENT_MAP_ID;
    // 反加速總開關：true=啟用檢測, false=完全不判斷
    public static volatile boolean ACCELERATOR_CHECK_ENABLED;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ALT_SETTINGS_FILE).toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            //TODO
            INJUSTICE_COUNT = Integer.parseInt(set.getProperty("Abnormal_packet_Count", "8"));
            JUSTICE_COUNT = Integer.parseInt(set.getProperty("Justice_Count", "12"));
            CHECK_STRICTNESS = Integer.parseInt(set.getProperty("Check_Strictness", "110"));
            CHECK_MOVE_STRICTNESS = Integer.parseInt(set.getProperty("Check_Move_Strictness", "110"));
            PUNISHMENT_TYPE = Integer.parseInt(set.getProperty("Punishment_Type", "1"));
            PUNISHMENT_TIME = Integer.parseInt(set.getProperty("Punishment_Time", "5"));
            PUNISHMENT_MAP_ID = Integer.parseInt(set.getProperty("Punishment_Map", "666"));
            ACCELERATOR_CHECK_ENABLED = Boolean.parseBoolean(set.getProperty("Accelerator_Check_Enabled", "true"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
        } finally {
            set.clear();
        }
    }
}