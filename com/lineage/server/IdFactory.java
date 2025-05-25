package com.lineage.server;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class IdFactory {
    private static final Log _log = LogFactory.getLog(IdFactory.class);
    private static IdFactory _instance;
    private final ArrayList<Integer> _MobblingList = new ArrayList<Integer>();
    private final ArrayList<Integer> _BigHotblingList = new ArrayList<Integer>();

    private Object _monitor;
    private AtomicInteger _nextId;
    /**
     * [原碼] 怪物對戰系統
     */
    private int _MobId;
    /**
     * [原碼] 大樂透系統
     */
    private int _BigHotId;

    public IdFactory() {
        _monitor = new Object();
    }

    public static IdFactory get() {
        if (_instance == null) {
            _instance = new IdFactory();
        }
        return _instance;
    }

    /**
     * [原碼] 怪物對戰系統
     */
    public static IdFactory getId() {
        if (_instance == null) {
            _instance = new IdFactory();
        }
        return _instance;
    }

    public int nextId() {
        synchronized (_monitor) {
            return _nextId.getAndIncrement();
        }
    }

    public int maxId() {
        synchronized (_monitor) {
            return _nextId.get();
        }
    }

    /**
     * 取回資料庫中已用最大編號
     */
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement(
                    //"SELECT MAX(id)+1 AS NEXTID FROM (SELECT `id` FROM `character_items` UNION ALL SELECT `id` FROM `character_warehouse` UNION ALL SELECT `id` FROM `character_elf_warehouse` UNION ALL SELECT `id` FROM `clan_warehouse` UNION ALL SELECT `id` FROM `character_shopinfo` UNION ALL SELECT `objid` AS `id` FROM `characters` UNION ALL SELECT `clan_id` AS `id` FROM `clan_data` UNION ALL SELECT `id` FROM `character_teleport` UNION ALL SELECT `id` FROM `mails` UNION ALL SELECT `objid` AS `id` FROM `character_pets`) t");
                    // 7.6信件
                    "SELECT MAX(id)+1 AS NEXTID FROM (SELECT `id` FROM `character_items` UNION ALL SELECT `id` FROM `character_warehouse` UNION ALL SELECT `id` FROM `character_elf_warehouse` UNION ALL SELECT `id` FROM `clan_warehouse` UNION ALL SELECT `id` FROM `character_shopinfo` UNION ALL SELECT `objid` AS `id` FROM `characters` UNION ALL SELECT `clan_id` AS `id` FROM `clan_data` UNION ALL SELECT `id` FROM `character_teleport` UNION ALL SELECT `id` FROM `character_mail` UNION ALL SELECT `objid` AS `id` FROM `character_pets`) t");
            rs = ps.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("nextid");
            }
            if (id < ServerReading.get().minId()) {
                id = ServerReading.get().minId();
            }
            if (id < ServerReading.get().maxId()) {
                id = ServerReading.get().maxId();
            }
            _nextId = new AtomicInteger(id);
            _log.info("讀取->已用最大id編號: " + id + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error("id數據加載異常!", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void addMobId(int i) {
        _MobblingList.add(Integer.valueOf(i));
    }

    public int nextMobId() {
        _MobId += 1;
        while (_MobblingList.contains(Integer.valueOf(_MobId))) {
            _MobId += 1;
        }
        return _MobId;
    }

    /**
     * [原碼] 大樂透系統
     */
    public void addBigHotId(int i) {
        _BigHotblingList.add(Integer.valueOf(i));
    }

    public int nextBigHotId() {
        _BigHotId += 1;
        while (_BigHotblingList.contains(Integer.valueOf(_BigHotId))) {
            _BigHotId += 1;
        }
        return _BigHotId;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.IdFactory JD-Core Version: 0.6.2
 */