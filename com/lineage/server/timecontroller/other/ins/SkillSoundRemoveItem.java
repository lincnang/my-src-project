package com.lineage.server.timecontroller.other.ins;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 輔助(自動刪物)
 */
public class SkillSoundRemoveItem extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundRemoveItem.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundRemoveItem(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
            if (_pc.isDead()) {
                return;
            }
            if (_pc.isParalyzedX()) {
                return;
            }
            // 是否鬼魂/傳送/商店
            if (_pc.isParalyzedX1(_pc)) {
                return;
            }
            if (!_pc.isAutoRemoveItem()) {
                return;
            }
            synchronized (this) {
                // 輔助(自動刪物)
                if (_pc.isAutoRemoveItem()) {
                    // 取回PC刪物名單
                    for (final Object itemObject : _pc.getRemoveItemInventory().getItems()) {
                        // 取回PC身上物品名單
                        for (final L1ItemInstance item : _pc.getInventory().getItems()) {
                            final L1ItemInstance RemoveItemitem = (L1ItemInstance) itemObject;
                            if (item.getItem().getItemId() == RemoveItemitem.getItemId() && !item.isEquipped() && item.getEnchantLevel() == 0 && !item.getItem().isCantDelete()) {
                                _pc.getInventory().removeItem(item);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
