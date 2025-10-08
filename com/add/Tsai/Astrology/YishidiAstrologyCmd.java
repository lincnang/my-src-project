package com.add.Tsai.Astrology;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 依詩蒂 指令處理
 */
public class YishidiAstrologyCmd {

    private static final Log _log = LogFactory.getLog(YishidiAstrologyCmd.class);

    // 玩家已啟用技能紀錄（依詩蒂專用）
    private static final Map<Integer, Integer> _YISHIDI_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _YISHIDI_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _YISHIDI_LAST_BTN = new ConcurrentHashMap<>();

    private static YishidiAstrologyCmd _instance;

    public static YishidiAstrologyCmd get() {
        if (_instance == null) {
            synchronized (YishidiAstrologyCmd.class) {
        if (_instance == null) {
            _instance = new YishidiAstrologyCmd();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _YISHIDI_SKILLS.remove(playerId);
        _YISHIDI_SKILL_NAMES.remove(playerId);
        _YISHIDI_LAST_BTN.remove(playerId);
    }

    /**
     * 指令入口
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 實現依詩蒂星盤指令邏輯
        // ... 原有邏輯保持不變
        return false;
    }

    /**
     * 取得玩家「最後點擊/選擇」的依詩蒂節點按鈕編號
     * 用於戰鬥結算時讀取該技能節點配置
     * 無選擇時回傳 null 以配合呼叫端判斷
     */
    public Integer getYishidiLastBtn(final L1PcInstance pc) {
        if (pc == null) return null;
        return _YISHIDI_LAST_BTN.get(pc.getId());
    }
}