package com.add.Tsai;

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
import java.util.Map;

/**
 * 聖物鑲嵌
 *
 * @author hero
 */
public class holySetTable {
    private static final Log _log = LogFactory.getLog(holySetTable.class);
    private static final HashMap<Integer, holyPolySet> _holyIndex = new HashMap<>();
    private static holySetTable _instance;

    public static holySetTable get() {
        if (_instance == null) {
            _instance = new holySetTable();
        }
        return _instance;
    }

    /**
     * 載入娃娃資料
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int t = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_聖物套卡`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final String msg1 = rs.getString("組合套卡名稱");
                String needisd = rs.getString("需求的變身卡編號").replaceAll(" ", "");
                final String[] needi_tmp = needisd.split(",");
                final int[] needids = new int[needi_tmp.length];
                for (int i = 0; i < needi_tmp.length; i++) {
                    needids[i] = Integer.parseInt(needi_tmp[i]);
                }
                String needquest = rs.getString("需求的玩家任務編號").replaceAll(" ", "");
                final String[] needq_tmp = needquest.split(",");
                final int[] needquests = new int[needq_tmp.length];
                for (int k = 0; k < needq_tmp.length; k++) {
                    needquests[k] = Integer.parseInt(needq_tmp[k]);
                }
                String needname = rs.getString("需求的變身卡名稱").replaceAll(" ", "");
                final String[] needn_tmp = needname.split(",");
                final String[] neednames = new String[needn_tmp.length];
                for (int j = 0; j < needn_tmp.length; j++) {
                    neednames[j] = needn_tmp[j];
                }
                final int questid = rs.getInt("任務紀錄編號");
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
                final holyPolySet holy = new holyPolySet(id, msg1, needids, needquests, neednames, questid, str, dex, con, Int, wis, cha, ac, hp, mp, hpr, mpr, dmg, bowdmg, hit, bowhit, dmgr, mdmgr, sp, mhit, mr, fire, water, wind, earth);
                _holyIndex.put(id, holy);
                t++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->系統_聖物套卡: " + t + "(" + timer.get() + "ms)");
    }

    public int HolySize() {
        return _holyIndex.size();
    }

    public holyPolySet getHoly(final int id) {
        return _holyIndex.get(id);
    }

    public Integer[] getHolyIds() {
        return _holyIndex.keySet().toArray(new Integer[0]);
    }
}