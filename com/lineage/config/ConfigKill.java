package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigKill {
    public static final Map<Integer, String> KILL_TEXT_LIST = new HashMap<Integer, String>();
    private static final Log _log = LogFactory.getLog(ConfigKill.class);
    private static final String _kill_text = "./config/☆服務器設定表☆/殺人死亡公告設定表.txt";
    public static int KILLLEVEL = 90;// 公告等級設置

    public static void load() throws ConfigErrorException {
        try {
            // 取回檔案
            final InputStream is = new FileInputStream(new File(_kill_text));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            final LineNumberReader lnr = new LineNumberReader(isr);
            boolean isWhile = false;
            int i = 1;
            String desc = null;
            while ((desc = lnr.readLine()) != null) {
                if (!isWhile) {// 忽略第一行
                    isWhile = true;
                    continue;
                }
                if ((desc.trim().length() == 0) || desc.startsWith("#")) {
                    continue;
                }
                if (desc.startsWith("KILLLEVEL")) {
                    desc = desc.replaceAll(" ", "");// 取代空白
                    KILLLEVEL = Integer.parseInt(desc.substring(10));
                } else {
                    KILL_TEXT_LIST.put(new Integer(i++), desc);
                }
            }
            is.close();
            isr.close();
            lnr.close();
        } catch (final Exception e) {
            _log.error("設置檔案遺失: " + _kill_text);
        }
    }
}
