package com.lineage.config;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class ConfigAI {

    private ConfigAI() {}

    // 全域關閉 L1J AI，強制採用原生 AI
    public static final boolean USE_L1J_AI = false;
    public static final Set<Integer> L1J_AI_NPC_IDS = new HashSet<Integer>();
    public static final Set<Integer> EXCLUDE_NPC_IDS = new HashSet<Integer>();

    public static void load() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("./config/ai_settings.properties"));
        } catch (Exception e) {
            // 若無配置，採用預設（全部不啟用）
            return;
        }

        // 移除 ai.use_l1j_mode，全域採原生 AI（白名單仍可覆蓋）

        try {
            final String white = prop.getProperty("ai.l1j_mode_npc_ids", "").trim();
            if (!white.isEmpty()) {
                for (String id : white.split(",")) {
                    L1J_AI_NPC_IDS.add(Integer.parseInt(id.trim()));
                }
            }
        } catch (Exception ignored) {}

        try {
            final String black = prop.getProperty("ai.exclude_npc_ids", "").trim();
            if (!black.isEmpty()) {
                for (String id : black.split(",")) {
                    EXCLUDE_NPC_IDS.add(Integer.parseInt(id.trim()));
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * 判斷指定 NPC 是否使用 L1J AI。
     * 規則：黑名單 > 白名單 > 全域開關。
     */
    public static boolean useL1JAIForNpc(final int npcId) {
        if (EXCLUDE_NPC_IDS.contains(npcId)) {
            return false;
        }
        if (!L1J_AI_NPC_IDS.isEmpty()) {
            return L1J_AI_NPC_IDS.contains(npcId);
        }
        return USE_L1J_AI;
    }
}


