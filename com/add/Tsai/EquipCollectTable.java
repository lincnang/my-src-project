package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipCollectTable {
    private static final Log _log = LogFactory.getLog(EquipCollectTable.class);
    private static EquipCollectTable _instance;
    private final List<L1EquipCollect> _EquipCollectIndex = new ArrayList<>();

    public static EquipCollectTable getInstance() {
        if (_instance == null) {
            _instance = new EquipCollectTable();
        }
        return _instance;
    }

    public void loadEquipCollectTable() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_成就系統");
            rs = pstm.executeQuery();
            this.fillEquipCollectTable(rs);
        } catch (SQLException var9) {
            _log.error(var9.getLocalizedMessage(), var9);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close((Statement) pstm);
            SQLUtil.close(con);
        }
        _log.info("載入->[成就登入系統]: " + this._EquipCollectIndex.size() + " (" + timer.get() + "ms)");
    }

    private void fillEquipCollectTable(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int npcid = rs.getInt("npcid");
            String action = rs.getString("action");
            String note = rs.getString("note");
            int checkLevel = rs.getInt("checkLevel");
            int checkClass = rs.getInt("checkClass");
            String materials = rs.getString("materials").replaceAll(" ", "");
            String[] needids_tmp = materials.split(",");
            int[] needids = new int[needids_tmp.length];
            for (int i = 0; i < needids_tmp.length; ++i) {
                needids[i] = Integer.parseInt(needids_tmp[i]);
            }
            String materials_count = rs.getString("materials_count").replaceAll(" ", "");
            String[] needcounts_tmp = materials_count.split(",");
            int[] needcounts = new int[needcounts_tmp.length];
            for (int i = 0; i < needcounts_tmp.length; ++i) {
                needcounts[i] = Integer.parseInt(needcounts_tmp[i]);
            }
            String materials_enchants = rs.getString("materials_enchants").replaceAll(" ", "");
            String[] needenchants_tmp = materials_enchants.split(",");
            int[] needenchants = new int[needenchants_tmp.length];
            for (int i = 0; i < needenchants_tmp.length; ++i) {
                needenchants[i] = Integer.parseInt(needenchants_tmp[i]);
            }
            String sucesshtml = rs.getString("sucess_html");
            String failhtml = rs.getString("fail_html");
            String Allmessage = rs.getString("Allmessage");
            int quest = rs.getInt("quest");
            String givebuff = rs.getString("能力說明");
            L1EquipCollect EquipCollect = new L1EquipCollect();
            EquipCollect.set_npcid(npcid);
            EquipCollect.set_action(action);
            EquipCollect.set_note(note);
            EquipCollect.setCheckLevel(checkLevel);
            EquipCollect.setCheckClass(checkClass);
            EquipCollect.setMaterials(needids);
            EquipCollect.setMaterials_count(needcounts);
            EquipCollect.set_materials_enchants(needenchants);
            EquipCollect.set_sucesshtml(sucesshtml);
            EquipCollect.set_failhtml(failhtml);
            EquipCollect.set_Allmessage(Allmessage);
            EquipCollect.setquest(quest);
            EquipCollect.set_givebuff(givebuff);
            String key = npcid + action;
            this._EquipCollectIndex.add(EquipCollect);
            //         this.loadEquipCollect(npcid);
        }
    }

    //   private void loadEquipCollect(int npcid) {
    //      Connection con = null;
    //      PreparedStatement pstm = null;
    //      ResultSet rs = null;
    //
    //      try {
    //         con = DatabaseFactory.get().getConnection();
    //         pstm = con.prepareStatement("SELECT * FROM plugin_equip_collect WHERE `npcid` =?");
    //         pstm.setInt(1, npcid);
    //         rs = pstm.executeQuery();
    //
    //         while(rs.next()) {
    //            String action = rs.getString("action");
    //            String note = rs.getString("note");
    //            String key = npcid + action;
    //            this._EquipCollectList.put(key, note);
    //         }
    //      } catch (SQLException var11) {
    //         _log.error(var11.getLocalizedMessage(), var11);
    //      } finally {
    //         SQLUtil.close(rs);
    //         SQLUtil.close((Statement)pstm);
    //         SQLUtil.close(con);
    //      }
    //
    //   }
    public L1EquipCollect getTemplate(String craftkey) {
        for (L1EquipCollect equipCollect : this._EquipCollectIndex) {
            if (craftkey.equals(equipCollect.get_npcid() + equipCollect.get_action())) {
                return equipCollect;
            }
        }
        return null;
    }

    public List<L1EquipCollect> get_craftlist() {
        return this._EquipCollectIndex;
    }
}
