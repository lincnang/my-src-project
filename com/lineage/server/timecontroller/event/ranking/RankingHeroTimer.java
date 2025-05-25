package com.lineage.server.timecontroller.event.ranking;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 英雄風雲榜時間軸
 *
 * @author dexc
 */
public class RankingHeroTimer extends TimerTask {//src013
    private static final Log _log = LogFactory.getLog(RankingHeroTimer.class);
    private static RankingHeroTimer _instance;
    private static boolean _load = false;
    private static boolean _load2;
    private static String[] _userNameAll;
    private static String[] _userNameC;
    private static String[] _userNameK;
    private static String[] _userNameE;
    private static String[] _userNameW;
    private static String[] _userNameD;
    private static String[] _userNameG;
    private static String[] _userNameI;
    private static String[] _userNameWarrior;
    private static Map<Integer, String> _top10 = new HashMap<Integer, String>();// 全職業前10名
    private static Map<Integer, String> _top3C = new HashMap<Integer, String>();// 王族前3名
    private static Map<Integer, String> _top3K = new HashMap<Integer, String>();// 騎士前3名
    private static Map<Integer, String> _top3E = new HashMap<Integer, String>();// 妖精前3名
    private static Map<Integer, String> _top3W = new HashMap<Integer, String>();// 法師前3名
    private static Map<Integer, String> _top3D = new HashMap<Integer, String>();// 黑妖前3名
    private static Map<Integer, String> _top3G = new HashMap<Integer, String>();// 龍騎士前3名
    private static Map<Integer, String> _top3I = new HashMap<Integer, String>();// 幻術師前3名
    private static Map<Integer, String> _top3Warrior = new HashMap<Integer, String>();// 幻術師前3名
    private static String[] _userNameScore;
    private ScheduledFuture<?> _timer;

    public static RankingHeroTimer get() {
        if (_instance == null) {
            _instance = new RankingHeroTimer();
        }
        return _instance;
    }

    public static String[] userNameScore() {
        if (!_load2) {
            load2();
        }
        return _userNameScore;
    }

    /**
     * 取回前十名名單
     *
     */
    public static Map<Integer, String> get_top10() {
        return _top10;
    }

    /**
     * 取回王族前三名名單
     *
     */
    public static Map<Integer, String> get_top3C() {
        return _top3C;
    }

    /**
     * 取回騎士前三名名單
     *
     */
    public static Map<Integer, String> get_top3K() {
        return _top3K;
    }

    /**
     * 取回妖精前三名名單
     *
     */
    public static Map<Integer, String> get_top3E() {
        return _top3E;
    }

    /**
     * 取回法師前三名名單
     *
     */
    public static Map<Integer, String> get_top3W() {
        return _top3W;
    }

    /**
     * 取回黑妖前三名名單
     *
     */
    public static Map<Integer, String> get_top3D() {
        return _top3D;
    }

    /**
     * 取回龍騎士前三名名單
     *
     */
    public static Map<Integer, String> get_top3G() {
        return _top3G;
    }

    /**
     * 取回幻術師前三名名單
     *
     */
    public static Map<Integer, String> get_top3I() {
        return _top3I;
    }

    /**
     * 取回幻術師前三名名單
     *
     */
    public static Map<Integer, String> get_top3Warrior() {
        return _top3Warrior;
    }

    /**
     * 全職業風雲榜
     *
     */
    public static String[] userNameAll() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameAll[i].split(",");
            newUserName[i] = set[0];
            if (CharacterTable.doesCharNameExist(newUserName[i])) {
                _top10.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 王族風雲榜
     *
     */
    public static String[] userNameC() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameC[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3C.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 騎士風雲榜
     *
     */
    public static String[] userNameK() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameK[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3K.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 精靈風雲榜
     *
     */
    public static String[] userNameE() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameE[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3E.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 法師風雲榜
     *
     */
    public static String[] userNameW() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameW[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3W.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 黑妖風雲榜
     *
     */
    public static String[] userNameD() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameD[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3D.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 龍騎風雲榜
     *
     */
    public static String[] userNameG() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameG[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3G.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 幻術風雲榜
     *
     */
    public static String[] userNameI() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameI[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3I.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    /**
     * 戰士風雲榜
     *
     */
    public static String[] userNameWarrior() {
        if (!_load) {
            load();
        }
        String[] newUserName = new String[10];
        for (int i = 0; i < 10; i++) {
            final String[] set = _userNameWarrior[i].split(",");
            newUserName[i] = set[0];
            if ((i < 3) && (CharacterTable.doesCharNameExist(newUserName[i]))) {
                _top3Warrior.put(i, newUserName[i]);
            }
        }
        return newUserName;
    }

    public static void load() {
        try {
            _load = true;
            // 重置所有排行
            restart();
            // 全數玩家加入排行 (包含離線玩家, 不含GM角色)
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = DatabaseFactory.get().getConnection();
                // 經驗排行榜
                pstm = con.prepareStatement("SELECT * FROM `characters` WHERE `MeteLevel` >= 0 and `level` > 0 and `AccessLevel` <= 0 ORDER BY `MeteLevel` DESC,`Exp` DESC");
                rs = pstm.executeQuery();
                while (rs.next()) {
                    // 0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術
                    final String char_name = rs.getString("char_name");
                    final int MeteLevel = rs.getInt("MeteLevel");
                    final int level = rs.getInt("level");
                    final int type = rs.getInt("Type");
                    if (type == 0) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameC[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameC[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 1) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameK[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameK[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 2) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameE[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameE[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 3) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameW[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameW[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 4) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameD[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameD[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 5) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameG[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameG[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 6) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameI[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameI[i] = sbr.toString();
                                break;
                            }
                        }
                    } else if (type == 7) {
                        for (int i = 0, n = 10; i < n; i++) {
                            if (_userNameWarrior[i].equals(" ")) {
                                final StringBuffer sbr = new StringBuffer().append(char_name);
                                sbr.append(" (Lv.").append(level).append(")");
                                sbr.append(" (").append(MeteLevel).append("轉)");
                                _userNameWarrior[i] = sbr.toString();
                                break;
                            }
                        }
                    }
                    // 全職業排行
                    for (int i = 0, n = 10; i < n; i++) {
                        if (_userNameAll[i].equals(" ")) {
                            final StringBuffer sbr = new StringBuffer().append(char_name);
                            // 排行榜是否顯示等級資訊 by terry0412
                            sbr.append(" (Lv.").append(level).append(")");
                            sbr.append(" (").append(MeteLevel).append("轉)");
                            _userNameAll[i] = sbr.toString();
                            break;
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(1);
                }
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static void load2() {
        try {
            _load2 = true;
            restart2();
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = DatabaseFactory.get().getConnection();
                pstm = con.prepareStatement("SELECT * FROM `character_other` WHERE `score` > 0 ORDER BY `score` DESC");
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int char_id = rs.getInt("char_obj_id");
                    L1PcInstance pc = (L1PcInstance) World.get().findObject(char_id);
                    if (pc == null) {
                        continue;
                    }
                    String char_name = pc.getName();
                    int i = 0;
                    for (int n = 10; i < n; i++) {
                        if (_userNameScore[i].equals(" ")) {
                            StringBuffer sbr = new StringBuffer().append(char_name);
                            _userNameScore[i] = sbr.toString();
                            break;
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 重置所有排行
     */
    private static void restart() {
        _userNameAll = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameC = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameK = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameE = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameW = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameD = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameG = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameI = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
        _userNameWarrior = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ",};
    }

    private static void restart2() {
        _userNameScore = new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
    }

    public void start() {
        restart();
        final int timeMillis = 600 * 1000;// 10分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            load();
            load2();
        } catch (final Exception e) {
            _log.error("英雄風雲榜時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final RankingHeroTimer heroTimer = new RankingHeroTimer();
            heroTimer.start();
        } finally {
        }
    }
}
