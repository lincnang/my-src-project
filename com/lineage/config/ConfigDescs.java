package com.lineage.config;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigDescs {
    private static final Map<Integer, String> _show_desc = new HashMap<>();
    private static final String _show_desc_file = "./config/☆服務器設定表☆/定時公告循環設定表.txt";

    public static void load() throws ConfigErrorException {
        try {
            InputStream is = Files.newInputStream(new File(_show_desc_file).toPath());
            // 指定檔案編碼
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            LineNumberReader lnr = new LineNumberReader(isr);
            boolean isWhile = false;
            int i = 1;
            String desc = null;
            while ((desc = lnr.readLine()) != null) {
                if (!isWhile) { // 忽略第一行
                    isWhile = true;
                } else if ((desc.trim().length() != 0) && (!desc.startsWith("#"))) {
                    if (desc.startsWith("SERVER_NAME")) {
                        desc = desc.replaceAll(" ", ""); // 取代空白
                        Config.SERVERNAME = desc.substring(12);
                    } else {
                        _show_desc.put(i++, desc);
                    }
                }
            }
            is.close();
            isr.close();
            lnr.close();
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + _show_desc_file);
        }
    }

    /**
     * 傳回SHOW
     *
     */
    public static String getShow(int nameid) {
        try {
            return (String) _show_desc.get(nameid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 傳回SHOW size
     *
     */
    public static int get_show_size() {
        return _show_desc.size();
    }
}
