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
    // 使用 volatile 參考，確保一次性替換的可見性
    private static volatile Map<Integer, Map<Integer, L1SkillEnhance>> _enhanceMap = Collections.emptyMap();
    private static volatile Map<String, L1SkillEnhance> _cmdMap = Collections.emptyMap();

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
        // 使用本地快照，避免讀取執行緒遇到半載入狀態
        Map<Integer, Map<Integer, L1SkillEnhance>> newEnhanceMap = new HashMap<>();
        Map<String, L1SkillEnhance> newCmdMap = new HashMap<>();
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
                // 值域/容錯保護
                if (data.getSetting4() <= 0) {
                    // 避免除以0或負數
                    data.setSetting4(1.0);
                }
                Map<Integer, L1SkillEnhance> lvMap = newEnhanceMap.computeIfAbsent(skillId, k -> new HashMap<>());
                lvMap.put(bookLevel, data);
                if (data.getCmd() != null && !data.getCmd().isEmpty()) {
                    newCmdMap.put(data.getCmd().toLowerCase(), data);
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
        // 轉為不可變後一次性替換
        for (Map.Entry<Integer, Map<Integer, L1SkillEnhance>> e : newEnhanceMap.entrySet()) {
            e.setValue(Collections.unmodifiableMap(e.getValue()));
        }
        // 一次性替換引用（不可變）
        _enhanceMap = Collections.unmodifiableMap(newEnhanceMap);
        _cmdMap = Collections.unmodifiableMap(newCmdMap);
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
        if (bookLevel <= 0) {
            return null; // 未吃書不取強化
        }
        L1SkillEnhance v = lvMap.get(bookLevel);
        if (v != null) return v;
        // fallback：向下找直到1級
        for (int lv = bookLevel - 1; lv >= 1; lv--) {
            v = lvMap.get(lv);
            if (v != null) return v;
        }
        return null;
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
//        System.out.println("【DEBUG】SkillEnhanceTable 載入的技能ID列表: " + ids);
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
        return resultList.toArray(new String[0]);
    }

    /**
     * 取得玩家所有持有技能的icon,ACTION,名稱,等級攤平成陣列
     * [icon0, action0, name0, lv0, icon1, action1, name1, lv1, ...]
     */
    public static String[] getPlayerSkillIconsMixWithAction(L1PcInstance pc) {
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
                if (enhance != null) {
                    // 圖標處理
                    String icon = enhance.getIcon();
                    if (icon == null || icon.equals("0")) {
                        icon = "0"; // 預設圖標
                    }
                    resultList.add(icon); // #0 圖標

                    // 動態 ACTION 生成
                    String action = generateDynamicAction(skillId, enhance);
                    resultList.add(action); // #1 ACTION

                    // 技能名稱
                    L1Skills skillTemplate = SkillsTable.get().getTemplate(skillId);
                    String skillName = (skillTemplate != null) ? skillTemplate.getName() : String.valueOf(skillId);
                    resultList.add("　　" + skillName); // #2 名稱

                    // 技能等級
                    resultList.add(String.valueOf("Lv." + skillLevel)); // #3 等級
                }
            }
        }
                return resultList.toArray(new String[0]);
    }

    /**
     * 取得玩家所有持有技能的圖標數組，只顯示已學習的技能
     * 格式：[icon0, name0, lv0, icon1, name1, lv1, ...]
     * ACTION使用固定的magic001-magic010，但內部動態對應
     */
    public static String[] getPlayerSkillIconsMixWithFixedAction(L1PcInstance pc) {
        List<String> resultList = new ArrayList<>();
        List<String> actionMapping = new ArrayList<>(); // 儲存 ACTION 映射
        int objId = pc.getId();

        List<Integer> skillIdList = new ArrayList<>(getAllSkillIds());
        Collections.sort(skillIdList);

        int learnedSkillsCount = 0;
        for (int skillId : skillIdList) {
            if (CharSkillReading.get().spellCheck(objId, skillId)) {
                int skillLevel = CharSkillReading.get().getSkillLevel(objId, skillId);

                L1SkillEnhance enhance = SkillEnhanceTable.get().getEnhanceData(skillId, skillLevel);
                if (enhance == null && skillLevel == 0) {
                    enhance = SkillEnhanceTable.get().getEnhanceData(skillId, 1);
                }

                if (enhance != null) {
                    // 圖標處理
                    String icon = enhance.getIcon();
                    if (icon == null || icon.equals("0")) {
                        icon = "0";
                    }
                    resultList.add(icon);

                    // 技能名稱
                    L1Skills skillTemplate = SkillsTable.get().getTemplate(skillId);
                    String skillName = (skillTemplate != null) ? skillTemplate.getName() : String.valueOf(skillId);
                    resultList.add("　　" + skillName);

                    // 技能等級
                    resultList.add(String.valueOf("Lv." + skillLevel));

                    // 生成對應的動態 ACTION 並儲存映射
                    String dynamicAction = generateDynamicAction(skillId, enhance);
                    actionMapping.add(dynamicAction);

                    learnedSkillsCount++;

                    // 限制最多顯示10個技能
                    if (learnedSkillsCount >= 10) {
                        break;
                    }
                }
            }
        }

        // 將 ACTION 映射存儲到暫存中供 CMD 使用
        storeSkillActionMapping(pc.getId(), actionMapping);

        
        return resultList.toArray(new String[0]);
    }

    // 玩家技能 ACTION 映射暫存
    private static final Map<Integer, List<String>> _playerActionMapping = new HashMap<>();

    private static void storeSkillActionMapping(int playerObjId, List<String> actionMapping) {
        _playerActionMapping.put(playerObjId, new ArrayList<>(actionMapping));
    }

    public static List<String> getPlayerActionMapping(int playerObjId) {
        return _playerActionMapping.get(playerObjId);
    }

    /**
     * 生成動態 ACTION 名稱
     * 優先使用資料庫中的對話檔指令，如果沒有則基於技能ID生成
     */
    private static String generateDynamicAction(int skillId, L1SkillEnhance enhance) {
        // 優先使用資料庫中的對話檔指令
        String dbAction = enhance.getCmd();
        if (dbAction != null && !dbAction.trim().isEmpty()) {
            return dbAction.trim();
        }

        // 如果資料庫沒有設定，則生成智能 ACTION
        L1Skills skill = SkillsTable.get().getTemplate(skillId);
        if (skill != null) {
            String skillName = skill.getName();
            String semanticAction = generateSemanticActionName(skillName, skillId);
            if (semanticAction != null) {
                return semanticAction;
            }
        }

        // 最後的備用方案：直接使用 skill_ 加上技能ID
        return "skill_" + skillId;
    }

    /**
     * 基於技能名稱生成語義化的 ACTION 名稱
     */
    private static String generateSemanticActionName(String skillName, int skillId) {
        if (skillName == null) {
            return null;
        }

        // 基於技能名稱的關鍵字匹配
        String lowerName = skillName.toLowerCase();

        if (lowerName.contains("聖") || lowerName.contains("結界") || lowerName.contains("祝福")) {
            return "holy_" + skillId;
        } else if (lowerName.contains("狂暴") || lowerName.contains("狂戰")) {
            return "berserk_" + skillId;
        } else if (lowerName.contains("回復") || lowerName.contains("治療") || lowerName.contains("癒合")) {
            return "heal_" + skillId;
        } else if (lowerName.contains("冰") || lowerName.contains("寒")) {
            return "ice_" + skillId;
        } else if (lowerName.contains("火") || lowerName.contains("炎")) {
            return "fire_" + skillId;
        } else if (lowerName.contains("風") || lowerName.contains("風")) {
            return "wind_" + skillId;
        } else if (lowerName.contains("雷") || lowerName.contains("電")) {
            return "thunder_" + skillId;
        } else if (lowerName.contains("毒") || lowerName.contains("劇毒")) {
            return "poison_" + skillId;
        } else if (lowerName.contains("精準") || lowerName.contains("準確")) {
            return "precise_" + skillId;
        } else if (lowerName.contains("雙重") || lowerName.contains("雙擊")) {
            return "double_" + skillId;
        } else if (lowerName.contains("三重") || lowerName.contains("三擊")) {
            return "triple_" + skillId;
        } else if (lowerName.contains("屬性")) {
            return "element_" + skillId;
        } else if (lowerName.contains("衝暈") || lowerName.contains("眩暈")) {
            return "stun_" + skillId;
        } else if (lowerName.contains("防禦") || lowerName.contains("防護")) {
            return "defense_" + skillId;
        } else if (lowerName.contains("攻擊") || lowerName.contains("打擊")) {
            return "attack_" + skillId;
        }

        return null; // 無法匹配關鍵字
    }


    /**
     * CMD action: magic_cardv1 - 多技能同時顯示
     */
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        // 主清單（進入卡冊頁面）- 使用固定 ACTION 格式
        if ("magic_cardv1".equalsIgnoreCase(cmd)) {
            String[] dataImgs = getPlayerSkillIconsMixWithFixedAction(pc);
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magic_cardv1", dataImgs));
            return true;
        }

        // 處理固定的 magic001-magic010，使用動態映射
        if (cmd.matches("^magic00[1-9]|magic010$")) {
            return handleFixedMagicAction(pc, cmd);
        }

        // 處理動態技能 ACTION：skill_技能ID
        if (cmd.startsWith("skill_")) {
            try {
                int skillId = Integer.parseInt(cmd.substring(6)); // 移除 "skill_" 前綴
                return handleSkillAction(pc, skillId);
            } catch (NumberFormatException e) {
                System.out.println("【ERROR】無效的 skill_ 指令格式: " + cmd);
                return false;
            }
        }

        // 處理語義化 ACTION：holy_49, berserk_52, heal_49 等
        if (cmd.matches("^[a-zA-Z_]+_\\d+$")) {
            try {
                String[] parts = cmd.split("_");
                int skillId = Integer.parseInt(parts[parts.length - 1]);
                return handleSkillAction(pc, skillId);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("【ERROR】無效的語義化指令格式: " + cmd);
                return false;
            }
        }

        // 原有的固定邏輯保留作為備用（資料庫中的對話檔指令）
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

    /**
     * 處理固定格式的 magic ACTION (magic001-magic010)
     * 使用動態映射到對應的技能
     */
    private static boolean handleFixedMagicAction(L1PcInstance pc, String cmd) {
        List<String> actionMapping = getPlayerActionMapping(pc.getId());
        if (actionMapping == null) {
            System.out.println("【ERROR】找不到玩家的 ACTION 映射: " + pc.getId());
            return false;
        }

        // 從 magic001, magic002... 提取索引 (0-based)
        int index = Integer.parseInt(cmd.substring(5)) - 1; // magic001 -> index 0

        if (index < 0 || index >= actionMapping.size()) {
            System.out.println("【WARNING】ACTION 索引超出範圍或玩家沒有這麼多技能: " + cmd + " -> index " + index + ", 技能總數: " + actionMapping.size());
            return false;
        }

        String actualAction = actionMapping.get(index);
        
        // 使用映射到的實際 ACTION 處理
        if (actualAction.startsWith("skill_")) {
            try {
                int skillId = Integer.parseInt(actualAction.substring(6));
                                return handleSkillAction(pc, skillId);
            } catch (NumberFormatException e) {
                System.out.println("【ERROR】映射的 ACTION 格式錯誤: " + actualAction);
                return false;
            }
        } else if (actualAction.matches("^[a-zA-Z_]+_\\d+$")) {
            try {
                String[] parts = actualAction.split("_");
                int skillId = Integer.parseInt(parts[parts.length - 1]);
                                return handleSkillAction(pc, skillId);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("【ERROR】映射的語義 ACTION 格式錯誤: " + actualAction);
                return false;
            }
        } else if (actualAction.startsWith("magic")) {
            // 如果映射到的是 magic 格式，直接查找對應技能
                        L1SkillEnhance enhance = _cmdMap.get(actualAction.toLowerCase());
            if (enhance != null) {
                return handleSkillAction(pc, enhance.getSkillId());
            } else {
                System.out.println("【ERROR】找不到 magic ACTION 對應的技能: " + actualAction);
                return false;
            }
        }

        System.out.println("【ERROR】無法處理的 ACTION 格式: " + actualAction);
        return false;
    }

    /**
     * 處理技能 ACTION 的通用方法
     */
    private static boolean handleSkillAction(L1PcInstance pc, int skillId) {
        Map<Integer, L1SkillEnhance> allLvMap = SkillEnhanceTable.get().getEnhanceBySkillId(skillId);
        if (allLvMap == null || allLvMap.isEmpty()) {
            System.out.println("【ERROR】找不到技能ID " + skillId + " 的強化資料");
            return false;
        }

        List<String> explainList = new ArrayList<>();
        List<Integer> lvList = new ArrayList<>(allLvMap.keySet());
        Collections.sort(lvList);

        // 取得第一級的圖標作為顯示圖標
        L1SkillEnhance firstLvEnhance = allLvMap.get(lvList.get(0));
        if (firstLvEnhance != null && firstLvEnhance.getIcon() != null) {
            explainList.add(firstLvEnhance.getIcon());
        } else {
            explainList.add("0"); // 預設圖標
        }

        // 添加各等級說明
        for (int lv : lvList) {
            L1SkillEnhance lvEnhance = allLvMap.get(lv);
            if (lvEnhance == null) continue;
            String desc = lvEnhance.getMsg1();
            if (desc == null || desc.trim().isEmpty()) {
                // 如果沒有說明文字，生成基本說明
                L1Skills skill = SkillsTable.get().getTemplate(skillId);
                String skillName = (skill != null) ? skill.getName() : "技能" + skillId;
                desc = skillName + " Lv." + lv + " - 無說明";
            }
            desc = desc.replace(",", " "); // 去逗號
            explainList.add(desc);
        }

        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magic_list", explainList.toArray(new String[0])));
        return true;
    }
}