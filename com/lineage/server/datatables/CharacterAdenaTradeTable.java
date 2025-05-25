package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CharacterAdenaTradeStorage;
import com.lineage.server.templates.L1CharacterAdenaTrade;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CharacterAdenaTradeTable implements CharacterAdenaTradeStorage {
    private static final Log _log = LogFactory.getLog(CharacterAdenaTradeTable.class);
    private final Map<Integer, L1CharacterAdenaTrade> _adenaTrades = new HashMap<>();
    private int _maxId = 999;
    private Collection<L1CharacterAdenaTrade> _allValues;

    @Override
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_金幣買賣系統");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("id");
                final int adena_count = rs.getInt("adena_count");
                final int over = rs.getInt("over");
                final int count = rs.getInt("count");
                final int objid = rs.getInt("objid");
                final String name = rs.getString("name");
                final L1CharacterAdenaTrade value = new L1CharacterAdenaTrade();
                value.set_Id(id);
                value.set_adena_count(adena_count);
                value.set_over(over);
                value.set_count(count);
                value.set_objid(objid);
                value.set_name(name);
                if (id > _maxId) {
                    _maxId = id;
                }
                _adenaTrades.put(id, value);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統_金幣買賣系統]: " + _adenaTrades.size() + "(" + timer.get() + "ms)");
    }

    @Override
    public int nextId() {
        _maxId += 1;
        return _maxId;
    }

    @Override
    public boolean createAdenaTrade(final L1CharacterAdenaTrade adenaTrade) {
        if (adenaTrade == null) {
            return false;
        }
        if (_adenaTrades.containsKey(adenaTrade.get_Id())) {
            return false;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int i = 0;
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO Manly_金幣買賣系統 SET `id`=?,`adena_count`=?,`over`=?,`count`=?,`objid`=?,`name`=?");
            pstm.setInt(++i, adenaTrade.get_Id());
            pstm.setInt(++i, adenaTrade.get_adena_count());
            pstm.setInt(++i, adenaTrade.get_over());
            pstm.setInt(++i, adenaTrade.get_count());
            pstm.setInt(++i, adenaTrade.get_objid());
            pstm.setString(++i, adenaTrade.get_name());
            pstm.execute();
            _adenaTrades.put(adenaTrade.get_Id(), adenaTrade);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return true;
    }

    @Override
    public boolean updateAdenaTrade(final int id, final int over) {
        if (!_adenaTrades.containsKey(id)) {
            return false;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int i = 0;
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE Manly_金幣買賣系統 SET `over`=? WHERE `id`=?");
            pstm.setInt(++i, over);
            pstm.setInt(++i, id);
            pstm.execute();
            final L1CharacterAdenaTrade adenaTrade = _adenaTrades.get(id);
            adenaTrade.set_over(over);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return true;
    }

    @Override
    public L1CharacterAdenaTrade getCharacterAdenaTrade(final int id) {
        return _adenaTrades.get(id);
    }

    @Override
    public Map<Integer, L1CharacterAdenaTrade> getAdenaTrades() {
        return _adenaTrades;
    }

    @Override
    public Collection<L1CharacterAdenaTrade> getAllCharacterAdenaTrades() {
        Collection<L1CharacterAdenaTrade> vs = _allValues;
        return (vs != null) ? vs : (_allValues = Collections.unmodifiableCollection(_adenaTrades.values()));
    }
}
