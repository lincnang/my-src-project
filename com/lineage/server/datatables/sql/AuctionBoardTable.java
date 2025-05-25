package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.config.Config;
import com.lineage.server.datatables.storage.AuctionBoardStorage;
import com.lineage.server.templates.L1AuctionBoardTmp;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AuctionBoardTable implements AuctionBoardStorage {
    private static final Log _log = LogFactory.getLog(AuctionBoardTable.class);
    private static final Map<Integer, L1AuctionBoardTmp> _boards = new HashMap<Integer, L1AuctionBoardTmp>();

    private Calendar timestampToCalendar(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_board_auction_盟屋`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                L1AuctionBoardTmp board = new L1AuctionBoardTmp();
                int houseId = rs.getInt("house_id");
                String house_name = rs.getString("house_name");
                int house_area = rs.getInt("house_area");
                Calendar deadline = timestampToCalendar((Timestamp) rs.getObject("deadline"));
                String location = rs.getString("location");
                long price = rs.getLong("price");
                String old_owner = rs.getString("old_owner");
                int old_owner_id = rs.getInt("old_owner_id");
                String bidder = rs.getString("bidder");
                int bidder_id = rs.getInt("bidder_id");
                if (!location.startsWith("$")) {
                    String townName = "";
                    if ((houseId >= 262145) && (houseId <= 262189)) {
                        townName = "$1242 ";
                    }
                    if ((houseId >= 327681) && (houseId <= 327691)) {
                        townName = "$1513 ";
                    }
                    if ((houseId >= 458753) && (houseId <= 458819)) {
                        townName = "$2129 ";
                    }
                    if ((houseId >= 524289) && (houseId <= 524294)) {
                        townName = "$381 ";
                    }
                    location = townName + " " + location;
                }
                if (house_name.equals("null")) {
                    house_name = location + "$1195";
                }
                board.setHouseId(houseId);
                board.setHouseName(house_name);
                board.setHouseArea(house_area);
                board.setDeadline(deadline);
                board.setPrice(price);
                board.setLocation(location);
                board.setOldOwner(old_owner);
                board.setOldOwnerId(old_owner_id);
                board.setBidder(bidder);
                board.setBidderId(bidder_id);
                _boards.put(Integer.valueOf(board.getHouseId()), board);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->盟屋拍賣公告欄資料數量: " + _boards.size() + "(" + timer.get() + "ms)");
    }

    public Map<Integer, L1AuctionBoardTmp> getAuctionBoardTableList() {
        return _boards;
    }

    public L1AuctionBoardTmp getAuctionBoardTable(int houseId) {
        return (L1AuctionBoardTmp) _boards.get(Integer.valueOf(houseId));
    }

    public void insertAuctionBoard(L1AuctionBoardTmp board) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `server_board_auction_盟屋` SET `house_id`=?,`house_name`=?,`house_area`=?,`deadline`=?,`price`=?,`location`=?,`old_owner`=?,`old_owner_id`=?,`bidder`=?,`bidder_id`=?");
            pstm.setInt(1, board.getHouseId());
            pstm.setString(2, board.getHouseName());
            pstm.setInt(3, board.getHouseArea());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
            String fm = sdf.format(Calendar.getInstance(tz).getTime());
            pstm.setString(4, fm);
            pstm.setLong(5, board.getPrice());
            pstm.setString(6, board.getLocation());
            pstm.setString(7, board.getOldOwner());
            pstm.setInt(8, board.getOldOwnerId());
            pstm.setString(9, board.getBidder());
            pstm.setInt(10, board.getBidderId());
            pstm.execute();
            _boards.put(Integer.valueOf(board.getHouseId()), board);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateAuctionBoard(L1AuctionBoardTmp board) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `server_board_auction_盟屋` SET `house_name`=?,`house_area`=?,`deadline`=?,`price`=?,`location`=?,`old_owner`=?,`old_owner_id`=?,`bidder`=?,`bidder_id`=? WHERE `house_id`=?");
            pstm.setString(1, board.getHouseName());
            pstm.setInt(2, board.getHouseArea());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String fm = sdf.format(board.getDeadline().getTime());
            pstm.setString(3, fm);
            pstm.setLong(4, board.getPrice());
            pstm.setString(5, board.getLocation());
            pstm.setString(6, board.getOldOwner());
            pstm.setInt(7, board.getOldOwnerId());
            pstm.setString(8, board.getBidder());
            pstm.setInt(9, board.getBidderId());
            pstm.setInt(10, board.getHouseId());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteAuctionBoard(int houseId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `server_board_auction_盟屋` WHERE `house_id`=?");
            pstm.setInt(1, houseId);
            pstm.execute();
            _boards.remove(Integer.valueOf(houseId));
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.AuctionBoardTable JD-Core Version: 0.6.2
 */