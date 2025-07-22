package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
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

    // 技能冷卻時間（單位：秒）
    public static int ShadowRecovery_Cooldown;

    // 持續HOT時間（單位：秒）
    public static int ShadowRecovery_HotTime;

    // 近戰閃避加成
    public static int ShadowRecovery_EvasionMelee;

    // 遠距閃避加成
    public static int ShadowRecovery_EvasionRanged;

    // 路西法被動：基礎減傷
    public static int LUCIFER_PASSIVE_BASE_REDUCE;
    // 路西法被動：每幾級增加一點減傷
    public static int LUCIFER_PASSIVE_LV_STEP;
    // 路西法被動：每次等級加成的減傷數值
    public static int LUCIFER_PASSIVE_LV_BONUS;


    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigSkillDarkElf).toPath());
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
            // 取得並轉型各設定項目，如果檔案沒有會帶入預設值
            ShadowRecovery_Cooldown = Integer.parseInt(set.getProperty("ShadowRecovery_Cooldown", "1200")); // 預設1200秒
            ShadowRecovery_HotTime = Integer.parseInt(set.getProperty("ShadowRecovery_HotTime", "5"));       // 預設5秒
            ShadowRecovery_EvasionMelee = Integer.parseInt(set.getProperty("ShadowRecovery_EvasionMelee", "15")); // 預設+15
            ShadowRecovery_EvasionRanged = Integer.parseInt(set.getProperty("ShadowRecovery_EvasionRanged", "15")); // 預設+15
            // 路西法被動技能設定
            LUCIFER_PASSIVE_BASE_REDUCE = Integer.parseInt(set.getProperty("Lucifer_BaseReduce", "5")); // 預設5
            LUCIFER_PASSIVE_LV_STEP = Integer.parseInt(set.getProperty("Lucifer_LvStep", "10")); // 預設每10級加成
            LUCIFER_PASSIVE_LV_BONUS = Integer.parseInt(set.getProperty("Lucifer_LvBonus", "1")); // 預設每次+1


        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillDarkElf);
        } finally {
            set.clear();
        }
    }
}