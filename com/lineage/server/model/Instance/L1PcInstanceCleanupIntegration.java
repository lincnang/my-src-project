package com.lineage.server.model.Instance;

import com.add.Tsai.PlayerDataCleanupManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * L1PcInstance 清理集成類
 * 用於在玩家登入/登出時觸發數據清理
 */
public class L1PcInstanceCleanupIntegration {
    private static final Log _log = LogFactory.getLog(L1PcInstanceCleanupIntegration.class);
    
    /**
     * 玩家登入時調用
     */
    public static void onPlayerLogin(L1PcInstance pc) {
        try {
            if (pc != null) {
                PlayerDataCleanupManager.getInstance().onPlayerLogin(pc);
                _log.debug("玩家登入清理集成: " + pc.getName());
            }
        } catch (Exception e) {
            _log.error("玩家登入清理集成失敗", e);
        }
    }
    
    /**
     * 玩家登出時調用
     */
    public static void onPlayerLogout(L1PcInstance pc) {
        try {
            if (pc != null) {
                PlayerDataCleanupManager.getInstance().onPlayerLogout(pc);
                _log.debug("玩家登出清理集成: " + pc.getName());
            }
        } catch (Exception e) {
            _log.error("玩家登出清理集成失敗", e);
        }
    }
    
    /**
     * 玩家活動時調用（可在關鍵操作時調用）
     */
    public static void updatePlayerActivity(L1PcInstance pc) {
        try {
            if (pc != null) {
                PlayerDataCleanupManager.getInstance().updatePlayerActivity(pc);
            }
        } catch (Exception e) {
            _log.error("玩家活動更新失敗", e);
        }
    }
}
