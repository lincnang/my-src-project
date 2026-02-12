package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.NpcHonorTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Honor {
    private static final Map<Integer, Boolean> _dailyQuestStatus = new ConcurrentHashMap<>();
    private static final Logger _log = Logger.getLogger(Honor.class.getName());
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
            pc.sendPackets(new S_SystemMessage("你已完成第 " + stage + " 階段，無法再次進入。"));
            return;
        }

        if (stage > 1 && !hasCompletedStage(pc, stage - 1)) {
            pc.sendPackets(new S_SystemMessage("尚未完成第 " + (stage - 1) + " 階段，無法進入第 " + stage + " 階段。"));
            return;
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

        L1WilliamHonor targetStage = getHonorByStageIndex(stage);
        if (targetStage == null) {
            pc.sendPackets(new S_SystemMessage("無法對應此階段的爵位資料，請通知GM。"));
            return;
        }

        int currentHonor = pc.getHonor();
        int requiredHonor = targetStage.getHonorMin();
        int remain = requiredHonor - currentHonor;

        if (remain > 0) {
            pc.sendPackets(new S_SystemMessage("任務目標:擊殺 " + names + "，還需「" + remain + "」點爵位積分。"));
        } else {
            pc.sendPackets(new S_SystemMessage("任務目標：擊殺 " + names + "，你已達成該階段積分，完成擊殺即可升階！"));
        }

        if (currentHonor >= requiredHonor) {
            Honor.getInstance().checkHonor(pc, true, false);
        }

    }

    /**
     * 判斷玩家是否已完成該階段（達到該爵位等級）
     * 修改：只要爵位等級達到，就視為已完成該階段，不能再進入
     */
    private boolean hasCompletedStage(L1PcInstance pc, int stage) {
        // ✅ 只檢查爵位等級，不檢查積分
        // 例如：玩家爵位 Lv.3，則第 1、2、3 階段都視為已完成
        return pc.getHonorLevel() >= stage;
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
        return _stageMapId.getOrDefault(stage, 4); // 預設錯誤值
    }

    private int[] getStageCoords(int stage) {
        return _stageCoords.getOrDefault(stage, new int[]{33443, 32808});

    }

    private void loadHonor() {
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM 系統_爵位能力 ORDER BY 爵位階級 ASC");
             ResultSet rs = ps.executeQuery()) {

            int count = 0;
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
                count++;
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while loading 系統_爵位能力 table", e);
        }
    }

    public int getHonorLevel(int honor) {
        for (L1WilliamHonor h : _itemIdIndex.values()) {
            if (h.getIsActive() == 1 && honor >= h.getHonorMin() && honor < h.getHonorMax()) {
                return h.getHonorLevel();
            }
        }
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

    /**
     * 每日重置積分（不影響已獲得的爵位等級）
     */
    public void resetHonorIfNotCompleted(L1PcInstance pc, boolean isOffline) throws Exception {
        if (!isDailyQuestCompleted(pc)) {
            // ✅ 只清零積分，不改變爵位等級
            pc.setHonor(0);
            _dailyQuestStatus.put(pc.getId(), false);
            if (!isOffline) {
                pc.sendPackets(new S_SystemMessage("你未完成今日爵位任務，當日積分已清除。"));
                pc.sendPackets(new S_SystemMessage("你的爵位等級 Lv." + pc.getHonorLevel() + " 保持不變。"));
            }
        }
        markDailyQuestComplete(pc);
        pc.save();
    }

    /**
     * 檢查並處理玩家威望升級（每次只升一級，避免跨階）
     */
    public void checkHonor(L1PcInstance pc, boolean fromMission, boolean forceTeleport) {
        if (pc == null) return;
        synchronized (pc) {
            clearOldStageEntries();

            int currentHonor = pc.getHonor();
            int currentLevel = pc.getHonorLevel();
            int nextLevel = currentLevel + 1;

            L1WilliamHonor nextStage = getTemplate(nextLevel);
            if (nextStage == null) return; // 已達最高等級

            // 積分達到下一階段門檻才升階
            if (currentHonor >= nextStage.getHonorMin()) {
                applyHonorUpgrade(pc, nextLevel);
            }
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
        return _itemIdIndex.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .skip(stage - 1L)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 手動清除所有玩家的爵位每日紀錄與進入紀錄。
     */
    public void forceDailyReset() {
        _dailyQuestStatus.clear();
        _enteredStageMap.clear();
        _log.log(Level.INFO, "Honor daily cache has been cleared manually.");
    }
}