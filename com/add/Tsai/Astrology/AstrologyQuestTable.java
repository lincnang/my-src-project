package com.add.Tsai.Astrology;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.AstrologyQuestStorage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * 星盤任務編號表
 *
 * @author hero
 */
public class AstrologyQuestTable implements AstrologyQuestStorage {
    private static final Log _log = LogFactory.getLog(AstrologyQuestTable.class);
    /**
     * 玩家星盤記錄
     */
    private static final HashMap<Integer, QuestAstrology> _astrologyQuestIndex = new HashMap<>();
    private static AstrologyQuestTable _instance;

    public static AstrologyQuestTable get() {
        if (_instance == null) {
            _instance = new AstrologyQuestTable();
        }
        return _instance;
    }

    /**
     * 載入星盤任務編號表
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int t = 0;
        QuestAstrology questAstrology;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_星盤`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int objId = rs.getInt("objid");

                if (!_astrologyQuestIndex.containsKey(objId)) {
                    questAstrology = new QuestAstrology();
                    _astrologyQuestIndex.put(objId, questAstrology);
                } else {
                    questAstrology = _astrologyQuestIndex.get(objId);
                }

                final int key = rs.getInt("星盤編號");
                final int num = rs.getInt("目前數量");

                final AstrologyQuest astrologyQuest = new AstrologyQuest(objId, key, num);
                questAstrology._astrologyQuest.put(key, astrologyQuest);
                t++;

            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->[character_星盤]紀錄: " + t + "(" + timer.get() + "ms)");
    }

    /**
     * 獲取到玩家指定的星盤記錄<br>
     * 變更方法以獲取真正的記錄檔案 by 聖子默默
     *
     * @param objId         玩家 objId
     * @param astrologyType 星盤編號
     */
    @Override
    public AstrologyQuest get(int objId, int astrologyType) {
        if (_astrologyQuestIndex == null || _astrologyQuestIndex.isEmpty()) {
            return null;
        }
        if (_astrologyQuestIndex.containsKey(objId)) {
            return _astrologyQuestIndex.get(objId)._astrologyQuest.get(astrologyType);
        }
        return null;
    }

    /**
     * 保存玩家星盤信息
     *
     * @param objId 玩家 objId
     * @param key   星盤編號
     * @param num   目前數量
     */
    @Override
    public void storeQuest(int objId, int key, int num) {
        Connection co = null;
        PreparedStatement pm = null;
        QuestAstrology questAstrology;

        if (!_astrologyQuestIndex.containsKey(objId)) {
            questAstrology = new QuestAstrology();
            _astrologyQuestIndex.put(objId, questAstrology);
        } else {
            questAstrology = _astrologyQuestIndex.get(objId);
        }
        if (questAstrology._astrologyQuest.containsKey(key)) {
            _log.warn("星盤記錄寫入異常,玩家編號: " + objId + " 星盤編號: " + key + " 已經存在.");
            _log.error(questAstrology._astrologyQuest.get(key).toString());
            return;
        }

        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_星盤` SET `objid`=?,`星盤編號`=?,`目前數量`=?");
            int i = 0;
            pm.setInt(++i, objId);
            pm.setInt(++i, key);
            pm.setInt(++i, num);
            pm.execute();

            final AstrologyQuest astrologyQuest = new AstrologyQuest(objId, key, num);
            questAstrology._astrologyQuest.put(key, astrologyQuest);

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    /**
     * 更新玩家星盘信息
     *
     * @param objId 玩家 objId
     * @param key   星盤編號
     * @param num   目前數量
     */
    @Override
    public void updateQuest(int objId, int key, int num) {
        //AstrologyQuest data = _astrologyQuestIndex.get(objId);
        _astrologyQuestIndex.get(objId)._astrologyQuest.put(key, new AstrologyQuest(objId, key, num));
        //_astrologyQuestIndex.put(objId, new AstrologyQuest(objId, key, num));
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `character_星盤` SET `目前數量`=? WHERE `objid`=? AND `星盤編號`=?");
            int i = 0;
            pm.setInt(++i, num);
            pm.setInt(++i, objId);
            pm.setInt(++i, key);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void delQuest(int objId, int key) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE  FROM `character_星盤` WHERE `objid`=? AND `星盤編號`=?");
            int i = 0;
            pm.setInt(++i, objId);
            pm.setInt(++i, key);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void storeQuest2(int objId, int key, int value) {

    }

    @Override
    public void delQuest2(int key) {

    }

    private static class QuestAstrology {
        /**
         * 星盘编号为主键 新的 AstrologyQuest 实例
         */
        private final Map<Integer, AstrologyQuest> _astrologyQuest = new HashMap<>();
    }

}