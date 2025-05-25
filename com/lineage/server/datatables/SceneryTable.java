package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.templates.L1Scenery;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SceneryTable {
    private static final Log _log = LogFactory.getLog(SceneryTable.class);
    private static final Map<Integer, L1Scenery> _sceneryList = new HashMap<>();
    private static final Map<Integer, L1Scenery> _fieldList = new HashMap<>();
    private static SceneryTable _instance;

    public static SceneryTable get() {
        if (_instance == null) {
            _instance = new SceneryTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `spawnlist_scenery`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int gfxid = rs.getInt("gfxid");
                int locx = rs.getInt("locx");
                int locy = rs.getInt("locy");
                int heading = rs.getInt("heading");
                int mapid = rs.getInt("mapid");
                String html = rs.getString("html");
                L1Scenery scenery = new L1Scenery();
                scenery.set_gfxid(gfxid);
                scenery.set_locx(locx);
                scenery.set_locy(locy);
                scenery.set_heading(heading);
                scenery.set_mapid(mapid);
                scenery.set_html(html);
                _sceneryList.put(id, scenery);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->景觀設置資料數量: " + _sceneryList.size() + "(" + timer.get() + "ms)");
        satrt();
    }

    private void satrt() {
        for (L1Scenery scenery : _sceneryList.values()) {
            L1FieldObjectInstance field = (L1FieldObjectInstance) NpcTable.get().newNpcInstance(71081);
            if (field != null) {
                field.setId(IdFactoryNpc.get().nextId());
                field.setGfxId(scenery.get_gfxid());
                field.setTempCharGfx(scenery.get_gfxid());
                field.setMap((short) scenery.get_mapid());
                field.setX(scenery.get_locx());
                field.setY(scenery.get_locy());
                field.setHomeX(scenery.get_locx());
                field.setHomeY(scenery.get_locy());
                field.setHeading(scenery.get_heading());
                World.get().storeObject(field);
                World.get().addVisibleObject(field);
                _fieldList.put(field.getId(), scenery);
            }
        }
    }

    public String get_sceneryHtml(int objid) {
        L1Scenery scenery = (L1Scenery) _fieldList.get(objid);
        if ((scenery != null) && (!scenery.get_html().equals("0"))) {
            return scenery.get_html();
        }
        return null;
    }

    public void storeScenery(String note, int gfxid, int locx, int locy, int heading, int mapid, String html) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `spawnlist_scenery` SET `note`=?,`gfxid`=?,`locx`=?,`locy`=?,`heading`=?,`mapid`=?,`html`=?");
            int i = 0;
            pstm.setString(++i, note);
            pstm.setInt(++i, gfxid);
            pstm.setInt(++i, locx);
            pstm.setInt(++i, locy);
            pstm.setInt(++i, heading);
            pstm.setInt(++i, mapid);
            pstm.setString(++i, html);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.SceneryTable JD-Core Version: 0.6.2
 */