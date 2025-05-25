package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 血盟技能材料設置
 *
 * @author dexc
 */
public final class ConfigClan {
    private static final String ConfigClan = "./config/☆老爹專屬製作☆遊戲設定☆/血盟技能材料設置.properties";
    /**
     * 血盟製作材料
     */
    public static int material_1;
    public static int material_2;
    public static int material_3;
    /**
     * 血盟製作材料
     */
    public static int Quantity_item_1;
    public static int Quantity_item_2;
    public static int Quantity_item_3;

    public static void load() throws ConfigErrorException {
        //_log.info("載入服務器限制設置!");
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(ConfigClan));
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            set.load(isr);
            is.close();
            /** 血盟製作材料 */
            material_1 = Integer.parseInt(set.getProperty("material_1", "8"));
            material_2 = Integer.parseInt(set.getProperty("material_2", "8"));
            material_3 = Integer.parseInt(set.getProperty("material_3", "8"));
            /** 血盟製作材料 */
            Quantity_item_1 = Integer.parseInt(set.getProperty("Quantity_item_1", "8"));
            Quantity_item_2 = Integer.parseInt(set.getProperty("Quantity_item_2", "8"));
            Quantity_item_3 = Integer.parseInt(set.getProperty("Quantity_item_3", "8"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + ConfigClan);
        } finally {
            set.clear();
        }
    }
}