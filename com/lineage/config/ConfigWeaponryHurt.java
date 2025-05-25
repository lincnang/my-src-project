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
public final class ConfigWeaponryHurt {
    /**
     * 各階段強化值附加傷害
     */
    private static final String ConfigWeaponryHurt = "./config/☆老爹專屬製作☆遊戲設定☆/武器傷害加成設置.properties";
    // ---------------------------------------------------
    public static boolean WEAPON_POWER;// 武器+9(含)以上附加額外增加傷害值 是否開啟
    public static int[] WEAPON_POWER_LIST;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            //final InputStream is = new FileInputStream(new File(ConfigOtherSet2));
            InputStream is = Files.newInputStream(new File("./config/☆老爹專屬製作☆遊戲設定☆/武器傷害加成設置.properties").toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            //-----------------------------------------------------------------
            // 武器+9(含)以上附加額外增加傷害值
            /** 是否開啟 */
            WEAPON_POWER = Boolean.parseBoolean(set.getProperty("WeaponPower", "false"));
            /** 各階段強化值附加傷害 */
            if (WEAPON_POWER == true) {
                WEAPON_POWER_LIST = new int[]{Integer.parseInt(set.getProperty("Weapon_Power_09", "0")), Integer.parseInt(set.getProperty("Weapon_Power_10", "0")), Integer.parseInt(set.getProperty("Weapon_Power_11", "0")), Integer.parseInt(set.getProperty("Weapon_Power_12", "0")), Integer.parseInt(set.getProperty("Weapon_Power_13", "0")), Integer.parseInt(set.getProperty("Weapon_Power_14", "0")), Integer.parseInt(set.getProperty("Weapon_Power_15", "0")), Integer.parseInt(set.getProperty("Weapon_Power_16", "0")), Integer.parseInt(set.getProperty("Weapon_Power_17", "0")), Integer.parseInt(set.getProperty("Weapon_Power_18", "0")), Integer.parseInt(set.getProperty("Weapon_Power_19", "0")), Integer.parseInt(set.getProperty("Weapon_Power_20", "0")),};
            }
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigWeaponryHurt);
        } finally {
            set.clear();
        }
    }
}