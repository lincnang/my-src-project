package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CharApprenticeTable {
    private static final Logger _log = Logger.getLogger(CharApprenticeTable.class.getName());
    private static CharApprenticeTable _instance;
    private final HashMap<Integer, L1Apprentice> _masterList = new HashMap<Integer, L1Apprentice>();

    public static CharApprenticeTable getInstance() {
        if (_instance == null) {
            _instance = new CharApprenticeTable();
        }
        return _instance;
    }

    public final void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_apprentice ORDER BY master_id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int master_id = rs.getInt("master_id");
                int apprentice_id1 = rs.getInt("apprentice_id1");
                int apprentice_id2 = rs.getInt("apprentice_id2");
                int apprentice_id3 = rs.getInt("apprentice_id3");
                int apprentice_id4 = rs.getInt("apprentice_id4");
                L1PcInstance master = null;
                L1PcInstance apprentice = null;
                ArrayList<L1PcInstance> totalList = new ArrayList<L1PcInstance>(4);
                for (L1CharName l1char : CharacterTable.get().getCharNameList()) {
                    if ((master == null) && (l1char.getId() == master_id)) {
                        master = CharacterTable.get().restoreCharacter(l1char.getName());
                    } else if ((l1char.getId() == apprentice_id1) || (l1char.getId() == apprentice_id2) || (l1char.getId() == apprentice_id3) || (l1char.getId() == apprentice_id4)) {
                        apprentice = CharacterTable.get().restoreCharacter(l1char.getName());
                        if (apprentice != null) {
                            totalList.add(apprentice);
                        }
                    }
                }
                L1Apprentice l1apprentice = new L1Apprentice(master, (L1PcInstance[]) totalList.toArray(new L1PcInstance[totalList.size()]));
                _masterList.put(Integer.valueOf(master_id), l1apprentice);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->師徒系統資料數量: " + _masterList.size() + "(" + timer.get() + "ms)");
    }

    public final void insertApprentice(L1Apprentice apprentice) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_apprentice SET master_id=?, apprentice_id1=?");
            pstm.setInt(1, apprentice.getMaster().getId());
            pstm.setInt(2, ((L1PcInstance) apprentice.getTotalList().get(0)).getId());
            pstm.execute();
            _masterList.put(Integer.valueOf(apprentice.getMaster().getId()), apprentice);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public final void updateApprentice(int master_id, ArrayList<L1PcInstance> totalList) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE character_apprentice SET apprentice_id1=?, apprentice_id2=?, apprentice_id3=?, apprentice_id4=? WHERE master_id=?");
            pstm.setInt(1, totalList.size() > 0 ? ((L1PcInstance) totalList.get(0)).getId() : 0);
            pstm.setInt(2, totalList.size() > 1 ? ((L1PcInstance) totalList.get(1)).getId() : 0);
            pstm.setInt(3, totalList.size() > 2 ? ((L1PcInstance) totalList.get(2)).getId() : 0);
            pstm.setInt(4, totalList.size() > 3 ? ((L1PcInstance) totalList.get(3)).getId() : 0);
            pstm.setInt(5, master_id);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public final void deleteApprentice(int master_id) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_apprentice WHERE master_id=?");
            pstm.setInt(1, master_id);
            pstm.execute();
            _masterList.remove(Integer.valueOf(master_id));
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public final L1Apprentice getApprentice(L1PcInstance pc) {
        // 傳入角色objId 查看masterList(師父清單)是否有此角色及徒弟
        int objid = pc.getId();
        L1Apprentice charApprentice = (L1Apprentice) _masterList.get(Integer.valueOf(objid));
        if (charApprentice != null) {
            if (charApprentice.getMaster() != pc) {
                charApprentice.setMaster(pc);
            }
            return charApprentice;
        }
        Iterator<?> localIterator2;
        for (Iterator<L1Apprentice> localIterator1 = _masterList.values().iterator(); localIterator1.hasNext(); localIterator2.hasNext()) {
            L1Apprentice apprentice = (L1Apprentice) localIterator1.next();
            if (apprentice.getTotalList().contains(pc)) {
                // System.out.println("玩家:"+pc.getName()+"
                // 師父名稱:"+apprentice.getMaster().getName()+(apprentice.getMaster().getOnlineStatus()==1?"
                // 在線上":" 不在線上"));
                return apprentice;
            }
            localIterator2 = apprentice.getTotalList().iterator();
            continue;
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.CharApprenticeTable JD-Core Version: 0.6.2
 */