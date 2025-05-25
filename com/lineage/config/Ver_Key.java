package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 驗證Ver設置
 *
 */
public final class Ver_Key {
    //private static final String RATES_CONFIG_FILE = "LineageKey.ini";
    private static final String RATES_CONFIG_FILE = "./config/LineageKey.ini";
    public static int Key;//驗證代碼

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(RATES_CONFIG_FILE).toPath());
            //InputStream is = new FileInputStream(new File("LineageKey.ini"));
            set.load(is);
            is.close();
            Key = Integer.parseInt(set.getProperty("Key", "0"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: LineageKey.ini");
        } finally {
            set.clear();
        }
    }
}
