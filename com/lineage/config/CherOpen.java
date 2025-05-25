package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 自動掛機設置config
 * <p>
 * Miranda
 */
public final class CherOpen {
    private static final String THREAD_POOL_FILE = "./config/職業開放.ini";
    /**
     * 職業開關 王族
     */
    public static int PRINCE;
    /**
     * 職業開關 騎士
     */
    public static int KNIGHT;
    /**
     * 職業開關 妖精
     */
    public static int ELF;
    /**
     * 職業開關 法師
     */
    public static int WIZARD;
    /**
     * 職業開關 黑妖
     */
    public static int DARKELF;
    /**
     * 職業開關 龍騎士
     */
    public static int DRAGONKNIGHT;
    /**
     * 職業開關 幻術師
     */
    public static int ILLUSIONIST;
    /*
     * 職業開關 戰士
     */
    public static int Warrior;

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            InputStream is = new FileInputStream(new File(THREAD_POOL_FILE));
            set.load(is);
            is.close();
            // 職業開關 1開0關 by Miranda
            PRINCE = Integer.parseInt(set.getProperty("PRINCE", "1"));
            KNIGHT = Integer.parseInt(set.getProperty("KNIGHT", "1"));
            ELF = Integer.parseInt(set.getProperty("ELF", "1"));
            WIZARD = Integer.parseInt(set.getProperty("WIZARD", "1"));
            DARKELF = Integer.parseInt(set.getProperty("DARKELF", "1"));
            DRAGONKNIGHT = Integer.parseInt(set.getProperty("DRAGONKNIGHT", "1"));
            ILLUSIONIST = Integer.parseInt(set.getProperty("ILLUSIONIST", "1"));
            Warrior = Integer.parseInt(set.getProperty("Warrior", "1"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: ./config/職業開放.ini");
        } finally {
            set.clear();
        }
    }
}
