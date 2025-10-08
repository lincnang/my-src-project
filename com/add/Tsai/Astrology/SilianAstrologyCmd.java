package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 絲莉安 指令處理
 */
public class SilianAstrologyCmd {

    private static final Log _log = LogFactory.getLog(SilianAstrologyCmd.class);

    // 玩家已啟用技能紀錄（絲莉安專用）
    private static final Map<Integer, Integer> _SILIAN_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _SILIAN_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _SILIAN_LAST_BTN = new ConcurrentHashMap<>();

    // 偏移避免與其他星盤共用 quest key
    private static final int QUEST_KEY_OFFSET = 2000;
    private int qk(int id) { return QUEST_KEY_OFFSET + id; }

    private static SilianAstrologyCmd _instance;

    public static SilianAstrologyCmd get() {
        if (_instance == null) {
            synchronized (SilianAstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new SilianAstrologyCmd();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _SILIAN_SKILLS.remove(playerId);
        _SILIAN_SKILL_NAMES.remove(playerId);
        _SILIAN_LAST_BTN.remove(playerId);
    }

    /**
     * 指令入口（完整複製 AttonAstrologyCmd 流程，改為絲莉安命名與 UI）
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 實現絲莉安星盤指令邏輯
        // ... 原有邏輯保持不變
        return false;
    }

    /**
     * 取得玩家當前啟用的絲莉安技能節點按鈕編號
     * 備註：回傳值為節點按鈕順序，用於 SilianAstrologyTable.get().getData(key)
     */
    public Integer getSilianSkillActive(final L1PcInstance pc) {
        if (pc == null) return null;
        return _SILIAN_LAST_BTN.get(pc.getId());
    }
}