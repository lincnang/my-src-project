package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 法師魔法技能調整
 *
 * @author dexc
 */
public final class ConfigSkillWizard {
    /***/
    private static final String ConfigSkillWizard = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/法師_技能設定表.properties";
    /** #施放負面魔法是否依照智力，超過IntBigger點每提升一點就增加IntRepeated %機率， */
    /**
     * 光裂兩次機率
     */
    public static int DISINTEGRATE_RND;
    /**
     * #當智力大於(含)IntLess點，機率百分之百? (True=是, False=否)
     */
    public static boolean AddInt;
    public static int IntBigger;
    public static int IntRepeated;
    public static int IntLess;
    /**
     * 法師-二轉：聖結界施放變為施法對象周圍X格都有效果 (設0關閉)
     */
    public static int Immune_TO_Turn2;//法師二轉
    /**
     * 法師-四轉：究光跟流星雨傷害提升X% (設定1.1為增加10%傷害) (設1.0關閉)
     */
    public static double Meteor_TO_Turn4;//法師二轉
    /***/
    /***/
    public static String CURSE_SEC;
    /**
     * 法師新技能 治癒逆行機率設定
     **/
    public static int DEATH_HEAL;
    /***/
    public static String NO_CD;
    /***/
    /***/
    /***/
    /***/
    public static String HAS_SKILL;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigSkillWizard).toPath());
            set.load(is);
            is.close();
            DISINTEGRATE_RND = Integer.parseInt(set.getProperty("Disintegrate_Rnd", "25"));
            AddInt = Boolean.parseBoolean(set.getProperty("Add_Int", "false"));
            IntBigger = Integer.parseInt(set.getProperty("Int_Bigger", "25"));
            IntRepeated = Integer.parseInt(set.getProperty("Int_Repeated", "1"));
            IntLess = Integer.parseInt(set.getProperty("Int_Less", "127"));
            Immune_TO_Turn2 = Integer.parseInt(set.getProperty("Immune_TO_Turn2", "1"));
            Meteor_TO_Turn4 = Double.parseDouble(set.getProperty("Meteor_TO_Turn4", "1.0"));
            CURSE_SEC = set.getProperty("CURSE_SEC", "3~7");
            DEATH_HEAL = Integer.parseInt(set.getProperty("DEATH_HEAL", "20"));//法師新技能 治癒逆行機率設定
            NO_CD = set.getProperty("NO_CD", "null");
            HAS_SKILL = set.getProperty("HAS_SKILL", "null");
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillWizard);
        } finally {
            set.clear();
        }
    }
}