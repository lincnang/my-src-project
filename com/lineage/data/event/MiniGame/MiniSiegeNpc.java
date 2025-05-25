package com.lineage.data.event.MiniGame;

import com.lineage.config.ConfigThebes;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * 底比斯大戰遊戲NPC(次元之門)召喚控制項時間軸
 */
public class MiniSiegeNpc extends TimerTask {
    private static MiniSiegeNpc _instance;
    private Timer _timeHandler = new Timer(true);
    private boolean _isOver = false;
    // 底比斯大戰遊戲NPC召喚處理時間軸已開始時間(秒)
    private int _startTime = 0;

    private MiniSiegeNpc() {// 啟動NPC召喚處理時間軸
        // 開始執行此時間軸 間隔一秒
        _timeHandler.schedule(this, 1000, 1000);
        // 交由線程工廠 處理
        GeneralThreadPool.get().execute(this);
    }

    public static MiniSiegeNpc getStart() {
        if (_instance == null) {
            _instance = new MiniSiegeNpc();
        }
        return _instance;
    }

    @Override
    public void run() {
        if (_isOver) { // NPC召喚是否結束
            try {
                clear(); // 清除召喚NPC
                TimeUnit.MILLISECONDS.sleep((ConfigThebes.MiniSiege_PlayTime + 8) * 1000L); // 遊戲時間 (秒)
                if (MiniSiege.getInstance().running) { // 判斷遊戲時間到而且遊戲還在進行中未分出勝負強制結束
                    MiniSiege.getInstance().setStage(5); // 時間到結束
                }
                TimeUnit.MILLISECONDS.sleep(((long) ConfigThebes.MiniSiege_NextTime * 60 * 1000) - (2 * 1000)); // 再次開啟間隔時間(分)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _startTime++;
        if (_startTime == 2) { // 2秒
            spawnCrack();// 召喚NPC
        }
        // 關閉前10秒提示
		/*if (_startTime == ((ConfigOther.MiniSiege_ReadyTime + 2) - 10) {
			World.get().broadcastServerMessage("\\aE風木村莊的次元之門即將在10秒後消失...");
		}*/
        // 到時間關閉
        if (_startTime >= (ConfigThebes.MiniSiege_ReadyTime + 2)) {
            _isOver = true;
        }
    }

    /**
     * 清除召喚NPC
     */
    private void clear() {
        _startTime = 0;
        _isOver = false;
        World.get().broadcastServerMessage("\\aE風木村的底比斯大戰次元之門消失了。");
        for (L1Object obj : WorldNpc.get().all()) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getNpcId() == 4070087) {
                    npc.deleteMe();
                }
            }
        }
        if (MiniSiege.getInstance().running) { // 判斷開啟
            if (MiniSiege.getMembersCount() < ConfigThebes.MiniSiege_MinPlayer) {
                MiniSiege.getInstance().setStage(4); // 人數不足強制結束
            } else {
                MiniSiege.getInstance().setStage(1); // 人數足夠 開啟攻塔
            }
        }
    }

    /**
     * 次元之門開啟
     */
    private void spawnCrack() {
        L1Location loc = new L1Location(32620, 33181, 4);
        L1SpawnUtil.spawnR(loc, 4070087, -1, 1);
        //World.get().broadcastServerMessage("\\aE風木村出現底比斯大戰次元之門,"+ConfigOther.MiniSiege_ReadyTime+"秒後結束,報名盡快");
        World.get().broadcastServerMessage("\\aE風木村出現底比斯大戰次元之門,");
        World.get().broadcastServerMessage("\\aE想參加的盡快哦, " + ConfigThebes.MiniSiege_ReadyTime + "秒後結束報名。");
        if (!MiniSiege.getInstance().running) { // 判斷關閉
            MiniSiege.getInstance().ini();
        }
    }
}