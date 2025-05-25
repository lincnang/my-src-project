package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CardQuestStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 變身任務編號表
 *
 * @author hero
 */
public class CardQuestTable implements CardQuestStorage {
    private static final Log _log = LogFactory.getLog(CardQuestTable.class);
    private static final List<CardQuest> _cardQuestIndex = new ArrayList<>();
    private static CardQuestTable _instance;

    public static CardQuestTable get() {
        if (_instance == null) {
            _instance = new CardQuestTable();
        }
        return _instance;
    }

    /**
     * 載入變身任務編號表
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int t = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_變身卡帳號`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String account = rs.getString("帳號名稱");
                final int questId = rs.getInt("任務編號");
                final CardQuest cardQuest = new CardQuest(account, questId);
                _cardQuestIndex.add(cardQuest);
                t++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->系統_變身任務編號: " + t + "(" + timer.get() + "ms)");
    }

    @Override
    public List<CardQuest> get(String account) {
        return _cardQuestIndex;
    }

    @Override
    public void storeQuest(String account, int key, CardQuest paramCharQuest) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_變身卡帳號` SET `帳號名稱`=?,`任務編號`=?");
            int i = 0;
            pm.setString(++i, account);
            pm.setInt(++i, key);
            pm.execute();
            //------
            final CardQuest cardQuest = new CardQuest(account, key);
            _cardQuestIndex.add(cardQuest);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void storeQuest(String paramInt1, int paramInt2, CardQuest paramCharQuest, int paramInt3) {
    }

    @Override
    public void updateQuest(String account, int key, CardQuest paramCharQuest) {
    }

    @Override
    public void delQuest(String account, int key) {
    }

    @Override
    public void storeQuest2(String account, int key, int value) {
    }

    @Override
    public void delQuest2(int key) {
    }

    public boolean IsQuest(L1PcInstance pc, int questId) {
        try {
            if (_cardQuestIndex == null) {
                return false;
            }
            for (CardQuest quest : _cardQuestIndex) {
                if (quest.getAccount().equals(pc.getAccountName()) && quest.getQuestId() == questId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}