package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.QuestTable;
import com.lineage.server.datatables.storage.CharacterQuestStorage;
import com.lineage.server.templates.CharQuest;
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
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CharacterQuestTable implements CharacterQuestStorage {
    private static final Log _log = LogFactory.getLog(CharacterQuestTable.class);
    private static final Map<Integer, HashMap<Integer, CharQuest>> _questList = new HashMap<>();

    private static void delete() {
        deleteData(110);
        deleteData(117);
        deleteData(137);
        deleteData(139);
        deleteData(147);
    }

    private static void deleteData(final int questid) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE FROM `character_quests` WHERE `quest_id`=?");
            pm.setInt(1, questid);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    private static void delete(final int objid) {
        _questList.remove(objid);
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=?");
            pm.setInt(1, objid);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    private static int[] getArray(String s) {
        if ((s == null) || (s.equals(""))) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        String sTemp = null;
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    private static String getStatus(int[] status) {
        if (status == null) {
            return "0";
        }
        StringBuilder result = new StringBuilder();
        try {
            int statussize = status.length;
            for (int i = 0; i < statussize; i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(status[i]);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return result.toString();
    }

    @Override
    public void load() {
        delete();
        final PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `character_quests`");
            rs = pm.executeQuery();
            while (rs.next()) {
                final int char_id = rs.getInt("char_id");
                if (CharObjidTable.get().isChar(char_id) != null) {
                    final int key = rs.getInt("quest_id");
                    switch (key) {
                        case 110:
                        case 117:
                        case 137:
                        case 139:
                        case 147:
                            break;
                        default:
                            final int quest_step = rs.getInt("quest_step");
                            final int[] mob_count = getArray(rs.getString("mob_count"));
                            final CharQuest quest = new CharQuest();
                            quest.set_quest_step(quest_step);
                            quest.set_mob_count(mob_count);
                            HashMap<Integer, CharQuest> hsMap = _questList.get(char_id);
                            if (hsMap == null) {
                                hsMap = new HashMap<>();
                                hsMap.put(key, quest);
                                _questList.put(char_id, hsMap);
                            } else {
                                hsMap.put(key, quest);
                            }
                            break;
                    }
                } else {
                    delete(char_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        _log.info("讀取->人物任務紀錄資料數量: " + _questList.size() + "(" + timer.get() + "ms)");
    }

    @Override
    public Map<Integer, CharQuest> get(final int char_id) {
        return _questList.get(char_id);
    }

    @Override
    public void storeQuest(final int char_id, final int key, final CharQuest value) {
        HashMap<Integer, CharQuest> hsMap = _questList.get(char_id);
        if (hsMap == null) {
            hsMap = new HashMap<>();
            hsMap.put(key, value);
            _questList.put(char_id, hsMap);
        } else {
            hsMap.put(key, value);
        }
        L1Quest quest = null;
        if (value.get_quest_step() == 1) {
            quest = QuestTable.get().getTemplate(key);
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            String add = "";
            if (quest != null) {
                add = ",`note`=?";
                ;
            }
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_quests` SET `char_id`=?,`quest_id`=?,`quest_step`=?,`mob_count`=?" + add);
            int i = 0;
            pm.setInt(++i, char_id);
            pm.setInt(++i, key);
            pm.setInt(++i, value.get_quest_step());
            pm.setString(++i, getStatus(value.get_mob_count()));
            if (quest != null) {
                pm.setString(++i, quest.get_note());
            }
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void storeQuest2(final int char_id, final int key, final int value) {
        Connection co = null;
        PreparedStatement pm = null;
        try {
            String add = "";
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_quests` SET `char_id`=?,`quest_id`=?,`quest_step`=?" + add);
            int i = 0;
            pm.setInt(++i, char_id);
            pm.setInt(++i, key);
            pm.setInt(++i, value);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void updateQuest(final int char_id, final int key, final CharQuest value) {
        HashMap<Integer, CharQuest> hsMap = _questList.get(char_id);
        if (hsMap == null) {
            hsMap = new HashMap<>();
            hsMap.put(key, value);
            _questList.put(char_id, hsMap);
        } else {
            hsMap.put(key, value);
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("UPDATE `character_quests` SET `quest_step`=?, `mob_count`=? WHERE `char_id`=? AND `quest_id`=?");
            int i = 0;
            pm.setInt(++i, value.get_quest_step());
            pm.setString(++i, getStatus(value.get_mob_count()));
            pm.setInt(++i, char_id);
            pm.setInt(++i, key);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void delQuest(final int char_id, final int key) {
        HashMap<Integer, CharQuest> hsMap = _questList.get(char_id);
        if (hsMap == null) {
            return;
        } else {
            hsMap.remove(key);
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=? AND `quest_id`=?");
            int i = 0;
            pm.setInt(++i, char_id);
            pm.setInt(++i, key);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void delQuest2(final int questId) {
        final Map<Integer, HashMap<Integer, CharQuest>> hsMap = _questList;
        final Set<Integer> keySet = hsMap.keySet();
        for (final Integer charId : keySet) {
            HashMap<Integer, CharQuest> pchasQuest = _questList.get(charId);
            if (pchasQuest == null) {
                continue;
            } else {
                pchasQuest.remove(questId);
            }
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("DELETE FROM `character_quests` WHERE `quest_id`=?");
            int i = 0;
            pm.setInt(++i, questId);
            pm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }

    @Override
    public void storeQuest(int char_id, int key, CharQuest value, int clean) {
        HashMap<Integer, CharQuest> hsMap = _questList.get(char_id);
        if (hsMap == null) {
            hsMap = new HashMap<>();
            hsMap.put(key, value);
            _questList.put(char_id, hsMap);
        } else {
            hsMap.put(key, value);
        }
        L1Quest quest = null;
        if (value.get_quest_step() == 1) {
            quest = QuestTable.get().getTemplate(key);
        }
        Connection co = null;
        PreparedStatement pm = null;
        try {
            String add = "";
            if (quest != null) {
                add = ",`note`=?";
            }
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("INSERT INTO `character_quests` SET `char_id`=?,`quest_id`=?,`quest_step`=?,`clean`=?,`mob_count`=?" + add);
            int i = 0;
            pm.setInt(++i, char_id);
            pm.setInt(++i, key);
            pm.setInt(++i, value.get_quest_step());
            pm.setInt(++i, clean);
            pm.setString(++i, getStatus(value.get_mob_count()));
            if (quest != null) {
                pm.setString(++i, quest.get_note());
            }
            pm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
    }
}
