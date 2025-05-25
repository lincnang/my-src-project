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
                for (String S : set.getProperty("CAN_USE_ATTACK_SKILL").split(",")) {
                    CAN_USE_ATTACK_SKILL.add(Integer.valueOf(Integer.parseInt(S)));
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
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + THREAD_POOL_FILE);
        } finally {
            set.clear();
        }
    }
}
