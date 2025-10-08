package com.lineage.server.model.Instance;

import com.lineage.echo.ClientExecutor;
import com.lineage.config.Config;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.pcdelay.PcTelDelayAI;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class PcAI implements Runnable {
    private static final Log _log = LogFactory.getLog(PcAI.class);
    // 移除未使用的隨機數產生器，避免警告
    private final L1PcInstance _pc;
    private long _lastBossTeleportMs = 0L;
    private String _lastDebugMsg = null;
    private long _lastDebugAt = 0L;

    public PcAI(final L1PcInstance pc) {
        _pc = pc;
    }

    public void startAI() {
        // 啟動 AI 執行緒
        GeneralThreadPool.get().execute(this);
    }

    @Override
    public void run() {
        try {
            _pc.setAiRunning(true);  // 設定 AI 正在運行
            dbg("PcAI 啟動");
            while (_pc.getMaxHp() > 0) {
                // 當角色被睡眠或癱瘓時，暫停 200 毫秒
                while (_pc.isSleeped() || _pc.isParalyzedX() || _pc.isParalyzed()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                // 執行 AI 處理，若返回 true，則終止 AI
                if (AIProcess()) {
                    dbg("PcAI 停止條件觸發，結束執行");
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(Math.max(200, _pc.getSleepTimeAI()));
                } catch (final Exception e) {
                    _log.error("Error in move delay: ", e);
                    dbg("PcAI move delay 例外: " + e.getClass().getSimpleName());
                    break;
                }
            }
            // 結束前不需要在死亡狀態下持續等待，直接停止 AI
            _pc.stopPcAI();  // 停止 AI
            dbg("PcAI 已停止");
        } catch (final Exception e) {
            _log.error("Exception in PcAI: " + this._pc.getName(), e);
            dbg("PcAI 例外: " + e.getClass().getSimpleName());
        }
    }

    private boolean AIProcess() {
        try {
            // 檢查角色狀態，如果不符合條件則終止 AI（並提示原因）
            if (_pc == null) {
                _log.warn("PcAI stop: pc is null");
                return true;
            }
            if (!_pc.isActivated()) {
                dbg("停止：未啟用");
                return true;
            }
            if (_pc.isDead() || _pc.getCurrentHp() <= 0) {
                dbg("停止：死亡或HP≤0 (HP=" + _pc.getCurrentHp() + ")");
                return true;
            }
            // 連線檢查 (避免 NPE)
            final ClientExecutor conn = _pc.getNetConnection();
            if (conn == null) {
                dbg("停止：連線不存在");
                return true;
            }
            // 地圖是否允許自動（放寬：僅在地圖為空時停止）
            if (_pc.getMap() == null) {
                dbg("停止：地圖資訊不存在");
                return true;
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
                dbg("無目標 -> searchTarget/noTarget");
                _pc.searchTarget();  // 搜尋目標
            }
            if (_pc.is_now_target() == null) {
                dbg("無目標 -> noTarget");
                _pc.noTarget();  // 無目標時的處理
            } else {
                dbg("有目標 -> onTarget");
                _pc.onTarget();  // 有目標時的處理
            }
        } catch (final Exception e) {
            _log.error("Exception in AIProcess: " + this._pc.getName(), e);
            // 可恢復錯誤：不要終止 AI，讓下一循環重試
            return false;
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
            // 傳送冷卻（避免頻繁觸發）
            long now = System.currentTimeMillis();
            if (now - _lastBossTeleportMs < 5000L) { // 5 秒冷卻
                dbg("BOSS偵測：冷卻中");
                return false;
            }
            boolean ok = false;
            for (final L1Object objid : World.get().getVisibleObjects(pc, 5)) {
                if (objid instanceof L1NpcInstance) {
                    final L1NpcInstance _npc = (L1NpcInstance) objid;
                    if (_npc.getNpcTemplate().is_boss()) {
                        pc.autoRandomTeleport(0, 40);
                        ok = true;
                        _lastBossTeleportMs = now;
                        dbg("BOSS偵測：觸發傳送");
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
            // 第一組
            if (_pc.get_autoBuyItem1() > 0 && _pc.get_autoBuyItemNum1() > 0) {
                if (_pc.getInventory().getWeight240() >= 197) {
                    dbg("自動購買1：背包已滿");
                } else if (!_pc.getInventory().checkItem(_pc.get_autoBuyItem1(), 1) && _pc.getInventory().checkItem(40308, (long) _pc.get_autoBuyItemAdena1() * _pc.get_autoBuyItemNum1())) {
                    _pc.getInventory().consumeItem(40308, (long) _pc.get_autoBuyItemAdena1() * _pc.get_autoBuyItemNum1());
                    _pc.getInventory().storeItem(_pc.get_autoBuyItem1(), _pc.get_autoBuyItemNum1());
                    L1Item item = ItemTable.get().getTemplate(_pc.get_autoBuyItem1());
                    _pc.sendPackets(new S_SystemMessage("購買了 " + item.getName() + " " + _pc.get_autoBuyItemNum1() + "個。"));
                }
            }
            // 第二組
            if (_pc.get_autoBuyItem2() > 0 && _pc.get_autoBuyItemNum2() > 0) {
                if (_pc.getInventory().getWeight240() >= 197) {
                    dbg("自動購買2：背包已滿");
                } else if (!_pc.getInventory().checkItem(_pc.get_autoBuyItem2(), 1) && _pc.getInventory().checkItem(40308, (long) _pc.get_autoBuyItemAdena2() * _pc.get_autoBuyItemNum2())) {
                    _pc.getInventory().consumeItem(40308, (long) _pc.get_autoBuyItemAdena2() * _pc.get_autoBuyItemNum2());
                    _pc.getInventory().storeItem(_pc.get_autoBuyItem2(), _pc.get_autoBuyItemNum2());
                    L1Item item = ItemTable.get().getTemplate(_pc.get_autoBuyItem2());
                    _pc.sendPackets(new S_SystemMessage("購買了 " + item.getName() + " " + _pc.get_autoBuyItemNum2() + "個。"));
                }
            }
        } catch (Exception e) {
            _log.error("Exception in CheckAutoBuyItem: ", e);
        }
    }

    private void dbg(final String msg) {
        try {
            if (!Config.DEBUG) {
                return;
            }
            final long now = System.currentTimeMillis();
            if (!msg.equals(_lastDebugMsg) || (now - _lastDebugAt) > 1000L) {
                if (_pc != null) {
                    _pc.sendPackets(new S_SystemMessage("[PCAI] " + msg));
                }
                _lastDebugMsg = msg;
                _lastDebugAt = now;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("[PCAI] " + (_pc != null ? _pc.getName() : "<null>") + ": " + msg);
            }
        } catch (Throwable ignored) {
        }
    }
}
