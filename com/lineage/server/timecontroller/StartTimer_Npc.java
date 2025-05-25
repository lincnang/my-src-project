package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.npc.*;

import java.util.concurrent.TimeUnit;

public class StartTimer_Npc {
    public void start() throws InterruptedException {
        NpcChatTimer npcChatTimeController = new NpcChatTimer();
        npcChatTimeController.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcHprTimer npcHprTimer = new NpcHprTimer();
        npcHprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcMprTimer npcMprTimer = new NpcMprTimer();
        npcMprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcDeleteTimer npcDeleteTimer = new NpcDeleteTimer();
        npcDeleteTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcExistTimer npcexistTimer = new NpcExistTimer();// BOSS存在時間限制
        npcexistTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcDeadTimer npcDeadTimer = new NpcDeadTimer();
        npcDeadTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcDigestItemTimer digestItemTimer = new NpcDigestItemTimer();
        digestItemTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcSpawnBossTimer bossTimer = new NpcSpawnBossTimer();
        bossTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcShopTimer shopTimer = new NpcShopTimer();
        shopTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcRestTimer restTimer = new NpcRestTimer();
        restTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcWorkTimer workTimer = new NpcWorkTimer();
        workTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        NpcBowTimer bow = new NpcBowTimer();
        bow.start();
        // NPC寶箱 by terry0412
        final NpcBoxRepairTimer boxRepair = new NpcBoxRepairTimer();
        boxRepair.start();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.StartTimer_Npc JD-Core Version: 0.6.2
 */