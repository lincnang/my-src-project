package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 戰士設置
 *
 * @author dexc
 */
public final class ConfigSkillWarrior {
    private static final String ConfigSkillWarrior = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/戰士_技能設定表.properties";
    /**
     * 戰士魔法 泰坦:子彈 泰坦:魔法 泰坦:岩石 血量低於多少%才發動(0.4=40%  正服設定40%)
     */
    public static double Warrior_Magic;
    /**
     * 戰士魔法 (泰坦: 魔法)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
     */
    public static int isPassive_Tatin_Magic;
    /**
     * 戰士魔法 (泰坦: 岩石)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
     */
    public static int isPassive_Tatin_Rock;
    /**
     * 戰士魔法 (泰坦: 子彈)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
     */
    public static int isPassive_Tatin_Bullet;
    /**
     * 戰士魔法 (粉碎)
     */
    public static int isPassive_Tatin_CRASH;
    /**
     * 戰士魔法 (狂暴)
     */
    public static int isPassive_Tatin_FURY;
    /**
     * 蓋亞技能
     */
    public static int Gaia_HotTime;    // 蓋亞技能HOT秒數
    public static int Gaia_Cooldown;   // 蓋亞技能冷卻（秒）
    /**
     * 粉碎 狂暴 發動率提升為20%(原本10%)
     */
    public static int WHYW2;
    /**
     * 四轉：泰坦魔法發動血量提升為80%就發動(原本60%)
     */
    public static int WHYW4;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigSkillWarrior).toPath());
            set.load(is);
            is.close();
            //戰士魔法 泰坦:子彈 泰坦:魔法 泰坦:岩石 血量低於多少%才發動(0.4=40%  正服設定40%)
            Warrior_Magic = Double.parseDouble(set.getProperty("Warrior_Magic", "0.4"));
            //戰士魔法 (泰坦: 魔法)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
            isPassive_Tatin_Magic = Integer.parseInt(set.getProperty("isPassive_Tatin_Magic", "10"));
            //戰士魔法 (泰坦: 岩石)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
            isPassive_Tatin_Rock = Integer.parseInt(set.getProperty("isPassive_Tatin_Rock", "10"));
            //戰士魔法 (泰坦: 子彈)反彈時額外隨機傷害增加(設定50 = 隨機反彈1 ~ 50傷害)
            isPassive_Tatin_Bullet = Integer.parseInt(set.getProperty("isPassive_Tatin_Bullet", "10"));
            //戰士魔法 (粉碎)
            isPassive_Tatin_CRASH = Integer.parseInt(set.getProperty("isPassive_Tatin_CRASH", "19"));
            //戰士魔法 (狂暴)
            isPassive_Tatin_FURY = Integer.parseInt(set.getProperty("isPassive_Tatin_FURY", "19"));
            WHYW2 = Integer.parseInt(set.getProperty("WarriorTurn_Tatin_CRASH", "15"));
            WHYW4 = Integer.parseInt(set.getProperty("WarriorTurn_Tatin_Magic", "15"));
            Gaia_HotTime = Integer.parseInt(set.getProperty("Gaia_HotTime", "5")); // 預設5秒
            Gaia_Cooldown = Integer.parseInt(set.getProperty("Gaia_Cooldown", "1200"));

        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillWarrior);
        } finally {
            set.clear();
        }
    }
}