package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.data.QuestClass;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class QuestTable {
    private static final Log _log = LogFactory.getLog(QuestTable.class);
    private static final HashMap<Integer, L1Quest> _questList = new HashMap<>();
    public static int MINQID = 0;
    public static int MAXQID = 0;
    private static QuestTable _instance;

    public static QuestTable get() {
        if (_instance == null) {
            _instance = new QuestTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_quest` ORDER BY `id`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String questname = rs.getString("questname");
                String questclass = rs.getString("questclass");
                if (!questclass.equals("0")) {
                    boolean queststart = rs.getBoolean("queststart");
                    if (queststart) {
                        if (id > MAXQID) {
                            MAXQID = id;
                        }
                        boolean del = rs.getBoolean("del");
                        int questuser = rs.getInt("questuser");
                        int questlevel = rs.getInt("questlevel");
                        int difficulty = rs.getInt("difficulty");
                        String note = rs.getString("note");
                        if (queststart) {
                            L1Quest quest = new L1Quest();
                            quest.set_id(id);
                            quest.set_questname(questname);
                            quest.set_questclass(questclass);
                            quest.set_queststart(queststart);
                            quest.set_del(del);
                            quest.set_questuser(questuser);
                            quest.set_questlevel(questlevel);
                            quest.set_difficulty(difficulty);
                            quest.set_note(note);
                            QuestClass.get().addList(id, questclass);
                            _questList.put(id, quest);
                        }
                    }
                }
            }
            _log.info("讀取->Quest(任務)設置資料數量: " + _questList.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            start();
        }
    }

    private void start() {
        try {
            for (L1Quest quest : _questList.values()) {
                QuestClass.get().execute(quest);
                TimeUnit.MILLISECONDS.sleep(20L);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public L1Quest getTemplate(int id) {
        return (L1Quest) _questList.get(id);
    }

    public HashMap<Integer, L1Quest> getList() {
        return _questList;
    }

    public int size() {
        return _questList.size();
    }

    public int levelQuest(L1PcInstance pc, int level) {
        int i = 0;
        for (Integer key : _questList.keySet()) {
            L1Quest value = (L1Quest) _questList.get(key);
            if (level >= value.get_questlevel()) {
                if (!pc.getQuest().isEnd(key)) {
                    if (value.check(pc)) {
                        i++;
                    }
                }
            }
        }
        return i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.QuestTable JD-Core Version: 0.6.2
 */