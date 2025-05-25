package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 王子設置
 *
 * @author dexc
 */
public final class ConfigSkillCrown {
    private static final String ConfigSkillCrown = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/王族_技能設定表.properties";
    /**
     * 二轉：精準目標傷害增加10%(原本1倍)(設0.0關閉)
     */
    public static double Crown_TRUE_TARGET_Turn2;
    /**
     * 四轉：勇猛意志隨機傷害提升為2倍(原本1.5倍)(設1.5關閉)
     */
    public static double Crown_BRAVE_MENTAL_Turn4;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigSkillCrown).toPath());
            set.load(is);
            is.close();
            Crown_TRUE_TARGET_Turn2 = Double.parseDouble(set.getProperty("Crown_TRUE_TARGET_Turn2", "1.0"));
            Crown_BRAVE_MENTAL_Turn4 = Double.parseDouble(set.getProperty("Crown_BRAVE_MENTAL_Turn4", "1.0"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillCrown);
        } finally {
            set.clear();
        }
    }
}