package com.lineage.server.timecontroller;

import com.lineage.config.ConfigAlt;
import com.lineage.server.timecontroller.event.WorldChatTimer;
import com.lineage.server.timecontroller.server.*;

import java.util.concurrent.TimeUnit;
//import com.lineage.server.timecontroller.server.ServerShipTimer;

/**
 * 服務器專用時間軸 初始化啟動
 *
 * @author dexc
 */
public class StartTimer_Server {
    public void start() throws InterruptedException {
        // 陷阱召喚處理時間軸
        final ServerTrapTimer trapTimer = new ServerTrapTimer();
        trapTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 村莊系統
        // ServerHomeTownTime.getInstance();
        // TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 城戰計時軸
        final ServerWarTimer warTimer = new ServerWarTimer();
        warTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 拍賣公告欄 更新計時器
        final ServerAuctionTimer auctionTimeController = new ServerAuctionTimer();
        auctionTimeController.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 血盟小屋稅收計時器
        final ServerHouseTaxTimer houseTaxTimeController = new ServerHouseTaxTimer();
        houseTaxTimeController.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 燈光照明計時器
        final ServerLightTimer lightTimeController = new ServerLightTimer();
        lightTimeController.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 元素石生成 計時器
        if (ConfigAlt.ELEMENTAL_STONE_AMOUNT > 0) {
            final ServerElementalStoneTimer elementalStoneGenerator = new ServerElementalStoneTimer();
            elementalStoneGenerator.start();
            TimeUnit.MILLISECONDS.sleep(50);// 延遲
        }
        // 地面物品清除
        if (ConfigAlt.ALT_ITEM_DELETION_TIME > 0) {
            final ServerDeleteItemTimer deleteitem = new ServerDeleteItemTimer();
            deleteitem.start();
            TimeUnit.MILLISECONDS.sleep(50);// 延遲
        }
        // 自動重啟計時器
        final ServerRestartTimer autoRestart = new ServerRestartTimer();
        autoRestart.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 計時地圖時間軸
        final ServerUseMapTimer useMapTimer = new ServerUseMapTimer();
        useMapTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 檢查重置限時地監時間軸
        final ServerResetMapTimer resetMapTimer = new ServerResetMapTimer();
        resetMapTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        /* 變更限時物品檢測方法 by 聖子默默
        // 物品使用期限計時時間軸異常重啟
        final ServerItemUserTimer userTimer = new ServerItemUserTimer();
        userTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        */
        final WorldChatTimer worldChat = new WorldChatTimer();
        worldChat.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
    }
}
