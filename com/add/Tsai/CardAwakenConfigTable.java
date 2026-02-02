package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 覺醒設定載入與查詢
 */
public class CardAwakenConfigTable {
    private static final Log _log = LogFactory.getLog(CardAwakenConfigTable.class);
    private static CardAwakenConfigTable _instance;

    // key: cardId -> (stage -> config)
    private final Map<Integer, Map<Integer, CardAwakenConfig>> byCardId = new HashMap<>();

    public static CardAwakenConfigTable get() {
        if (_instance == null) {
            _instance = new CardAwakenConfigTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_變身卡覺醒設定`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("流水號");
                String cmd = rs.getString("卡冊指令");
                int cardId = rs.getInt("卡片流水號");
                int stage = rs.getInt("階段");
                String htmlKey = rs.getString("html_key");
                String title = rs.getString("標題文字");
                String desc = rs.getString("說明文字");
                int expPerFeed = rs.getInt("每素材EXP");
                int successRate = rs.getInt("成功機率");
                int failKeep = rs.getInt("失敗保留EXP");
                int successQuestId = rs.getInt("覺醒成功任務編號");
                // 仍需使用：覺醒後 polyId 與 變身時效加秒
                Integer awakenPolyId = (Integer) rs.getObject("覺醒後polyId");
                int addPolyTime = rs.getInt("覺醒後時效加秒");
                int costDiscount = 0;
                String feedRule = null;
                String feedList = rs.getString("素材道具清單");
                int[] feedItems = parseItemList(feedList);
                String demandList = null;
                int demandCount = 1;
                try {
                    demandList = rs.getString("需求道具清單");
                    // 需求道具數量已移除，改由顯示字串表達。保留預設 1。
                } catch (Exception ignored) {}
                String demandDisplay = "";
                try { demandDisplay = rs.getString("需求道具顯示"); } catch (Exception ignored) {}

                int[] demandItems = parseItemList(demandList);
                CardAwakenConfig cfg = new CardAwakenConfig(id, cmd, cardId, stage, htmlKey, title, desc,
                        expPerFeed, successRate, failKeep,
                        successQuestId, awakenPolyId, addPolyTime, costDiscount, feedRule, feedItems,
                        demandItems, demandCount, demandDisplay);
                byCardId.computeIfAbsent(cardId, k -> new HashMap<>()).put(stage, cfg);
                count++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private int[] parseItemList(String listStr) {
        if (listStr == null || listStr.trim().isEmpty()) return new int[0];
        String[] parts = listStr.replace(" ", "").split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                arr[i] = Integer.parseInt(parts[i]);
            } catch (Exception ignored) {
                arr[i] = 0;
            }
        }
        return arr;
    }

    public CardAwakenConfig getConfig(int cardId, int stage) {
        Map<Integer, CardAwakenConfig> m = byCardId.get(cardId);
        if (m == null) return null;
        return m.get(stage);
    }

    public int getMaxStage(int cardId) {
        Map<Integer, CardAwakenConfig> m = byCardId.get(cardId);
        if (m == null || m.isEmpty()) return 0;
        int max = 0;
        for (Integer s : m.keySet()) if (s != null && s > max) max = s;
        return max;
    }
}


