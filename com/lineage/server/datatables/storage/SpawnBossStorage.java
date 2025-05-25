package com.lineage.server.datatables.storage;

import com.lineage.server.model.L1Spawn;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * BOSS召喚資料
 *
 * @author dexc
 */
public interface SpawnBossStorage {
    /**
     * 初始化載入
     */
    public void load();

    /**
     * 更新資料庫 下次召喚時間紀錄
     *
     */
    public void upDateNextSpawnTime(final int id, final Calendar spawnTime);

    /**
     * BOSS召喚列表中物件
     *
     */
    public L1Spawn getTemplate(final int key);

    /**
     * BOSS召喚列表中物件(NPCID)
     *
     * @return _bossId
     */
    public List<Integer> bossIds();

    public abstract Map<Integer, L1Spawn> get_bossSpawnTable();

    public int BossSeachSize();

    public L1Spawn getBossSeach(final int id);
}
