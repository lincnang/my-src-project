package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.data.event.LeavesSet;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharOtherStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 額外紀錄資料（執行緒安全、含 leaves_time_exp 重啟反推）
 *
 * 修整重點：
 * 1) _otherMap 改為 ConcurrentHashMap，避免多執行緒資料撕裂。
 * 2) load() 讀到 logintime 後，依 LeavesSet 反推 leaves_time_exp 回記憶體，避免重啟後歸 0。
 * 3) tam() SQL 修正（UPDATE SET a='0', b='0'），原程式用 AND 錯誤。
 * 4) 其他註解與小防呆。
 *
 * @author dexc
 * @editor Tsai（老師版修整）
 */
public class CharOtherTable implements CharOtherStorage {

    private static final Log _log = LogFactory.getLog(CharOtherTable.class);

    /** 角色額外資料快取（以 char_obj_id 為 key） */
    private static final Map<Integer, L1PcOther> _otherMap = new ConcurrentHashMap<>();

    /**
     * 刪除遺失額外紀錄資料（角色已不存在）
     */
    private static void delete(final int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_other` WHERE `char_obj_id`=?");
            ps.setInt(1, objid);
            ps.execute();

            _otherMap.remove(objid);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 放入快取（不存在才放；若已存在不覆蓋）
     */
    private static void addMap(final int objId, final L1PcOther other) {
        if (objId <= 0 || other == null) return;
        _otherMap.putIfAbsent(objId, other);
    }

    /**
     * 初始化載入
     */
    @Override
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int loaded = 0;

        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_other`");
            rs = ps.executeQuery();

            final int nowMin = (int) (System.currentTimeMillis() / 1000L / 60L);

            while (rs.next()) {
                final int char_obj_id = rs.getInt("char_obj_id");

                // 檢查該資料所屬是否遺失
                if (CharObjidTable.get().isChar(char_obj_id) != null) {
                    final int logintime = rs.getInt("logintime");      // 以分鐘為單位
                    final int hpup = rs.getInt("hpup");                // HP人參，玩家已增加的HP值
                    final int mpup = rs.getInt("mpup");                // MP人參，玩家已增加的MP值
                    final int score = rs.getInt("score");              // 設置積分
                    // final int title = rs.getInt("title");           // 頭銜（未使用）
                    final int color = rs.getInt("color");              // 名稱色彩
                    final int usemap = rs.getInt("usemap");            // 計時地圖編號
                    final int usemaptime = rs.getInt("usemaptime");    // 計時地圖可用時間
                    final int clanskill = rs.getInt("clanskill");      // 設置血盟技能
                    final int killCount = rs.getInt("killCount");      // 殺人次數
                    final int deathCount = rs.getInt("deathCount");    // 被殺次數
                    final int getbonus = rs.getInt("getbonus");        // 贊助
                    final int Artifact = rs.getInt("Artifact");
                    final int Lv_Artifact = rs.getInt("Lv_Artifact");
                    final int Artifact1 = rs.getInt("Artifact1");
                    final int Lv_Redmg_Artifact = rs.getInt("Lv_Redmg_Artifact");

                    final L1PcOther other = new L1PcOther();
                    other.set_objid(char_obj_id);
                    other.set_login_time(logintime);
                    other.set_addhp(hpup);
                    other.set_addmp(mpup);
                    other.set_score(score);
                    // other.set_title(title);
                    other.set_color(color);

                    if (usemaptime <= 0) {
                        other.set_usemap(-1);
                        other.set_usemapTime(0);
                    } else {
                        other.set_usemap(usemap);
                        other.set_usemapTime(usemaptime);
                    }

                    other.set_clanskill(clanskill);
                    other.set_killCount(killCount);
                    other.set_deathCount(deathCount);
                    other.set_getbonus(getbonus);
                    other.setArtifact(Artifact);
                    other.setLv_Artifact(Lv_Artifact);
                    other.setArtifact1(Artifact1);
                    other.setLv_Redmg_Artifact(Lv_Redmg_Artifact);

                    // === 反推 leaves_time_exp 回記憶體（避免重啟後顯示為 0） ===
                    // 規則：以 logintime 往回推，算出「段數 N」，再轉為 leaves_time_exp = N * EXP
                    // 注意：LeavesSet.TIME、LeavesSet.EXP 皆由活動設定提供
                    try {
                        if (LeavesSet.START && logintime > 0 && nowMin >= logintime) {
                            int deltaMin = nowMin - logintime;         // 已經經過的分鐘數
                            if (LeavesSet.TIME > 0) {
                                int N = deltaMin / LeavesSet.TIME;     // 累積的「段數」
                                int leavesExp = Math.max(0, N * LeavesSet.EXP);
                                other.set_leaves_time_exp(leavesExp);
                            } else {
                                // 防呆：TIME 設 0 會出錯，直接設 0
                                other.set_leaves_time_exp(0);
                            }
                        } else {
                            other.set_leaves_time_exp(0);
                        }
                    } catch (Throwable t) {
                        // 任何例外都不讓載入中斷，log 記錄後將 leaves 設 0
                        other.set_leaves_time_exp(0);
                        _log.error("反推 leaves_time_exp 失敗: char_id=" + char_obj_id, t);
                    }

                    addMap(char_obj_id, other);
                    loaded++;

                } else {
                    // 角色已不存在，清除遺孤資料
                    delete(char_obj_id);
                }
            }

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }

        _log.info("讀取->額外紀錄資料數量: " + loaded + " (" + timer.get() + "ms)");
    }

    /**
     * 取回保留額外紀錄（回傳快取中資料）
     */
    @Override
    public L1PcOther getOther(final L1PcInstance pc) {
        if (pc == null) return null;
        return _otherMap.get(pc.getId());
    }

    /**
     * 增加/更新 保留額外紀錄（同時更新 DB）
     */
    @Override
    public void storeOther(final int objId, final L1PcOther other) {
        if (objId <= 0 || other == null) return;

        final L1PcOther cached = _otherMap.get(objId);
        if (cached == null) {
            // 不存在 -> 新增（先放入快取，再入庫）
            addMap(objId, other);
            addNewOther(other);
        } else {
            // 已存在 -> 更新（使用傳入 other 作為真實值）
            _otherMap.put(objId, other); // 先覆蓋快取，避免舊值殘留
            updateOther(other);
        }
    }

    /**
     * 更新紀錄（UPDATE）
     */
    private void updateOther(final L1PcOther other) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            final int hpup = other.get_addhp();
            final int mpup = other.get_addmp();
            final int score = other.get_score();
            // final int title = other.get_title();
            final int color = other.get_color();
            final int usemap = other.get_usemap();
            final int usemapTime = other.get_usemapTime();
            final int clanskill = other.get_clanskill();
            final int killCount = other.get_killCount();
            final int deathCount = other.get_deathCount();
            final int getbonus = other.get_getbonus();
            final int Artifact = other.getArtifact();
            final int Lv_Artifact = other.getLv_Artifact();
            final int Artifact1 = other.getArtifact1();
            final int Lv_Redmg_Artifact = other.getLv_Redmg_Artifact();

            // 以目前時間(分)為基準，若活動開啟且 leaves_time_exp > 0，將 logintime 往回推 N*TIME
            int logintime = (int) (System.currentTimeMillis() / 1000L / 60L);
            final int leavesExp = Math.max(0, other.get_leaves_time_exp());
            if (LeavesSet.START && LeavesSet.EXP > 0 && LeavesSet.TIME > 0) {
                int N = leavesExp / LeavesSet.EXP; // 以 EXP 換算段數
                if (N > 0) {
                    logintime -= (N * LeavesSet.TIME);
                }
            }
            other.set_login_time(logintime);

            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement(
                    "UPDATE `character_other` SET " +
                            "`logintime`=?,`hpup`=?,`mpup`=?," +
                            "`score`=?,`color`=?,`usemap`=?,`usemaptime`=?," +
                            "`clanskill`=?,`killCount`=?,`deathCount`=?,`getbonus`=?," +
                            "`Artifact`=?,`Lv_Artifact`=?,`Artifact1`=?,`Lv_Redmg_Artifact`=?" +
                            " WHERE `char_obj_id`=?");

            int i = 0;
            ps.setInt(++i, logintime);
            ps.setInt(++i, hpup);
            ps.setInt(++i, mpup);
            ps.setInt(++i, score);
            ps.setInt(++i, color);
            ps.setInt(++i, usemap);
            ps.setInt(++i, usemapTime);
            ps.setInt(++i, clanskill);
            ps.setInt(++i, killCount);
            ps.setInt(++i, deathCount);
            ps.setInt(++i, getbonus);
            ps.setInt(++i, Artifact);
            ps.setInt(++i, Lv_Artifact);
            ps.setInt(++i, Artifact1);
            ps.setInt(++i, Lv_Redmg_Artifact);
            ps.setInt(++i, other.get_objid());

            ps.execute();

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 增加紀錄（INSERT）
     */
    private void addNewOther(final L1PcOther other) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            final int oid = other.get_objid();
            final int hpup = other.get_addhp();
            final int mpup = other.get_addmp();
            final int score = other.get_score();
            final int color = other.get_color();
            final int usemap = other.get_usemap();
            final int usemapTime = other.get_usemapTime();
            final int clanskill = other.get_clanskill();
            final int killCount = other.get_killCount();
            final int deathCount = other.get_deathCount();
            final int getbonus = other.get_getbonus();
            final int Artifact = other.getArtifact();
            final int Lv_Artifact = other.getLv_Artifact();
            final int Artifact1 = other.getArtifact1();
            final int Lv_Redmg_Artifact = other.getLv_Redmg_Artifact();

            // 以目前時間(分)為基準，若活動開啟且 leaves_time_exp > 0，將 logintime 往回推 N*TIME
            int logintime = (int) (System.currentTimeMillis() / 1000L / 60L);
            final int leavesExp = Math.max(0, other.get_leaves_time_exp());
            if (LeavesSet.START && LeavesSet.EXP > 0 && LeavesSet.TIME > 0) {
                int N = leavesExp / LeavesSet.EXP; // 以 EXP 換算段數
                if (N > 0) {
                    logintime -= (N * LeavesSet.TIME);
                }
            }
            other.set_login_time(logintime);

            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement(
                    "INSERT INTO `character_other` SET `char_obj_id`=?,`logintime`=?,`hpup`=?," +
                            "`mpup`=?,`score`=?,`color`=?,`usemap`=?,`usemaptime`=?," +
                            "`clanskill`=?,`killCount`=?,`deathCount`=?,`getbonus`=?,`Artifact`=?,`Lv_Artifact`=?,`Artifact1`=?,`Lv_Redmg_Artifact`=?");

            int i = 0;
            ps.setInt(++i, oid);
            ps.setInt(++i, logintime);
            ps.setInt(++i, hpup);
            ps.setInt(++i, mpup);
            ps.setInt(++i, score);
            ps.setInt(++i, color);
            ps.setInt(++i, usemap);
            ps.setInt(++i, usemapTime);
            ps.setInt(++i, clanskill);
            ps.setInt(++i, killCount);
            ps.setInt(++i, deathCount);
            ps.setInt(++i, getbonus);
            ps.setInt(++i, Artifact);
            ps.setInt(++i, Lv_Artifact);
            ps.setInt(++i, Artifact1);
            ps.setInt(++i, Lv_Redmg_Artifact);

            ps.execute();

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 歸 0 殺人/死亡次數（全部玩家）
     */
    @Override
    public void tam() {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            // 先把快取歸 0
            for (L1PcOther other : _otherMap.values()) {
                if (other == null) continue;
                other.set_killCount(0);
                other.set_deathCount(0);
            }
            // 修正 SQL：UPDATE ... SET a='0', b='0'
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `character_other` SET `killCount`='0', `deathCount`='0'");
            ps.execute();

        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}
