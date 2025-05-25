package com.lineage.server.model.Instance;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.pcdelay.PcTelDelayAI;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.PcAutoThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PcAI implements Runnable {
    private static final Log _log = LogFactory.getLog(PcAI.class);
    private static final Random _random = new Random();
    private final L1PcInstance _pc;

    public PcAI(final L1PcInstance pc) {
        _pc = pc;
    }

    public void startAI() {
        // 啟動 AI 執行緒
        PcAutoThreadPool.get().execute(this);
    }

    @Override
    public void run() {
        try {
            _pc.setAiRunning(true);  // 設定 AI 正在運行
            while (_pc.getMaxHp() > 0) {
                // 當角色被睡眠或癱瘓時，暫停 200 毫秒
                while (_pc.isSleeped() || _pc.isParalyzedX() || _pc.isParalyzed()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                // 執行 AI 處理，若返回 true，則終止 AI
                if (AIProcess()) {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(Math.max(200, _pc.getSleepTimeAI()));
                } catch (final Exception e) {
                    _log.error("Error in move delay: ", e);
                    break;
                }
            }
            do {
                try {
                    TimeUnit.MILLISECONDS.sleep(Math.max(200, _pc.getSleepTimeAI()));
                } catch (final Exception e) {
                    _log.error("Error in attack delay: ", e);
                    break;
                }
            } while (_pc.isDead());
            _pc.stopPcAI();  // 停止 AI
        } catch (final Exception e) {
            _log.error("Exception in PcAI: " + this._pc.getName(), e);
        }
    }

    private boolean AIProcess() {
        try {
            // 檢查角色狀態，如果不符合條件則終止 AI
            if (_pc == null || _pc.getOnlineStatus() == 0 || _pc.isDead() || _pc.getCurrentHp() <= 0 || _pc.isGhost() || !_pc.isActivated() || _pc.getNetConnection().getIp() == null || !_pc.getMap().isAutoBot()) {
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
     * @param pc
     * @return
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
