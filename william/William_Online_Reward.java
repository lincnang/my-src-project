package william;

import com.lineage.DatabaseFactory;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 線上抽獎系統
 */
public class William_Online_Reward {
    public static final HashMap<Integer, William_Online_Reward> _itemIdIndex = new HashMap<>();
    private static final Log _logx = LogFactory.getLog(William_Online_Reward.class.getName());
    private static final ConcurrentHashMap<String, L1PcInstance> playerList = new ConcurrentHashMap<>();
    private static final int _int8 = 128;
    private static final int _int7 = 64;
    private static final int _int6 = 32;
    private static final int _int5 = 16;
    private static final int _int4 = 8;
    private static final int _int3 = 4;
    private static final int _int2 = 2;
    private static final int _int1 = 1;
    private static Logger _log = Logger.getLogger(William_Online_Reward.class.getName());
    private static William_Online_Reward _instance;
    private static boolean _isCrown;
    private static boolean _isKnight;
    private static boolean _isElf;
    private static boolean _isWizard;
    private static boolean _isDarkelf;
    private static boolean _isDragonKnight;
    private static boolean _isIllusionist;
    private static boolean _isWarrior;
    private static int _use_type = 255;
    private int _dayWeek;
    private int _hour;
    private int _minute;
    private int _people;
    private int _give_item;
    private int _give_count_min;
    private int _give_count_max;
    private int _check_level;
    private int _check_lvturn;
    private int _check_prestige;
    private int _check_mapId;
    private int _check_classId;
    private int _check_VIP;

    private William_Online_Reward() {
        loadChackDrop();
    }

    public William_Online_Reward(final int dayWeek, final int hour, final int minute, final int people, final int give_item, final int give_count_min, final int give_count_max, final int check_level, final int check_lvturn, final int check_prestige, final int check_mapId, final int check_classId, final int check_VIP) {
        _dayWeek = dayWeek;
        _hour = hour;
        _minute = minute;
        _people = people;
        _give_item = give_item;
        _give_count_min = give_count_min;
        _give_count_max = give_count_max;
        _check_level = check_level;
        _check_lvturn = check_lvturn;
        _check_prestige = check_prestige;
        _check_mapId = check_mapId;
        _check_classId = check_classId;
        _check_VIP = check_VIP;
    }

    public static William_Online_Reward getInstance() {
        if (_instance == null) {
            _instance = new William_Online_Reward();
        }
        return _instance;
    }

    public static void reload() {
        @SuppressWarnings("unused") final William_Online_Reward oldInstance = _instance;
        _instance = new William_Online_Reward();
        _itemIdIndex.clear();
    }

    private static void loadChackDrop() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 功能_線上即時抽獎系統");
            rs = pstm.executeQuery();
            fillChackDrop(rs);
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, "error while creating 功能_線上即時抽獎系統 table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private static void fillChackDrop(final ResultSet rs) throws SQLException {
        final PerformanceTimer timer = new PerformanceTimer();
        int d = 0;
        while (rs.next()) {
            final int dayWeek = rs.getInt("星期幾");
            final int hour = rs.getInt("幾點(24H)");
            final int minute = rs.getInt("幾分");
            final int people = rs.getInt("人數到達開獎");
            final int give_item = rs.getInt("物品編號");
            final int give_count_min = rs.getInt("物品最小數量");
            final int give_count_max = rs.getInt("物品最大數量");
            final int check_level = rs.getInt("等級資格");
            final int check_lvturn = rs.getInt("轉身資格");
            final int check_prestige = rs.getInt("陣營積分");
            final int check_mapId = rs.getInt("地圖");
            final int check_classId = rs.getInt("職業判斷");
            final int check_VIP = rs.getInt("VIP資格");
            final William_Online_Reward armor_upgrade = new William_Online_Reward(dayWeek, hour, minute, people, give_item, give_count_min, give_count_max, check_level, check_lvturn, check_prestige, check_mapId, check_classId, check_VIP);
            _itemIdIndex.put(d, armor_upgrade);
            d++;
        }
        _logx.info("讀取->[系統]_線上即時抽獎系統資料數量: " + _itemIdIndex.size() + "(" + timer.get() + "ms)");
    }

    public static boolean isTime(final int dw, final int hh, final int mm) {
        final William_Online_Reward[] william_Online_Reward = getInstance().getDissolveList();
        William_Online_Reward william_Online_Reward1 = null;
        William_Online_Reward william_Online_Reward_ok = null;
        for (int i = 0; i < william_Online_Reward.length; i++) {
            william_Online_Reward1 = getInstance().getTemplate(i);
            if (william_Online_Reward1.getDayWeek() != 0) {
                if (william_Online_Reward1.getDayWeek() == 7 && dw == 1) { // 星期天
                    if (william_Online_Reward1.getHour() == hh // 時
                            && william_Online_Reward1.getMinute() == mm) { // 分
                        william_Online_Reward_ok = william_Online_Reward1;
                        break;
                    }
                } else if (william_Online_Reward1.getDayWeek() == dw - 1 // 星期1-6
                        && william_Online_Reward1.getHour() == hh // 時
                        && william_Online_Reward1.getMinute() == mm) { // 分
                    william_Online_Reward_ok = william_Online_Reward1;
                    break;
                }
            }
        }
        if (william_Online_Reward_ok != null) {
            // 線上玩家必須大於設置玩家
            if (World.get().getAllPlayers().size() > william_Online_Reward_ok.get_people()) {
                RandomGiftsOnline(william_Online_Reward_ok.get_people(), william_Online_Reward_ok.get_give_item(), william_Online_Reward_ok.get_give_count_max(), william_Online_Reward_ok.get_give_count_min(), william_Online_Reward_ok.get_check_level(), william_Online_Reward_ok.get_check_lvturn(), william_Online_Reward_ok.get_check_prestige(), william_Online_Reward_ok.get_check_mapId(), william_Online_Reward_ok.get_check_classId(), william_Online_Reward_ok.get_check_VIP());
            }
            return true;
        }
        return false;
    }

    public static L1PcInstance[] getAllPlayersToArray() {
        return playerList.values().toArray(new L1PcInstance[0]);
    }

    public static void RandomGiftsOnline(final int people, final int itemId, final int max, final int min, final int level, final int lvturn, final int prestige, final int mapid, final int class_id, final int vip) {
        set_use_type(class_id);
        final L1PcInstance[] player = World.get().getAllPlayersToArray();
        L1PcInstance pc = null;
        for (int i = 0; i < World.get().getAllPlayers().size(); i++) {
            pc = player[i];
            if (pc.getLevel() >= level) { // 等級
                if (pc.getMeteLevel() >= lvturn) { // 轉生
                    if (pc.get_other().get_score() >= prestige) { // 陣營積分
                        if (!pc.isGm()) { // GM不會抽到
                            if (mapid == -1 || pc.getMapId() == mapid) {
                                if (pc.get_vipLevel() >= vip) { // VIP等級
                                    if (is_use(pc)) {
                                        playerList.put(pc.getName(), pc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final L1PcInstance[] player1 = getAllPlayersToArray();
        L1PcInstance pc1 = null;
        try {
            for (int i = 0; i < people; i++) {
                // 5秒後系統隨機抽取一位中獎者！
                World.get().broadcastPacketToAll(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(L1WilliamSystemMessage.ShowMessage(1))); // 屏幕中央
                TimeUnit.MILLISECONDS.sleep(1000);
                // 4秒後系統隨機抽取一位中獎者！
                World.get().broadcastPacketToAll(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(2))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(L1WilliamSystemMessage.ShowMessage(2))); // 屏幕中央
                TimeUnit.MILLISECONDS.sleep(1000);
                // 3秒後系統隨機抽取一位中獎者！
                World.get().broadcastPacketToAll(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(3))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(L1WilliamSystemMessage.ShowMessage(3))); // 屏幕中央
                TimeUnit.MILLISECONDS.sleep(1000);
                // 2秒後系統隨機抽取一位中獎者！
                World.get().broadcastPacketToAll(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(4))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(L1WilliamSystemMessage.ShowMessage(4))); // 屏幕中央
                TimeUnit.MILLISECONDS.sleep(1000);
                // 1秒後系統隨機抽取一位中獎者！
                World.get().broadcastPacketToAll(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(5))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(L1WilliamSystemMessage.ShowMessage(5))); // 屏幕中央
                TimeUnit.MILLISECONDS.sleep(1000);
                pc1 = player1[RandomArrayList.getInt(playerList.size())];
                // 恭喜%s被系統隨機抽取中獎。
                World.get().broadcastPacketToAll(new S_SystemMessage(String.format(L1WilliamSystemMessage.ShowMessage(6), pc1.getName()))); // 對話框
                World.get().broadcastPacketToAll(new S_PacketBoxGree(String.format(L1WilliamSystemMessage.ShowMessage(6), pc1.getName()))); // 屏幕中央
                CreateNewItem.createNewItemRandomGifts(pc1, itemId, RandomArrayList.getInt(max) + min);
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        playerList.clear();
        _isCrown = false;
        _isKnight = false;
        _isElf = false;
        _isWizard = false;
        _isDarkelf = false;
        _isDragonKnight = false;
        _isIllusionist = false;
        _isWarrior = false;
    }

    public static boolean is_use(final L1PcInstance pc) {
        try {
            if (pc.isCrown() && _isCrown) {
                return true;
            }
            if (pc.isKnight() && _isKnight) {
                return true;
            }
            if (pc.isElf() && _isElf) {
                return true;
            }
            if (pc.isWizard() && _isWizard) {
                return true;
            }
            if (pc.isDarkelf() && _isDarkelf) {
                return true;
            }
            if (pc.isDragonKnight() && _isDragonKnight) {
                return true;
            }
            if (pc.isIllusionist() && _isIllusionist) {
                return true;
            }
            if (pc.isWarrior() && _isWarrior) {
                return true;
            }
        } catch (final Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return false;
    }

    public William_Online_Reward getTemplate(final int d) {
        return _itemIdIndex.get(d);
    }

    public William_Online_Reward[] getDissolveList() {
        return _itemIdIndex.values().toArray(new William_Online_Reward[0]);
    }

    public int getDayWeek() {
        return _dayWeek;
    }

    public int getHour() {
        return _hour;
    }

    public int getMinute() {
        return _minute;
    }

    public int get_people() {
        return _people;
    }

    public int get_give_item() {
        return _give_item;
    }

    public int get_give_count_min() {
        return _give_count_min;
    }

    public int get_give_count_max() {
        return _give_count_max;
    }

    public int get_check_level() {
        return _check_level;
    }

    public int get_check_lvturn() {
        return _check_lvturn;
    }

    public int get_check_prestige() {
        return _check_prestige;
    }

    public int get_check_mapId() {
        return _check_mapId;
    }

    public int get_check_classId() {
        return _check_classId;
    }

    public int get_check_VIP() {
        return _check_VIP;
    }

    public int get_use_type() {
        return _use_type;
    }

    public static void set_use_type(int use_type) {
        _use_type = use_type;
        if (use_type >= _int8) {
            use_type -= _int8;
            _isWarrior = true;
        }
        if (use_type >= _int7) {
            use_type -= _int7;
            _isIllusionist = true;
        }
        if (use_type >= _int6) {
            use_type -= _int6;
            _isDragonKnight = true;
        }
        if (use_type >= _int5) {
            use_type -= _int5;
            _isDarkelf = true;
        }
        if (use_type >= _int4) {
            use_type -= _int4;
            _isWizard = true;
        }
        if (use_type >= _int3) {
            use_type -= _int3;
            _isElf = true;
        }
        if (use_type >= _int2) {
            use_type -= _int2;
            _isKnight = true;
        }
        if (use_type >= _int1) {
            use_type--;
            _isCrown = true;
        }
    }
}
