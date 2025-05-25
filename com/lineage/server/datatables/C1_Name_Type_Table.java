package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.c1.C1Executor;
import com.lineage.server.templates.L1Name_Power;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class C1_Name_Type_Table {
    private static final Log _log = LogFactory.getLog(C1_Name_Type_Table.class);
    private static final Map<Integer, HashMap<Integer, L1Name_Power>> _types = new HashMap<>();
    private static final Map<Integer, HashMap<Integer, Integer>> _typesLv = new HashMap<>();
    private static final Map<Integer, HashMap<Integer, Integer>> _typesLv_down = new HashMap<>();
    private static C1_Name_Type_Table _instance;

    public static C1_Name_Type_Table get() {
        if (_instance == null) {
            _instance = new C1_Name_Type_Table();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int i = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_陣營_名稱設定`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int c1_id = rs.getInt("c1_id");
                int c1_type = rs.getInt("c1_type");
                String c1_name_type = rs.getString("c1_name_type");
                String c1_classname = rs.getString("c1_classname");
                int set = rs.getInt("set");
                int down = rs.getInt("down");
                int int1 = rs.getInt("int1");
                int int2 = rs.getInt("int2");
                int int3 = rs.getInt("int3");
                L1Name_Power power = new L1Name_Power();
                power.set_c1_id(c1_id);
                power.set_c1_name_type(c1_name_type);
                C1Executor classname = power(c1_classname, int1, int2, int3);
                if (classname != null) {
                    power.set_c1_classname(classname);
                    power.set_set(set);
                    power.set_down(down);
                    HashMap<Integer, L1Name_Power> types = _types.get(c1_type);
                    if (types == null) {
                        types = new HashMap<>();
                    }
                    types.put(c1_id, power);
                    HashMap<Integer, Integer> typesLv = _typesLv.get(c1_type);
                    if (typesLv == null) {
                        typesLv = new HashMap<>();
                    }
                    typesLv.put(c1_id, set);
                    HashMap<Integer, Integer> typesLv_down = _typesLv_down.get(c1_type);
                    if (typesLv_down == null) {
                        typesLv_down = new HashMap<>();
                    }
                    typesLv_down.put(c1_id, down);
                    _types.put(c1_type, types);
                    _typesLv.put(c1_type, typesLv);
                    i++;
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->陣營階級能力記錄數量: " + i + "(" + timer.get() + "ms)");
    }

    public L1Name_Power get(int key1, int key2) {
        HashMap<?, ?> powers = _types.get(key1);
        if (powers != null) {
            return (L1Name_Power) powers.get(key2);
        }
        return null;
    }

    public HashMap<Integer, L1Name_Power> get(int key1) {
        return _types.get(key1);
    }

    public int getLv(int key1, int score) {
        HashMap<?, ?> powers = _typesLv.get(key1);
        if (powers == null) {
            return 0;
        }
        for (int i = powers.size(); i > 0; i--) {
            Integer ps = (Integer) powers.get(i);
            if (score >= ps) {
                return i;
            }
        }
        return 0;
    }

    private C1Executor power(String className, int int1, int int2, int int3) {
        if (className.equals("0")) {
            return null;
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.lineage.server.model.c1.");
            stringBuilder.append(className);
            Class<?> cls = Class.forName(stringBuilder.toString());
            C1Executor exe = (C1Executor) cls.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            exe.set_power(int1, int2, int3);
            return exe;
        } catch (ClassNotFoundException e) {
            String error = "發生[陣營階級能力檔案]錯誤, 檢查檔案是否存在:" + className;
            _log.error(error);
        } catch (IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (InvocationTargetException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (SecurityException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchMethodException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.C1_Name_Type_Table JD-Core Version: 0.6.2
 */