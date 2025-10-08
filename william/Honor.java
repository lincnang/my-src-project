package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.NpcHonorTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Honor {
    private static final Map<Integer, Boolean> _dailyQuestStatus = new ConcurrentHashMap<>();
    private static final Log _log = LogFactory.getLog(Honor.class);
    private static Honor _instance;
    private final Map<Integer, L1WilliamHonor> _itemIdIndex = new HashMap<>();
    private final Map<Integer, Map<Integer, LocalDate>> _enteredStageMap = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> _stageMapId = new HashMap<>();
    private static final Map<Integer, int[]> _stageCoords = new HashMap<>();

    private Honor() {

        loadHonor();
        initStageMap();
    }

    public static Honor getInstance() {
        if (_instance == null) {
            _instance = new Honor();
        }
        return _instance;
    }

    public void enterStage(L1PcInstance pc, int stage) {
        if (hasCompletedStage(pc, stage)) {
            pc.sendPackets(new S_SystemMessage("你已完成第 " + (stage + 1) + " 階段，無法再次進入。"));
            return;
        }

        if (stage > 1 && !hasCompletedStage(pc, stage - 1)) {
            pc.sendPackets(new S_SystemMessage("尚未完成第 " + (stage) + " 階段，無法進入第 " + (stage + 1) + " 階段。"));
            return;
        }

        // 先確認階段是否對應啟用中的階級，未開放則直接返回
        L1WilliamHonor targetStageCheck = getHonorByStageIndex(stage);
        if (targetStageCheck == null) {
            // 再嘗試更寬鬆對應（相容舊按鈕/腳本）
            targetStageCheck = getHonorByAnyStage(stage);
            if (targetStageCheck == null) {
                // 最後退路：使用第一個已啟用階級，避免玩家被卡死
                targetStageCheck = getFirstActiveTemplate();
                if (targetStageCheck == null) {
                    pc.sendPackets(new S_SystemMessage("爵位資料未載入，請通知GM。"));
                    return;
                }
            }
        }

        if (!hasEnteredStageToday(pc, stage)) {
            markEnteredStage(pc, stage);
        }

        int mapId = getStageMapId(stage);
        int[] coords = getStageCoords(stage);
        L1Teleport.teleport(pc, coords[0], coords[1], (short) mapId, 5, true);

        List<Integer> npcIds = NpcHonorTable.get().getNpcIdListByMap(mapId);
        StringBuilder names = new StringBuilder();
        for (Integer npcId : npcIds) {
            L1Npc npcTemp = NpcTable.get().getTemplate(npcId);
            if (npcTemp != null) {
                names.append("「").append(npcTemp.get_name()).append("」 ");
            }
        }

        L1WilliamHonor targetStage = targetStageCheck;

        int currentHonor = pc.getHonor();
        int requiredHonor = targetStage.getHonorMax();
        int remain = requiredHonor - currentHonor;

        if (remain > 0) {
            pc.sendPackets(new S_SystemMessage("第 " + (stage + 1) + " 階任務目標:擊殺 " + names + "，還需「" + remain + "」點爵位積分。"));
        } else {
            pc.sendPackets(new S_SystemMessage("第 " + (stage + 1) + " 階任務目標：擊殺 " + names + "，你已達成該階段積分，完成擊殺即可升階！"));
        }

        if (currentHonor >= requiredHonor) {
            Honor.getInstance().checkHonor(pc, true, false);
        }

    }

    private boolean hasCompletedStage(L1PcInstance pc, int stage) {
        // 0-based 階段：第 0 階對應等級 1，因此需要完成等級 = stage + 1
        int requiredLevel = stage + 1;
        int currentStage = getHonorLevel(pc.getHonor());
        return currentStage >= requiredLevel && pc.getHonorLevel() >= requiredLevel;
    }

    // 判斷今天是否已進入此階段
    public boolean hasEnteredStageToday(L1PcInstance pc, int stage) {
        return _enteredStageMap.getOrDefault(pc.getId(), new HashMap<>())
                .getOrDefault(stage, LocalDate.MIN).equals(LocalDate.now());
    }

    // 記錄今天進入此階段
    public void markEnteredStage(L1PcInstance pc, int stage) {
        _enteredStageMap.computeIfAbsent(pc.getId(), k -> new HashMap<>())
                .put(stage, LocalDate.now());
    }
    private void initStageMap() {
        // 地圖對應設定
        _stageMapId.put(1, 10503);
        _stageMapId.put(2, 10504);
        _stageMapId.put(3, 10505);
        _stageMapId.put(4, 10506);
        _stageMapId.put(5, 10507);
        // 傳送座標設定
        _stageCoords.put(1, new int[]{32852, 32875});
        _stageCoords.put(2, new int[]{32873, 32755});
        _stageCoords.put(3, new int[]{32741, 32885});
        _stageCoords.put(4, new int[]{32786, 32905});
        _stageCoords.put(5, new int[]{32727, 32842});

        _stageMapId.put(6, 10508);
        _stageMapId.put(7, 10509);
        _stageMapId.put(8, 15441);
        _stageMapId.put(9, 15442);
        _stageMapId.put(10, 15443);

        _stageCoords.put(6, new int[]{32781, 32795});
        _stageCoords.put(7, new int[]{32769, 32764});
        _stageCoords.put(8, new int[]{32603, 32753});
        _stageCoords.put(9, new int[]{32904, 32771});
        _stageCoords.put(10, new int[]{32788, 32840});

        _stageMapId.put(11, 15444);
        _stageMapId.put(12, 15445);
        _stageMapId.put(13, 15446);
        _stageMapId.put(14, 15447);
        _stageMapId.put(15, 15448);
        _stageMapId.put(16, 15449);
        _stageMapId.put(17, 15450);
        _stageMapId.put(18, 15451);
        _stageMapId.put(19, 15452);

        _stageCoords.put(11, new int[]{32775, 32962});
        _stageCoords.put(12, new int[]{32657, 32807});
        _stageCoords.put(13, new int[]{32717, 32775});
        _stageCoords.put(14, new int[]{32793, 32744});
        _stageCoords.put(15, new int[]{32678, 32975});

        _stageCoords.put(16, new int[]{32705, 32800});
        _stageCoords.put(17, new int[]{32776, 32837});
        _stageCoords.put(18, new int[]{32731, 32872});
        _stageCoords.put(19, new int[]{32670, 32861});
    }

    private int getStageMapId(int stage) {
        // 0-based 階段：以 stage+1 對應原有地圖配置
        return _stageMapId.getOrDefault(stage + 1, 4); // 預設錯誤值
    }

    private int[] getStageCoords(int stage) {
        // 0-based 階段：以 stage+1 對應原有座標配置
        return _stageCoords.getOrDefault(stage + 1, new int[]{33443, 32808});

    }

    private void loadHonor() {
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM 系統_爵位能力 ORDER BY 爵位階級 ASC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int level = rs.getInt("爵位階級");
                L1WilliamHonor honor = new L1WilliamHonor(
                        level,
                        rs.getInt("爵位值max"),
                        rs.getInt("爵位值min"),
                        rs.getString("爵位階級名稱"),
                        rs.getInt("是否開啟爵位功能"),
                        rs.getInt("體力"), rs.getInt("魔力"), rs.getInt("力量"), rs.getInt("敏捷"),
                        rs.getInt("智力"), rs.getInt("體質"), rs.getInt("精神"), rs.getInt("魅力"),
                        rs.getInt("體力回復量"), rs.getInt("魔力回復量"), rs.getInt("地屬性"),
                        rs.getInt("水屬性"), rs.getInt("火屬性"), rs.getInt("風屬性"),
                        rs.getInt("昏迷抗性"), rs.getInt("石化耐性"), rs.getInt("睡眠耐性"),
                        rs.getInt("寒冰耐性"), rs.getInt("支撐耐性"), rs.getInt("暗黑耐性"),
                        rs.getInt("魔防"), rs.getInt("魔攻"), rs.getInt("近距離命中"), rs.getInt("遠距離命中"),
                        rs.getInt("近物理傷害"), rs.getInt("遠物理傷害"), rs.getInt("減免物理傷害"),
                        rs.getInt("魔法傷害"), rs.getInt("減免魔法傷害"), rs.getInt("防禦"),
                        rs.getInt("魔法命中"), rs.getInt("PVP傷害減免"), rs.getInt("無視PVP傷害減免"),
                        rs.getInt("昏迷命中"), rs.getInt("阻擋武器+%")
                );
                _itemIdIndex.put(level, honor);
            }
            _log.info("讀取->系統_爵位能力資料數量: " + _itemIdIndex.size());
        } catch (SQLException e) {
            _log.error("error while loading 系統_爵位能力 table", e);
        }
    }

    public int getHonorLevel(int honor) {
        // 以階級排序，確保邏輯穩定；最後一階改為 honor >= min 即落入
        if (_itemIdIndex.isEmpty()) {
            return 0;
        }
        List<Map.Entry<Integer, L1WilliamHonor>> sorted = new ArrayList<>(_itemIdIndex.entrySet());
        sorted.sort(Map.Entry.comparingByKey());

        for (int i = 0; i < sorted.size(); i++) {
            L1WilliamHonor h = sorted.get(i).getValue();
            if (h.getIsActive() != 1) {
                continue;
            }
            boolean isLast = true;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getValue().getIsActive() == 1) {
                    isLast = false;
                    break;
                }
            }
            if (!isLast) {
                if (honor >= h.getHonorMin() && honor < h.getHonorMax()) {
                    return h.getHonorLevel();
                }
            } else {
                if (honor >= h.getHonorMin()) {
                    return h.getHonorLevel();
                }
            }
        }
        // 若未命中任一區間，回傳 0（未入門）
        return 0;
    }

    public int getMaxLevel() {
        return _itemIdIndex.keySet().stream().max(Integer::compareTo).orElse(0);
    }

    public boolean isDailyQuestCompleted(L1PcInstance pc) {
        return _dailyQuestStatus.getOrDefault(pc.getId(), false);
    }

    public void markDailyQuestComplete(L1PcInstance pc) {
        _dailyQuestStatus.put(pc.getId(), true);
    }

    public void resetHonorIfNotCompleted(L1PcInstance pc, boolean isOffline) throws Exception {
        if (!isDailyQuestCompleted(pc)) {
            pc.setHonor(0);
            _dailyQuestStatus.put(pc.getId(), false);
            if (!isOffline) {
                pc.sendPackets(new S_SystemMessage("你未完成今日爵位任務，當日積分已清除。"));
            }
        }
        markDailyQuestComplete(pc);
        pc.save();
    }

    /**
     * 檢查並處理玩家威望升級
     */
    public void checkHonor(L1PcInstance pc, boolean fromMission, boolean forceTeleport) {
        if (pc == null) return;
        clearOldStageEntries();

        int currentHonor = pc.getHonor();
        int currentLevel = pc.getHonorLevel();
        int realLevel = getHonorLevel(currentHonor);

        if (realLevel > currentLevel) {
            applyHonorUpgrade(pc, realLevel);
            return;
        }

        // 邊界安全升級：honor 已達當前等級上限，但 realLevel 仍等於 currentLevel
        L1WilliamHonor curr = getTemplate(currentLevel);
        if (curr != null) {
            int currMax = curr.getHonorMax();
            if (currentHonor >= currMax) {
                int nextLevel = currentLevel + 1;
                L1WilliamHonor next = getTemplate(nextLevel);
                if (next != null && next.getIsActive() == 1) {
                    applyHonorUpgrade(pc, nextLevel);
                    return;
                }
            }
        }

        if (realLevel == currentLevel && fromMission && forceTeleport) {
            applyHonorUpgrade(pc, realLevel);
            return;
        }
    }

    private void applyHonorUpgrade(L1PcInstance pc, int level) {
        L1WilliamHonor honor = getTemplate(level);
        if (honor == null) {
            return;
        }

        L1WilliamHonor.delHonorSkill(pc, pc.getHonorLevel());
        pc.setHonorLevel(level);
        L1WilliamHonor.getHonorSkill(pc);
        L1WilliamHonor.showHonorSkill(pc, level);

        pc.sendPackets(new S_PacketBoxGree("【" + pc.getName() + "】爵位階級提升至 Lv." + level + "！"));
        pc.sendPackets(new S_SystemMessage("你已完成階段任務，已返回家園，請重新登入以確保能力完全套用。"));

        markEnteredStage(pc, level);

        GeneralThreadPool.get().schedule(() -> {
            try {
                // 不要在這邊 return，改為延遲等待
                int wait = 0;
                while ((pc.isTeleport() || pc.getNetConnection() == null) && wait < 10) {
                    Thread.sleep(100); // 最多等1秒
                    wait++;
                }
                int homeX = 33423;
                int homeY = 32825;
                short homeMap = 4;
                L1Teleport.teleport(pc, homeX, homeY, homeMap, 5, true);

            } catch (Exception e) {
            }
        }, 3000); // 延後 3 秒傳送
        try {
            pc.save();
        } catch (Exception e) {
        }
    }

    // 可放在每天凌晨的某個排程或玩家登入時觸發
    public void clearOldStageEntries() {
        LocalDate today = LocalDate.now();
        _enteredStageMap.values().forEach(stageMap ->
                stageMap.entrySet().removeIf(e -> !e.getValue().equals(today)));
    }

    public L1WilliamHonor getTemplate(int level) {
        return _itemIdIndex.get(level);
    }

    public Map<Integer, L1WilliamHonor> getHonorMap() {
        return _itemIdIndex;
    }

    public L1WilliamHonor getHonorByStageIndex(int stage) {
        // 先嘗試「階段 == 爵位階級」直接對應（最直覺：stage=0 -> 等級0）
        L1WilliamHonor direct = _itemIdIndex.get(stage);
        if (direct != null && direct.getIsActive() == 1) {
            return direct;
        }

        // 否則回退為「第 N 個啟用中的階級」，以相容舊設計
        List<Map.Entry<Integer, L1WilliamHonor>> sorted = new ArrayList<>(_itemIdIndex.entrySet());
        sorted.sort(Map.Entry.comparingByKey());
        int idx = 0;
        for (Map.Entry<Integer, L1WilliamHonor> e : sorted) {
            L1WilliamHonor h = e.getValue();
            if (h.getIsActive() != 1) {
                continue;
            }
            if (idx == stage) {
                return h;
            }
            idx++;
        }
        return null;
    }

    // 寬鬆對應：優先直接等級，否則嘗試 0-based 索引與 +/-1 回退
    public L1WilliamHonor getHonorByAnyStage(int stage) {
        L1WilliamHonor h = getHonorByStageIndex(stage);
        if (h != null) return h;
        if (stage > 0) {
            h = getHonorByStageIndex(stage - 1);
            if (h != null) return h;
        }
        h = getTemplate(stage);
        if (h != null && h.getIsActive() == 1) return h;
        h = getTemplate(stage + 1);
        if (h != null && h.getIsActive() == 1) return h;
        return null;
    }

    // 取得第一個啟用中的階級模板（依爵位階級遞增）
    public L1WilliamHonor getFirstActiveTemplate() {
        List<Map.Entry<Integer, L1WilliamHonor>> entries = new ArrayList<>(_itemIdIndex.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Integer, L1WilliamHonor> e : entries) {
            L1WilliamHonor h = e.getValue();
            if (h != null && h.getIsActive() == 1) {
                return h;
            }
        }
        return null;
    }
}