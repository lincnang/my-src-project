package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 幻術師設置
 *
 * @author dexc
 */
public final class ConfigSkillIllusion {
    private static final String ConfigSkillIllusion = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/幻術師_技能設定表.properties";
    /**
     * 化身技能承受傷害(數字越高自己被打的傷害越高)
     */
    public static double ILLUSION_AVATAR_DAMAGE;
    /**
     * 幻術 - 疼痛的歡愉最大傷害限制
     */
    public static int JOY_OF_PAIN_DMG;
    /**
     * 幻術-二轉：奇古獸攻擊傷害增加10%
     */
    public static double IS2;
    /**
     * 幻術-四轉：化身傷害提升為1.5倍(原本1.3倍)
     */
    public static double IS4;
    public static int Skulldamage;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigSkillIllusion));
            set.load(is);
            is.close();
            IS2 = Double.parseDouble(set.getProperty("Illusion_damage_Turn2", "0.1"));
            IS4 = Double.parseDouble(set.getProperty("Illusion_Dmg_Turn4", "1.5"));
            JOY_OF_PAIN_DMG = Integer.parseInt(set.getProperty("JOY_OF_PAIN_DMG", "0"));
            ILLUSION_AVATAR_DAMAGE = Double.parseDouble(set.getProperty("ILLUSION_AVATAR_DAMAGE", "1.5"));
            Skulldamage = Integer.parseInt(set.getProperty("Skull_damage", "10"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillIllusion);
        } finally {
            set.clear();
        }
    }
}