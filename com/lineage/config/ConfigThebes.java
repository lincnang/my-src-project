package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;

/**
 * 掃街系統設置
 *
 */
public final class ConfigThebes {
    private static final Log _log = LogFactory.getLog(ConfigThebes.class);
    private static final String ConfigThebes = "./config/☆老爹專屬製作☆遊戲設定☆/底比斯大戰爭霸設置.properties";
    /**
     * 是否開啟底比斯大戰遊戲
     **/
    public static boolean Mini_Siege;
    /**
     * 底比斯大戰遊戲-摧毀守護塔獎勵道具
     **/
    public static int A_TowerItemId;
    /**
     * 底比斯大戰遊戲-摧毀守護塔獎勵道具數量
     **/
    public static int A_TowerItemCount;
    /**
     * 底比斯大戰遊戲-摧毀中塔獎勵道具
     **/
    public static int B_TowerItemId;
    /**
     * 底比斯大戰遊戲-摧毀中塔獎勵道具數量
     **/
    public static int B_TowerItemCount;
    /**
     * 底比斯大戰遊戲-最終獲勝獎勵道具
     **/
    public static int C_TowerItemId;
    /**
     * 底比斯大戰遊戲-最終獲勝獎勵道具數量
     **/
    public static int C_TowerItemCount;
    /**
     * 底比斯大戰遊戲-最終召喚BOSS編號
     **/
    // public static int MiniSiege_BossId;
    public static int[] MiniSiege_BossId;
    /**
     * 伺服器開啟後什麼時間才開啟底比斯大戰遊戲NPC召喚
     **/
    public static Calendar MiniSiege_StartTime;
    /**
     * 底比斯大戰遊戲-加入or裂痕NPC存在時間
     **/
    public static int MiniSiege_ReadyTime;
    /**
     * 底比斯大戰遊戲-遊戲時間 (秒)
     **/
    public static int MiniSiege_PlayTime;
    /**
     * 底比斯大戰遊戲-下次開啟時間(分)
     **/
    public static int MiniSiege_NextTime;
    /**
     * 底比斯大戰遊戲-遊戲最少人數
     **/
    public static int MiniSiege_MinPlayer;

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigThebes));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            // 是否開啟底比斯大戰遊戲
            Mini_Siege = Boolean.parseBoolean(set.getProperty("Mini_Siege", "false"));
            // 伺服器開啟後什麼時間才開啟底比斯大戰遊戲NPC召喚
            final String tmp13 = set.getProperty("minisiege_starttime", "");
            if (!tmp13.equalsIgnoreCase("null")) {
                final String[] temp = tmp13.split(":");
                if (temp.length == 3) {
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                    MiniSiege_StartTime = cal;
                } else {
                    _log.info("[底比斯大戰遊戲]啟動時間有誤, 請重新設置!");
                }
            }
            // 底比斯大戰遊戲-摧毀守護塔獎勵道具
            A_TowerItemId = Integer.parseInt(set.getProperty("A_TowerItemId", "40308"));
            // 底比斯大戰遊戲-摧毀守護塔獎勵道具數量
            A_TowerItemCount = Integer.parseInt(set.getProperty("A_TowerItemCount", "10000"));
            // 底比斯大戰遊戲-摧毀中塔獎勵道具
            B_TowerItemId = Integer.parseInt(set.getProperty("B_TowerItemId", "40308"));
            // 底比斯大戰遊戲-摧毀中塔獎勵道具數量
            B_TowerItemCount = Integer.parseInt(set.getProperty("B_TowerItemCount", "20000"));
            // 底比斯大戰遊戲-最終獲勝獎勵道具
            C_TowerItemId = Integer.parseInt(set.getProperty("C_TowerItemId", "40308"));
            // 底比斯大戰遊戲-最終獲勝獎勵道具數量
            C_TowerItemCount = Integer.parseInt(set.getProperty("C_TowerItemCount", "30000"));
            // 底比斯大戰遊戲-最終召喚BOSS編號
            // MiniSiege_BossId =
            // Integer.parseInt(set.getProperty("MiniSiege_BossId", "45601"));
            String[] s1;
            s1 = set.getProperty("MiniSiege_BossId", "").split(",");
            MiniSiege_BossId = new int[s1.length];
            for (int i = 0; i < s1.length; i++) {
                MiniSiege_BossId[i] = Integer.parseInt(s1[i]);
            }
            // 底比斯大戰遊戲-裂痕NPC存在時間 (秒)
            MiniSiege_ReadyTime = Integer.parseInt(set.getProperty("MiniSiege_ReadyTime", "60"));
            // 底比斯大戰遊戲-遊戲時間 (秒)
            MiniSiege_PlayTime = Integer.parseInt(set.getProperty("MiniSiege_PlayTime", "300"));
            // 底比斯大戰遊戲-下次開啟時間(分)
            MiniSiege_NextTime = Integer.parseInt(set.getProperty("MiniSiege_NextTime", "60"));
            // 底比斯大戰遊戲-遊戲最少人數
            MiniSiege_MinPlayer = Integer.parseInt(set.getProperty("MiniSiege_MinPlayer", "3"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigThebes);
        } finally {
            set.clear();
        }
    }
}