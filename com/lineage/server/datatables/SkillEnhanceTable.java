package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * skills_技能強化 資料讀取與查詢
 * 用於技能強化查表、圖示 (icon) 顯示、技能等級對應
 */
public final class SkillEnhanceTable {
    private static final Log _log = LogFactory.getLog(SkillEnhanceTable.class);
    private static final Map<Integer, Map<Integer, L1SkillEnhance>> _enhanceMap = new HashMap<>();
    private static final Map<String, L1SkillEnhance> _cmdMap = new HashMap<>();

    private static SkillEnhanceTable _instance;

    /** 單例存取 */
    public static SkillEnhanceTable get() {
        if (_instance == null) {
            _instance = new SkillEnhanceTable();
        }
        return _instance;
    }
    /** 載入資料（啟動時調用） */
    public static void load() {
        _enhanceMap.clear();
        _cmdMap.clear();  // <-- 新增這行，重新載入時要清空cmdMap
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `skills_技能強化`");
            rs = pstm.executeQuery();

            while (rs.next()) {
                L1SkillEnhance data = new L1SkillEnhance();
                data.setId(rs.getInt("Id"));
                data.setSkillId(rs.getInt("技能編號"));
                data.setNote(rs.getString("備註說明"));
                data.setBookLevel(rs.getInt("技能吃書等級"));
                data.setSetting1(rs.getInt("設定1"));
                data.setSetting2(rs.getInt("設定2"));
                data.setSetting3(rs.getInt("設定3"));
                data.setSetting4(rs.getDouble("設定4"));
                data.setIcon(rs.getString("icon")); // 圖示資料
                data.setMsg1(rs.getString("技能升級說明")); // 新增這一行
                data.setCmd(rs.getString("對話檔指令"));   // 新增這一行
                int skillId = data.getSkillId();
                int bookLevel = data.getBookLevel();
                Map<Integer, L1SkillEnhance> lvMap = _enhanceMap.computeIfAbsent(skillId, k -> new HashMap<>());
                lvMap.put(bookLevel, data);
                if (data.getCmd() != null && !data.getCmd().isEmpty()) {
                    _cmdMap.put(data.getCmd().toLowerCase(), data);
                }
                count++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[skills_技能強化] 總筆數: " + count + " (" + timer.get() + "ms)");
    }
    /**
     * 取得指定 skillId、指定等級(bookLevel) 的強化資料
     */
    public L1SkillEnhance getEnhanceData(int skillId, int bookLevel) {
        Map<Integer, L1SkillEnhance> lvMap = _enhanceMap.get(skillId);
        if (lvMap == null) {
            return null;
        }

        return lvMap.get(bookLevel);
    }
    /**
     * 取得某個 skillId 下所有等級的強化資料
     */
    public Map<Integer, L1SkillEnhance> getEnhanceBySkillId(int skillId) {
        return _enhanceMap.get(skillId);
    }
    /**
     * 取得所有技能編號（自動根據資料庫 skills_技能強化 載入）
     * 用於技能清單自動化
     */
    public static Set<Integer> getAllSkillIds() {
        Set<Integer> ids = _enhanceMap.keySet();
        System.out.println("【DEBUG】SkillEnhanceTable 載入的技能ID列表: " + ids);
        return ids;
    }

    /**
     * 取得玩家所有持有技能的icon,名稱,等級攤平成陣列
     * [icon0, name0, lv0, icon1, name1, lv1, ...]
     */
    public static String[] getPlayerSkillIconsMix(L1PcInstance pc) {
        List<String> resultList = new ArrayList<>();
        int objId = pc.getId();

        // 先取出所有技能ID，並排序
        List<Integer> skillIdList = new ArrayList<>(getAllSkillIds());
        Collections.sort(skillIdList); // 由小到大排序

        for (int skillId : skillIdList) {
            if (CharSkillReading.get().spellCheck(objId, skillId)) {
                int skillLevel = CharSkillReading.get().getSkillLevel(objId, skillId);

                // fallback到1級
                L1SkillEnhance enhance = SkillEnhanceTable.get().getEnhanceData(skillId, skillLevel);
                if (enhance == null && skillLevel == 0) {
                    enhance = SkillEnhanceTable.get().getEnhanceData(skillId, 1);
                }
                if (enhance != null && enhance.getIcon() != null) {
                    resultList.add(enhance.getIcon());
                    L1Skills skillTemplate = SkillsTable.get().getTemplate(skillId);
                    String skillName = (skillTemplate != null) ? skillTemplate.getName() : String.valueOf(skillId);
                    resultList.add("　　" + skillName);
                    resultList.add(String.valueOf("Lv." + skillLevel));
                }
            }
        }
        System.out.println("【DEBUG】icon+名稱+等級陣列：" + resultList);
        return resultList.toArray(new String[0]);
    }


    /**
     * CMD action: magic_cardv1 - 多技能同時顯示
     */
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        // 主清單（進入卡冊頁面）
        if ("magic_cardv1".equalsIgnoreCase(cmd)) {
            String[] dataImgs = getPlayerSkillIconsMix(pc);
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magic_cardv1", dataImgs));
            return true;
        }

        // 顯示所有等級分開，每格一行
        L1SkillEnhance enhance = _cmdMap.get(cmd.toLowerCase());
        if (enhance != null) {
            int skillId = enhance.getSkillId();
            Map<Integer, L1SkillEnhance> allLvMap = SkillEnhanceTable.get().getEnhanceBySkillId(skillId);
            List<String> explainList = new ArrayList<>();
            explainList.add(enhance.getIcon()); // #0
            List<Integer> lvList = new ArrayList<>(allLvMap.keySet());
            Collections.sort(lvList);
            for (int lv : lvList) {
                L1SkillEnhance lvEnhance = allLvMap.get(lv);
                if (lvEnhance == null) continue;
                String desc = lvEnhance.getMsg1();
                if (desc == null) desc = "";
                desc = desc.replace(",", " "); // 去逗號
                explainList.add(desc); // #1 #2 #3 ...
            }
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magic_list", explainList.toArray(new String[0])));
            return true;
        }
        return false;
    }
}