package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 黑暗妖精設置
 *
 * @author dexc
 */
public final class ConfigSkillDarkElf {
    private static final String ConfigSkillDarkElf = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/黑暗妖精_技能設定表.properties";
    /**
     * #黑妖 燃燒鬥志發動機率(%)
     */
    public static int BURNINGSPIRITchance;
    /**
     * 黑妖 燃燒鬥志傷害倍率
     */
    public static int BURNINGSPIRITdmg;
    /**
     * 雙重破壞傷害倍率
     */
    public static double Double_Brake_Dmg;
    /**
     * 破壞盔甲傷害倍率
     */
    public static double Armor_Break_Dmg;
    /**
     * 破壞盔甲命中機率
     */
    public static int Damage1;
    public static int Damage2;
    public static int Damage3;
    /**
     * 黑妖-二轉：燃燒鬥志發動機率提升10%(原本33%)(設0關閉)
     */
    public static int D2;
    /**
     * 黑妖-四轉：雙重破壞傷害倍率提升為2倍(原本1.8倍)設定0.2表示原本1.8+0.2=2倍 等於是設定額外加X%傷害值意思 (設0.0關閉)
     */
    public static double D4;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigSkillDarkElf));
            set.load(is);
            is.close();
            Damage1 = Integer.parseInt(set.getProperty("Damage_1", "10"));
            Damage2 = Integer.parseInt(set.getProperty("Damage_2", "10"));
            Damage3 = Integer.parseInt(set.getProperty("Damage_3", "10"));
            BURNINGSPIRITchance = Integer.parseInt(set.getProperty("Burning_chance", "33"));
            BURNINGSPIRITdmg = Integer.parseInt(set.getProperty("Burning_dmg", "5"));
            //黑妖 雙重破壞傷害倍率(說明:設置1.8為1.8倍 )
            Double_Brake_Dmg = Double.parseDouble(set.getProperty("Double_Brake_Dmg", "1.8"));
            //黑妖 破壞盔甲傷害倍率(說明:設置0.58為額外加0.58%傷害)
            Armor_Break_Dmg = Double.parseDouble(set.getProperty("Armor_Break_Dmg", "0.58"));
            D2 = Integer.parseInt(set.getProperty("Burning_dmg_Turn2", "1"));
            D4 = Double.parseDouble(set.getProperty("Double_Brake_Turn4", "1.0"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillDarkElf);
        } finally {
            set.clear();
        }
    }
}