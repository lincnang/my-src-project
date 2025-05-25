package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 武防相關設置
 *
 * @author dexc
 */
public final class ConfigWeaponArmor {
    private static final String ConfigWeaponArmor = "./config/☆老爹專屬製作☆遊戲設定☆/武防相關設置.properties";
    public static int ELYOS_ENCHANT_SUCCESS;
    public static int Orim_ENCHANT_SUCCESS;
    public static int Orim_ENCHANT_SUCCESS_BLESS;
    public static int enchantovertell1;//防武安定廣播
    public static int enchantovertell2;
    public static boolean enchantoverTF;
    public static int enchantweapon;
    public static int enchantarmor;
    public static int enchantoverType;//防武安定廣播END
    /**
     * BOSS水晶強化機率
     */
    public static boolean Cross_BOSS;//boss水晶
    public static boolean DE_BOSS;//boss水晶
    public static int WEAPON100;
    public static int ARMOR100;
    public static int ELYOS_ENCHANT;// 最高只能強化到
    public static int ELYOS2_ENCHANT;// 最高只能強化到

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigWeaponArmor).toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            ELYOS_ENCHANT_SUCCESS = Integer.parseInt(set.getProperty("Elyos_Enchant_Success", "0"));
            Orim_ENCHANT_SUCCESS = Integer.parseInt(set.getProperty("Orim_Enchant_Success", "0"));
            Orim_ENCHANT_SUCCESS_BLESS = Integer.parseInt(set.getProperty("Orim_Enchant_Success_Bless", "0"));
            //防武安定沖裝廣播
            enchantweapon = Integer.parseInt(set.getProperty("Enchant_over_Weapon", "20"));
            enchantarmor = Integer.parseInt(set.getProperty("Enchant_over_Armor", "20"));
            enchantovertell1 = Integer.parseInt(set.getProperty("Enchant_broadcast_Weapon", "3"));
            enchantovertell2 = Integer.parseInt(set.getProperty("Enchant_broadcast_Armor", "3"));
            enchantoverTF = Boolean.parseBoolean(set.getProperty("Enchant_Stability_broadcast", "false")); //是否啟用過多少安定廣播系統
            enchantoverType = Integer.parseInt(set.getProperty("Enchant_over_Type", "1"));
            //boss水晶
            Cross_BOSS = Boolean.parseBoolean(set.getProperty("Crystal_Boss_Type", "false"));
            //boss水晶
            DE_BOSS = Boolean.parseBoolean(set.getProperty("Crystal_Boss_failure", "false"));
            WEAPON100 = Integer.parseInt(set.getProperty("Weapon_100", "30"));
            ARMOR100 = Integer.parseInt(set.getProperty("Armor_100", "30"));
            // 最高只能強化到
            ELYOS_ENCHANT = Integer.parseInt(set.getProperty("Elyos_Enchant_Weapon", "0"));
            // 最高只能強化到
            ELYOS2_ENCHANT = Integer.parseInt(set.getProperty("Elyos_Enchant_Armor", "0"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigWeaponArmor);
        } finally {
            set.clear();
        }
    }
}