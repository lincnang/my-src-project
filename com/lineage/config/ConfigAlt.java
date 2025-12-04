package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Properties;

public final class ConfigAlt {
    public static final int MAX_NPC = 35;
    // public static int PVP_WEAPON;//武器PVP
    private static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";
    public static boolean WAR_DOLL;
    public static short GLOBAL_CHAT_LEVEL;
    public static short WHISPER_CHAT_LEVEL;
    public static byte AUTO_LOOT;
    public static int LOOTING_RANGE;
    public static boolean ALT_NONPVP;
    public static boolean ALT_PUNISHMENT;
    public static boolean ALT_ATKMSG;
    public static boolean DAMAGE_EFFECT_ENABLED;
    public static int DAMAGE_EFFECT_LOW_GFX;
    public static int DAMAGE_EFFECT_MID_GFX;
    public static int DAMAGE_EFFECT_HIGH_GFX;
    public static int DAMAGE_EFFECT_CRITICAL_GFX;
    public static boolean CLAN_ALLIANCE;
    public static int ALT_ITEM_DELETION_TIME;
    public static boolean ALT_WHO_COMMANDX;
    public static int ALT_WHO_TYPE;
    public static double ALT_WHO_COUNT;
    public static int ALT_WAR_TIME;
    public static int ALT_WAR_TIME_UNIT;
    public static int ALT_WAR_INTERVAL;
    public static int ALT_WAR_INTERVAL_UNIT;
    public static int ALT_RATE_OF_DUTY;
    public static boolean SPAWN_HOME_POINT;
    public static int SPAWN_HOME_POINT_RANGE;
    public static int SPAWN_HOME_POINT_COUNT;
    public static int SPAWN_HOME_POINT_DELAY;
    public static int ELEMENTAL_STONE_AMOUNT;
    public static int HOUSE_TAX_INTERVAL;
    public static int HOUSE_TAX_ADENA;
    public static int MAX_NPC_ITEM;
    public static int MAX_PERSONAL_WAREHOUSE_ITEM;
    public static int MAX_CLAN_WAREHOUSE_ITEM;
    public static int DELETE_CHARACTER_AFTER_LV;
    public static boolean DELETE_CHARACTER_AFTER_7DAYS;
    public static int NPC_DELETION_TIME;
    public static int MIN_MONSTER_DEATH_DISPLAY_TIME;
    public static int BOSS_MONSTER_DEATH_DISPLAY_TIME;
    public static boolean ENABLE_DIRECT_RESPAWN_TRIGGER;
    public static int DEFAULT_CHARACTER_SLOT;
    public static int MEDICINE;
    public static int POWER;
    public static int POWERMEDICINE;
    public static boolean DORP_ITEM;
    public static int MAX_PARTY_SIZE;
    public static boolean ALT_WARPUNISHMENT;
    /**
     * 初始記憶座標數量上限
     */
    public static int CHAR_BOOK_INIT_COUNT;
    // 是否開放給其他人看見[陣營稱號]和[轉生稱號]
    //public static boolean SHOW_SP_TITLE;
    /**
     * 最大可擴充數量上限
     */
    //public static int CHAR_BOOK_MAX_CHARGE;
    //public static int DRAGON_DROP_MONEY;
    //public static int BOSS_DROP_MONEY;
    //public static boolean WAR_Hier;// 攻城旗幟內是否允許攜帶祭司 true:允許 false:禁止
    public static boolean WAR_summon;// 攻城旗幟內是否允許召喚寵物 true:允許 false:禁止
    /**
     * 血盟設置
     **/
    public static int clancreatelv;
    public static int clanforwarlv;
    public static boolean NPCid;//NPC編號
    private static String pValue;
    //public static String CreateCharInfo;// 玩家出生公告 (null = 不公告)
    private static String pName;

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File("./config/altsettings.properties").toPath());
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            GLOBAL_CHAT_LEVEL = Short.parseShort(set.getProperty("GlobalChatLevel", "30"));
            WHISPER_CHAT_LEVEL = Short.parseShort(set.getProperty("WhisperChatLevel", "5"));
            AUTO_LOOT = Byte.parseByte(set.getProperty("AutoLoot", "2"));
            LOOTING_RANGE = Integer.parseInt(set.getProperty("LootingRange", "3"));
            ALT_NONPVP = Boolean.parseBoolean(set.getProperty("NonPvP", "true"));
            ALT_PUNISHMENT = Boolean.parseBoolean(set.getProperty("Punishment", "true"));
            ALT_WARPUNISHMENT = Boolean.parseBoolean(set.getProperty("WarPunishment", "false"));
            DAMAGE_EFFECT_ENABLED = Boolean.parseBoolean(set.getProperty("DamageEffectEnabled", "true"));
            DAMAGE_EFFECT_LOW_GFX = Integer.parseInt(set.getProperty("DamageEffectLowGfx", "16021"));
            DAMAGE_EFFECT_MID_GFX = Integer.parseInt(set.getProperty("DamageEffectMidGfx", "17304"));
            DAMAGE_EFFECT_HIGH_GFX = Integer.parseInt(set.getProperty("DamageEffectHighGfx", "17261"));
            DAMAGE_EFFECT_CRITICAL_GFX = Integer.parseInt(set.getProperty("DamageEffectCriticalGfx", "17261"));
            CLAN_ALLIANCE = Boolean.parseBoolean(set.getProperty("ClanAlliance", "true"));
            ALT_ITEM_DELETION_TIME = Integer.parseInt(set.getProperty("ItemDeletionTime", "10"));
            if (ALT_ITEM_DELETION_TIME > 60) {
                ALT_ITEM_DELETION_TIME = 60;
            }
            ALT_WHO_COMMANDX = Boolean.parseBoolean(set.getProperty("WhoCommandx", "false"));
            // WHO 顯示 額外設置方式 0:對話視窗顯示 1:視窗顯示
            // 這一項設置必須在WhoCommandx = true才有作用
            ALT_WHO_TYPE = Integer.parseInt(set.getProperty("Who_type", "0"));
            ALT_WHO_COUNT = Double.parseDouble(set.getProperty("WhoCommandcount", "1.0"));
            if (ALT_WHO_COUNT < 1.0D) {
                ALT_WHO_COUNT = 1.0D;
            }
            String strWar = set.getProperty("WarTime", "2h");
            if (strWar.contains("d")) {
                ALT_WAR_TIME_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.contains("h")) {
                ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.contains("m")) {
                ALT_WAR_TIME_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }
            ALT_WAR_TIME = Integer.parseInt(strWar);// 攻城戰持續時間
            strWar = set.getProperty("WarInterval", "4d");
            if (strWar.contains("d")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.contains("h")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.contains("m")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            } else if (pName.equalsIgnoreCase("NPCid")) {//NPC編號
                NPCid = Boolean.parseBoolean(pValue);
            }
            ALT_WAR_INTERVAL = Integer.parseInt(strWar);// 攻城戰間隔時間
            SPAWN_HOME_POINT = Boolean.parseBoolean(set.getProperty("SpawnHomePoint", "true"));
            SPAWN_HOME_POINT_COUNT = Integer.parseInt(set.getProperty("SpawnHomePointCount", "2"));
            SPAWN_HOME_POINT_DELAY = Integer.parseInt(set.getProperty("SpawnHomePointDelay", "100"));
            SPAWN_HOME_POINT_RANGE = Integer.parseInt(set.getProperty("SpawnHomePointRange", "8"));
            ELEMENTAL_STONE_AMOUNT = Integer.parseInt(set.getProperty("ElementalStoneAmount", "300"));
            HOUSE_TAX_INTERVAL = Integer.parseInt(set.getProperty("HouseTaxInterval", "10"));
            HOUSE_TAX_ADENA = Integer.parseInt(set.getProperty("HouseTaxAdena", "2000"));
            MAX_NPC_ITEM = Integer.parseInt(set.getProperty("MaxNpcItem", "8"));
            MAX_PERSONAL_WAREHOUSE_ITEM = Integer.parseInt(set.getProperty("MaxPersonalWarehouseItem", "100"));
            MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(set.getProperty("MaxClanWarehouseItem", "200"));
            DELETE_CHARACTER_AFTER_LV = Integer.parseInt(set.getProperty("DeleteCharacterAfterLV", "60"));
            DELETE_CHARACTER_AFTER_7DAYS = Boolean.parseBoolean(set.getProperty("DeleteCharacterAfter7Days", "True"));
            NPC_DELETION_TIME = Integer.parseInt(set.getProperty("NpcDeletionTime", "10"));
            MIN_MONSTER_DEATH_DISPLAY_TIME = Integer.parseInt(set.getProperty("MinMonsterDeathDisplayTime", "2"));
            BOSS_MONSTER_DEATH_DISPLAY_TIME = Integer.parseInt(set.getProperty("BossMonsterDeathDisplayTime", "60"));
            ENABLE_DIRECT_RESPAWN_TRIGGER = Boolean.parseBoolean(set.getProperty("EnableDirectRespawnTrigger", "true"));
            DEFAULT_CHARACTER_SLOT = Integer.parseInt(set.getProperty("DefaultCharacterSlot", "4"));
            MEDICINE = Integer.parseInt(set.getProperty("Medicine", "20"));
            POWER = Integer.parseInt(set.getProperty("Power", "35"));
            POWERMEDICINE = Integer.parseInt(set.getProperty("MedicinePower", "45"));
            DORP_ITEM = Boolean.parseBoolean(set.getProperty("dorpitem", "true"));
            MAX_PARTY_SIZE = Integer.parseInt(set.getProperty("MaxPT", "8"));
            // 血盟設置
            clancreatelv = Integer.parseInt(set.getProperty("clan_create_lv", "50"));
            clanforwarlv = Integer.parseInt(set.getProperty("clan_forwar_lv", "50"));
            CHAR_BOOK_INIT_COUNT = Integer.parseInt(set.getProperty("CharBookInitCount", "60"));
            WAR_DOLL = Boolean.parseBoolean(set.getProperty("war_doll", "true"));
            //CHAR_BOOK_MAX_CHARGE = Integer.parseInt(set.getProperty("CharBookMaxCharge", "4"));
            //DRAGON_DROP_MONEY = Integer.parseInt(set.getProperty("DrgonDropMoney", "1000"));
            //BOSS_DROP_MONEY = Integer.parseInt(set.getProperty("BossDropMoney", "3000"));
            // 攻城旗幟內是否允許攜帶祭司 true:允許 false:禁止
            //WAR_Hier = Boolean.parseBoolean(set.getProperty("war_hier", "true"));
            // 攻城旗幟內是否允許召喚寵物 true:允許 false:禁止
            WAR_summon = Boolean.parseBoolean(set.getProperty("war_summon", "true"));
            // 是否開放給其他人看見[陣營稱號]和[轉生稱號]
            //SHOW_SP_TITLE = Boolean.parseBoolean(set.getProperty("ShowSpTitle","false"));
            //PVP_WEAPON = Integer.parseInt(set.getProperty("PVP_WEAPON", "",""));
            NPCid = Boolean.parseBoolean(set.getProperty("NPCid", "false"));//NPC編號
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
        } finally {
            set.clear();
        }
    }
}
