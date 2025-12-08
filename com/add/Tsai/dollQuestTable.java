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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 娃娃任務編號表
 *
 * @author hero
 */
public class dollQuestTable implements DollQuestStorage {
    private static final Log _log = LogFactory.getLog(dollQuestTable.class);
    private static final List<dollQuest> _dollQuestIndex = new ArrayList<>();
    // 優化查詢速度的緩存：帳號 -> 任務ID集合 (使用 ConcurrentHashMap 提升併發效能)
    private static final Map<String, Set<Integer>> _questCache = new ConcurrentHashMap<>();
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
                
                // 更新緩存 (ConcurrentHashMap 支援併發，computeIfAbsent 是原子操作)
                // 使用 ConcurrentHashMap.newKeySet() 創建線程安全的 Set
                _questCache.computeIfAbsent(account, k -> ConcurrentHashMap.newKeySet()).add(questId);
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
            // 使用 INSERT IGNORE 或 ON DUPLICATE KEY UPDATE 避免重複
            pm = co.prepareStatement("INSERT IGNORE INTO `character_娃娃卡帳號` (`帳號名稱`,`任務編號`) VALUES (?,?)");
            int i = 0;
            pm.setString(++i, account);
            pm.setInt(++i, key);
            int affectedRows = pm.executeUpdate();

            // 只有在成功插入時才添加到記憶體中
            if (affectedRows > 0) {
                final dollQuest dollQuest = new dollQuest(account, key);
                _dollQuestIndex.add(dollQuest);
                
                // 更新緩存
                _questCache.computeIfAbsent(account, k -> ConcurrentHashMap.newKeySet()).add(key);
//                _log.info("娃娃卡任務記錄: 帳號=" + account + ", 任務編號=" + key);
            } else {
                _log.info("娃娃卡任務已存在，跳過: 帳號=" + account + ", 任務編號=" + key);
            }
        } catch (final SQLException e) {
            _log.error("儲存娃娃卡任務時發生錯誤: " + e.getLocalizedMessage(), e);
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
            // ConcurrentHashMap 讀取時不需要 synchronized 鎖，效能極高
            Set<Integer> quests = _questCache.get(pc.getAccountName());
            if (quests != null) {
                return quests.contains(questId);
            }
            return false;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}