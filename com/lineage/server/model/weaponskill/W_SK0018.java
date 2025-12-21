package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.monitor.PerformanceMonitor;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 格蘭肯的憤怒
 * PVP 傷害減免疊加技能 + 昏迷抗性減少效果
 * 每層效果：PVP 傷害減免 -1%、昏迷抗性 -2%
 * 最大疊加：5 層
 * 觸發機率：50%
 */
public class W_SK0018 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0018.class);

    // 節流機制：防止技能被濫用
    private static final long SKILL_COOLDOWN_MS = 300; // 0.3秒冷卻時間
    private static final ConcurrentHashMap<Integer, Long> _lastUsedTime = new ConcurrentHashMap<>();

    // 執行緒調度器
    // private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final java.util.Map<Integer, java.util.concurrent.ScheduledFuture<?>> _timers = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<Integer, Integer> _stackCounts = new java.util.concurrent.ConcurrentHashMap<>();

    // 清理機制：定期清理無效的玩家資料
    private static long lastCleanupTime = 0;
    private static final long CLEANUP_INTERVAL_MS = 5 * 60 * 1000; // 5分鐘清理一次

    /**
     * 清理無效的玩家資料（斷線的玩家）
     */
    private static void cleanupInvalidPlayers() {
        long now = System.currentTimeMillis();
        if (now - lastCleanupTime < CLEANUP_INTERVAL_MS) {
            return;
        }

        try {
            com.lineage.server.world.World world = com.lineage.server.world.World.get();

            // 方法1：使用 World.findObject() - 高效查找
            java.util.List<Integer> offlinePlayerIds = new java.util.ArrayList<>();

            for (Integer playerId : _timers.keySet()) {
                // 使用 findObject 查找玩家
                com.lineage.server.model.L1Object obj = world.findObject(playerId);

                // 檢查是否為在線玩家
                boolean isOnline = false;
                if (obj instanceof com.lineage.server.model.Instance.L1PcInstance) {
                    com.lineage.server.model.Instance.L1PcInstance pc =
                        (com.lineage.server.model.Instance.L1PcInstance) obj;
                    isOnline = pc.getNetConnection() != null;
                }

                if (!isOnline) {
                    offlinePlayerIds.add(playerId);
                }
            }

            // 清理離線玩家的資料
            for (Integer playerId : offlinePlayerIds) {
                ScheduledFuture<?> future = _timers.remove(playerId);
                if (future != null) {
                    future.cancel(false);
                }
                _stackCounts.remove(playerId);

                if (_log.isDebugEnabled()) {
                    _log.debug("[W_SK0018] Cleaned up offline player ID: " + playerId);
                }
            }

            lastCleanupTime = now;

            // 記錄清理統計
            if (_log.isDebugEnabled()) {
                java.util.Collection<com.lineage.server.model.Instance.L1PcInstance> allPlayers = null;
                int onlineCount = 0;

                try {
                    allPlayers = world.getAllPlayers();
                    if (allPlayers != null) {
                        onlineCount = allPlayers.size();
                    }
                } catch (Exception e) {
                    _log.warn("[W_SK0018] Error getting online player count", e);
                }

                _log.debug("[W_SK0018] Cleanup completed. Timers: " + _timers.size() +
                          ", Stacks: " + _stackCounts.size() + ", Online: " + onlineCount +
                          ", Cleaned: " + offlinePlayerIds.size());
            }
        } catch (Exception e) {
            _log.error("[W_SK0018] Error during cleanup", e);
        }
    }

    // 技能常數設定
    private static final int MAX_STACK = 5; // 最大疊加層數
    private static final int PVP_DAMAGE_REDUCTION_PER_STACK = -1; // 每層 PVP 傷害減免
    private static final int STUN_RESISTANCE_REDUCTION_PER_STACK = -2; // 每層昏迷抗性減少
    private static final int SKILL_DURATION_MS = 8000; // 技能持續時間（毫秒）
    private static final double TRIGGER_CHANCE = 1.0; // 0.5 觸發機率（50%）// 10% 機率:  = 0.1 //  100% 機率:  = 1.0

    // 隨機數生成器
    private static final Random _random = new Random();

    public W_SK0018() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0018();
    }

    /**
     * 執行技能邏輯
     *
     * @param pc     使用技能的玩家角色
     * @param target 技能目標角色
     * @param weapon 使用的武器
     * @param srcdmg 原始傷害
     * @return 最終傷害
     */
    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
                                     final L1ItemInstance weapon, final double srcdmg) {
        try (PerformanceMonitor.PerformanceTracker tracker =
             PerformanceMonitor.trackSkill("GRANKEN_FURY", pc != null ? pc.getName() : "Unknown")) {

            // === 0. 節流檢查（防止技能濫用） ===
            if (pc != null) {
                int pcId = pc.getId();
                Long lastUsed = _lastUsedTime.get(pcId);
                long now = System.currentTimeMillis();
                if (lastUsed != null && now - lastUsed < SKILL_COOLDOWN_MS) {
                    return srcdmg; // 冷卻中，直接返回原傷害
                }
                _lastUsedTime.put(pcId, now);
            }

            // === 1. 機率判斷（直接用SQL欄位 random1, random2） ===
            int activationChance = random(weapon); // L1WeaponSkillType 的 random(weapon)
            int chance = _random.nextInt(1000);
            if (chance >= activationChance) {
                return srcdmg; // 未觸發，直接回傳原傷害
            }

            if (pc == null || target == null || !(target instanceof L1PcInstance)) {
                return 0;
            }

            L1PcInstance targetPc = (L1PcInstance) target;

            // 定期清理無效資料
            cleanupInvalidPlayers();

            // 獲取當前疊加層數
            // int currentStacks = targetPc.getTemporaryEffect("PVP_DAMAGE_SKILL");
            int currentStacks = _stackCounts.getOrDefault(targetPc.getId(), 0);
            
            // 取消舊的移除排程
            java.util.concurrent.ScheduledFuture<?> future = _timers.remove(targetPc.getId());
            if (future != null) {
                future.cancel(false);
            }

            if (currentStacks < MAX_STACK) {
                // 增加疊加層數
                currentStacks++;
                // targetPc.addTemporaryEffect("PVP_DAMAGE_SKILL", currentStacks, SKILL_DURATION_MS);
                _stackCounts.put(targetPc.getId(), currentStacks);

                // 套用單層效果 (累加)
                // PVP減免是 +=，所以直接加負值
                targetPc.setPvpDmg_R(PVP_DAMAGE_REDUCTION_PER_STACK);
                // 昏迷抗性使用 addRegistStun
                targetPc.addRegistStun(STUN_RESISTANCE_REDUCTION_PER_STACK);
                
                // 顯示提示訊息
                int totalPvp = currentStacks * PVP_DAMAGE_REDUCTION_PER_STACK;
                int totalStun = currentStacks * STUN_RESISTANCE_REDUCTION_PER_STACK;
                targetPc.sendPackets(new S_SystemMessage(
                        String.format("格蘭肯疊加到 %d 層：PVP減免 %d%%，昏迷抗性 %d%%。",
                                currentStacks, totalPvp, totalStun)
                ));
            } else {
                // 已達最大層數，僅刷新時間
                targetPc.sendPackets(new S_SystemMessage("效果已達最大疊加層數。"));
            }

            // 排程移除效果 (移除所有層數)
            final int stacksToRemove = currentStacks;
            future = com.lineage.server.thread.GeneralThreadPool.get().schedule((Runnable) () -> removeSkillEffects(targetPc, stacksToRemove),
                    SKILL_DURATION_MS);
            _timers.put(targetPc.getId(), future);

            // 播放動畫，可用SQL欄位設定
            int animId = get_gfxid2(); // 建議統一和W_SK0017，資料庫填18856
            if (animId > 0) {
                pc.sendPacketsAll(new S_SkillSound(target.getId(), animId));
            }
            targetPc.sendPackets(new S_InventoryIcon(10017, true, 3215, 8));

            return srcdmg;
        } catch (Exception e) {
            _log.error("技能執行時發生錯誤: " + e.getLocalizedMessage(), e);
            return 0;
        }
    }


    /**
     * 套用技能效果（PVP 傷害減免 + 昏迷抗性減少）
     *
     * @param target 目標角色
     * @param stacks 當前疊加層數
     */
    private void applySkillEffects(L1Character target, int stacks) {
        // 已整合至 start_weapon_skill
    }

    /**
     * 移除技能效果（PVP 傷害減免 + 昏迷抗性減少）
     *
     * @param target      目標角色
     * @param stacks      要移除的層數
     */
    private void removeSkillEffects(L1Character target, int stacks) {
        if (target instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) target;

            // 檢查玩家是否仍在線上
            if (targetPc.getNetConnection() == null) {
                // 玩家已斷線，只清理靜態資料
                _stackCounts.remove(targetPc.getId());
                _timers.remove(targetPc.getId());
                _log.debug("[W_SK0018] Player disconnected, removing effect data for ID: " + targetPc.getId());
                return;
            }

            // 恢復效果 (扣除之前增加的負值 = 加上正值)
            int pvpRestore = stacks * -PVP_DAMAGE_REDUCTION_PER_STACK; // e.g. 5 * -(-1) = 5
            int stunRestore = stacks * -STUN_RESISTANCE_REDUCTION_PER_STACK; // e.g. 5 * -(-2) = 10

            targetPc.setPvpDmg_R(pvpRestore);
            targetPc.addRegistStun(stunRestore);

            // 清除暫存狀態
            // targetPc.removeTemporaryEffect("PVP_DAMAGE_SKILL");
            _stackCounts.remove(targetPc.getId());
            _timers.remove(targetPc.getId());

            targetPc.sendPackets(new S_SystemMessage("格蘭肯憤怒技能已恢復。"));
        }
    }
}
