package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.DollQuestStorage;
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
 * 娃娃任務編號表
 *
 * @author hero
 */
public class dollQuestTable implements DollQuestStorage {
    private static final Log _log = LogFactory.getLog(dollQuestTable.class);
    private static final List<dollQuest> _dollQuestIndex = new ArrayList<>();
    private static dollQuestTable _instance;

    public static dollQuestTable get() {
        if (_instance == null) {
            _instance = new dollQuestTable();
        }
        return _instance;
    }

    /**
     * 載入娃娃任務編號表
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int t = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_娃娃卡帳號`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String account = rs.getString("帳號名稱");
                final int questId = rs.getInt("任務編號");
                final dollQuest dollQuest = new dollQuest(account, questId);
                _dollQuestIndex.add(dollQuest);
                t++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->系統_卡片娃娃任務編號: " + t + "(" + timer.get() + "ms)");
    }

    @Override
    public List<dollQuest> get(String account) {
        return _dollQuestIndex;
    }

    @Override
    public void storeQuest(String account, int key, dollQuest paramCharQuest) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_娃娃卡帳號` SET `帳號名稱`=?,`任務編號`=?");
            int i = 0;
            pm.setString(++i, account);
            pm.setInt(++i, key);
            pm.execute();
            //------
            final dollQuest dollQuest = new dollQuest(account, key);
            _dollQuestIndex.add(dollQuest);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void storeQuest(String paramInt1, int paramInt2, dollQuest paramCharQuest, int paramInt3) {
    }

    @Override
    public void updateQuest(String account, int key, dollQuest paramCharQuest) {
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
            if (_dollQuestIndex == null) {
                return false;
            }
            for (dollQuest quest : _dollQuestIndex) {
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