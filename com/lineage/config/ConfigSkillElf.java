package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服務器活動設置
 *
 * @author dexc
 */
public final class ConfigSkillElf {
    private static final String ConfigSkillElf = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/妖精_技能設定表.properties";
    public static int TRIPLE_ARROW_DEX;//三重矢原始傷害倍率
    public static double TRIPLE_ARROW_DMG;//三重矢原始傷害倍率
    public static double STRIKER_DMG;
    public static double STRIKER_DMG2;
    public static double SOUL_OF_FLAME_DAMAGE;
    /**
     * 精準射擊機率設定
     **/
    public static int Precision1;
    public static int Precision2;
    public static int Precision3;
    public static double E2TRIPLE_ARROW;
    public static double E2ELEMENTAL_FIRE;
    public static double E4STRIKER_GALE;
    public static double E4SOUL_OF_FLAME;
    public static double E4NATURES_BLESSING;
    public static int ElfThree;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigSkillElf));
            set.load(is);
            is.close();
            //敏捷大於多少三重矢強化
            TRIPLE_ARROW_DEX = Integer.parseInt(set.getProperty("Triple_Arrow_Dex", "180"));
            //三重矢原始傷害倍率
            TRIPLE_ARROW_DMG = Double.parseDouble(set.getProperty("Triple_Arrow_Dmg", "0.8"));
            STRIKER_DMG = Double.parseDouble(set.getProperty("STRIKER_DMG", "1.0"));
            STRIKER_DMG2 = Double.parseDouble(set.getProperty("STRIKER_DMG2", "1.0"));
            SOUL_OF_FLAME_DAMAGE = Double.parseDouble(set.getProperty("SOUL_OF_FLAME_DAMAGE", "1.0"));
            Precision1 = Integer.parseInt(set.getProperty("Precision_1", "0"));
            Precision2 = Integer.parseInt(set.getProperty("Precision_2", "0"));
            Precision3 = Integer.parseInt(set.getProperty("Precision_3", "0"));
            E2TRIPLE_ARROW = Double.parseDouble(set.getProperty("ElfTurn_Triple_Arrow_Dmg", "1.0"));
            E2ELEMENTAL_FIRE = Double.parseDouble(set.getProperty("ElfTurn_ELEMENTAL_FIRE", "1.0"));
            E4STRIKER_GALE = Double.parseDouble(set.getProperty("ElfTurn_Precision", "1.0"));
            E4SOUL_OF_FLAME = Double.parseDouble(set.getProperty("ElfTurn_FLAME", "1.0"));
            E4NATURES_BLESSING = Double.parseDouble(set.getProperty("ElfTurn_BLESSING", "1.0"));
            ElfThree = Integer.parseInt(set.getProperty("Triple_Arrow", "3"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillElf);
        } finally {
            set.clear();
        }
    }
}