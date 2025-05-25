package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 掃街系統設置
 *
 */
public final class ConfigFreeKill {
    private static final String ConfigFreeKill = "./config/☆老爹專屬製作☆遊戲設定☆/掃街系統設置.properties";
    /**
     * 隨機掃街啟動開關
     */
    public static boolean FREE_FIGHT_SWITCH; //src015
    /**
     * 隨機掃街死亡掉落裝備機率
     */
    public static int FREE_FIGHT_DROP_CHANCE_A;
    /**
     * 隨機掃街死亡掉落裝備機率
     */
    public static int FREE_FIGHT_DROP_CHANCE_B;
    /**
     * 隨機掃街持續時間 (單位:小時)
     */
    public static int FREE_FIGHT_REMAIN_TIME;
    /**
     * 隨機掃街時段(24小時制 00:00~23:59)
     */
    public static String[] FREE_FIGHT_TIME_LIST;
    /**
     * 隨機掃街地圖抽取張數 (將隨機抽選A~B張地圖出來掃街)
     */
    public static int FREE_FIGHT_MAP_MIN;
    /**
     * 隨機掃街地圖抽取張數 (將隨機抽選A~B張地圖出來掃街)
     */
    public static int FREE_FIGHT_MAP_MAX;
    /**
     * 隨機掃街地圖列表 (逗號區隔)
     */
    public static String[] FREE_FIGHT_MAP_LIST;
    /**
     * 隨機掃街死亡掉落道具最高數量
     */
    public static int FREE_FIGHT_MAX_DROP;
    /**
     * 是否開啟全地圖掃街
     */
    public static boolean FREE_FIGHT_ALLMAP;

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigFreeKill));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            FREE_FIGHT_SWITCH = Boolean.parseBoolean(set.getProperty("Free_Fight_Switch", "false"));
            FREE_FIGHT_DROP_CHANCE_A = Integer.parseInt(set.getProperty("Free_Fight_Drop_Chance_A", "0"));
            FREE_FIGHT_DROP_CHANCE_B = Integer.parseInt(set.getProperty("Free_Fight_Drop_Chance_B", "0"));
            FREE_FIGHT_ALLMAP = Boolean.parseBoolean(set.getProperty("Free_Fight_All_Map", "false"));
            String temp_3 = set.getProperty("Free_Fight_Time_List");
            if (temp_3 != null) {
                FREE_FIGHT_TIME_LIST = temp_3.split(",");
            }
            FREE_FIGHT_REMAIN_TIME = Integer.parseInt(set.getProperty("Free_Fight_Remain_Time", "1"));
            FREE_FIGHT_MAX_DROP = Integer.parseInt(set.getProperty("Free_Fight_Max_Drop", "1"));
            String fightTmp = set.getProperty("Free_Fight_Map_Choice", "");
            if (!fightTmp.equalsIgnoreCase("null")) {
                String[] fightTmp2 = fightTmp.split("-");
                if (fightTmp2.length == 2) {
                    FREE_FIGHT_MAP_MIN = Integer.parseInt(fightTmp2[0]);
                    FREE_FIGHT_MAP_MAX = Integer.parseInt(fightTmp2[1]);
                }
            }
            String temp_4 = set.getProperty("Free_Fight_Map_List");
            if (temp_4 != null) {
                FREE_FIGHT_MAP_LIST = temp_4.split(",");
            }
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigFreeKill);
        } finally {
            set.clear();
        }
    }
}