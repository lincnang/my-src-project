package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 潘朵拉商城消費給予VIP狀態
 */
public final class ConfigGiveVip {
    private static final String GIVE_VIP_FILE = "./config/☆老爹專屬製作☆遊戲設定☆/潘朵拉商城消費設置.properties";
    /**
     * 是否開啟潘朵拉商城消費多少給予VIP狀態
     **/
    public static boolean Give_Vip;
    /**
     * (第1組)消費多少給予VIP狀態
     **/
    public static int Give_Vip_Price_1;
    /**
     * (第1組)給予VIP狀態等級
     **/
    public static int Give_Vip_Level_1;
    /**
     * (第1組)給予VIP狀態時間(日)
     **/
    public static int Give_Vip_Day_1;
    /**
     * (第2組)消費多少給予VIP狀態
     **/
    public static int Give_Vip_Price_2;
    /**
     * (第2組)給予VIP狀態等級
     **/
    public static int Give_Vip_Level_2;
    /**
     * (第2組)給予VIP狀態時間(日)
     **/
    public static int Give_Vip_Day_2;
    /**
     * (第3組)消費多少給予VIP狀態
     **/
    public static int Give_Vip_Price_3;
    /**
     * (第3組)給予VIP狀態等級
     **/
    public static int Give_Vip_Level_3;
    /**
     * (第3組)給予VIP狀態時間(日)
     **/
    public static int Give_Vip_Day_3;
    /**
     * (第4組)消費多少給予VIP狀態
     **/
    public static int Give_Vip_Price_4;
    /**
     * (第4組)給予VIP狀態等級
     **/
    public static int Give_Vip_Level_4;
    /**
     * (第4組)給予VIP狀態時間(日)
     **/
    public static int Give_Vip_Day_4;
    /**
     * (第5組)消費多少給予VIP狀態
     **/
    public static int Give_Vip_Price_5;
    /**
     * (第5組)給予VIP狀態等級
     **/
    public static int Give_Vip_Level_5;
    /**
     * (第5組)給予VIP狀態時間(日)
     **/
    public static int Give_Vip_Day_5;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(GIVE_VIP_FILE));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            // 是否開啟潘朵拉商城消費多少給予VIP狀態
            Give_Vip = Boolean.parseBoolean(set.getProperty("give_vip", "false"));
            // (第1組)消費多少給予VIP狀態
            Give_Vip_Price_1 = Integer.parseInt(set.getProperty("give_vip_price_1", "10000"));
            // (第1組)給予VIP狀態等級
            Give_Vip_Level_1 = Integer.parseInt(set.getProperty("give_vip_level_1", "1"));
            // (第1組)給予VIP狀態時間(日)
            Give_Vip_Day_1 = Integer.parseInt(set.getProperty("give_vip_day_1", "1"));
            // (第2組)消費多少給予VIP狀態
            Give_Vip_Price_2 = Integer.parseInt(set.getProperty("give_vip_price_2", "20000"));
            // (第2組)給予VIP狀態等級
            Give_Vip_Level_2 = Integer.parseInt(set.getProperty("give_vip_level_2", "2"));
            // (第2組)給予VIP狀態時間(日)
            Give_Vip_Day_2 = Integer.parseInt(set.getProperty("give_vip_day_2", "2"));
            // (第3組)消費多少給予VIP狀態
            Give_Vip_Price_3 = Integer.parseInt(set.getProperty("give_vip_price_3", "30000"));
            // (第3組)給予VIP狀態等級
            Give_Vip_Level_3 = Integer.parseInt(set.getProperty("give_vip_level_3", "3"));
            // (第3組)給予VIP狀態時間(日)
            Give_Vip_Day_3 = Integer.parseInt(set.getProperty("give_vip_day_3", "3"));
            // (第4組)消費多少給予VIP狀態
            Give_Vip_Price_4 = Integer.parseInt(set.getProperty("give_vip_price_4", "40000"));
            // (第4組)給予VIP狀態等級
            Give_Vip_Level_4 = Integer.parseInt(set.getProperty("give_vip_level_4", "4"));
            // (第4組)給予VIP狀態時間(日)
            Give_Vip_Day_4 = Integer.parseInt(set.getProperty("give_vip_day_4", "4"));
            // (第5組)消費多少給予VIP狀態
            Give_Vip_Price_5 = Integer.parseInt(set.getProperty("give_vip_price_5", "50000"));
            // (第5組)給予VIP狀態等級
            Give_Vip_Level_5 = Integer.parseInt(set.getProperty("give_vip_level_5", "5"));
            // (第5組)給予VIP狀態時間(日)
            Give_Vip_Day_5 = Integer.parseInt(set.getProperty("give_vip_day_5", "5"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + GIVE_VIP_FILE);
        } finally {
            set.clear();
        }
    }
}