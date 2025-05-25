package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 龍騎士設置
 *
 * @author dexc
 */
public final class ConfigSkillDragon {
    private static final String ConfigSkillDragon = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/龍騎士_技能設定表.properties";
    /**
     * 屠宰衝暈機率(1=1%)
     */
    public static int FOE_SLAYER_RND;
    /**弱點曝光相關設定*/
    /**
     * 屠宰衝暈秒數(1000=1秒)
     */
    public static int FOE_SLAYER_SEC;
    /**
     * 裝備 (410189 殲滅者鎖鏈劍) 額外弱點曝光機率(%)
     */
    public static int VULNERABILITY_OTHER;
    /**
     * #弱點曝光啟動機率
     */
    public static int VULNERABILITY_ROM;
    public static int Vulnerability1;
    public static int Vulnerability2;
    public static int Vulnerability3;
    /**
     * 奪命之雷(攻擊者等級小於被攻擊者
     */
    public static int SLAY_BREAK_1;
    /**
     * 奪命之雷(攻擊者等級等於被攻擊者)
     */
    public static int SLAY_BREAK_2;
    /**
     * 奪命之雷(攻擊者等級大於等於被攻擊者)
     */
    public static int SLAY_BREAK_3;
    /**
     * 奪命之雷是否限制瞬移
     */
    public static boolean SLAY_BREAK_NOT_TELEPORT;
    /**
     * 致命身軀發動機率
     */
    public static int MORTAL_BODY_ROM;
    /**
     * 致命身軀反彈傷害
     */
    public static int MORTAL_BODY_DMG;
    /**
     * 龍騎士-二轉：弱點曝光額外增加多少傷害,這邊為原本傷害再加額外轉生傷害
     */
    public static int DK2weaknss1;
    public static int DK2weaknss2;
    public static int DK2weaknss3;
    /**
     * 屠宰者追加15%機率衝暈一秒
     */
    public static int DK4RANDOM;
    /**
     * 屠宰者追加15%機率衝暈一秒
     */
    public static int DK4SHOCKTIME;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigSkillDragon));
            set.load(is);
            is.close();
            FOE_SLAYER_RND = Integer.parseInt(set.getProperty("FOE_SLAYER_RND", "25"));
            FOE_SLAYER_SEC = Integer.parseInt(set.getProperty("FOE_SLAYER_SEC", "100"));
            Vulnerability1 = Integer.parseInt(set.getProperty("Vulnerability1", "20"));
            Vulnerability2 = Integer.parseInt(set.getProperty("Vulnerability2", "40"));
            Vulnerability3 = Integer.parseInt(set.getProperty("Vulnerability3", "60"));
            VULNERABILITY_ROM = Integer.parseInt(set.getProperty("VULNERABILITY_ROM", "15"));
            DK2weaknss1 = Integer.parseInt(set.getProperty("DragonTurn_Vulnerability_1", "5"));
            DK2weaknss2 = Integer.parseInt(set.getProperty("DragonTurn_Vulnerability_2", "10"));
            DK2weaknss3 = Integer.parseInt(set.getProperty("DragonTurn_Vulnerability_3", "15"));
            DK4RANDOM = Integer.parseInt(set.getProperty("DragonTurn_FOE_SLAYER_RND", "15"));
            DK4SHOCKTIME = Integer.parseInt(set.getProperty("DragonTurn_FOE_SLAYER_SEC", "1"));
            SLAY_BREAK_1 = Integer.parseInt(set.getProperty("SLAY_BREAK_1", "5"));
            SLAY_BREAK_2 = Integer.parseInt(set.getProperty("SLAY_BREAK_2", "10"));
            SLAY_BREAK_3 = Integer.parseInt(set.getProperty("SLAY_BREAK_3", "15"));
            SLAY_BREAK_NOT_TELEPORT = Boolean.parseBoolean(set.getProperty("SLAY_BREAK_NOT_TELEPORT", "true"));
            MORTAL_BODY_ROM = Integer.parseInt(set.getProperty("MORTAL_BODY_ROM", "23"));
            MORTAL_BODY_DMG = Integer.parseInt(set.getProperty("MORTAL_BODY_DMG", "30"));
            VULNERABILITY_OTHER = Integer.parseInt(set.getProperty("VULNERABILITY_OTHER", "10"));
            /**裝備 (410189 殲滅者鎖鏈劍) 額外弱點曝光機率(%)*/
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillDragon);
        } finally {
            set.clear();
        }
    }
}