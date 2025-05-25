package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public final class ConfigOther {
    private static final String LIANG = "./config/other.properties";
    public static boolean SPEED = false;
    public static double SPEED_TIME = 1.0D;
    public static boolean KILLRED = true;
    public static int RATE_XP_WHO = 1;
    public static boolean CLANDEL;
    public static boolean CLANTITLE;
    public static int CLANCOUNT;
    public static boolean LIGHT;
    public static boolean HPBAR;
    public static boolean SHOPINFO;
    public static int HOMEHPR;
    public static int HOMEMPR;
    public static int INNHPR;
    public static int INNMPR;
    public static int CASTLEHPR;
    public static int CASTLEMPR;
    public static int FORESTHPR;
    public static int FORESTMPR;
    public static int SET_GLOBAL;
    public static int SET_GLOBAL_COUNT;
    public static int SET_GLOBAL_TIME;
    public static int ENCOUNTER_LV;
    public static int ILLEGAL_SPEEDUP_PUNISHMENT;
    public static int BOSS_MATERIAL;
    public static int BOSS_COUNT;
    public static int AC_170;
    public static int AC_160;
    public static int AC_150;
    public static int AC_140;
    public static int AC_130;
    public static int AC_120;
    public static int AC_110;
    public static int AC_100;
    public static int AC_90;
    public static int AC_80;
    public static int AC_70;
    public static int AC_60;
    public static int AC_50;
    public static int AC_40;
    public static int AC_30;
    public static int AC_20;
    public static int AC_10;
    public static int CUSTOM_HPR;// 自定義地圖回血量
    public static int CUSTOM_MPR;// 自定義地圖回魔量
    public static int CUSTOM_MAPID;// 自定義回血魔地圖
    public static double Critical_Dmg;//近距離爆擊
    public static double Critical_Dmg_B;//遠距離爆擊

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File("./config/other.properties").toPath());
            set.load(is);
            is.close();
            SPEED = Boolean.parseBoolean(set.getProperty("speed", "false"));
            SPEED_TIME = Double.parseDouble(set.getProperty("speed_time", "1.0"));
            ILLEGAL_SPEEDUP_PUNISHMENT = Integer.parseInt(set.getProperty("Punishment", "0"));
            BOSS_MATERIAL = Integer.parseInt(set.getProperty("Boss_Material ", "40308"));
            BOSS_COUNT = Integer.parseInt(set.getProperty("Boss_COUNT  ", "250000"));
            ENCOUNTER_LV = Integer.parseInt(set.getProperty("encounter_lv", "20"));
            KILLRED = Boolean.parseBoolean(set.getProperty("kill_red", "false"));
            RATE_XP_WHO = Integer.parseInt(set.getProperty("rate_xp_who", "1"));
            CLANDEL = Boolean.parseBoolean(set.getProperty("clanadel", "false"));
            CLANTITLE = Boolean.parseBoolean(set.getProperty("clanatitle", "false"));
            CLANCOUNT = Integer.parseInt(set.getProperty("clancount", "100"));
            LIGHT = Boolean.parseBoolean(set.getProperty("light", "false"));
            HPBAR = Boolean.parseBoolean(set.getProperty("hpbar", "false"));
            SHOPINFO = Boolean.parseBoolean(set.getProperty("shopinfo", "false"));
            HOMEHPR = Integer.parseInt(set.getProperty("Home_hpr", "10"));
            HOMEMPR = Integer.parseInt(set.getProperty("Home_mpr", "10"));
            INNHPR = Integer.parseInt(set.getProperty("Inn_hpr", "10"));
            INNMPR = Integer.parseInt(set.getProperty("Inn_mpr", "10"));
            CASTLEHPR = Integer.parseInt(set.getProperty("Castle_hpr", "10"));
            CASTLEMPR = Integer.parseInt(set.getProperty("Castle_mpr", "10"));
            FORESTHPR = Integer.parseInt(set.getProperty("Forest_hpr", "10"));
            FORESTMPR = Integer.parseInt(set.getProperty("Forest_mpr", "10"));
            SET_GLOBAL = Integer.parseInt(set.getProperty("set_global", "100"));
            SET_GLOBAL_COUNT = Integer.parseInt(set.getProperty("set_global_count", "100"));
            SET_GLOBAL_TIME = Integer.parseInt(set.getProperty("set_global_time", "5"));
            AC_170 = Integer.parseInt(set.getProperty("AC_170", "0"));
            AC_160 = Integer.parseInt(set.getProperty("AC_160", "0"));
            AC_150 = Integer.parseInt(set.getProperty("AC_150", "0"));
            AC_140 = Integer.parseInt(set.getProperty("AC_140", "0"));
            AC_130 = Integer.parseInt(set.getProperty("AC_130", "0"));
            AC_120 = Integer.parseInt(set.getProperty("AC_120", "0"));
            AC_110 = Integer.parseInt(set.getProperty("AC_110", "0"));
            AC_100 = Integer.parseInt(set.getProperty("AC_100", "0"));
            AC_90 = Integer.parseInt(set.getProperty("AC_90", "0"));
            AC_80 = Integer.parseInt(set.getProperty("AC_80", "0"));
            AC_70 = Integer.parseInt(set.getProperty("AC_70", "0"));
            AC_60 = Integer.parseInt(set.getProperty("AC_60", "0"));
            AC_50 = Integer.parseInt(set.getProperty("AC_50", "0"));
            AC_40 = Integer.parseInt(set.getProperty("AC_40", "0"));
            AC_30 = Integer.parseInt(set.getProperty("AC_30", "0"));
            AC_20 = Integer.parseInt(set.getProperty("AC_20", "0"));
            AC_10 = Integer.parseInt(set.getProperty("AC_10", "0"));
            // 自訂義回血魔區
            CUSTOM_HPR = Integer.parseInt(set.getProperty("Costom_hpr", "20"));
            CUSTOM_MPR = Integer.parseInt(set.getProperty("Costom_mpr", "20"));
            CUSTOM_MAPID = Integer.parseInt(set.getProperty("Costom_mapid", "4"));
            Critical_Dmg = Double.parseDouble(set.getProperty("Critical_Dmg", "1.00"));
            Critical_Dmg_B = Double.parseDouble(set.getProperty("Critical_Dmg_B", "1.00"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + LIANG);
        } finally {
            set.clear();
        }
    }
}
