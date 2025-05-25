package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharOtherStorage1;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther1;
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
import java.util.StringTokenizer;


public class CharOtherTable1
        implements CharOtherStorage1 {
    private static final Log _log = LogFactory.getLog(CharOtherTable1.class);
    private static final Map<Integer, L1PcOther1> _otherMap = new HashMap<>();


    private static void delete(int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_內掛` WHERE `人物objid`=?");
            ps.setInt(1, objid);
            ps.execute();

            // 同步從快取 Map 中移除
            _otherMap.remove(objid);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private static void addMap(int objId, L1PcOther1 other) {
        _otherMap.putIfAbsent(objId, other);
    }

    private static int[] getArray(String s) {
        if (s == null || s.equals("")) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        int[] iReturn = new int[iSize];
        int i = 0;
        while (i < iSize) {
            String sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
            i++;
        }
        return iReturn;
    }

    /**
     * 從資料庫載入所有角色的內掛設定，並存入快取 Map 中
     */
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_內掛`");
            rs = ps.executeQuery();

            while (rs.next()) {
                int char_obj_id = rs.getInt("人物objid");
                // 檢查角色是否存在於角色基本資料表中
                if (CharObjidTable.get().isChar(char_obj_id) != null) {
                    // 依序讀取欄位
                    int type1 = rs.getInt("購買道具編號");
                    int type2 = rs.getInt("購買道具數量");
                    int type3 = rs.getInt("輔助道具編號");
                    int type4 = rs.getInt("輔助道具數量");
                    int type5 = rs.getInt("是否自動購買箭");
                    int type6 = rs.getInt("被玩家攻擊是否順移");
                    int type7 = rs.getInt("開啟仇人瞬移");
                    int type8 = rs.getInt("無目標是否順移");
                    int type9 = rs.getInt("開啟boss順移");
                    int type10 = rs.getInt("搶怪禮貌模式");
                    int type11 = rs.getInt("魔法紀錄");
                    int type12 = rs.getInt("type12");
                    int type13 = rs.getInt("type13");
                    int type14 = rs.getInt("type14");
                    int type15 = rs.getInt("type15");
                    int type16 = rs.getInt("type16");
                    int type17 = rs.getInt("type17");
                    int type18 = rs.getInt("type18");
                    int type19 = rs.getInt("type19");
                    int type20 = rs.getInt("type20");
                    int type21 = rs.getInt("type21");
                    int type22 = rs.getInt("type22");
                    int type23 = rs.getInt("type23");
                    int type24 = rs.getInt("type24");
                    int type25 = rs.getInt("type25");
                    int type26 = rs.getInt("type26");
                    int type27 = rs.getInt("type27");
                    int type28 = rs.getInt("type28");
                    int type29 = rs.getInt("type29");
                    String type30 = rs.getString("type30");
                    String type31 = rs.getString("type31");
                    String type32 = rs.getString("type32");
                    String type33 = rs.getString("type33");
                    String type34 = rs.getString("type34");
                    String type35 = rs.getString("type35");
                    String type36 = rs.getString("type36");
                    String type37 = rs.getString("type37");
                    String type38 = rs.getString("type38");
                    String type39 = rs.getString("type39");
                    String type40 = rs.getString("type40");
                    int type41 = rs.getInt("type41");
                    int type42 = rs.getInt("type42");
                    int type43 = rs.getInt("type43");
                    int type44 = rs.getInt("type44");
                    int type45 = rs.getInt("type45");
                    int type46 = rs.getInt("type46");
                    int type47 = rs.getInt("type47");
                    int type48 = rs.getInt("type48");
                    int type49 = rs.getInt("type49");
                    int type50 = rs.getInt("type50");
                    String type51 = rs.getString("type51");

                    // 建立內掛設定物件並塞值
                    L1PcOther1 other = new L1PcOther1();
                    other.set_objid(char_obj_id);
                    other.set_type1(type1);
                    other.set_type2(type2);
                    other.set_type3(type3);
                    other.set_type4(type4);
                    other.set_type5(type5);
                    other.set_type6(type6);
                    other.set_type7(type7);
                    other.set_type8(type8);
                    other.set_type9(type9);
                    other.set_type10(type10);
                    other.set_type11(type11);
                    other.set_type12(type12);
                    other.set_type13(type13);
                    other.set_type14(type14);
                    other.set_type15(type15);
                    other.set_type16(type16);
                    other.set_type17(type17);
                    other.set_type18(type18);
                    other.set_type19(type19);
                    other.set_type20(type20);
                    other.set_type21(type21);
                    other.set_type22(type22);
                    other.set_type23(type23);
                    other.set_type24(type24);
                    other.set_type25(type25);
                    other.set_type26(type26);
                    other.set_type27(type27);
                    other.set_type28(type28);
                    other.set_type29(type29);
                    other.set_type30(type30);
                    other.set_type31(type31);
                    other.set_type32(type32);
                    other.set_type33(type33);
                    other.set_type34(type34);
                    other.set_type35(type35);
                    other.set_type36(type36);
                    other.set_type37(type37);
                    other.set_type38(type38);
                    other.set_type39(type39);
                    other.set_type40(type40);
                    other.set_type41(type41);
                    other.set_type42(type42);
                    other.set_type43(type43);
                    other.set_type44(type44);
                    other.set_type45(type45);
                    other.set_type46(type46);
                    other.set_type47(type47);
                    other.set_type48(type48);
                    other.set_type49(type49);
                    other.set_type50(type50);
                    other.set_type51(type51);

                    // 加入快取 Map
                    addMap(char_obj_id, other);
                    continue;
                }

                // 若角色已不存在，刪除對應的內掛資料
                delete(char_obj_id);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }

        // 顯示載入結果
        _log.info("載入額外紀錄資料數量: " + _otherMap.size() + " (" + timer.get() + "ms)");
    }

    /**
     * 取得角色的內掛設定
     *
     * @param pc 角色物件
     * @return L1PcOther1 內掛設定物件
     */
    public L1PcOther1 getOther(L1PcInstance pc) {
        L1PcOther1 otherTmp = _otherMap.get(pc.getId());
        return otherTmp;
    }

    public void storeOther(int objId, L1PcOther1 other) {
        L1PcOther1 otherTmp = _otherMap.get(objId);
        if (otherTmp == null) {
            addMap(objId, other);
            addNewOther(other);
        } else {
            updateOther(other);
        }
    }

    /**
     * 更新角色的內掛設定（已存在才會執行）
     */
    private void updateOther(L1PcOther1 other) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            int type1 = other.get_type1();
            int type2 = other.get_type2();
            int type3 = other.get_type3();
            int type4 = other.get_type4();
            int type5 = other.get_type5();
            int type6 = other.get_type6();
            int type7 = other.get_type7();
            int type8 = other.get_type8();
            int type9 = other.get_type9();
            int type10 = other.get_type10();
            int type11 = other.get_type11();
            int type12 = other.get_type12();
            int type13 = other.get_type13();
            int type14 = other.get_type14();
            int type15 = other.get_type15();
            int type16 = other.get_type16();
            int type17 = other.get_type17();
            int type18 = other.get_type18();
            int type19 = other.get_type19();
            int type20 = other.get_type20();
            int type21 = other.get_type21();
            int type22 = other.get_type22();
            int type23 = other.get_type23();
            int type24 = other.get_type24();
            int type25 = other.get_type25();
            int type26 = other.get_type26();
            int type27 = other.get_type27();
            int type28 = other.get_type28();
            int type29 = other.get_type29();
            String type30 = other.get_type30();
            String type31 = other.get_type31();
            String type32 = other.get_type32();
            String type33 = other.get_type33();
            String type34 = other.get_type34();
            String type35 = other.get_type35();
            String type36 = other.get_type36();
            String type37 = other.get_type37();
            String type38 = other.get_type38();
            String type39 = other.get_type39();
            String type40 = other.get_type40();
            int type41 = other.get_type41();
            int type42 = other.get_type42();
            int type43 = other.get_type43();
            int type44 = other.get_type44();
            int type45 = other.get_type45();
            int type46 = other.get_type46();
            int type47 = other.get_type47();
            int type48 = other.get_type48();
            int type49 = other.get_type49();
            int type50 = other.get_type50();
            String type51 = other.get_type51();

            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `character_內掛` SET `購買道具編號`=?,`購買道具數量`=?,`輔助道具編號`=?,`輔助道具數量`=?,`是否自動購買箭`=?,`被玩家攻擊是否順移`=?,`開啟仇人瞬移`=?,`無目標是否順移`=?,`開啟boss順移`=?,`搶怪禮貌模式`=?,`魔法紀錄`=?,`type12`=?,`type13`=?,`type14`=?,`type15`=?,`type16`=?,`type17`=?,`type18`=?,`type19`=?,`type20`=?,`type21`=?,`type22`=?,`type23`=?,`type24`=?,`type25`=?,`type26`=?,`type27`=?,`type28`=?,`type29`=?,`type30`=?,`type31`=?,`type32`=?,`type33`=?,`type34`=?,`type35`=?,`type36`=?,`type37`=?,`type38`=?,`type39`=?,`type40`=?,`type41`=?,`type42`=?,`type43`=?,`type44`=?,`type45`=?,`type46`=?,`type47`=?,`type48`=?,`type49`=?,`type50`=?,`type51`=? WHERE `人物objid`=?");

            int i = 0;
            ps.setInt(++i, type1);
            ps.setInt(++i, type2);
            ps.setInt(++i, type3);
            ps.setInt(++i, type4);
            ps.setInt(++i, type5);
            ps.setInt(++i, type6);
            ps.setInt(++i, type7);
            ps.setInt(++i, type8);
            ps.setInt(++i, type9);
            ps.setInt(++i, type10);
            ps.setInt(++i, type11);
            ps.setInt(++i, type12);
            ps.setInt(++i, type13);
            ps.setInt(++i, type14);
            ps.setInt(++i, type15);
            ps.setInt(++i, type16);
            ps.setInt(++i, type17);
            ps.setInt(++i, type18);
            ps.setInt(++i, type19);
            ps.setInt(++i, type20);
            ps.setInt(++i, type21);
            ps.setInt(++i, type22);
            ps.setInt(++i, type23);
            ps.setInt(++i, type24);
            ps.setInt(++i, type25);
            ps.setInt(++i, type26);
            ps.setInt(++i, type27);
            ps.setInt(++i, type28);
            ps.setInt(++i, type29);
            ps.setString(++i, type30);
            ps.setString(++i, type31);
            ps.setString(++i, type32);
            ps.setString(++i, type33);
            ps.setString(++i, type34);
            ps.setString(++i, type35);
            ps.setString(++i, type36);
            ps.setString(++i, type37);
            ps.setString(++i, type38);
            ps.setString(++i, type39);
            ps.setString(++i, type40);
            ps.setInt(++i, type41);
            ps.setInt(++i, type42);
            ps.setInt(++i, type43);
            ps.setInt(++i, type44);
            ps.setInt(++i, type45);
            ps.setInt(++i, type46);
            ps.setInt(++i, type47);
            ps.setInt(++i, type48);
            ps.setInt(++i, type49);
            ps.setInt(++i, type50);
            ps.setString(++i, type51);
            ps.setInt(++i, other.get_objid());

            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 新增角色的內掛設定（如果不存在則新增）
     * 這個方法會在角色登出時被呼叫，將角色的內掛設定存入資料庫。
     * 這裡的 SQL 語句是 INSERT INTO，表示新增一筆資料。
     * 如果角色已經存在於資料庫中，則會使用 updateOther 方法更新資料。
     * <p>
     * /*
     */
    private void addNewOther(L1PcOther1 other) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            int oid = other.get_objid();
            int type1 = other.get_type1();
            int type2 = other.get_type2();
            int type3 = other.get_type3();
            int type4 = other.get_type4();
            int type5 = other.get_type5();
            int type6 = other.get_type6();
            int type7 = other.get_type7();
            int type8 = other.get_type8();
            int type9 = other.get_type9();
            int type10 = other.get_type10();
            int type11 = other.get_type11();
            int type12 = other.get_type12();
            int type13 = other.get_type13();
            int type14 = other.get_type14();
            int type15 = other.get_type15();
            int type16 = other.get_type16();
            int type17 = other.get_type17();
            int type18 = other.get_type18();
            int type19 = other.get_type19();
            int type20 = other.get_type20();
            int type21 = other.get_type21();
            int type22 = other.get_type22();
            int type23 = other.get_type23();
            int type24 = other.get_type24();
            int type25 = other.get_type25();
            int type26 = other.get_type26();
            int type27 = other.get_type27();
            int type28 = other.get_type28();
            int type29 = other.get_type29();
            String type30 = other.get_type30();
            String type31 = other.get_type31();
            String type32 = other.get_type32();
            String type33 = other.get_type33();
            String type34 = other.get_type34();
            String type35 = other.get_type35();
            String type36 = other.get_type36();
            String type37 = other.get_type37();
            String type38 = other.get_type38();
            String type39 = other.get_type39();
            String type40 = other.get_type40();
            int type41 = other.get_type41();
            int type42 = other.get_type42();
            int type43 = other.get_type43();
            int type44 = other.get_type44();
            int type45 = other.get_type45();
            int type46 = other.get_type46();
            int type47 = other.get_type47();
            int type48 = other.get_type48();
            int type49 = other.get_type49();
            int type50 = other.get_type50();
            String type51 = other.get_type51();
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `character_內掛` SET `人物objid`=?,`購買道具編號`=?,`購買道具數量`=?,`輔助道具編號`=?,`輔助道具數量`=?,`是否自動購買箭`=?,`被玩家攻擊是否順移`=?,`開啟仇人瞬移`=?,`無目標是否順移`=?,`開啟boss順移`=?,`搶怪禮貌模式`=?,`魔法紀錄`=?,`type12`=?,`type13`=?,`type14`=?,`type15`=?,`type16`=?,`type17`=?,`type18`=?,`type19`=?,`type20`=?,`type21`=?,`type22`=?,`type23`=?,`type24`=?,`type25`=?,`type26`=?,`type27`=?,`type28`=?,`type29`=?,`type30`=?,`type31`=?,`type32`=?,`type33`=?,`type34`=?,`type35`=?,`type36`=?,`type37`=?,`type38`=?,`type39`=?,`type40`=?,`type41`=?,`type42`=?,`type43`=?,`type44`=?,`type45`=?,`type46`=?,`type47`=?,`type48`=?,`type49`=?,`type50`=?,`type51`=?");
            int i = 0;
            ps.setInt(++i, oid);
            ps.setInt(++i, type1);
            ps.setInt(++i, type2);
            ps.setInt(++i, type3);
            ps.setInt(++i, type4);
            ps.setInt(++i, type5);
            ps.setInt(++i, type6);
            ps.setInt(++i, type7);
            ps.setInt(++i, type8);
            ps.setInt(++i, type9);
            ps.setInt(++i, type10);
            ps.setInt(++i, type11);
            ps.setInt(++i, type12);
            ps.setInt(++i, type13);
            ps.setInt(++i, type14);
            ps.setInt(++i, type15);
            ps.setInt(++i, type16);
            ps.setInt(++i, type17);
            ps.setInt(++i, type18);
            ps.setInt(++i, type19);
            ps.setInt(++i, type20);
            ps.setInt(++i, type21);
            ps.setInt(++i, type22);
            ps.setInt(++i, type23);
            ps.setInt(++i, type24);
            ps.setInt(++i, type25);
            ps.setInt(++i, type26);
            ps.setInt(++i, type27);
            ps.setInt(++i, type28);
            ps.setInt(++i, type29);
            ps.setString(++i, type30);
            ps.setString(++i, type31);
            ps.setString(++i, type32);
            ps.setString(++i, type33);
            ps.setString(++i, type34);
            ps.setString(++i, type35);
            ps.setString(++i, type36);
            ps.setString(++i, type37);
            ps.setString(++i, type38);
            ps.setString(++i, type39);
            ps.setString(++i, type40);
            ps.setInt(++i, type41);
            ps.setInt(++i, type42);
            ps.setInt(++i, type43);
            ps.setInt(++i, type44);
            ps.setInt(++i, type45);
            ps.setInt(++i, type46);
            ps.setInt(++i, type47);
            ps.setInt(++i, type48);
            ps.setInt(++i, type49);
            ps.setInt(++i, type50);
            ps.setString(++i, type51);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 重置所有角色的 killCount 與 deathCount 為 0
     * 看起來像是 GM 或維護用的清空統計功能。
     */
    public void tam() {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            // 先從快取 Map 中建立一個迭代器（雖然這裡根本沒用到，但可能是預留未來要針對每個角色操作）
            for (L1PcOther1 l1PcOther1 : _otherMap.values()) {
                // 取得每一個 L1PcOther1 物件（目前這裡沒有進一步操作）
                // ❗這段目前是空轉的，可能是未來要個別清空設定或寫進 log
            }
            // 建立資料庫連線
            cn = DatabaseFactory.get().getConnection();
            // 建立 SQL 語句：將所有角色的 killCount 跟 deathCount 都設為 0
            // ❗注意：這是一次性全表更新，沒有條件 WHERE，會影響整張表的所有紀錄！
            ps = cn.prepareStatement("UPDATE `character_內掛` SET `killCount`='0' AND `deathCount`='0'");
            // 執行 SQL
            ps.execute();
        } catch (SQLException e) {
            // 錯誤時輸出日誌
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 安全關閉資源
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}


