package com.lineage.config;

import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 服務器限制設置
 *
 * @author dexc
 */
public final class ConfigDoll {
    private static final Log _log = LogFactory.getLog(ConfigDoll.class);
    private static final String ALT_SETTINGS_FILE = "./config/魔法娃娃卡合成設定.properties";
    public static int[] DOLL_LIST_1;//給與娃娃列陣
    public static int[] DOLL_LIST_2;//給與娃娃列陣
    public static int[] DOLL_LIST_3;//給與娃娃列陣
    public static int[] DOLL_LIST_4;//給與娃娃列陣
    public static int[] DOLL_LIST_5;//給與娃娃列陣
    public static int CONSUME2;//合成幾率
    public static int CONSUME3;//合成幾率
    public static int CONSUME4;//合成幾率
    public static int CONSUME5;//合成幾率
    public static int ZUIDAJILV2;//合成二階段最大幾率
    public static int ZUIXIAOJILV2;//合成二階段最小几率
    public static int ZENGJIAJILV2;//合成二階段增加幾率數量
    public static int SHULIANG2;//合成二階段需要的娃娃數量
    public static int ZUIDAJILV3;//合成三階段最大幾率
    public static int ZUIXIAOJILV3;//合成三階段最小几率
    public static int ZENGJIAJILV3;//合成三階段增加幾率數量
    public static int SHULIANG3;//合成三階段需要的娃娃數量
    public static int ZUIDAJILV4;//合成四階段最大幾率
    public static int ZUIXIAOJILV4;//合成四階段最小几率
    public static int ZENGJIAJILV4;//合成四階段增加幾率數量
    public static int SHULIANG4;//合成四階段需要的娃娃數量
    public static int ZUIDAJILV5;//合成五階段最大幾率
    public static int ZUIXIAOJILV5;//合成五階段最小几率
    public static int ZENGJIAJILV5;//合成五階段增加幾率數量
    public static int SHULIANG5;//合成五階段需要的娃娃數量
    public static List<Integer> DOLL1 = new ArrayList<>();//倉庫封包判定1階段
    public static List<Integer> DOLL2 = new ArrayList<>();//倉庫封包判定2階段
    public static List<Integer> DOLL3 = new ArrayList<>();//倉庫封包判定3階段
    public static List<Integer> DOLL4 = new ArrayList<>();//倉庫封包判定4階段

    public static void load() throws ConfigErrorException {
        final PerformanceTimer timer = new PerformanceTimer();
        final Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File(ALT_SETTINGS_FILE).toPath());
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            DOLL_LIST_1 = toIntArray(set.getProperty("Doll_List1", ""), ",");
            DOLL_LIST_2 = toIntArray(set.getProperty("Doll_List2", ""), ",");
            DOLL_LIST_3 = toIntArray(set.getProperty("Doll_List3", ""), ",");
            DOLL_LIST_4 = toIntArray(set.getProperty("Doll_List4", ""), ",");
            DOLL_LIST_5 = toIntArray(set.getProperty("Doll_List5", ""), ",");
            CONSUME2 = Integer.parseInt(set.getProperty("Consume2", "50"));
            CONSUME3 = Integer.parseInt(set.getProperty("Consume3", "50"));
            CONSUME4 = Integer.parseInt(set.getProperty("Consume4", "50"));
            CONSUME5 = Integer.parseInt(set.getProperty("Consume5", "50"));
            ZUIDAJILV2 = Integer.parseInt(set.getProperty("ZUIDAJILV2", "50"));
            ZUIDAJILV3 = Integer.parseInt(set.getProperty("ZUIDAJILV3", "50"));
            ZUIDAJILV4 = Integer.parseInt(set.getProperty("ZUIDAJILV4", "50"));
            ZUIDAJILV5 = Integer.parseInt(set.getProperty("ZUIDAJILV5", "50"));
            ////////////////////////////////////////////////////////////////////
            ZUIXIAOJILV2 = Integer.parseInt(set.getProperty("ZUIXIAOJILV2", "10"));
            ZUIXIAOJILV3 = Integer.parseInt(set.getProperty("ZUIXIAOJILV3", "10"));
            ZUIXIAOJILV4 = Integer.parseInt(set.getProperty("ZUIXIAOJILV4", "10"));
            ZUIXIAOJILV5 = Integer.parseInt(set.getProperty("ZUIXIAOJILV5", "10"));
            ////////////////////////////////////////////////////////////////////
            ZENGJIAJILV2 = Integer.parseInt(set.getProperty("ZENGJIAJILV2", "10"));
            ZENGJIAJILV3 = Integer.parseInt(set.getProperty("ZENGJIAJILV3", "10"));
            ZENGJIAJILV4 = Integer.parseInt(set.getProperty("ZENGJIAJILV4", "10"));
            ZENGJIAJILV5 = Integer.parseInt(set.getProperty("ZENGJIAJILV5", "10"));
            ////////////////////////////////////////////////////////////////////
            SHULIANG2 = Integer.parseInt(set.getProperty("SHULIANG2", "2"));
            SHULIANG3 = Integer.parseInt(set.getProperty("SHULIANG3", "2"));
            SHULIANG4 = Integer.parseInt(set.getProperty("SHULIANG4", "2"));
            SHULIANG5 = Integer.parseInt(set.getProperty("SHULIANG5", "2"));
            if (set.getProperty("doll1") != null) {
                for (final String str : set.getProperty("doll1").split(",")) {
                    DOLL1.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("doll2") != null) {
                for (final String str : set.getProperty("doll2").split(",")) {
                    DOLL2.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("doll3") != null) {
                for (final String str : set.getProperty("doll3").split(",")) {
                    DOLL3.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("doll4") != null) {
                for (final String str : set.getProperty("doll4").split(",")) {
                    DOLL4.add(Integer.parseInt(str));
                }
            }
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
        } finally {
            set.clear();
            _log.info("Config/魔法娃娃卡合成設定讀取完成 (" + timer.get() + "ms)");
        }
    }

    public static int[] toIntArray(final String text, final String type) {
        StringTokenizer st = new StringTokenizer(text, type);
        int[] iReturn = new int[st.countTokens()];
        for (int i = 0; i < iReturn.length; i++) {
            iReturn[i] = Integer.parseInt(st.nextToken());
        }
        return iReturn;
    }
}
