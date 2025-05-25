package com.lineage.config;

import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Properties;

/**
 * 自創陣營戰系統
 *
 */
public final class Configcamp_war {
    private static final String Configcamp_war = "./config/☆老爹專屬製作☆遊戲設定☆/陣營戰設置.properties";
    /**
     * 自創陣營戰系統 2017/09/12 By shinogmae新增
     */
    public static int RedBlueJoin_itemid;//報名道具編號
    public static int RedBlueJoin_count;//報名道具數量
    public static int RedBluePc_amount;//各隊報名人數
    public static int RedBlueLv_min;//角色最低等級限制
    public static int RedBlueLv_max;//角色最高等級限制
    public static int RedBlueTime_all;//活動時間
    public static int RedBlueTime_clear;//活動場次清潔時間
    public static int RedBlueEffect_time;//活動開始凍結玩家時間
    public static int[] RedBlueEnd_map;//活動結束傳送的地圖座標
    public static int[] RedBlueRed_map1;//ROOM1 紅隊傳送的地圖座標
    public static int[] RedBlueBlue_map1;//ROOM1 藍隊傳送的地圖座標
    public static int[] RedBlueRed_map2;//ROOM2 紅隊傳送的地圖座標
    public static int[] RedBlueBlue_map2;//ROOM2 藍隊傳送的地圖座標
    public static int RedBlueStart_point;//各隊角色初始積分
    public static int RedBlueNormal_point;//擊殺敵方成員所得積分
    public static int RedBlueLeader_point;//擊殺敵方對長所得積分
    public static int RedBlueBonus_itemid;//活動獎勵道具
    public static int RedBlueBonus_count;//活動獎勵數量
    public static int RedBlueReward_times;//可以領取幾次獎勵
    public static Calendar RedBlueReward_RESET_TIME;  //src040
    //---------------------------------------------------
    private static Log _log;

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(Configcamp_war).toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            //自創陣營戰系統 相關設定
            RedBlueJoin_itemid = Integer.parseInt(set.getProperty("RedBlue_Join_itemid", "40308"));
            RedBlueJoin_count = Integer.parseInt(set.getProperty("RedBlue_Join_count", "100"));
            RedBluePc_amount = Integer.parseInt(set.getProperty("RedBlue_Pc_amount", "5"));
            RedBlueLv_min = Integer.parseInt(set.getProperty("RedBlue_Lv_min", "70"));
            RedBlueLv_max = Integer.parseInt(set.getProperty("RedBlue_Lv_max", "99"));
            RedBlueTime_all = Integer.parseInt(set.getProperty("RedBlue_Time_all", "600"));
            RedBlueTime_clear = Integer.parseInt(set.getProperty("RedBlue_Time_clear", "1800"));
            RedBlueEffect_time = Integer.parseInt(set.getProperty("RedBlue_Effect_time", "10"));
            RedBlueStart_point = Integer.parseInt(set.getProperty("RedBlue_Start_point", "5"));
            RedBlueNormal_point = Integer.parseInt(set.getProperty("RedBlue_Normal_point", "1"));
            RedBlueLeader_point = Integer.parseInt(set.getProperty("RedBlue_Leader_point", "5"));
            RedBlueBonus_itemid = Integer.parseInt(set.getProperty("RedBlue_Bonus_itemid", "40308"));
            RedBlueBonus_count = Integer.parseInt(set.getProperty("RedBlue_Bonus_count", "1000"));
            RedBlueReward_times = Integer.parseInt(set.getProperty("RedBlue_Reward_times", "5"));
            String rb1 = set.getProperty("RedBlue_End_map", "33080,33392,4");
            if (!rb1.equalsIgnoreCase("null")) {
                String[] rb11 = rb1.split(",");
                int[] rb111 = {Integer.parseInt(rb11[0]), Integer.parseInt(rb11[1]), Integer.parseInt(rb11[2])};
                RedBlueEnd_map = rb111;
            }
            String rb2 = set.getProperty("RedBlue_Red_map1", "33080,33392,4");
            if (!rb2.equalsIgnoreCase("null")) {
                String[] rb22 = rb2.split(",");
                int[] rb222 = {Integer.parseInt(rb22[0]), Integer.parseInt(rb22[1]), Integer.parseInt(rb22[2])};
                RedBlueRed_map1 = rb222;
            }
            String rb3 = set.getProperty("RedBlue_Blue_map1", "33080,33392,4");
            if (!rb3.equalsIgnoreCase("null")) {
                String[] rb33 = rb3.split(",");
                int[] rb333 = {Integer.parseInt(rb33[0]), Integer.parseInt(rb33[1]), Integer.parseInt(rb33[2])};
                RedBlueBlue_map1 = rb333;
            }
            String rb4 = set.getProperty("RedBlue_Red_map2", "33080,33392,4");
            if (!rb4.equalsIgnoreCase("null")) {
                String[] rb44 = rb4.split(",");
                int[] rb444 = {Integer.parseInt(rb44[0]), Integer.parseInt(rb44[1]), Integer.parseInt(rb44[2])};
                RedBlueRed_map2 = rb444;
            }
            String rb5 = set.getProperty("RedBlue_Blue_map2", "33080,33392,4");
            if (!rb5.equalsIgnoreCase("null")) {
                String[] rb55 = rb5.split(",");
                int[] rb555 = {Integer.parseInt(rb55[0]), Integer.parseInt(rb55[1]), Integer.parseInt(rb55[2])};
                RedBlueBlue_map2 = rb555;
            }
            final String tmp12 = set.getProperty("RedBlue_Reward_RESET_TIME", "");
            if (!tmp12.equalsIgnoreCase("null")) {
                final String[] temp = tmp12.split(":");
                if (temp.length == 3) {
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                    RedBlueReward_RESET_TIME = cal;
                } else {
                    _log.info("[對戰獎勵] 重置時間有誤, 請重新設置!");
                }
            }
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + Configcamp_war);
        } finally {
            set.clear();
        }
    }
}