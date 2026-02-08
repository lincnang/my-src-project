package com.lineage.server.model.Instance;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.pcdelay.PcTelDelayAI;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledFuture;

/**
 * 玩家 AI 控制器 - 事件驅動架構
 * 
 * 重構日期: 2025-10-12
 * 重構原因: 原 while-loop 阻塞模式導致線程池耗盡
 * 架構模式: Event-Driven (schedule-based) 非阻塞執行
 * 
 * 優勢:
 * - 100 個掛機玩家從佔用 100 個線程 → 共享 10-20 個線程
 * - 線程使用時間: 永久佔用 → 每次僅 10-50ms
 * - 配合 NpcAI 重構,總線程使用率從 512/512 → 30-50/300
 */
public class PcAI implements Runnable {
    private static final Log _log = LogFactory.getLog(PcAI.class);
    private final L1PcInstance _pc;
    private ScheduledFuture<?> _future;
    private volatile boolean _isRunning = false;

    public PcAI(final L1PcInstance pc) {
        _pc = pc;
    }

    /**
     * 啟動 AI - 事件驅動模式
     * 使用 schedule() 而非 execute(),避免線程阻塞
     */
    public void startAI() {
        if (_isRunning) {
            return; // 防止重複啟動
        }
        _isRunning = true;
        _pc.setAiRunning(true);
        schedule(0); // 立即開始第一次執行
    }

    /**
     * 停止 AI
     * 取消排程任務並清理狀態
     */
    public void stopAI() {
        _isRunning = false;
        _pc.setAiRunning(false);
        if (_future != null && !_future.isCancelled()) {
            _future.cancel(false);
            _future = null;
        }
    }

    /**
     * AI 執行週期 - 每次僅處理一個步驟
     * 執行完成後根據狀態決定:
     * 1. 重新排程 (繼續 AI)
     * 2. 停止 (角色死亡/離線/條件不符)
     * 控制狀態下AI繼續運行但行為受限
     */
    @Override
    public void run() {
        try {
            // 檢查是否應該繼續執行
            if (!shouldContinue()) {
                stopAI();
                return;
            }

            // 執行一次 AI 處理
            if (AIProcess()) {
                // AI 處理返回 true 表示應該停止
                stopAI();
                return;
            }

            // 計算下次執行延遲
            // 在控制狀態下降低執行頻率，節省資源
            long baseDelay = Math.max(200, _pc.getSleepTimeAI());
            long nextDelay = _pc.isInAnyControlState() ? 1000 : baseDelay; // 控制狀態下1秒執行一次
            schedule(nextDelay);

        } catch (final Exception e) {
            _log.error("Exception in PcAI run(): " + _pc.getName(), e);
            stopAI();
        }
    }

    /**
     * 檢查是否應該繼續執行 AI
     * AI在控制狀態下繼續運行，但行為會被限制
     * @return true 繼續, false 停止
     */
    private boolean shouldContinue() {
        return _isRunning
            && _pc != null
            && _pc.getMaxHp() > 0
            && !_pc.isDead();
            // 移除控制狀態檢查 - AI在控制狀態下繼續運行
    }

    /**
     * 檢查是否處於麻痺或睡眠狀態
     * @return true 被麻痺/睡眠, false 正常
     */
    private boolean isParalyzedOrSleeped() {
        return _pc.isSleeped() 
            || _pc.isParalyzedX() 
            || _pc.isParalyzed();
    }

    /**
     * 重新排程 AI 執行
     * @param delay 延遲時間 (毫秒)
     */
    private void schedule(long delay) {
        if (!_isRunning) {
            return;
        }
        try {
            _future = GeneralThreadPool.get().schedule(this, delay);
        } catch (Exception e) {
            _log.error("Failed to schedule PcAI for: " + _pc.getName(), e);
            stopAI();
        }
    }

    /**
     * AI 處理邏輯 - 執行一次 AI 週期
     * @return true 停止 AI, false 繼續執行
     */

    private boolean AIProcess() {
        try {
            // 檢查角色狀態，如果不符合條件則終止 AI
            if (_pc == null || _pc.getOnlineStatus() == 0 || _pc.isDead() || _pc.getCurrentHp() <= 0 || _pc.isGhost() || !_pc.isActivated() || _pc.getNetConnection().getIp() == null || !_pc.getMap().isAutoBot()) {
                return true;
            }

            // 掛機時間檢查
            if (com.lineage.config.ThreadPoolSetNew.checktimeguaji) {
                java.util.Calendar date = java.util.Calendar.getInstance();
                int nowHour = date.get(java.util.Calendar.HOUR_OF_DAY);
                for (int hour : com.lineage.config.ThreadPoolSetNew.GUAJI_ITEM) {
                    if (nowHour == hour) {
                        _pc.setActivated(false);
                        _pc.sendPackets(new com.lineage.server.serverpackets.S_SystemMessage("自動狩獵已停止(掛機時間限制)。"));
                        return true;
                    }
                }
            }

            // 狀態檢查：若被暈眩、凍結或強力拘束，暫時跳過本次AI執行 (不停止AI，等待狀態解除)
            // isParalyzedX() 已包含: SHOCK_STUN, KINGDOM_STUN, PHANTASM, EARTH_BIND, BONE_BREAK, 詛咒/毒素麻痺
            if (_pc.isParalyzed() || _pc.isParalyzedX() || _pc.isSleeped()
                    || _pc.hasSkillEffect(L1SkillId.STATUS_FREEZE)    // 凍結 (冰矛等)
                    || _pc.hasSkillEffect(L1SkillId.DESPERADO)        // 亡命之徒
                    || _pc.hasSkillEffect(L1SkillId.POWERGRIP)        // 拘束移動
                    || _pc.hasSkillEffect(L1SkillId.EMPIRE)           // 暈眩之劍 (王族)
                    || _pc.hasSkillEffect(L1SkillId.EIF_EMPIRE)       // 精靈之暈 (妖精)
                    || _pc.hasSkillEffect(L1SkillId.Shadow_Daze)      // 暗影暈眩 (黑妖)
                    || _pc.hasSkillEffect(L1SkillId.TITAN_STUN)       // 泰坦之暈 (戰士)
                    || _pc.hasSkillEffect(L1SkillId.Warrior_Charge)   // 戰士衝鋒
                    || _pc.hasSkillEffect(L1SkillId.FIRESTUN)         // 火焰之暈
                    || _pc.hasSkillEffect(L1SkillId.TRUEFIRESTUN)) {  // 真·火焰之暈
                return false; // 返回 false 表示繼續排程，但不執行後續動作
            }

            // 遇見BOSS飛
            this.ConfirmTheBOSS(_pc);
            // 檢查自動購買物品
            this.CheckAutoBuyItem();
            // 檢查自動移動範圍
            this.CheckAutoMoveRange();
            // 檢查和設定當前目標
            _pc.checkTarget();
            if (_pc.is_now_target() == null) {
                _pc.searchTarget();  // 搜尋目標
            }
            if (_pc.is_now_target() == null) {
                _pc.noTarget();  // 無目標時的處理
            } else {
                _pc.onTarget();  // 有目標時的處理
            }
        } catch (final Exception e) {
            _log.error("Exception in AIProcess: " + this._pc.getName(), e);
            return true; // 出現異常時終止 AI
        }
        return false; // PC AI 繼續執行
    }

    private void CheckAutoMoveRange() {
        // 檢查定點掛機的移動範圍
        if (_pc.getLsLocX() > 0 && _pc.getLsLocY() > 0) {
            Point point = new Point(_pc.getLsLocX(), _pc.getLsLocY());
            int maxDistance = _pc.getLocation().getTileLineDistance(point);
            if (maxDistance >= _pc.getLsRange()) {
                PcTelDelayAI.onPcAITeleportDelay(_pc, L1Teleport.TELEPORT, _pc.getLsLocX(), _pc.getLsLocY(), _pc.getMapId());
            }
        }
    }

    /**
     * 遇見BOSS飛
     *
     */
    private boolean ConfirmTheBOSS(final L1PcInstance pc) {
        try {
            if (!pc.getBossfei()) {
                return false;
            }
            boolean ok = false;
            for (final L1Object objid : World.get().getVisibleObjects(pc, 5)) {
                if (objid instanceof L1NpcInstance) {
                    final L1NpcInstance _npc = (L1NpcInstance) objid;
                    if (_npc.getNpcTemplate().is_boss()) {
                        pc.autoRandomTeleport(0, 40);
                        ok = true;
                        break;
                    }
                }
            }
            return ok;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private void CheckAutoBuyItem() {
        try {
            if (_pc.get_autoBuyItem1() > 0 && _pc.get_autoBuyItemNum1() > 0) {
                // 第一組自動道具購買
                if (_pc.getInventory().getWeight240() >= 197) {
                    _pc.sendPackets(new S_SystemMessage("背包已滿，無法購買物品。"));
                    return;
                }
                if (!_pc.getInventory().checkItem(_pc.get_autoBuyItem1(), 1) && _pc.getInventory().checkItem(40308, (long) _pc.get_autoBuyItemAdena1() * _pc.get_autoBuyItemNum1())) {
                    _pc.getInventory().consumeItem(40308, (long) _pc.get_autoBuyItemAdena1() * _pc.get_autoBuyItemNum1());
                    _pc.getInventory().storeItem(_pc.get_autoBuyItem1(), _pc.get_autoBuyItemNum1());
                    L1Item item = ItemTable.get().getTemplate(_pc.get_autoBuyItem1());
                    _pc.sendPackets(new S_SystemMessage("購買了 " + item.getName() + " " + _pc.get_autoBuyItemNum1() + "個。"));
                }
                // 第二組自動道具購買
                if (_pc.get_autoBuyItem2() > 0 && _pc.get_autoBuyItemNum2() > 0) {
                    if (_pc.getInventory().getWeight240() >= 197) {
                        _pc.sendPackets(new S_SystemMessage("背包已滿，無法購買物品。"));
                        return;
                    }
                    if (!_pc.getInventory().checkItem(_pc.get_autoBuyItem2(), 1) && _pc.getInventory().checkItem(40308, (long) _pc.get_autoBuyItemAdena2() * _pc.get_autoBuyItemNum2())) {
                        _pc.getInventory().consumeItem(40308, (long) _pc.get_autoBuyItemAdena2() * _pc.get_autoBuyItemNum2());
                        _pc.getInventory().storeItem(_pc.get_autoBuyItem2(), _pc.get_autoBuyItemNum2());
                        L1Item item = ItemTable.get().getTemplate(_pc.get_autoBuyItem2());
                        _pc.sendPackets(new S_SystemMessage("購買了 " + item.getName() + " " + _pc.get_autoBuyItemNum2() + "個。"));
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Exception in CheckAutoBuyItem: ", e);
        }
    }
}
