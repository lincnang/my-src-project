package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服务器人物属性设置
 *
 * @author dexc
 */
public final class ConfigCharSetting {
    private static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";
    public static int PRINCE_MAX_HP;
    public static int PRINCE_MAX_MP;
    public static int KNIGHT_MAX_HP;
    public static int KNIGHT_MAX_MP;
    public static int ELF_MAX_HP;
    public static int ELF_MAX_MP;
    public static int WIZARD_MAX_HP;
    public static int WIZARD_MAX_MP;
    public static int DARKELF_MAX_HP;
    public static int DARKELF_MAX_MP;
    public static int DRAGONKNIGHT_MAX_HP;
    public static int DRAGONKNIGHT_MAX_MP;
    public static int ILLUSIONIST_MAX_HP;
    public static int ILLUSIONIST_MAX_MP;
    public static int WARRIOR_MAX_HP;
    public static int WARRIOR_MAX_MP;
    /**
     * 最高等級.
     */
    public static int MAX_LEVEL;

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            InputStream is = new FileInputStream(new File(CHAR_SETTINGS_CONFIG_FILE));
            set.load(is);
            is.close();
            PRINCE_MAX_HP = Integer.parseInt(set.getProperty("Prince_Max_HP", "1000"));
            PRINCE_MAX_MP = Integer.parseInt(set.getProperty("Prince_Max_MP", "800"));
            KNIGHT_MAX_HP = Integer.parseInt(set.getProperty("Knight_Max_HP", "1400"));
            KNIGHT_MAX_MP = Integer.parseInt(set.getProperty("Knight_Max_MP", "600"));
            ELF_MAX_HP = Integer.parseInt(set.getProperty("Elf_Max_HP", "1000"));
            ELF_MAX_MP = Integer.parseInt(set.getProperty("Elf_Max_MP", "900"));
            WIZARD_MAX_HP = Integer.parseInt(set.getProperty("Wizard_Max_HP", "800"));
            WIZARD_MAX_MP = Integer.parseInt(set.getProperty("Wizard_Max_MP", "1200"));
            DARKELF_MAX_HP = Integer.parseInt(set.getProperty("Darkelf_Max_HP", "1000"));
            DARKELF_MAX_MP = Integer.parseInt(set.getProperty("Darkelf_Max_MP", "900"));
            DRAGONKNIGHT_MAX_HP = Integer.parseInt(set.getProperty("DragonKnight_Max_HP", "1400"));
            DRAGONKNIGHT_MAX_MP = Integer.parseInt(set.getProperty("DragonKnight_Max_MP", "600"));
            ILLUSIONIST_MAX_HP = Integer.parseInt(set.getProperty("Illusionist_Max_HP", "900"));
            ILLUSIONIST_MAX_MP = Integer.parseInt(set.getProperty("Illusionist_Max_MP", "1100"));
            WARRIOR_MAX_HP = Integer.parseInt(set.getProperty("Warrior_Max_HP", "1400"));
            WARRIOR_MAX_MP = Integer.parseInt(set.getProperty("Warrior_Max_MP", "600"));
            MAX_LEVEL = Integer.parseInt(set.getProperty("maxLevel", "99"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + CHAR_SETTINGS_CONFIG_FILE);
        } finally {
            set.clear();
        }
    }
}
