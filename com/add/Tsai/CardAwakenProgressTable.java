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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家覺醒進度存取
 */
public class CardAwakenProgressTable {
    private static final Log _log = LogFactory.getLog(CardAwakenProgressTable.class);
    private static CardAwakenProgressTable _instance;

    // 快取：(account|cardId) -> progress (線程安全)
    private final Map<String, CardAwakenProgress> cache = new ConcurrentHashMap<>();

    // 修復後代碼
    public static CardAwakenProgressTable get() {
        if (_instance == null) {
            synchronized (CardAwakenProgressTable.class) {
                if (_instance == null) {
                    _instance = new CardAwakenProgressTable();
                }
            }
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
            ps = cn.prepareStatement("SELECT * FROM `character_變身卡覺醒`");
            rs = ps.executeQuery();
            while (rs.next()) {
                String account = rs.getString("帳號名稱");
                int cardId = rs.getInt("卡片流水號");
                int stage = rs.getInt("階段");
                int exp = rs.getInt("exp");
                CardAwakenProgress p = new CardAwakenProgress(account, cardId, stage, exp);
                cache.put(key(account, cardId), p);
                count++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->character_變身卡覺醒: " + count + "(" + timer.get() + "ms)");
    }

    private String key(String account, int cardId) { return account + "|" + cardId; }

    public CardAwakenProgress get(String account, int cardId) {
        return cache.get(key(account, cardId));
    }

    public void upsert(CardAwakenProgress p) {
        cache.put(key(p.getAccount(), p.getCardId()), p);
        Connection co = null; PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("REPLACE INTO `character_變身卡覺醒`(`帳號名稱`,`卡片流水號`,`階段`,`exp`,`更新時間`) VALUES(?,?,?,?,NOW())");
            int i = 0;
            pm.setString(++i, p.getAccount());
            pm.setInt(++i, p.getCardId());
            pm.setInt(++i, p.getStage());
            pm.setInt(++i, p.getExp());
            pm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }
}


