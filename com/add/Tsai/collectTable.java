package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Collectibles;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 收藏鑲嵌
 *
 * @author hero
 */
public class collectTable {
    private static final Log _log = LogFactory.getLog(collectTable.class);
    private static final HashMap<Integer, collect> _collectIndex = new HashMap<>();
    private static collectTable _instance;
    public int total = 0;
    private ArrayList<L1Collectibles> _list = new ArrayList<>();
    private ArrayList<L1Collectibles> _list1 = new ArrayList<>();
    private ArrayList<L1Collectibles> _list2 = new ArrayList<>();
    private ArrayList<L1Collectibles> _list3 = new ArrayList<>();

    public static collectTable get() {
        if (_instance == null) {
            _instance = new collectTable();
        }
        return _instance;
    }

    public static void reload() {
        _instance._list.clear();
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
            ps = cn.prepareStatement("SELECT * FROM `收集_收藏能力系統`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final String msg1 = rs.getString("獲得能力時的訊息");
                final String msg2 = rs.getString("出現顯示能力頁面名稱");
                final String cmd = rs.getString("對話檔指令");
                final int questid = rs.getInt("任務編號");
                final String hgfx = rs.getString("黑色圖片");
                final String cgfx = rs.getString("彩色圖片");
                final int needitem_id = rs.getInt("needitem_id");
                //				final String[] needitem_id_tmp = needitem_id.split(","); //需要的物品列表
                final int needitem_count = rs.getInt("needitem_count");
                //				final String[] needitem_count_tmp = needitem_count.split(","); //需要的物品列表個數量
                final int needitem_enchant = rs.getInt("needitem_enchant");
                //				final String[] needitem_enchant_tmp = needitem_enchant.split(","); //需要的物品列表強化值
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
                //				if (needitem_id_tmp.length != needitem_id_tmp.length && needitem_id_tmp.length != needitem_id_tmp.length) {
                //					_log.error("收集_裝備武器資料錯誤: 設置數據不匹配" + needitem_id_tmp.length);
                //					continue;
                //				}
                //				final int[] needids = new int[needitem_id_tmp.length];
                //				for (int S = 0; S < needitem_id_tmp.length; S++) {
                //					needids[S] = Integer.parseInt(needitem_id_tmp[S]);
                //				}
                //				final int[] needcounts = new int[needitem_count_tmp.length];
                //				for (int S = 0; S < needitem_count_tmp.length; S++) {
                //					needcounts[S] = Integer.parseInt(needitem_count_tmp[S]);
                //				}
                //
                //				final int[] needenchant = new int[needitem_enchant_tmp.length];
                //				for (int S = 0; S < needitem_enchant_tmp.length; S++) {
                //					needenchant[S] = Integer.parseInt(needitem_enchant_tmp[S]);
                //				}
                final L1Collectibles item = new L1Collectibles();
                item.set_needids(needitem_id);
                item.set_needcounts(needitem_count);
                item.set_needenchant(needitem_enchant);
                this._list.add(item);
                final collect collect = new collect(id, msg1, msg2, cmd, questid, needitem_id, needitem_count, needitem_enchant, str, dex, con, Int, wis, cha, ac, hp, mp, hpr, mpr, dmg, bowdmg, hit, bowhit, dmgr, mdmgr, sp, mhit, mr, fire, water, wind, earth, hgfx, cgfx, shanbi, huibi, yaoshui, fuzhong, exp, hunmi, zhicheng, shihua, hanbing, anhei, shuimian);
                _collectIndex.put(id, collect);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->[收集_收藏能力系統]: " + i + "(" + timer.get() + "ms)");
    }

    public int CollectSize() {
        return _collectIndex.size();
    }

    public collect getCollect(final int id) {
        return _collectIndex.get(id);
    }

    public ArrayList<L1Collectibles> getList() {
        return this._list;
    }

    public collect getCollect(final String cmd) {
        for (final collect value : _collectIndex.values()) {
            if (cmd.equalsIgnoreCase(value.getCmd())) {
                return value;
            }
        }
        return null;
    }

    public Integer[] getCollectIds() {
        return _collectIndex.keySet().toArray(new Integer[0]);
    }

    /**
     * 傳回收集_裝備武器列表
     *
     */
    public ArrayList<L1Collectibles> getTypeList(final int type) {
        switch (type) {
            case 1:
                return this._list1;
            case 2:
                return this._list2;
            case 3:
                return this._list3;
            default:
                return this._list;
        }
    }
}
