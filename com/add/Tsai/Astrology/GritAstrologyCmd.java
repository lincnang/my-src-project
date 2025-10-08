package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守護星盤 - 格立特 指令處理（流程比照絲莉安）
 */
public class GritAstrologyCmd {

    private static final Log _log = LogFactory.getLog(GritAstrologyCmd.class);

    // 玩家已啟用技能紀錄（格立特專用）
    private static final Map<Integer, Integer> _GRIT_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _GRIT_SKILL_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _GRIT_LAST_BTN = new ConcurrentHashMap<>();

    // 偏移避免與其他星盤共用 quest key
    private static final int QUEST_KEY_OFFSET = 3000;
    private int qk(int id) { return QUEST_KEY_OFFSET + id; }

    private static GritAstrologyCmd _instance;

    public static GritAstrologyCmd get() {
        if (_instance == null) {
            synchronized (GritAstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new GritAstrologyCmd();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        _GRIT_SKILLS.remove(playerId);
        _GRIT_SKILL_NAMES.remove(playerId);
        _GRIT_LAST_BTN.remove(playerId);
    }

    /**
     * 指令入口（仿照 SilianAstrologyCmd 流程，改為格立特命名與 UI）
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        // 僅當處於格立特星盤或開啟入口時處理（絲莉安用 2，這裡沿用 3 做為 plate type）
        if (!"astr4".equals(cmd) && pc.getAstrologyPlateType() != 3) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面（開啟格立特星盤）
            if ("astr4".equals(cmd)) {
                pc.setAstrologyPlateType(3);
                updateUI(pc, "t_gretel");
                return true;
            }

            // 點擊格立特節點（直接嘗試解鎖或切換生效）
            if (cmd.startsWith("tgr_")) {
                int id = Integer.parseInt(cmd.substring(4));
                GritAstrologyData data = GritAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("格立特星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }

                // 前置檢查（若有需求可以在此加入自定規則；目前沿用線性順序規則）
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }

                // 建立/取得抽卡任務進度
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), qk(id));
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), qk(id), data.getCards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), qk(id), data.getCards());
                }

                // 實現格立特星盤邏輯...
                return true;
            }

        } catch (Exception e) {
            _log.error("格立特星盤指令處理錯誤", e);
        }
        return false;
    }
    
    private void updateUI(L1PcInstance pc, String htmlKey) {
        // UI更新邏輯
    }
    
    private boolean checkEndAstrologyQuest(L1PcInstance pc, int id) {
        // 前置檢查邏輯
        return false;
    }

    /**
     * 取得玩家「最後點擊/選擇」的格立特節點按鈕編號
     * 用於戰鬥時讀取該節點的技能特效與數值
     * 若尚未選擇則回傳 -1
     */
    public int getGritLastBtn(final L1PcInstance pc) {
        if (pc == null) return -1;
        Integer btn = _GRIT_LAST_BTN.get(pc.getId());
        return btn != null ? btn : -1;
    }
}