package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 騎士設置
 *
 * @author dexc
 */
public final class ConfigSkillKnight {
    private static final String ConfigSkillKnight = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/騎士_技能設定表.properties";
    /**
     * 衝暈秒數
     */
    public static String STUN_SEC;
    /**
     * 衝擊之暈(設定=攻擊者等級小於被攻擊者，攻擊者等級等於被攻擊者，攻擊者等級大於等於被攻擊者)
     */
    public static int ImpactHalo1;
    public static int ImpactHalo2;
    public static int ImpactHalo3;
    /**
     * 騎士 反擊屏障發動機率(%)
     */
    public static int BARRIERchance;
    /**
     * 反擊屏障傷害倍率
     */
    public static int Counterattack;
    /**
     * 傷害轉化為回血的比例（例如 50%）
     */
    public static int BARRIER_HEAL_PERCENT;

    /**
     * 騎士-二轉：衝擊之暈秒數增加一秒(原本1~5秒)(設0關閉)
     */
    public static int K2;
    /**
     * 騎士-四轉：反擊屏障機率增加10%(原本35%)(設0關閉)
     */
    public static int K4;
    public static boolean KnightYN;
    public static boolean elfisBARRIER = false;
    public static int SHOCK1TIME;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(ConfigSkillKnight).toPath());
            set.load(is);
            is.close();
            STUN_SEC = set.getProperty("SHOCK_STUN_TIMER ", "3~6");
            ImpactHalo1 = Integer.parseInt(set.getProperty("Impact_Halo_1", "1"));
            ImpactHalo2 = Integer.parseInt(set.getProperty("Impact_Halo_2", "1"));
            ImpactHalo3 = Integer.parseInt(set.getProperty("Impact_Halo_3", "1"));
            BARRIERchance = Integer.parseInt(set.getProperty("Counter_Barrier", "33"));
            Counterattack = Integer.parseInt(set.getProperty("CounterAttack", "3.0"));
            BARRIER_HEAL_PERCENT = Integer.parseInt(set.getProperty("BARRIER_HEAL_PERCENT", "50"));
            K2 = Integer.parseInt(set.getProperty("KnightTurn_Shock_TIMER", "1"));
            K4 = Integer.parseInt(set.getProperty("KnightTurn_Counter_Barrier", "1"));
            elfisBARRIER = Boolean.parseBoolean(set.getProperty("elf_isBarrier", "false"));
            SHOCK1TIME = Integer.parseInt(set.getProperty("SHOCK_TIME", "2"));
            KnightYN = Boolean.parseBoolean(set.getProperty("KnightYN", "true"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigSkillKnight);
        } finally {
            set.clear();
        }
    }
}