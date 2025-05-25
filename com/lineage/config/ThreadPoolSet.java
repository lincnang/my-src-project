package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
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
     * 假人線程池<br>
     * 預定150<br>
     * GM_Blink,假人攻擊/移動/擺攤/掛網/練功尋怪/釣魚<br>
     */
    public static int DE_POOL_SIZE;
    /**
     * 伺服器用線程池<br>
     * 預定500<br>
     * 伺服器專用<br>
     * 怪物/任務
     */
    public static int SCHEDULED_CORE_POOL_SIZE;
    /**
     * 伺服器專用線池<br>
     * 預定20<br>
     * 玩家使用<br>
     * 目前只參考到隱身<br>
     */
    public static int PC_SCHEDULER_POOL_SIZE;
    /**
     * 玩家專屬線程<BR>
     * 職業回血8/職業回魔8/組隊1/背包物品自動儲存1/人物自動儲存1<BR>
     * 人物升級配點1/人物刪除1/釣魚1/鬼魂/地獄/外掛偵測/錯位<BR>
     * 職業可見物更新8<BR>
     */
    public static int PC_OTHER_POOL_SIZE;
    /**
     * 控制怪物專用<BR>
     */
    public static int NPC_AI_POOL_SIZE;
    /**
     * 內掛專用<BR>
     */
    public static int PC_AUTO_POOL_SIZE;
    public static int MoveTeleport;
    public static int MOVE_SLEEP;
    public static int ATTACTK_SLEEP;
    public static int ATTACK_SPEED;
    public static int WALK_SPEED;
    public static int REXT;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = Files.newInputStream(new File(THREAD_POOL_FILE).toPath());
            set.load(is);
            is.close();
            DE_POOL_SIZE = Integer.parseInt(set.getProperty("De_Pool", "150"));
            SCHEDULED_CORE_POOL_SIZE = Integer.parseInt(set.getProperty("Server_Pool", "300"));
            PC_SCHEDULER_POOL_SIZE = Integer.parseInt(set.getProperty("Server_Pc_Pool", "20"));
            PC_OTHER_POOL_SIZE = Integer.parseInt(set.getProperty("Pc_Pool", "50"));
            NPC_AI_POOL_SIZE = Integer.parseInt(set.getProperty("Npc_Ai_Pool", "300"));
            PC_AUTO_POOL_SIZE = Integer.parseInt(set.getProperty("Pc_Auto_Pool", "300"));
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
