package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 類名稱：即時獎勵系統數據<br>
 * 類描述：<br>
 * 創建人:darling<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public class LimitedReward {
    public static final HashMap<Integer, L1WilliamLimitedReward> _itemIdIndex = new HashMap<Integer, L1WilliamLimitedReward>();
    public static final HashMap<Integer, L1WilliamLimitedReward> _itemIdIndex1 = new HashMap<Integer, L1WilliamLimitedReward>();
    private static final Log _logx = LogFactory.getLog(LimitedReward.class.getName());
    private static Logger _log = Logger.getLogger(LimitedReward.class.getName());
    private static LimitedReward _instance;

    private LimitedReward() {
        loadChackSerial();
    }

    public static LimitedReward getInstance() {
        if (_instance == null) {
            _instance = new LimitedReward();
        }
        return _instance;
    }

    public static void reload() {
        @SuppressWarnings("unused") final LimitedReward oldInstance = _instance;
        _instance = new LimitedReward();
        _itemIdIndex.clear();
    }

    private static void loadChackSerial() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_等級到達獎勵系統");
            rs = pstm.executeQuery();
            fillChackSerial(rs);
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, "error while creating 系統_等級到達獎勵系統 table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private static void fillChackSerial(ResultSet rs) throws SQLException {
        PerformanceTimer timer = new PerformanceTimer();
        while (rs.next()) {
            int id = rs.getInt("流水號");//順序(一定要從0開始)
            boolean activity = rs.getInt("活動是否開啟") == 1;//活動是否開啟(1=開啟、0=關閉)
            int check_classId = rs.getInt("職業判斷");//領取職業 王族:1 騎士:2  精靈:4 法師:8 黑妖:16 龍騎:32 幻術:64 戰士:128	資料庫我預設值設定255，沒有戰士的版本你可以改成127
            int check_level = rs.getInt("等級");//判定等級
            int check_item = rs.getInt("判定需求道具");//判定需求道具(這個欄位的道具編號不能有重複的，否則後果自行負責)
            int check_itemCount = rs.getInt("判定需求道具的數量");//判定需求道具的數量
            String surplus_msg = rs.getString("surplus_msg");//字串裡面加入%s(代表：判定需求道具 - 判定需求道具的數量身上有的數量)的餘額
            int surplus_msg_color = rs.getInt("surplus_msg_color");
            String itemId = rs.getString("給予的道具編號");//給的道具編號
            String count = rs.getString("給予的道具數量");//給予的道具數量
            String enchantlvl = rs.getString("給予的道具加成");//給的強化等級
            int totalCount = rs.getInt("總共給幾名");//總共給幾名
            int appearCount = rs.getInt("已到達人數");//目前達到人數
            int quest_id = rs.getInt("quest_id");//判定任務編號(避免降等級後又得到)
            int quest_step = rs.getInt("quest_step");
            String message = rs.getString("message");
            String message_end = rs.getString("message_end");
            L1WilliamLimitedReward armor_upgrade = new L1WilliamLimitedReward(id, activity, check_classId, check_level, check_item, check_itemCount, surplus_msg, surplus_msg_color, itemId, count, enchantlvl, totalCount, appearCount, quest_id, quest_step, message, message_end);
            _itemIdIndex.put(Integer.valueOf(id), armor_upgrade);
            _itemIdIndex1.put(Integer.valueOf(check_item), armor_upgrade);
        }
        _logx.info("讀取->[系統]_等級到達獎勵系統資料數量: " + _itemIdIndex.size() + "(" + timer.get() + "ms)");
    }

    public static void limitedRewardToList(int id, long appearCount) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE 系統_等級到達獎勵系統 SET 已到達人數=? WHERE 流水號=?");
            ps.setLong(1, appearCount);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            e.getLocalizedMessage();
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public static void limitedRewardToList_isOver(int id, int activity) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE 系統_等級到達獎勵系統 SET 活動是否開啟=? WHERE 流水號=?");
            ps.setInt(1, activity);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            e.getLocalizedMessage();
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public L1WilliamLimitedReward[] getLimitedRewardList() {
        return _itemIdIndex.values().toArray(new L1WilliamLimitedReward[_itemIdIndex.size()]);
    }

    public L1WilliamLimitedReward getTemplate(final int id) {
        return _itemIdIndex.get(id);
    }

    public L1WilliamLimitedReward getTemplate1(final int check_item) {
        return _itemIdIndex1.get(check_item);
    }
}
