package com.lineage.server.model.drop;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;
import com.lineage.server.templates.L1DropMob;

import java.util.ArrayList;
import java.util.Map;

/**
 * NPC持有物品取回
 *
 * @author dexc
 */
public interface SetDropExecutor {
    /**
     * 設置MAP資料
     *
     */
    public void addDropMapX(Map<Integer, ArrayList<L1DropMap>> droplists);

    /**
     * 設定全怪物掉落資料
     *
     */
    public void addDropMob(Map<Integer, L1DropMob> droplists);

    /**
     * NPC持有物品資料取回
     *
     */
    public void setDrop(L1NpcInstance npc, L1Inventory inventory);

    /**
     * NPC持有物品資料取回
     *
     */
    public void setDrop(L1NpcInstance npc, L1Inventory inventory, double random);

    /**
     * 設置MAP資料
     *
     */
    public void addDropMap(Map<Integer, ArrayList<L1Drop>> droplists);
}
