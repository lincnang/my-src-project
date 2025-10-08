package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * 特殊系統設置
 *
 */
public final class ConfigOtherSet2 {
    private static final Log _log = LogFactory.getLog(ConfigOtherSet2.class);
    private static final String ConfigOtherSet2 = "./config/☆老爹專屬製作☆遊戲設定☆/額外系統設置.properties";
    // ---------------------------------------------------
    public static boolean WHO_ONLINE_MSG_ON;
    public static boolean NewCreate;//出生公告
    /**
     * GM上線是否自動隱身
     */
    public static boolean ALT_GM_HIDE;
    public static int[] NEW_CHAR_LOC;//新手出生座標
    /**
     * 交易等級限制
     */
    public static int Trade_Level;
    /**
     * 交倉庫使用等級限制
     */
    public static int Dwarf_Level;
    /**
     * 奪魂特效編號
     */
    public static int Soul_Hp_Gfx;
    /**
     * 反彈特效編號
     */
    public static int Death_Pant_Gfx;
    public static int MAX_DOLL_COUNT;
    /**
     * 最大娃娃攜帶量
     */
    public static int MAX_DOLL_COUNT2;
    public static String MAX_DOLL_METE;//轉生娃娃攜帶量
    public static List<Integer> DRAGON_KEY_MAP_LIST = new ArrayList<>();
    public static List<Integer> GIVE_ITEM_LIST = new ArrayList<>();
    public static List<Integer> NO_AI_MAP_LIST = new ArrayList<>();
    public static String Protector_Name;//***守護者***
    /**
     * 連線獎勵等級限制
     */
    public static short ONLINE_GIFT_LEVEL;
    public static boolean Dividend;//紅利
    public static int DividendItem;
    public static int DividendQuantity;//紅利END
    public static int DividendQuantityCount;// SRC0910
    /**
     * 劍靈狀態圖示編號
     **/
    public static int Weapon_Soul_IconId;
    /**
     * 劍靈狀態圖示訊息編號
     **/
    public static int Weapon_Soul_StringId;
    /**
     * 是否開啟紅騎士團
     **/
    public static boolean Red_Knight;
    /**
     * 參加紅騎士團等級
     **/
    public static int Red_Knight_Lv;
    /**
     * 是否開啟升級經驗獎勵狀態
     **/
    public static boolean LEVEL_UP;
    /**
     * 開啟後多久級給予經驗獎勵狀態
     **/
    public static int LEVEL_UP_LV;
    /**
     * 開啟後升級經驗獎勵狀態經驗倍數
     **/
    public static double LEVEL_UP_EXP;
    /**
     * 潘朵拉銅像使用扣除道具
     **/
    public static int STATUE_MAGIC_ITEMID;
    /**
     * 潘朵拉銅像使用扣除道具數量
     **/
    public static int STATUE_MAGIC_ITEMCOUNT;
    /**
     * 潘朵拉銅像指定輔助魔法ID
     **/
    public static int[] STATUE_MAGIC_SKILLID;
    /**
     * 潘朵拉銅像指定輔助魔法時間
     **/
    public static int[] STATUE_MAGIC_SKILLTIME;
    /**
     * 潘朵拉銅像是否補滿血量
     **/
    public static boolean STATUE_MAGIC_MAXHP;
    /**
     * 冰女副本限制可開啟地圖清單
     */
    public static List<Integer> iceKeyMapList = new ArrayList<>();
    public static int CHANGE_COUNT;
    public static int R_83055;
    public static int R_80033;
    // 是否開放給其他人看見[陣營稱號]和[轉生稱號]
    public static boolean SHOW_SP_TITLE;
    /**
     * 殷海薩龍之祝福指數外置設定
     */
    public static int LEAVES_MAXEXP;
    /**
     * 妈祖狀態經驗倍數
     */
    public static double Mazu_Exp;
    public static boolean APPRENTICE_SWITCH;
    public static int APPRENTICE_LEVEL;
    public static int APPRENTICE_EXP_BONUS;
    public static int APPRENTICE_ITEM_ID;
    /*是否啟動組隊同地圖經驗加成*/
    public static boolean partyexp = true;
    //衝裝贖回
    public static String Item_Price;
    public static int partynum10;
    public static int partynum20;
    public static int partynum30;
    public static int partyexplv;//END
    // 地圖使用時間已重置
    public static int[] Reset_Map_Time;
    public static Calendar QUEST_SET_RESET_TIME; // src035
    public static Calendar MAZU_RESET_TIME; // src040
    /**
     * 殺BOSS領物品設置
     **/
    public static int Npc_Conquest;
    public static int Npc_Conquest2;
    /**
     * 2個娃娃合成幾率
     **/
    public static int DOLLSIZE2_CHANCE;
    /**
     * 3個娃娃合成幾率
     **/
    public static int DOLLSIZE3_CHANCE;
    /**
     * 4個娃娃合成幾率
     **/
    public static int DOLLSIZE4_CHANCE;
    // 元寶偵測紀錄 by terry0412
    /**
     * 娃娃大成功合成幾率
     **/
    public static int DOLL_CHANCE_BIG;
    /**
     * 啟動開關
     */
    public static boolean ADENA_CHECK_SWITCH;
    /**
     * 每XX秒判斷一次
     */
    public static int ADENA_CHECK_TIME_SEC;
    /**
     * 差異數量達到多少以上才紀錄 (位置:\物品操作日誌\元寶差異紀錄)
     */
    public static int ADENA_CHECK_COUNT_DIFFER;
    /**
     * Tam幣系统-多少秒獲得1次Tam幣
     **/
    public static int Tam_Time;
    /**
     * Tam幣系統-1次獲得多少Tam幣(會乘以玩家開通的數量，比如設10000，玩家開通2個號就是20000個，最多隻能開通3個)
     **/
    public static int TAM_COUNT;
    /**
     * Tam幣系統-是否開啟獲得Tam幣提示訊息
     **/
    public static boolean Tam_Msg;
    //衝裝贖回
    public static String getItem_Price;
    /**
     * PVP設置
     **/
    public static boolean PVP_WEAPON;// PVP武
    public static int PVP_plus;// PVP+
    public static boolean PVP_ARMOR;// PVP防
    public static int PVP_plus2;// PVP+
    /**
     * 陣營威望搶奪開關
     */
    public static boolean Prestigesnatch;
    /**
     * 同陣營搶奪積分設定
     */
    public static double camp1;
    /**
     * 非同陣營搶奪積分設定
     */
    public static double camp2;
    /**
     * 陣營加入等級限制
     */
    public static int CAMPLEVEL;
    /**
     * 陣營加入特效顯示
     */
    public static int CAMPGFX;
    public static int PcLevelUp; //玩家等級限制
    public static int drainedMana;    //瑪那吸魔上限
    public static boolean war_pet_summ; //戰爭召喚寵物
    public static String NO_CD; //防無延遲的技能

    public static void load() throws ConfigErrorException {
        // _log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            //final InputStream is = new FileInputStream(new File(ConfigOtherSet2));
            InputStream is = Files.newInputStream(new File("./config/☆老爹專屬製作☆遊戲設定☆/額外系統設置.properties").toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            //-----------------------------------------------------------------
            WHO_ONLINE_MSG_ON = Boolean.parseBoolean(set.getProperty("WHO_ONLINE_MSG_ON", "true"));
            //** 出生公告 **//
            NewCreate = Boolean.parseBoolean(set.getProperty("NewCreate", "false"));
            /** GM上線是否自動隱身 */
            ALT_GM_HIDE = Boolean.parseBoolean(set.getProperty("GmHide", "true"));
            // 交易等級限制
            Trade_Level = Integer.parseInt(set.getProperty("trade_level", "52"));
            // 交倉庫使用等級限制
            Dwarf_Level = Integer.parseInt(set.getProperty("dwarf_level", "5"));
            // 奪魂特效編號
            Soul_Hp_Gfx = Integer.parseInt(set.getProperty("soul_hp_gfx", "11677"));
            // 反彈特效編號
            Death_Pant_Gfx = Integer.parseInt(set.getProperty("death_pant_gfx", "10710"));
            MAX_DOLL_COUNT = Integer.parseInt(set.getProperty("MaxDollCount", "1"));/** 最大娃娃攜帶量 */
            MAX_DOLL_COUNT2 = Integer.parseInt(set.getProperty("MaxDollCount2", "1"));/** 最大娃娃攜帶量 */
            MAX_DOLL_METE = set.getProperty("MAX_DOLL_METE", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");//轉生娃娃攜帶量
            //***守護者***/
            Protector_Name = set.getProperty("Protector_Name", "");
            /** 連線獎勵等級限制 */
            ONLINE_GIFT_LEVEL = Short.parseShort(set.getProperty("onlinegiftlevel", "5"));
            //贊助紅利
            Dividend = Boolean.parseBoolean(set.getProperty("Dividend", "false"));
            DividendItem = Integer.parseInt(set.getProperty("DividendItem", "1"));
            DividendQuantity = Integer.parseInt(set.getProperty("DividendQuantity", "1"));
            DividendQuantityCount = Integer.parseInt(set.getProperty("DividendQuantityCount", "1"));// SRC0910
            // 劍靈狀態圖示編號
            Weapon_Soul_IconId = Integer.parseInt(set.getProperty("Weapon_Soul_IconId", "5223"));
            // 劍靈狀態圖示訊息編號
            Weapon_Soul_StringId = Integer.parseInt(set.getProperty("Weapon_Soul_StringId", "2174"));
            // 是否開啟紅騎士團
            Red_Knight = Boolean.parseBoolean(set.getProperty("red_knight", "false"));
            // 參加紅騎士團等級
            Red_Knight_Lv = Integer.parseInt(set.getProperty("red_knight_lv", "70"));
            // 是否開啟升級經驗獎勵狀態
            LEVEL_UP = Boolean.parseBoolean(set.getProperty("level_up", "false"));
            // 開啟後多久級給予經驗獎勵狀態
            LEVEL_UP_LV = Integer.parseInt(set.getProperty("level_up_lv", "60"));
            // 開啟後升級經驗獎勵狀態經驗倍數
            LEVEL_UP_EXP = Double.parseDouble(set.getProperty("level_up_exp", "1.23"));
            // 潘朵拉銅像使用道具
            STATUE_MAGIC_ITEMID = Integer.parseInt(set.getProperty("Statue_Magic_ItemId", "40308"));
            // 潘朵拉銅像使用扣除道具數量
            STATUE_MAGIC_ITEMCOUNT = Integer.parseInt(set.getProperty("Statue_Magic_ItemCount", "10000"));
            //商幣比值
            CHANGE_COUNT = Integer.parseInt(set.getProperty("Change_count", "1000000"));
            R_83055 = Integer.parseInt(set.getProperty("Minibus_Production", "2000000"));
            R_80033 = Integer.parseInt(set.getProperty("Silver_Transfer_LV", "85"));
            //衝裝贖回
            Item_Price = set.getProperty("Item_Price", "260124,500");
            // 是否開放給其他人看見[陣營稱號]和[轉生稱號]
            SHOW_SP_TITLE = Boolean.parseBoolean(set.getProperty("ShowSpTitle", "false"));
            // 殷海薩龍之祝福指數外置設定
            LEAVES_MAXEXP = Integer.parseInt(set.getProperty("LEAVES_MAXEXP", "500"));
            // 妈祖狀態經驗倍數
            Mazu_Exp = Double.parseDouble(set.getProperty("Mazu_Exp", "1.0"));
            APPRENTICE_SWITCH = Boolean.parseBoolean(set.getProperty("Apprentice_Switch", "true"));
            APPRENTICE_LEVEL = Integer.parseInt(set.getProperty("Apprentice_Level", "70"));
            APPRENTICE_EXP_BONUS = Integer.parseInt(set.getProperty("Apprentice_Exp_Bonus", "0"));
            APPRENTICE_ITEM_ID = Integer.parseInt(set.getProperty("Apprentice_ItemId", "30336"));
            PcLevelUp = Integer.parseInt(set.getProperty("PcLevelUp", "10"));//玩家等級限制
            drainedMana = Integer.parseInt(set.getProperty("drainedMana", "5"));    //瑪那吸魔上限
            war_pet_summ = Boolean.parseBoolean(set.getProperty("war_pet_summ", "false")); //戰爭召喚寵物
            NO_CD = set.getProperty("NO_CD", "null"); //防無延遲的技能
            /*是否啟動組隊同地圖經驗加成*/
            partyexp = Boolean.parseBoolean(set.getProperty("Party_Exp", "true"));
            partynum10 = Integer.parseInt(set.getProperty("Party_people_10", "10"));
            partynum20 = Integer.parseInt(set.getProperty("Party_people_20", "10"));
            partynum30 = Integer.parseInt(set.getProperty("Party_people_30", "10"));
            partyexplv = Integer.parseInt(set.getProperty("Party_Exp_Level", "99"));//END
            /** 殺BOSS領物品設置 **/
            Npc_Conquest = Integer.parseInt(set.getProperty("Npc_Conquest", "50"));
            Npc_Conquest2 = Integer.parseInt(set.getProperty("Npc_Conquest2", "50"));
            // 2個娃娃合成幾率
            DOLLSIZE2_CHANCE = Integer.parseInt(set.getProperty("Dool_size2_chance", "10"));
            // 3個娃娃合成幾率
            DOLLSIZE3_CHANCE = Integer.parseInt(set.getProperty("Dool_size3_chance", "15"));
            // 4個娃娃合成幾率
            DOLLSIZE4_CHANCE = Integer.parseInt(set.getProperty("Dool_size4_chance", "20"));
            // 娃娃大成功合成幾率
            DOLL_CHANCE_BIG = Integer.parseInt(set.getProperty("Dool_chance_big", "10"));
            // 元寶偵測紀錄 by terry0412
            /** 啟動開關 */
            ADENA_CHECK_SWITCH = Boolean.parseBoolean(set.getProperty("Adena_Check_Switch", "false"));
            /** 每XX秒判斷一次 */
            ADENA_CHECK_TIME_SEC = Integer.parseInt(set.getProperty("Adena_Check_TimeSec", "5"));
            /** 差異數量達到多少以上才紀錄 (位置:\物品操作日誌\元寶差異紀錄) */
            ADENA_CHECK_COUNT_DIFFER = Integer.parseInt(set.getProperty("Adena_Check_CountDiffer", "100"));
            // Tam幣系统-多少秒獲得1次Tam幣
            Tam_Time = Integer.parseInt(set.getProperty("Tam_Time", "600")); // 成長果實系統(Tam幣)
            // Tam幣系統-1次獲得多少Tam幣(會乘以玩家開通的數量，比如設10000，玩家開通2個號就是20000個，最多隻能開通3個)
            TAM_COUNT = Integer.parseInt(set.getProperty("TAM_COUNT", "10000")); // 成長果實系統(Tam幣)
            // Tam幣系統-是否開啟獲得Tam幣提示訊息
            Tam_Msg = Boolean.parseBoolean(set.getProperty("Tam_Msg", "false"));
            Item_Price = set.getProperty("Item_Price", "260124,500");//衝裝贖回
            // PVP系統 武器
            PVP_WEAPON = Boolean.parseBoolean(set.getProperty("PVP_WEAPON", "false"));
            // PVP+
            PVP_plus = Integer.parseInt(set.getProperty("PVP_plus", "1"));
            // PVP系統 防具
            PVP_ARMOR = Boolean.parseBoolean(set.getProperty("PVP_ARMOR", "false"));
            // PVP+
            PVP_plus2 = Integer.parseInt(set.getProperty("PVP_plus2", "1"));
            // 150429 Smile 新增威望搶奪系統(開關與設定值)
            Prestigesnatch = Boolean.parseBoolean(set.getProperty("Prestigesnatch", "false"));
            // 同陣營搶奪積分設定
            camp1 = Double.parseDouble(set.getProperty("camp1", "0.25"));
            // 非同陣營搶奪積分設定
            camp2 = Double.parseDouble(set.getProperty("camp2", "0.5"));
            // 陣營等級加入限制外置設定
            CAMPLEVEL = Integer.parseInt(set.getProperty("CAMPLEVEL", "50"));
            // 陣營加入特效顯示外置設定
            CAMPGFX = Integer.parseInt(set.getProperty("CAMPGFX", "12335"));
            // 潘朵拉銅像指定輔助魔法ID 魔法時間
            String[] s;
            s = set.getProperty("Statue_Magic_SkillId", "").split(",");
            STATUE_MAGIC_SKILLID = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                STATUE_MAGIC_SKILLID[i] = Integer.parseInt(s[i]);
            }
            s = set.getProperty("Statue_Magic_SkillTime", "").split(",");
            STATUE_MAGIC_SKILLTIME = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                STATUE_MAGIC_SKILLTIME[i] = Integer.parseInt(s[i]);
            }
            // 潘朵拉銅像是否補滿血量
            STATUE_MAGIC_MAXHP = Boolean.parseBoolean(set.getProperty("Statue_Magic_Maxhp", "false"));
            if (set.getProperty("GiveItemList") != null) {
                for (String str : set.getProperty("GiveItemList").split(",")) {
                    GIVE_ITEM_LIST.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("NoAIMapList") != null) {
                for (String str : set.getProperty("NoAIMapList").split(",")) {
                    NO_AI_MAP_LIST.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("DragonKeyMapList") != null) {
                for (String str : set.getProperty("DragonKeyMapList").split(",")) {
                    DRAGON_KEY_MAP_LIST.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("iceKeyMapList") != null) {
                for (String str : set.getProperty("iceKeyMapList").split(",")) {
                    iceKeyMapList.add(Integer.parseInt(str));
                }
            }
            //角色出生座標 (格式: locx, locy, mapid) (設置 null 則啟動內建出生座標) by terry0412
            final String tmp12 = set.getProperty("NewCharLoc", "");
            if (!tmp12.equalsIgnoreCase("null")) {
                final String[] temp = tmp12.trim().split(","); // 去掉空白再分開
                if (temp.length == 3) { // 固定值: 3
                    NEW_CHAR_LOC = new int[3];
                    NEW_CHAR_LOC[0] = Integer.parseInt(temp[0]);
                    NEW_CHAR_LOC[1] = Integer.parseInt(temp[1]);
                    NEW_CHAR_LOC[2] = Integer.parseInt(temp[2]);
                } else {
                    _log.info("[角色出生座標] 座標格式有誤, 請重新設置!");
                }
            }
            // 地圖使用時間已重置
            final String tmp14 = set.getProperty("Reset_Map_Time", "");
            if (!tmp14.equalsIgnoreCase("null")) {
                final String[] temp = tmp14.trim().split(":");
                if (temp.length == 3) {
                    Reset_Map_Time = new int[3];
                    Reset_Map_Time[0] = Integer.parseInt(temp[0]);
                    Reset_Map_Time[1] = Integer.parseInt(temp[1]);
                    Reset_Map_Time[2] = Integer.parseInt(temp[2]);
                } else {
                    _log.info("[地圖使用] 重置時間有誤, 請重新設置!");
                }
            }
            final String tmp10 = set.getProperty("Quest_Set_Reset_Time", "");
            if (!tmp10.equalsIgnoreCase("null")) {
                final String[] temp = tmp10.split(":");
                if (temp.length == 3) {
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                    QUEST_SET_RESET_TIME = cal;
                } else {
                    _log.info("[每日重置任務] 重置時間有誤, 請重新設置!");
                }
            }
            final String tmp11 = set.getProperty("Mazu_Reset_Time", "");
            if (!tmp11.equalsIgnoreCase("null")) {
                final String[] temp = tmp11.split(":");
                if (temp.length == 3) {
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                    MAZU_RESET_TIME = cal;
                } else {
                    _log.info("[媽祖加持] 重置時間有誤, 請重新設置!");
                }
            }
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigOtherSet2);
        } finally {
            set.clear();
        }
    }
}