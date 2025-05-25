package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharacterConfigStorage;
import com.lineage.server.templates.L1Config;
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
 * 快速鍵紀錄
 *
 * @author dexc
 */
public class CharacterConfigTable implements CharacterConfigStorage {

    private static final Log _log = LogFactory.getLog(CharacterConfigTable.class);

    private static final Map<Integer, L1Config> _configList = new HashMap<>();

    /**
     * 刪除遺失資料
     *
     */
    private static void delete(final int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_config` WHERE `object_id`=?");
            ps.setInt(1, objid);
            ps.execute();

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 初始化載入
     */
    @Override
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `character_config`");
            rs = pstm.executeQuery();

            L1Config l1Configl;
            while (rs.next()) {
                final int objid = rs.getInt("object_id");

                // 檢查該資料所屬是否遺失
                if (CharObjidTable.get().isChar(objid) != null) {
                    l1Configl = new L1Config();
                    l1Configl.setObjid(objid);
                    l1Configl.setLength(rs.getInt("length"));
                    l1Configl.setData(rs.getBytes("data"));

                    _configList.put(objid, l1Configl);

                } else {
                    // 資料遺失刪除記錄
                    delete(objid);
                }
            }

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("載入人物快速鍵紀錄資料數量: " + _configList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回 L1Config
     */
    @Override
    public L1Config get(final int objectId) {
        return _configList.get(objectId);
    }

    /**
     * 新建 L1Config
     */
    @Override
    public void storeCharacterConfig(final int objectId, final int length, final byte[] data) {
        L1Config config = new L1Config();
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `character_config` SET `object_id`=?,`length`=?,`data`=?");
            pstm.setInt(1, objectId);
            pstm.setInt(2, length);
            pstm.setBytes(3, data);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        config.setObjid(objectId);
        config.setLength(length);
        config.setData(data);
        _configList.put(objectId, config);
    }

    /**
     * 更新 L1Config
     */
    @Override
    public void updateCharacterConfig(final int objectId, final int length, final byte[] data) {
        L1Config config = _configList.get(objectId);
        if (config == null) {
            storeCharacterConfig(objectId, length, data);
            return;
        }
        config.setObjid(objectId);
        config.setLength(length);
        config.setData(data);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `character_config` SET `length`=?,`data`=? WHERE `object_id`=?");
            pstm.setInt(1, length);
            pstm.setBytes(2, data);
            pstm.setInt(3, objectId);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _configList.put(objectId, config); // 2024/04/27 新增
    }
}
