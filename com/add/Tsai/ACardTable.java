package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
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

/**
 * 卡片能力鑲嵌
 *
 * @author hero
 */
public class ACardTable {
    private static final Log _log = LogFactory.getLog(ACardTable.class);
    private static final HashMap<Integer, ACard> _cardIndex = new HashMap<>();
    private static ACardTable _instance;

    public static ACardTable get() {
        if (_instance == null) {
            _instance = new ACardTable();
        }
        return _instance;
    }

    /**
     * 載入變身卡資料
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_變身卡冊`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final int group = rs.getInt("群組");
                final String msg1 = rs.getString("獲得能力時的訊息");
                final String msg2 = rs.getString("出現顯示能力頁面名稱");
                final String cmd = rs.getString("對話檔指令");
                final int questid = rs.getInt("任務編號");
                final int polyid = rs.getInt("變身編號");
                final int polytime = rs.getInt("變身時效");
                final int polyitemid = rs.getInt("變身消耗道具編號");
                final int polyitemcount = rs.getInt("變身消耗道具數量");
                final int str = rs.getInt("力量");
                final int dex = rs.getInt("敏捷");
                final int con = rs.getInt("體質");
                final int Int = rs.getInt("智力");
                final int wis = rs.getInt("精神");
                final int cha = rs.getInt("魅力");
                final int ac = rs.getInt("防禦");
                final int hp = rs.getInt("血量");
                final int mp = rs.getInt("魔量");
                final int hpr = rs.getInt("回血量");
                final int mpr = rs.getInt("回魔量");
                final int dmg = rs.getInt("近距離傷害");
                final int bowdmg = rs.getInt("遠距離傷害");
                final int hit = rs.getInt("近距離命中");
                final int bowhit = rs.getInt("遠戰攻擊命中");
                final int dmgr = rs.getInt("物理減免傷害");
                final int mdmgr = rs.getInt("魔法減免傷害");
                final int sp = rs.getInt("魔攻");
                final int mhit = rs.getInt("魔法命中");
                final int mr = rs.getInt("魔法防禦");
                final int fire = rs.getInt("火屬性防禦");
                final int water = rs.getInt("水屬性防禦");
                final int wind = rs.getInt("風屬性防禦");
                final int earth = rs.getInt("地屬性防禦");
                final String hgfx = rs.getString("黑色圖片");
                final String cgfx = rs.getString("彩色圖片");
                final int shanbi = rs.getInt("閃避提升");
                final int huibi = rs.getInt("迴避提升");
                final int yaoshui = rs.getInt("藥水恢復率");
                final int fuzhong = rs.getInt("負重");
                final int exp = rs.getInt("經驗");
                final int hunmi = rs.getInt("昏迷耐性");
                final int zhicheng = rs.getInt("支撐耐性");
                final int shihua = rs.getInt("石化耐性");
                final int hanbing = rs.getInt("寒冰耐性");
                final int anhei = rs.getInt("暗黑耐性");
                final int shuimian = rs.getInt("睡眠耐性");
                final ACard card = new ACard(id, group, msg1, msg2, cmd, questid, polyid, polytime, polyitemid, polyitemcount, str, dex, con, Int, wis, cha, ac, hp, mp, hpr, mpr, dmg, bowdmg, hit, bowhit, dmgr, mdmgr, sp, mhit, mr, fire, water, wind, earth, hgfx, cgfx, shanbi, huibi, yaoshui, fuzhong, exp, hunmi, zhicheng, shihua, hanbing, anhei, shuimian);
                _cardIndex.put(id, card);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->系統_變身卡冊: " + i + "(" + timer.get() + "ms)");
    }

    public int ACardSize() { // 取得變身卡總數
        return _cardIndex.size(); //
    }

    public Integer[] getCardIndex() {
        return _cardIndex.keySet().toArray(new Integer[0]);
    }

    // 取得已獲得變身總數
    public int getCardHaveGroup(L1PcInstance pc, int group) {
        int count = 0;
        for (ACard card_s : _cardIndex.values()) {
            if (card_s.getGroup() == group && CardQuestTable.get().IsQuest(pc, card_s.getQuestId())) {
                count++;
            }
        }
        return count;
    }

    // 取得群組總數
    public int getCardGroupSize(int group) {
        int count = 0;
        for (ACard card_s : _cardIndex.values()) {
            if (card_s.getGroup() == group) {
                count++;
            }
        }
        return count;
    }


    public ACard getCard(final int id) {
        return _cardIndex.get(id);
    }

    public int getQuestIdByCardId(final int id) {
        ACard card = _cardIndex.get(id);
        if (card != null) {
            return card.getQuestId();
        }
        return 0;
    }
}