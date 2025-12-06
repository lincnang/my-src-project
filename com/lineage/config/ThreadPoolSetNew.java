package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 線程管理中心
 *
 * @author 老爹
 */
public final class ThreadPoolSetNew {
    private static final String THREAD_POOL_FILE = "./config/掛機專用.properties";
    /**
     * 內掛專用<BR>
     */
    public static int STOP_COUNT;
    public static int ATTACK_SPEED;
    public static int WALK_SPEED;
    public static int ATTACK_SKILL_RANDOM;
    public static int RESTART_AUTO;
    public static int RESTART_AUTO_START;
    public static int REXT;
    public static int TELEPORT_NEED_COUNT;
    public static int ITEMADENA1;
    public static int ITEMADENA2;
    public static int ITEMADENA3;
    public static int ITEMADENA4;
    public static int ITEMADENA5;
    public static int ITEMADENA6;
    public static int ITEMADENA7;
    public static int ITEMADENA8;
    public static int ITEMADENA9;
    public static int ITEMADENA10;
    public static int ITEMADENA11;
    public static ArrayList<Integer> CAN_USE_ATTACK_SKILL = new ArrayList<>();

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(THREAD_POOL_FILE).toPath());
            set.load(is);
            is.close();
            if (set.getProperty("CAN_USE_ATTACK_SKILL") != null) {
                String skillStr = set.getProperty("CAN_USE_ATTACK_SKILL").trim();
                if (!skillStr.isEmpty()) {
                    for (String S : skillStr.split(",")) {
                        String trimmed = S.trim();
                        if (!trimmed.isEmpty()) {
                            CAN_USE_ATTACK_SKILL.add(Integer.parseInt(trimmed));
                        }
                    }
                }
            }
            STOP_COUNT = Integer.parseInt(set.getProperty("STOP_COUNT", "3"));
            ATTACK_SKILL_RANDOM = Integer.parseInt(set.getProperty("ATTACK_SKILL_RANDOM", "5"));
            RESTART_AUTO = Integer.parseInt(set.getProperty("RESTART_AUTO", "60"));
            RESTART_AUTO_START = Integer.parseInt(set.getProperty("RESTART_AUTO_START", "5"));
            TELEPORT_NEED_COUNT = Integer.parseInt(set.getProperty("TELEPORT_NEED_COUNT", "100"));
            REXT = Integer.parseInt(set.getProperty("REXT", "100"));
            ATTACK_SPEED = Integer.parseInt(set.getProperty("ATTACK_SPEED", "350"));
            WALK_SPEED = Integer.parseInt(set.getProperty("WALK_SPEED", "450"));
            
            // 讀取商品價格設定
            ITEMADENA1 = Integer.parseInt(set.getProperty("ITEMADENA1", "80"));
            ITEMADENA2 = Integer.parseInt(set.getProperty("ITEMADENA2", "180"));
            ITEMADENA3 = Integer.parseInt(set.getProperty("ITEMADENA3", "0"));
            ITEMADENA4 = Integer.parseInt(set.getProperty("ITEMADENA4", "0"));
            ITEMADENA5 = Integer.parseInt(set.getProperty("ITEMADENA5", "0"));
            ITEMADENA6 = Integer.parseInt(set.getProperty("ITEMADENA6", "0"));
            ITEMADENA7 = Integer.parseInt(set.getProperty("ITEMADENA7", "800"));
            ITEMADENA8 = Integer.parseInt(set.getProperty("ITEMADENA8", "1500"));
            ITEMADENA9 = Integer.parseInt(set.getProperty("ITEMADENA9", "1500"));
            ITEMADENA10 = Integer.parseInt(set.getProperty("ITEMADENA10", "20000"));
            ITEMADENA11 = Integer.parseInt(set.getProperty("ITEMADENA11", "0"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + THREAD_POOL_FILE);
        } finally {
            set.clear();
        }
    }
}
