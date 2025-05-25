package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Inventory;
import com.lineage.server.thread.NpcAiThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * NPC AI Runnable
 *
 * @author daien
 */
public class NpcAI implements Runnable {
    private static final Log _log = LogFactory.getLog(NpcAI.class);
    private final L1NpcInstance _npc;

    /**
     * NPC AI Runnable
     *
     * @param npc
     */
    public NpcAI(final L1NpcInstance npc) {
        _npc = npc;
    }

    public void startAI() {
        NpcAiThreadPool.get().execute(this);
    }

    @Override
    public void run() {
        try {
            _npc.setAiRunning(true);
            while (!_npc._destroyed && !_npc.isDead() && _npc.getCurrentHp() > 0 && _npc.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_NONE) {
                while (_npc.isParalyzed() || _npc.isSleeped()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        _npc.setParalyzed(false);
                    }
                }
                // 中止AI的處理
                if (stopAIProcess()) {
                    break;
                }//*/
                try {
                    // 動作間隔時間
                    TimeUnit.MILLISECONDS.sleep(_npc.getSleepTime());
                } catch (final Exception e) {
                    break;
                }
            }
            //歸零技能使用計數
            _npc.mobSkill().resetAllSkillUseCount();
            do {
                try {
                    TimeUnit.MILLISECONDS.sleep(_npc.getSleepTime());
                } catch (final Exception e) {
                    break;
                }
            } while (_npc.isDeathProcessing());
            _npc.allTargetClear();
            _npc.setAiRunning(false);
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (final Exception e) {
            _log.error("NpcAI發生例外狀況: " + this._npc.getName(), e);
        }
    }

    /**
     * 中止AI的處理
     *
     * @return true:AI終了 false:AI續行
     */
    private boolean stopAIProcess() {
        try {
            _npc.setSleepTime(300);
            // 現有目標有效性檢查
            _npc.checkTarget();
            if ((_npc.is_now_target() == null) && (_npc.getMaster() == null)) {
                // 空っぽの場合はターゲットを探してみる
                // （主人がいる場合は自分でターゲットを探さない）
                _npc.searchTarget();
            }
            // 物品使用判斷
            _npc.onItemUse();
            if (_npc.is_now_target() == null) {//沒有攻擊目標
                // 檢查可撿取物品
                _npc.checkTargetItem();
                if (_npc.isPickupItem()) {//設定會撿東西
                    if (_npc.is_now_targetItem() == null) {//沒有撿取目標
                        // 可撿取物品探索
                        _npc.searchTargetItem();
                    }
                }
                if (_npc.is_now_targetItem() == null) {//沒有撿取目標
                    final boolean noTarget = _npc.noTarget();
                    if (noTarget) {
                        return true;
                    }
                } else {// 有要撿的東西
                    // onTargetItem();
                    final L1Inventory groundInventory = World.get().getInventory(_npc.is_now_targetItem().getX(), _npc.is_now_targetItem().getY(), _npc.is_now_targetItem().getMapId());
                    if (groundInventory.checkItem(_npc.is_now_targetItem().getItemId())) {
                        _npc.onTargetItem();
                    } else {
                        _npc._targetItemList.remove(_npc.is_now_targetItem());
                        _npc.set_now_targetItem(null);
                        _npc.setSleepTime(1000);
                        return false;
                    }
                }
            } else {//具有攻擊目標
                // NPC未躲藏的狀態
                if (_npc.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_NONE) {
                    _npc.onTarget();
                } else {
                    return true;
                }
            }
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (final Exception e) {
            _log.error("NpcAI發生例外狀況: " + this._npc.getName(), e);
        }
        return false; // NPC AI 繼續執行
    }
}
