package com.lineage.config;

import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
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
public final class Configpoly {
    private static final Log _log = LogFactory.getLog(Configpoly.class);
    private static final String ALT_SETTINGS_FILE = "./config/變身卡特殊合成設定.properties";
    public static int[] poly_LIST_1;//給與變身卡列陣
    public static int[] poly_LIST_2;//給與變身卡列陣
    public static int[] poly_LIST_3;//給與變身卡列陣
    public static int[] poly_LIST_4;//給與變身卡列陣
    public static int[] poly_LIST_5;//給與變身卡列陣
    public static int CONSUME2;//合成幾率
    public static int CONSUME3;//合成幾率
    public static int CONSUME4;//合成幾率
    public static int CONSUME5;//合成幾率
    public static int ZUIDAJILV2;//合成二階段最大幾率
    public static int ZUIXIAOJILV2;//合成二階段最小几率
    public static int ZENGJIAJILV2;//合成二階段增加幾率數量
    public static int SHULIANG2;//合成二階段需要的變身卡數量
    public static int ZUIDAJILV3;//合成三階段最大幾率
    public static int ZUIXIAOJILV3;//合成三階段最小几率
    public static int ZENGJIAJILV3;//合成三階段增加幾率數量
    public static int SHULIANG3;//合成三階段需要的變身卡數量
    public static int ZUIDAJILV4;//合成四階段最大幾率
    public static int ZUIXIAOJILV4;//合成四階段最小几率
    public static int ZENGJIAJILV4;//合成四階段增加幾率數量
    public static int SHULIANG4;//合成四階段需要的變身卡數量
    public static int ZUIDAJILV5;//合成五階段最大幾率
    public static int ZUIXIAOJILV5;//合成五階段最小几率
    public static int ZENGJIAJILV5;//合成五階段增加幾率數量
    public static int SHULIANG5;//合成五階段需要的變身卡數量
    public static List<Integer> poly1 = new ArrayList<>();//倉庫封包判定1階段
    public static List<Integer> poly2 = new ArrayList<>();//倉庫封包判定2階段
    public static List<Integer> poly3 = new ArrayList<>();//倉庫封包判定3階段
    public static List<Integer> poly4 = new ArrayList<>();//倉庫封包判定4階段

    public static void load() throws ConfigErrorException {
        final PerformanceTimer timer = new PerformanceTimer();
        final Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File(ALT_SETTINGS_FILE).toPath());
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            poly_LIST_1 = toIntArray(set.getProperty("poly_List1", ""), ",");
            poly_LIST_2 = toIntArray(set.getProperty("poly_List2", ""), ",");
            poly_LIST_3 = toIntArray(set.getProperty("poly_List3", ""), ",");
            poly_LIST_4 = toIntArray(set.getProperty("poly_List4", ""), ",");
            poly_LIST_5 = toIntArray(set.getProperty("poly_List5", ""), ",");
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
            if (set.getProperty("poly1") != null) {
                for (final String str : set.getProperty("poly1").split(",")) {
                    poly1.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("poly2") != null) {
                for (final String str : set.getProperty("poly2").split(",")) {
                    poly2.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("poly3") != null) {
                for (final String str : set.getProperty("poly3").split(",")) {
                    poly3.add(Integer.parseInt(str));
                }
            }
            if (set.getProperty("poly4") != null) {
                for (final String str : set.getProperty("poly4").split(",")) {
                    poly4.add(Integer.parseInt(str));
                }
            }
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
        } finally {
            set.clear();
            _log.info("Config/變身卡特殊合成設定讀取完成 (" + timer.get() + "ms)");
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
