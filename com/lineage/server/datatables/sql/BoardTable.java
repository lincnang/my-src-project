package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.BoardStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Board;
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

public class BoardTable implements BoardStorage {
    private static final Log _log = LogFactory.getLog(BoardTable.class);
    private static final Map<Integer, L1Board> _boards = new HashMap<Integer, L1Board>();
    private static int _maxid = 0;

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `server_board` ORDER BY `id`");
            rs = ps.executeQuery();
            while (rs.next()) {
                L1Board board = new L1Board();
                int id = rs.getInt("id");
                if (id > _maxid) {
                    _maxid = id;
                }
                board.set_id(id);
                board.set_name(rs.getString("name"));
                board.set_date(rs.getString("date"));
                board.set_title(rs.getString("title"));
                board.set_content(rs.getString("content"));
                _boards.put(Integer.valueOf(board.get_id()), board);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->佈告欄資料數量: " + _boards.size() + "(" + timer.get() + "ms)");
    }

    public Map<Integer, L1Board> getBoardMap() {
        return _boards;
    }

    public L1Board[] getBoardTableList() {
        return (L1Board[]) _boards.values().toArray(new L1Board[_boards.size()]);
    }

    public L1Board getBoardTable(int houseId) {
        return (L1Board) _boards.get(Integer.valueOf(houseId));
    }

    public int getMaxId() {
        return _maxid;
    }

    public void writeTopic(L1PcInstance pc, String date, String title, String content) {
        L1Board board = new L1Board();
        board.set_id(++_maxid);
        board.set_name(pc.getName());
        board.set_date(date);
        board.set_title(title);
        board.set_content(content);
        _boards.put(Integer.valueOf(board.get_id()), board);
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `server_board` SET `id`=?,`name`=?,`date`=?,`title`=?,`content`=?");
            ps.setInt(1, board.get_id());
            ps.setString(2, board.get_name());
            ps.setString(3, board.get_date());
            ps.setString(4, board.get_title());
            ps.setString(5, board.get_content());
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public void deleteTopic(int number) {
        L1Board board = (L1Board) _boards.get(Integer.valueOf(number));
        if (board != null) {
            Connection co = null;
            PreparedStatement ps = null;
            try {
                co = DatabaseFactory.get().getConnection();
                ps = co.prepareStatement("DELETE FROM `server_board` WHERE `id`=?");
                ps.setInt(1, board.get_id());
                ps.execute();
                _boards.remove(Integer.valueOf(number));
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.BoardTable JD-Core Version: 0.6.2
 */