package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 特殊系統設置
 *
 */
public final class ConfigWeaponryEffects {
    // ---------------------------------------------------
    // 武器加成特效
    private static final String ConfigWeaponryEffects = "./config/☆老爹專屬製作☆遊戲設定☆/武器加成特效設置.properties";
    /**
     * 特效延遲時間 (單位:秒)
     */
    public static int WEAPON_EFFECT_DELAY;
    /**
     * 武器額外持續特效1 (過3-過4)
     */
    public static int WEAPON_EFFECT_1;
    /**
     * 武器額外持續特效2 (過5-過6)
     */
    public static int WEAPON_EFFECT_2;
    /**
     * 武器額外持續特效3 (過7-過8)
     */
    public static int WEAPON_EFFECT_3;
    /**
     * 武器額外持續特效4 (過9-過10)
     */
    public static int WEAPON_EFFECT_4;
    /**
     * 武器額外持續特效5 (過11-過12)
     */
    public static int WEAPON_EFFECT_5;
    /**
     * 武器額外持續特效6 (過13-過14)
     */
    public static int WEAPON_EFFECT_6;
    public static int WEAPON_EFFECT_7;
    public static int WEAPON_EFFECT_8;
    public static int WEAPON_EFFECT_9;
    public static int WEAPON_EFFECT_10;
    public static int WEAPON_EFFECT_11;
    public static int WEAPON_EFFECT_12;
    public static int WEAPON_EFFECT_13;
    public static int WEAPON_EFFECT_14;
    public static int WEAPON_EFFECT_15;
    public static int WEAPON_EFFECT_16;
    public static int WEAPON_EFFECT_17;
    public static int WEAPON_EFFECT_18;
    public static int WEAPON_EFFECT_19;
    public static int WEAPON_EFFECT_20;
    public static int WEAPON_EFFECT_21;
    public static int WEAPON_EFFECT_22;
    public static int WEAPON_EFFECT_23;
    public static int WEAPON_EFFECT_24;
    public static int WEAPON_EFFECT_25;
    public static int WEAPON_EFFECT_26;
    public static int WEAPON_EFFECT_27;
    public static int WEAPON_EFFECT_28;
    public static int WEAPON_EFFECT_29;
    public static int WEAPON_EFFECT_30;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            //final InputStream is = new FileInputStream(new File(ConfigOtherSet2));
            InputStream is = Files.newInputStream(new File("./config/☆老爹專屬製作☆遊戲設定☆/武器加成特效設置.properties").toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            //-----------------------------------------------------------------
            // 武器加成特效
            /** 特效延遲時間 (單位:秒) */
            WEAPON_EFFECT_DELAY = Integer.parseInt(set.getProperty("Weapon_Effect_Delay", "0"));
            /** 武器額外持續特效1 (過3-過4) */
            WEAPON_EFFECT_1 = Integer.parseInt(set.getProperty("Weapon_Effect_1", "-1"));
            /** 武器額外持續特效2 (過5-過6) */
            WEAPON_EFFECT_2 = Integer.parseInt(set.getProperty("Weapon_Effect_2", "-1"));
            /** 武器額外持續特效3 (過7-過8) */
            WEAPON_EFFECT_3 = Integer.parseInt(set.getProperty("Weapon_Effect_3", "-1"));
            /** 武器額外持續特效4 (過9-過10) */
            WEAPON_EFFECT_4 = Integer.parseInt(set.getProperty("Weapon_Effect_4", "-1"));
            /** 武器額外持續特效5 (過11-過12) */
            WEAPON_EFFECT_5 = Integer.parseInt(set.getProperty("Weapon_Effect_5", "-1"));
            /** 武器額外持續特效6 (過13-過14) */
            WEAPON_EFFECT_6 = Integer.parseInt(set.getProperty("Weapon_Effect_6", "-1"));
            WEAPON_EFFECT_7 = Integer.parseInt(set.getProperty("Weapon_Effect_7", "-1"));
            WEAPON_EFFECT_8 = Integer.parseInt(set.getProperty("Weapon_Effect_8", "-1"));
            WEAPON_EFFECT_9 = Integer.parseInt(set.getProperty("Weapon_Effect_9", "-1"));
            WEAPON_EFFECT_10 = Integer.parseInt(set.getProperty("Weapon_Effect_10", "-1"));
            WEAPON_EFFECT_11 = Integer.parseInt(set.getProperty("Weapon_Effect_11", "-1"));
            WEAPON_EFFECT_12 = Integer.parseInt(set.getProperty("Weapon_Effect_12", "-1"));
            WEAPON_EFFECT_13 = Integer.parseInt(set.getProperty("Weapon_Effect_13", "-1"));
            WEAPON_EFFECT_14 = Integer.parseInt(set.getProperty("Weapon_Effect_14", "-1"));
            WEAPON_EFFECT_15 = Integer.parseInt(set.getProperty("Weapon_Effect_15", "-1"));
            WEAPON_EFFECT_16 = Integer.parseInt(set.getProperty("Weapon_Effect_16", "-1"));
            WEAPON_EFFECT_17 = Integer.parseInt(set.getProperty("Weapon_Effect_17", "-1"));
            WEAPON_EFFECT_18 = Integer.parseInt(set.getProperty("Weapon_Effect_18", "-1"));
            WEAPON_EFFECT_19 = Integer.parseInt(set.getProperty("Weapon_Effect_19", "-1"));
            WEAPON_EFFECT_20 = Integer.parseInt(set.getProperty("Weapon_Effect_20", "-1"));
            WEAPON_EFFECT_21 = Integer.parseInt(set.getProperty("Weapon_Effect_21", "-1"));
            WEAPON_EFFECT_22 = Integer.parseInt(set.getProperty("Weapon_Effect_22", "-1"));
            WEAPON_EFFECT_23 = Integer.parseInt(set.getProperty("Weapon_Effect_23", "-1"));
            WEAPON_EFFECT_24 = Integer.parseInt(set.getProperty("Weapon_Effect_24", "-1"));
            WEAPON_EFFECT_25 = Integer.parseInt(set.getProperty("Weapon_Effect_25", "-1"));
            WEAPON_EFFECT_26 = Integer.parseInt(set.getProperty("Weapon_Effect_26", "-1"));
            WEAPON_EFFECT_27 = Integer.parseInt(set.getProperty("Weapon_Effect_27", "-1"));
            WEAPON_EFFECT_28 = Integer.parseInt(set.getProperty("Weapon_Effect_28", "-1"));
            WEAPON_EFFECT_29 = Integer.parseInt(set.getProperty("Weapon_Effect_29", "-1"));
            WEAPON_EFFECT_30 = Integer.parseInt(set.getProperty("Weapon_Effect_30", "-1"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigWeaponryEffects);
        } finally {
            set.clear();
        }
    }
}