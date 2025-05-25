package com.lineage.server.Controller;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.ComesMonsterSpawn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 指定時間召怪召物系統
 */
public class ComesMonsterController implements Runnable {
    public static final Log _log = LogFactory.getLog(ComesMonsterController.class);
    private static final Map<Integer, Object[]> map = new ConcurrentHashMap<Integer, Object[]>();
    private static final List<Integer> task = new ArrayList<Integer>();
    private static ComesMonsterController ins;

    private ComesMonsterController() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement("SELECT * FROM 系統_怪物時間召怪設置");
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] object = new Object[14];
                object[0] = rs.getInt("屬性");
                object[1] = rs.getInt("指定編號");
                object[2] = rs.getInt("數量");
                object[3] = rs.getInt("地圖編號");
                object[4] = rs.getInt("座標_X");
                object[5] = rs.getInt("座標_Y");
                object[6] = rs.getInt("座標_X2");
                object[7] = rs.getInt("座標_Y2");
                object[8] = getArray(rs.getString("星期幾"));
                object[9] = rs.getTime("時間");
                object[10] = rs.getString("出現顯示文字");
                object[11] = rs.getInt("出現特效");
                object[12] = rs.getInt("刪除時間(分)");
                object[13] = rs.getInt("CycleTime");
                map.put(rs.getInt("流水號"), object);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, con);
        }
    }

    public static ComesMonsterController getInstance() {
        if (ins == null) {
            ins = new ComesMonsterController();
        }
        return ins;
    }

    public static final int[] getArray(String args) {
        final StringTokenizer st = new StringTokenizer(args, ",");
        final int[] sk = new int[st.countTokens()];
        for (int i = 0; i < sk.length; i++) {
            sk[i] = Integer.parseInt(st.nextToken());
        }
        return sk;
    }

    @Override
    public void run() {
        final Calendar cal = Calendar.getInstance();
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                cal.setTimeInMillis(System.currentTimeMillis());
                final int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
                for (final Integer i : map.keySet()) {
                    boolean to = false;
                    final Object[] obj = map.get(i);
                    for (int d : (int[]) obj[8]) {
                        if (d == 7 || d == day) {
                            to = true;
                            break;
                        }
                    }
                    if (!to) {
                        continue;
                    }
                    if (!task.contains(i)) {
                        new ComesMonsterSpawn(obj);
                        task.add(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
