package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfReading;
import com.lineage.server.datatables.storage.BoardOrimStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Rank;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardOrimTable implements BoardOrimStorage {
    private static final Log _log = LogFactory.getLog(BoardOrimTable.class);
    private static final List<L1Rank> _totalList = new CopyOnWriteArrayList<L1Rank>();
    private static int _maxid = 0;

    @SuppressWarnings("resource")
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Calendar calendar = Calendar.getInstance();
        int r;
        int n;
        int itemCount;
        int j;
        if (calendar.get(5) == 28) {
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            List<L1Rank> _checkList = new CopyOnWriteArrayList<L1Rank>();
            try {
                co = DatabaseFactory.get().getConnection();
                ps = co.prepareStatement("SELECT * FROM `server_board_orim_公佈欄` WHERE `time_order`<=? ORDER BY `score` DESC");
                ps.setTimestamp(1, timestamp);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String[] partyMember = rs.getString("partyMember").split(",");
                    List<String> checkList = new CopyOnWriteArrayList<String>();
                    for (String str : partyMember) {
                        checkList.add(str);
                    }
                    String leader = rs.getString("leader");
                    int score = rs.getInt("score");
                    L1Rank rank = new L1Rank(leader, checkList, score);
                    _checkList.add(rank);
                }
                List<String> _overList = new CopyOnWriteArrayList<String>();
                int i = 0;
                r = 5;
                for (n = _checkList.size(); (i < r) && (i < n); i++) {
                    itemCount = 0;
                    if (i == 0) {
                        itemCount = 3;
                    } else if ((i == 1) || (i == 2)) {
                        itemCount = 2;
                    } else if ((i == 3) || (i == 4)) {
                        itemCount = 1;
                    }
                    L1Rank rank = (L1Rank) _checkList.get(i);
                    j = 0;
                    for (int k = rank.getMemberSize(); j < k; j++) {
                        String value = null;
                        if (!_overList.contains(rank.getPartyLeader())) {
                            value = rank.getPartyLeader();
                        } else {
                            for (String memberName : rank.getPartyMember()) {
                                if (!_overList.contains(memberName)) {
                                    value = memberName;
                                }
                            }
                        }
                        if ((value != null) && (CharObjidTable.get().charObjid(value) > 0)) {
                            try {
                                L1ItemInstance item = ItemTable.get().createItem(56256);
                                item.setCount(itemCount);
                                DwarfReading.get().insertItem(CharacterTable.getAccountName(value), item);
                            } catch (Exception e) {
                                _log.error(e.getLocalizedMessage(), e);
                            }
                            _overList.add(value);
                        }
                    }
                }
                _checkList.clear();
                _overList.clear();
                ps = co.prepareStatement("DELETE FROM `server_board_orim_公佈欄` WHERE `time_order`<=?");
                ps.setTimestamp(1, timestamp);
                ps.execute();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(co);
            }
        }
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `server_board_orim_公佈欄` ORDER BY `score` DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id > _maxid) {
                    _maxid = id;
                }
                String[] partyMember = rs.getString("partyMember").split(",");
                List<String> checkList = new CopyOnWriteArrayList<String>();
                for (String str : partyMember) {
                    checkList.add(str);
                }
                String leader = rs.getString("leader");
                int score = rs.getInt("score");
                for (Iterator<L1Rank> iterator = _totalList.iterator(); iterator.hasNext(); ) {
                    L1Rank rank = (L1Rank) iterator.next();
                    if (leader.equals(rank.getPartyLeader()) || rank.getPartyMember().contains(leader)) {
                        leader = null;
                        break;
                    }
                }
                for (Iterator<String> iterator1 = checkList.iterator(); iterator1.hasNext(); ) {
                    String check_member = (String) iterator1.next();
                    for (L1Rank rank : _totalList) {
                        if ((check_member.equals(rank.getPartyLeader())) || (rank.getPartyMember().contains(check_member))) {
                            checkList.remove(check_member);
                            break;
                        }
                    }
                }
                L1Rank rank = new L1Rank(leader, checkList, score);
                _totalList.add(rank);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->歐林佈告欄資料數量: " + _totalList.size() + "(" + timer.get() + "ms)");
    }

    public List<L1Rank> getTotalList() {
        return _totalList;
    }

    public int writeTopic(int score, String leader, List<String> partyMember) {
        int length = partyMember.size();
        if (length <= 0) {
            return 0;
        }
        StringBuilder sbr = new StringBuilder();
        int i = 0;
        do {
            sbr.append((String) partyMember.get(i));
            if (++i >= length) {
                break;
            }
            sbr.append(",");
        } while (true);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `server_board_orim_公佈欄` SET `id`=?,`score`=?,`leader`=?,`partyMember`=?,`time_order`=?");
            ps.setInt(1, ++_maxid);
            ps.setInt(2, score);
            ps.setString(3, leader);
            ps.setString(4, sbr.toString());
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        for (L1Rank rank : _totalList) {
            if ((leader.equals(rank.getPartyLeader())) || (rank.getPartyMember().contains(leader))) {
                leader = null;
                break;
            }
        }
        for (String check_member : partyMember) {
            for (L1Rank rank : _totalList) {
                if ((check_member.equals(rank.getPartyLeader())) || (rank.getPartyMember().contains(check_member))) {
                    partyMember.remove(check_member);
                    break;
                }
            }
        }
        L1Rank rank = new L1Rank(leader, partyMember, score);
        int size = _totalList.size();
        int index = size;
        for (i = 0; i < size; i++) {
            if (score > ((L1Rank) _totalList.get(i)).getScore()) {
                index = i;
                break;
            }
        }
        _totalList.add(index, rank);
        return index + 1;
    }

    public void renewPcName(String oriName, String newName) {
        updateLeaderName(oriName, newName);
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkOkay;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `server_board_orim_公佈欄`");
            rs = ps.executeQuery();
            while (rs.next()) {
                String partyMember = rs.getString("partyMember");
                checkOkay = false;
                for (String str : partyMember.split(",")) {
                    if (oriName.equals(str)) {
                        checkOkay = true;
                        break;
                    }
                }
                if (checkOkay) {
                    updateMemberName(partyMember, partyMember.replace(oriName, newName));
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        for (L1Rank rank : _totalList) {
            if (oriName.equals(rank.getPartyLeader())) {
                rank.setPartyLeader(newName);
            } else {
                int index = rank.getPartyMember().indexOf(oriName);
                if (index >= 0) {
                    rank.getPartyMember().set(index, newName);
                }
            }
        }
    }

    private final void updateLeaderName(String oriName, String newName) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("UPDATE `server_board_orim_公佈欄` SET `leader`=? WHERE `leader`=?");
            ps.setString(1, newName);
            ps.setString(2, oriName);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    private final void updateMemberName(String oriName, String newName) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("UPDATE `server_board_orim_公佈欄` SET `partyMember`=? WHERE `partyMember`=?");
            ps.setString(1, newName);
            ps.setString(2, oriName);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.BoardOrimTable JD-Core Version: 0.6.2
 */