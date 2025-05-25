package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 轉生系統設置
 *
 */
public final class ConfigTurn {
    private static final String ATTR_SETTINGS_FILE = "./config/☆老爹專屬製作☆遊戲設定☆/轉生系統設置.properties";
    public static boolean ReincarnationBroad;// 轉生是否會公告
    public static int METE_LEVEL;
    public static int METE_MAX_COUNT;
    public static int METE_EXP_REDUCE;
    public static int METE_REMAIN_HP;
    public static int METE_REMAIN_MP;
    //-轉身天賦--------------------------------
    public static boolean METE_GIVE_POTION;
    /**
     * 重置轉生天賦需要的道具
     **/
    public static int ReiItemId;
    /**
     * 重置轉生天賦需要的道具數量
     **/
    public static int ReiItemCount;
    /**
     * 是否開啟使用轉生藥水給予天賦點數
     **/
    public static boolean ReiPoint;
    /**
     * 轉生藥水增加轉生天賦點數
     **/
    public static String ReiPoinMete;
    /**
     * 轉生天賦點數上限
     **/
    public static int ReiPointUp;
    /**
     * 全職第一項天賦技能等級上限
     **/
    public static int ReiPointLv_1;
    /**
     * 全職第二項天賦技能等級上限
     **/
    public static int ReiPointLv_2;
    /**
     * 全職第三項天賦技能等級上限
     **/
    public static int ReiPointLv_3;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ATTR_SETTINGS_FILE));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            // 轉生是否會公告
            ReincarnationBroad = Boolean.parseBoolean(set.getProperty("Mete_Remain_Broad", "false"));
            METE_LEVEL = Integer.parseInt(set.getProperty("Mete_Level", "99"));
            METE_MAX_COUNT = Integer.parseInt(set.getProperty("Mete_Max_Count", "20"));
            METE_EXP_REDUCE = Integer.parseInt(set.getProperty("Mete_Exp_Reduce", "0"));
            METE_REMAIN_HP = Integer.parseInt(set.getProperty("Mete_Remain_Hp", "15"));
            METE_REMAIN_MP = Integer.parseInt(set.getProperty("Mete_Remain_Mp", "15"));
            METE_GIVE_POTION = Boolean.parseBoolean(set.getProperty("Mete_Give_Potion", "false"));
            // 重置轉生天賦需要的道具
            ReiItemId = Integer.parseInt(set.getProperty("Talent_ItemId", "44070"));
            // 重置轉生天賦需要的道具數量
            ReiItemCount = Integer.parseInt(set.getProperty("Talent_ItemCount", "100"));
            // 是否開啟使用轉生藥水給予天賦點數
            ReiPoint = Boolean.parseBoolean(set.getProperty("Talent_Point", "false"));
            // 轉生藥水增加轉生天賦點數
            ReiPoinMete = set.getProperty("Talent_PoinMete", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
            // 轉生天賦點數上限
            ReiPointUp = Integer.parseInt(set.getProperty("Talent_PointUp", "20"));
            // 全職第一項天賦技能等級上限**/
            ReiPointLv_1 = Integer.parseInt(set.getProperty("Talent_PointLv_1", "10"));
            // 全職第二項天賦技能等級上限**/
            ReiPointLv_2 = Integer.parseInt(set.getProperty("Talent_PointLv_2", "10"));
            // 全職第三項天賦技能等級上限**/
            ReiPointLv_3 = Integer.parseInt(set.getProperty("Talent_PointLv_3", "10"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ATTR_SETTINGS_FILE);
        } finally {
            set.clear();
        }
    }
}