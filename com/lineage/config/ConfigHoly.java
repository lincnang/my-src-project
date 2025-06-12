package com.lineage.config;

import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 聖物系統參數設置
 *
 * @author dexc
 */
public final class ConfigHoly {
    private static final Log _log = LogFactory.getLog(ConfigHoly.class);
    private static final String HOLY_SETTINGS_FILE = "./config/聖物卡合成設定.properties";

    // 各階段聖物卡 ID 陣列
    public static int[] holy_LIST_1; // 聖物卡一階段
    public static int[] holy_LIST_2; // 聖物卡二階段
    public static int[] holy_LIST_3; // 聖物卡三階段
    public static int[] holy_LIST_4; // 聖物卡四階段
    public static int[] holy_LIST_5; // 聖物卡五階段

    // 各階段聖物合成幾率與所需數量
    public static int HOLY_CONSUME2; // 二階合成基礎幾率
    public static int HOLY_CONSUME3; // 三階合成基礎幾率
    public static int HOLY_CONSUME4; // 四階合成基礎幾率
    public static int HOLY_CONSUME5; // 五階合成基礎幾率

    public static int HOLY_MAX_RATE2; // 二階最大幾率
    public static int HOLY_MIN_RATE2; // 二階最小幾率
    public static int HOLY_STEP_RATE2; // 二階每次增加幾率
    public static int HOLY_COUNT2;     // 二階所需聖物數量

    public static int HOLY_MAX_RATE3; // 三階最大幾率
    public static int HOLY_MIN_RATE3; // 三階最小幾率
    public static int HOLY_STEP_RATE3; // 三階每次增加幾率
    public static int HOLY_COUNT3;     // 三階所需聖物數量

    public static int HOLY_MAX_RATE4; // 四階最大幾率
    public static int HOLY_MIN_RATE4; // 四階最小幾率
    public static int HOLY_STEP_RATE4; // 四階每次增加幾率
    public static int HOLY_COUNT4;     // 四階所需聖物數量

    public static int HOLY_MAX_RATE5; // 五階最大幾率
    public static int HOLY_MIN_RATE5; // 五階最小幾率
    public static int HOLY_STEP_RATE5; // 五階每次增加幾率
    public static int HOLY_COUNT5;     // 五階所需聖物數量

    // 倉庫封包判定階段（以聖物ID為主）
    public static List<Integer> holy1 = new ArrayList<>(); // 一階段
    public static List<Integer> holy2 = new ArrayList<>(); // 二階段
    public static List<Integer> holy3 = new ArrayList<>(); // 三階段
    public static List<Integer> holy4 = new ArrayList<>(); // 四階段

    /**
     * 讀取設定檔
     */
    public static void load() throws ConfigErrorException {
        final PerformanceTimer timer = new PerformanceTimer();
        final Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File(HOLY_SETTINGS_FILE).toPath());
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            set.load(isr);
            is.close();

            // 讀取各階段聖物卡ID
            holy_LIST_1 = toIntArray(set.getProperty("holy_List1", ""), ",");
            holy_LIST_2 = toIntArray(set.getProperty("holy_List2", ""), ",");
            holy_LIST_3 = toIntArray(set.getProperty("holy_List3", ""), ",");
            holy_LIST_4 = toIntArray(set.getProperty("holy_List4", ""), ",");
            holy_LIST_5 = toIntArray(set.getProperty("holy_List5", ""), ",");

            // 讀取各階段合成幾率與數量
            HOLY_CONSUME2 = Integer.parseInt(set.getProperty("HolyConsume2", "50"));
            HOLY_CONSUME3 = Integer.parseInt(set.getProperty("HolyConsume3", "50"));
            HOLY_CONSUME4 = Integer.parseInt(set.getProperty("HolyConsume4", "50"));
            HOLY_CONSUME5 = Integer.parseInt(set.getProperty("HolyConsume5", "50"));

            HOLY_MAX_RATE2 = Integer.parseInt(set.getProperty("HOLY_MAX_RATE2", "50"));
            HOLY_MIN_RATE2 = Integer.parseInt(set.getProperty("HOLY_MIN_RATE2", "10"));
            HOLY_STEP_RATE2 = Integer.parseInt(set.getProperty("HOLY_STEP_RATE2", "10"));
            HOLY_COUNT2 = Integer.parseInt(set.getProperty("HOLY_COUNT2", "2"));

            HOLY_MAX_RATE3 = Integer.parseInt(set.getProperty("HOLY_MAX_RATE3", "50"));
            HOLY_MIN_RATE3 = Integer.parseInt(set.getProperty("HOLY_MIN_RATE3", "10"));
            HOLY_STEP_RATE3 = Integer.parseInt(set.getProperty("HOLY_STEP_RATE3", "10"));
            HOLY_COUNT3 = Integer.parseInt(set.getProperty("HOLY_COUNT3", "2"));

            HOLY_MAX_RATE4 = Integer.parseInt(set.getProperty("HOLY_MAX_RATE4", "50"));
            HOLY_MIN_RATE4 = Integer.parseInt(set.getProperty("HOLY_MIN_RATE4", "10"));
            HOLY_STEP_RATE4 = Integer.parseInt(set.getProperty("HOLY_STEP_RATE4", "10"));
            HOLY_COUNT4 = Integer.parseInt(set.getProperty("HOLY_COUNT4", "2"));

            HOLY_MAX_RATE5 = Integer.parseInt(set.getProperty("HOLY_MAX_RATE5", "50"));
            HOLY_MIN_RATE5 = Integer.parseInt(set.getProperty("HOLY_MIN_RATE5", "10"));
            HOLY_STEP_RATE5 = Integer.parseInt(set.getProperty("HOLY_STEP_RATE5", "10"));
            HOLY_COUNT5 = Integer.parseInt(set.getProperty("HOLY_COUNT5", "2"));

            // 清空List避免重複
            holy1.clear();
            holy2.clear();
            holy3.clear();
            holy4.clear();

            // 讀取各階段聖物ID清單
            if (set.getProperty("holy1") != null) {
                for (final String str : set.getProperty("holy1").split(",")) {
                    holy1.add(Integer.parseInt(str.trim()));
                }
            }
            if (set.getProperty("holy2") != null) {
                for (final String str : set.getProperty("holy2").split(",")) {
                    holy2.add(Integer.parseInt(str.trim()));
                }
            }
            if (set.getProperty("holy3") != null) {
                for (final String str : set.getProperty("holy3").split(",")) {
                    holy3.add(Integer.parseInt(str.trim()));
                }
            }
            if (set.getProperty("holy4") != null) {
                for (final String str : set.getProperty("holy4").split(",")) {
                    holy4.add(Integer.parseInt(str.trim()));
                }
            }

        } catch (Exception e) {
            throw new ConfigErrorException("聖物合成設定檔案遺失: " + HOLY_SETTINGS_FILE);
        } finally {
            set.clear();
            _log.info("Config/聖物卡合成設定讀取完成 (" + timer.get() + "ms)");
        }
    }

    /**
     * 字串轉int陣列工具
     */
    public static int[] toIntArray(final String text, final String type) {
        String[] tokens = text.split(type);
        List<Integer> result = new ArrayList<>();
        for (String token : tokens) {
            try {
                if (!token.trim().isEmpty())
                    result.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException ignored) {}
        }
        int[] iReturn = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            iReturn[i] = result.get(i);
        }
        return iReturn;
    }
}
