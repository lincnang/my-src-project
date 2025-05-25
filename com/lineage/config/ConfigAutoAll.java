package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 自動輔助設置
 */
public final class ConfigAutoAll {
    private static final String Auto_All_File = "./config/☆老爹專屬製作☆遊戲設定☆/自動輔助內掛設置.properties";
    /**
     * 是否開啟核心自動輔助
     **/
    public static boolean Auto_All;
    /**
     * 自動喝水是否顯示(你覺得舒服多了)
     **/
    public static boolean Auto_Hp_Msg;
    /**
     * 自動喝水開啟後是否可以手動喝水
     **/
    public static boolean AutoWater;
    /**
     * 自動喝水(治愈藥水)最低補血量
     **/
    public static int Hp_1_Min;
    /**
     * 自動喝水(治愈藥水)最高補血量
     **/
    public static int Hp_1_Max;
    /**
     * 自動喝水(強力治愈藥水)最低補血量
     **/
    public static int Hp_2_Min;
    /**
     * 自動喝水(強力治愈藥水)最高補血量
     **/
    public static int Hp_2_Max;
    /**
     * 自動喝水(終極治愈藥水)最低補血量
     **/
    public static int Hp_3_Min;
    /**
     * 自動喝水(終極治愈藥水)最高補血量
     **/
    public static int Hp_3_Max;
    /**
     * 自動喝水(濃縮體力恢復劑)最低補血量
     **/
    public static int Hp_4_Min;
    /**
     * 自動喝水(濃縮體力恢復劑)最高補血量
     **/
    public static int Hp_4_Max;
    /**
     * 自動喝水(濃縮強力體力恢復劑)最低補血量
     **/
    public static int Hp_5_Min;
    /**
     * 自動喝水(濃縮強力體力恢復劑)最高補血量
     **/
    public static int Hp_5_Max;
    /**
     * 自動喝水(濃縮終極體力恢復劑)最低補血量
     **/
    public static int Hp_6_Min;
    /**
     * 自動喝水(濃縮終極體力恢復劑)最高補血量
     **/
    public static int Hp_6_Max;
    /**
     * 自動喝水(古代體力恢復劑)最低補血量
     **/
    public static int Hp_7_Min;
    /**
     * 自動喝水(古代體力恢復劑)最高補血量
     **/
    public static int Hp_7_Max;
    /**
     * 自動喝水(古代強力體力恢復劑)最低補血量
     **/
    public static int Hp_8_Min;
    /**
     * 自動喝水(古代強力體力恢復劑)最高補血量
     **/
    public static int Hp_8_Max;
    /**
     * 自動喝水(古代終極體力恢復劑)最低補血量
     **/
    public static int Hp_9_Min;
    /**
     * 自動喝水(古代終極體力恢復劑)最高補血量
     **/
    public static int Hp_9_Max;
    /**
     * 自動喝水(兔子的肝)最低補血量
     **/
    public static int Hp_10_Min;
    /**
     * 自動喝水(兔子的肝)最高補血量
     **/
    public static int Hp_10_Max;
    /**
     * 自動補魔(精神藥水)最低補魔量
     **/
    public static int Mp_1_Min;
    /**
     * 自動補魔(精神藥水)最高補魔量
     **/
    public static int Mp_1_Max;
    /**
     * 自動補魔(年糕)最低補魔量
     **/
    public static int Mp_2_Min;
    /**
     * 自動補魔(年糕)最高補魔量
     **/
    public static int Mp_2_Max;
    /**
     * 自動補魔(艾草年糕)最低補魔量
     **/
    public static int Mp_3_Min;
    /**
     * 自動補魔(艾草年糕)最高補魔量
     **/
    public static int Mp_3_Max;
    /**
     * 自動補魔(勇氣貨幣)最低補魔量
     **/
    public static int Mp_4_Min;
    /**
     * 自動補魔(勇氣貨幣)最高補魔量
     **/
    public static int Mp_4_Max;
    /**
     * 自動補魔(庫傑的靈藥)最低補魔量
     **/
    public static int Mp_5_Min;
    /**
     * 自動補魔(庫傑的靈藥)最高補魔量
     **/
    public static int Mp_5_Max;
    /**
     * 自動補魔(金粽子)最低補魔量
     **/
    public static int Mp_6_Min;
    /**
     * 自動補魔(金粽子)最高補魔量
     **/
    public static int Mp_6_Max;
    /**
     * 自動補魔(月餅)最低補魔量
     **/
    public static int Mp_7_Min;
    /**
     * 自動補魔(月餅)最高補魔量
     **/
    public static int Mp_7_Max;
    /**
     * 自動補魔(福月餅)最低補魔量
     **/
    public static int Mp_8_Min;
    /**
     * 自動補魔(福月餅)最高補魔量
     **/
    public static int Mp_8_Max;
    /**
     * 自動魂體轉換是否施法有動作
     **/
    public static boolean AutoIsBloody;
    /** 自動回城的座標 **/
    //public static int[] Auto_BackHome_Loc;
    /**
     * 自動魂體轉換開啟後是否可以手動魂體
     **/
    public static boolean AutoIsBloodyForMent;
    /**
     * 自動刪物可添加的上限
     **/
    public static int Remove_Item_Max;

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(Auto_All_File));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            Auto_All = Boolean.parseBoolean(set.getProperty("Auto_All", "false")); //是否開啟核心自動輔助
            AutoWater = Boolean.parseBoolean(set.getProperty("AutoWater", "false")); //自動喝水開啟後是否可以手動喝水
            Auto_Hp_Msg = Boolean.parseBoolean(set.getProperty("Auto_Hp_Msg", "false")); //自動喝水是否顯示(你覺得舒服多了)
            Hp_1_Min = Integer.parseInt(set.getProperty("Hp_1_Min", "15")); //治愈藥水-最低補血量
            Hp_1_Max = Integer.parseInt(set.getProperty("Hp_1_Max", "55")); //治愈藥水-最高補血量
            Hp_2_Min = Integer.parseInt(set.getProperty("Hp_2_Min", "15")); //強力治愈藥水-最低補血量
            Hp_2_Max = Integer.parseInt(set.getProperty("Hp_2_Max", "55")); //強力治愈藥水-最高補血量
            Hp_3_Min = Integer.parseInt(set.getProperty("Hp_3_Min", "15")); //終極治愈藥水-最低補血量
            Hp_3_Max = Integer.parseInt(set.getProperty("Hp_3_Max", "55")); //終極治愈藥水-最高補血量
            Hp_4_Min = Integer.parseInt(set.getProperty("Hp_4_Min", "15")); //濃縮體力恢復劑-最低補血量
            Hp_4_Max = Integer.parseInt(set.getProperty("Hp_4_Max", "55")); //濃縮體力恢復劑-最高補血量
            Hp_5_Min = Integer.parseInt(set.getProperty("Hp_5_Min", "15")); //濃縮強力體力恢復劑-最低補血量
            Hp_5_Max = Integer.parseInt(set.getProperty("Hp_5_Max", "55")); //濃縮強力體力恢復劑-最高補血量
            Hp_6_Min = Integer.parseInt(set.getProperty("Hp_6_Min", "15")); //濃縮終極體力恢復劑-最低補血量
            Hp_6_Max = Integer.parseInt(set.getProperty("Hp_6_Max", "55")); //濃縮終極體力恢復劑-最高補血量
            Hp_7_Min = Integer.parseInt(set.getProperty("Hp_7_Min", "15")); //古代體力恢復劑-最低補血量
            Hp_7_Max = Integer.parseInt(set.getProperty("Hp_7_Max", "55")); //古代體力恢復劑-最高補血量
            Hp_8_Min = Integer.parseInt(set.getProperty("Hp_8_Min", "15")); //古代強力體力恢復劑-最低補血量
            Hp_8_Max = Integer.parseInt(set.getProperty("Hp_8_Max", "55")); //古代強力體力恢復劑-最高補血量
            Hp_9_Min = Integer.parseInt(set.getProperty("Hp_9_Min", "15")); //古代終極體力恢復劑-最低補血量
            Hp_9_Max = Integer.parseInt(set.getProperty("Hp_9_Max", "55")); //古代終極體力恢復劑-最高補血量
            Hp_10_Min = Integer.parseInt(set.getProperty("Hp_10_Min", "200")); //兔子的肝-最低補血量
            Hp_10_Max = Integer.parseInt(set.getProperty("Hp_10_Max", "300")); //兔子的肝-最高補血量
            Mp_1_Min = Integer.parseInt(set.getProperty("Mp_1_Min", "5")); //精神藥水-最低補魔量
            Mp_1_Max = Integer.parseInt(set.getProperty("Mp_1_Max", "20")); //精神藥水-最高補魔量
            Mp_2_Min = Integer.parseInt(set.getProperty("Mp_2_Min", "5")); //年糕-最低補魔量
            Mp_2_Max = Integer.parseInt(set.getProperty("Mp_2_Max", "20")); //年糕-最高補魔量
            Mp_3_Min = Integer.parseInt(set.getProperty("Mp_3_Min", "5")); //艾草年糕-最低補魔量
            Mp_3_Max = Integer.parseInt(set.getProperty("Mp_3_Max", "20")); //艾草年糕-最高補魔量
            Mp_4_Min = Integer.parseInt(set.getProperty("Mp_4_Min", "5")); //勇氣貨幣-最低補魔量
            Mp_4_Max = Integer.parseInt(set.getProperty("Mp_4_Max", "20")); //勇氣貨幣-最高補魔量
            Mp_5_Min = Integer.parseInt(set.getProperty("Mp_5_Min", "5")); //庫傑的靈藥-最低補魔量
            Mp_5_Max = Integer.parseInt(set.getProperty("Mp_5_Max", "20")); //庫傑的靈藥-最高補魔量
            Mp_6_Min = Integer.parseInt(set.getProperty("Mp_6_Min", "5")); //金粽子-最低補魔量
            Mp_6_Max = Integer.parseInt(set.getProperty("Mp_6_Max", "20")); //金粽子-最高補魔量
            Mp_7_Min = Integer.parseInt(set.getProperty("Mp_7_Min", "5")); //月餅-最低補魔量
            Mp_7_Max = Integer.parseInt(set.getProperty("Mp_7_Max", "20")); //月餅-最高補魔量
            Mp_8_Min = Integer.parseInt(set.getProperty("Mp_8_Min", "5")); //福月餅-最低補魔量
            Mp_8_Max = Integer.parseInt(set.getProperty("Mp_8_Max", "20")); //福月餅-最高補魔量
            AutoIsBloody = Boolean.parseBoolean(set.getProperty("AutoIsBloody", "true")); //自動魂體是否有施法動作
            AutoIsBloodyForMent = Boolean.parseBoolean(set.getProperty("AutoIsBloodyForMent", "true")); //自動魂體開啟後是否可以手動魂體
            // 自動回城的座標 (格式: locx, locy, mapid) (設置 null 則啟動內建座標) 有隨機6格範圍 必須設安全區域
            // AutoBackHomeLoc = 33438,32811,4
			/*final String tmp1 = set.getProperty("AutoBackHomeLoc", "");
			if (!tmp1.equalsIgnoreCase("null")) {
				final String[] temp = tmp1.trim().split(","); // 去掉空白再分開
				if (temp.length == 3) { // 固定值: 3
					Auto_BackHome_Loc = new int[3];
					Auto_BackHome_Loc[0] = Integer.parseInt(temp[0]);
					Auto_BackHome_Loc[1] = Integer.parseInt(temp[1]);
					Auto_BackHome_Loc[2] = Integer.parseInt(temp[2]);
				} else {
					_log.info("[自動回城] 座標格式有誤, 請重新設置!");
				}
			}*/
            Remove_Item_Max = Integer.parseInt(set.getProperty("Remove_Item_Max", "50")); //自動刪物可添加的上限
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + Auto_All_File);
        } finally {
            set.clear();
        }
    }
}
