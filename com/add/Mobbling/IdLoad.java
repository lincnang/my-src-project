package com.add.Mobbling;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IdLoad {
    private static IdLoad _instance;
    private final IdFactory _loadId = IdFactory.getId();

    private IdLoad() {
        loadState();
    }

    public static IdLoad getInstance() {
        if (_instance == null) {
            _instance = new IdLoad();
        }
        return _instance;
    }

    private void loadState() {
        loadMobChid(); // [原碼] 怪物對戰系統
        loadBigHotChid(); // [原碼] 大樂透系統
    }

    private void loadMobChid() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `race_mobbling`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int i = rs.getInt("id");
                this._loadId.addMobId(i);
            }
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void loadBigHotChid() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `race_bighotbling`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int i = rs.getInt("id");
                this._loadId.addBigHotId(i);
            }
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}