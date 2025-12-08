package com.lineage.server.model.skill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 自動循環施放技能系統
 * 功能：施放技能後時間到自動再施放，再次手動施放則關閉
 */
public class L1AutoRecycleSkill {
    private static final Log _log = LogFactory.getLog(L1AutoRecycleSkill.class);

    // 儲存玩家的自動循環技能任務 <玩家ID_技能ID, 任務>
    private static final Map<String, AutoRecycleTask> _autoTasks = new ConcurrentHashMap<>();

    /**
     * 切換技能的自動循環狀態
     * @param pc 玩家
     * @param skillId 技能ID
     * @param duration 持續時間（毫秒）
     * @return true=啟動自動循環, false=關閉自動循環
     */
    public static boolean toggleAutoRecycle(L1PcInstance pc, int skillId, int duration) {
        if (duration <= 0) {
            return false;
        }
        String key = pc.getId() + "_" + skillId;

        // 取得技能名稱
        com.lineage.server.templates.L1Skills skill = com.lineage.server.datatables.SkillsTable.get().getTemplate(skillId);
        String skillName = (skill != null && skill.getName() != null) ? skill.getName() : "技能" + skillId;


        // 如果已存在，則關閉
        if (_autoTasks.containsKey(key)) {
            stopAutoRecycle(pc, skillId);
            pc.sendPackets(new com.lineage.server.serverpackets.S_SystemMessage(
                    "\\fY[自動循環] " + skillName + " 自動循環已關閉"));
            return false;
        }

        // 啟動自動循環
        AutoRecycleTask task = new AutoRecycleTask(pc, skillId, duration);
        _autoTasks.put(key, task);
        task.start();

        pc.sendPackets(new com.lineage.server.serverpackets.S_SystemMessage(
                "\\fG[自動循環] " + skillName + " 自動循環已啟動（每 " + (duration/1000) + " 秒）"));
        return true;
    }

    /**
     * 停止指定技能的自動循環
     */
    public static void stopAutoRecycle(L1PcInstance pc, int skillId) {
        String key = pc.getId() + "_" + skillId;
        AutoRecycleTask task = _autoTasks.remove(key);
        if (task != null) {
            task.stop();
        }
    }

    /**
     * 停止玩家所有自動循環技能（登出時調用）
     */
    public static void stopAllAutoRecycle(L1PcInstance pc) {
        _autoTasks.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(pc.getId() + "_")) {
                entry.getValue().stop();
                return true;
            }
            return false;
        });
    }

    /**
     * 檢查技能是否處於自動循環狀態
     */
    public static boolean isAutoRecycling(L1PcInstance pc, int skillId) {
        return _autoTasks.containsKey(pc.getId() + "_" + skillId);
    }

    /**
     * 自動循環任務類別
     */
    private static class AutoRecycleTask implements Runnable {
        private final L1PcInstance _pc;
        private final int _skillId;
        private final int _duration;
        private ScheduledFuture<?> _future;

        public AutoRecycleTask(L1PcInstance pc, int skillId, int duration) {
            _pc = pc;
            _skillId = skillId;
            _duration = duration;
        }

        public void start() {
            // 延遲 duration 毫秒後開始執行，然後每 duration 毫秒執行一次
            // GeneralThreadPool 的 scheduleAtFixedRate 單位固定為 MILLISECONDS，只需 3 個參數
            _future = GeneralThreadPool.get().scheduleAtFixedRate(
                    this,
                    _duration,
                    _duration
            );
        }

        public void stop() {
            if (_future != null) {
                _future.cancel(false);
                _future = null;
            }
        }

        @Override
        public void run() {
            try {
                // 檢查玩家是否還在線上
                if (_pc == null || _pc.getNetConnection() == null) {
                    stop();
                    return;
                }

                // 檢查玩家是否死亡、鬼魂、傳送中
                if (_pc.isDead() || _pc.isGhost() || _pc.isTeleport()) {
                    return;
                }

                // 反擊屏障需要雙手劍，若玩家改裝其他武器則立即終止自動循環
                if (requiresTwoHandedSword() && !hasTwoHandedSwordEquipped()) {
                    L1AutoRecycleSkill.stopAutoRecycle(_pc, _skillId);
                    _pc.sendPackets(new com.lineage.server.serverpackets.S_SystemMessage(
                            "\\fY[自動循環] 反擊屏障需要雙手劍，已停止自動循環"));
                    return;
                }

                // 自動施放技能
                com.lineage.server.templates.L1Skills skill = com.lineage.server.datatables.SkillsTable.get().getTemplate(_skillId);
                if (skill != null) {
                    // 使用 L1SkillUse 施放技能
                    L1SkillUse skillUse = new L1SkillUse();
                    skillUse.handleCommands(_pc, _skillId, _pc.getId(), _pc.getX(), _pc.getY(), 0, L1SkillUse.TYPE_NORMAL);

                } else {
                    _log.warn("[自動循環] 找不到技能模板 ID: " + _skillId + "，停止自動循環");
                    stop();
                }
            } catch (Exception e) {
                _log.error("[自動循環] 執行錯誤: " + e.getMessage(), e);
            }
        }

        private boolean requiresTwoHandedSword() {
            return (_skillId == L1SkillId.COUNTER_BARRIER) || (_skillId == 71);
        }

        private boolean hasTwoHandedSwordEquipped() {
            final L1ItemInstance weapon = _pc.getWeapon();
            if (weapon == null) {
                return false;
            }
            final L1Item item = weapon.getItem();
            if (item == null) {
                return false;
            }
            final boolean isSwordType = (item.getType() == 3); // 3=劍類
            return isSwordType && item.isTwohandedWeapon();
        }
    }
}