package com.lineage.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 線程管理中心
 *
 * @author 老爹
 */
public final class ThreadPoolSet {
    private static final String THREAD_POOL_FILE = "./config/自動掛機設置.properties";

    /**
     * 通用排程池（AI、系統、技能、怪物死亡等）<br>
     * 預設200<br>
     * GeneralThreadPool._scheduler 使用
     */
    public static int SCHEDULER_CORE_POOL;

    /**
     * 玩家專用排程池（L1PcMonitor 等）<br>
     * 預設100<br>
     * GeneralThreadPool._pcScheduler 使用
     */
    public static int PC_SCHEDULER_POOL;

    // 跟隨系統專用設定（FollowPc使用）
    public static int ATTACK_SPEED;
    public static int WALK_SPEED;
    public static int REXT;

    // 以下變數已廢棄，保留向後兼容
    @Deprecated
    public static int MoveTeleport;
    @Deprecated
    public static int MOVE_SLEEP;
    @Deprecated
    public static int ATTACTK_SLEEP;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(THREAD_POOL_FILE).toPath());
            set.load(is);
            is.close();

            // 讀取 GeneralThreadPool 專用配置
            SCHEDULER_CORE_POOL = Integer.parseInt(set.getProperty("SchedulerCorePool", "200"));
            PC_SCHEDULER_POOL = Integer.parseInt(set.getProperty("PcSchedulerPool", "100"));

            // 其他設定
            MoveTeleport = Integer.parseInt(set.getProperty("MoveTeleport", "10"));
            MOVE_SLEEP = Integer.parseInt(set.getProperty("Move_Sleep", "800"));
            ATTACTK_SLEEP = Integer.parseInt(set.getProperty("Attactk_Sleep", "500"));
            ATTACK_SPEED = Integer.parseInt(set.getProperty("ATTACK_SPEED", "500"));
            WALK_SPEED = Integer.parseInt(set.getProperty("WALK_SPEED", "500"));
            REXT = Integer.parseInt(set.getProperty("REXT", "500"));
        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + THREAD_POOL_FILE);
        } finally {
            set.clear();
        }
    }
}
