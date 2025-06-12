package com.lineage.server.timecontroller.event;

import com.lineage.server.datatables.lock.ShopLimitReading;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 每日商店限購紀錄重置排程
 *
 * @author L1J-AI
 */
public class DailyShopResetTask implements Runnable {

    private static final Log _log = LogFactory.getLog(DailyShopResetTask.class);

    @Override
    public void run() {
        try {
            // 呼叫我們在步驟三建立的管理類別中的重置方法
            ShopLimitReading.get().resetDailyPurchases();
            _log.info("每日商店限購紀錄已成功重置。");
        } catch (final Exception e) {
            _log.error("重置每日商店限購紀錄時發生錯誤", e);
        }
    }
}