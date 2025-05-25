package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ConfigGuardTower {
    private static final Log _log = LogFactory.getLog(ConfigGuardTower.class);
    private static final String GUARD_SETTINGS_FILE = "./config/守塔系統設置.properties";
    public static boolean ISOPEN;
    public static int LEVEL;
    public static int TOWERID;
    public static int TOWERX;
    public static int TOWERY;
    public static int MAXPC;
    public static int MAPX;
    public static int MAPY;
    public static int MAPID;
    public static int NEEDITEM;
    public static int NEEDITEMCOUNT;
    public static int GIFT_Count;
    public static int ComfortGift_COUNT;
    public static int GIFT;
    public static int COMFORTGIFT;
    public static List<Integer> X = new ArrayList<>();
    public static List<Integer> Y = new ArrayList<>();

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(GUARD_SETTINGS_FILE).toPath());
            set.load(is);
            is.close();
            if (set.getProperty("MobX") != null) {
                for (String str : set.getProperty("MobX").split(",")) {
                    X.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("MobY") != null) {
                for (String str : set.getProperty("MobY").split(",")) {
                    Y.add(Integer.parseInt(str));
                }
            }
            ISOPEN = Boolean.parseBoolean(set.getProperty("IsOpen", "false"));
            LEVEL = Integer.parseInt(set.getProperty("PcLevel", "50"));
            TOWERID = Integer.parseInt(set.getProperty("TowerId", "989997"));
            TOWERX = Integer.parseInt(set.getProperty("TowerX", "32701"));
            TOWERY = Integer.parseInt(set.getProperty("TowerY", "32896"));
            MAXPC = Integer.parseInt(set.getProperty("MaxPlayer", "30"));
            MAPX = Integer.parseInt(set.getProperty("MapX", "32700"));
            MAPY = Integer.parseInt(set.getProperty("MapY", "32886"));
            MAPID = Integer.parseInt(set.getProperty("MapId", "95"));
            NEEDITEM = Integer.parseInt(set.getProperty("NeedItemId", "40308"));
            NEEDITEMCOUNT = Integer.parseInt(set.getProperty("NeedCount", "10000"));
            GIFT = Integer.parseInt(set.getProperty("GiftId", "60"));
            COMFORTGIFT = Integer.parseInt(set.getProperty("ComfortGift", "40308"));
            GIFT_Count = Integer.parseInt(set.getProperty("GIFT_Count", "10000"));
            ComfortGift_COUNT = Integer.parseInt(set.getProperty("ComfortGift_COUNT", "10000"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            throw new ConfigErrorException("設置檔案遺失: " + GUARD_SETTINGS_FILE);
        } finally {
            set.clear();
        }
    }
}
