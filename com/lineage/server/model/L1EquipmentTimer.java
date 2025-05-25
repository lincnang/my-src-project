package com.lineage.server.model;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.TimerTask;

/**
 * 計時物件使用時間軸
 *
 * @author dexc
 */
public class L1EquipmentTimer extends TimerTask {
    private final L1PcInstance _pc;// 擁有者
    private final L1ItemInstance _item;// 計時物件

    public L1EquipmentTimer(final L1PcInstance pc, final L1ItemInstance item) {
        this._pc = pc;
        this._item = item;
    }

    @Override
    public void run() {
        if ((this._item.getRemainingTime() - 1) >= 0) {// 還有剩餘時間
            this._item.setRemainingTime(this._item.getRemainingTime() - 1);
            this._pc.getInventory().updateItem(this._item, L1PcInventory.COL_REMAINING_TIME);
            String classname = _item.getItem().getclassname();
            if (!_pc.isActivated() && classname.startsWith("teleport.Hang_fu")) { // 掛機符
                this.cancel();
            }
            if (this._pc.getOnlineStatus() == 0) {
                this.cancel();
            }
        } else {// 使用時間已結束
            // this._pc.getInventory().removeItem(this._item, 1);
            _pc.sendPackets(new S_ServerMessage(_item.getItem().getNameId() + "的使用時間已結束。"));
            if (_item.isEquipped()) {// 裝備中道具
                _pc.getInventory().setEquipped(_item, false);// 解除裝備
            }
            if (_pc.getDoll(_item.getId()) != null) {// 使用中娃娃
                _pc.getDoll(_item.getId()).deleteDoll();// 娃娃收回
            }
            String classname = _item.getItem().getclassname();
            if (classname.startsWith("teleport.Hang_fu")) { // 掛機符
                if (_pc.isActivated()) {
                    _pc.setActivated(false);
                    _pc.sendPackets(new S_ServerMessage("\\aD 自動狩獵道具使用時間已結束，停止自動狩獵。"));
                    L1PolyMorph.undoPoly(_pc);//停止變身
                    L1Teleport.teleport(_pc, _pc.getLocation(), _pc.getHeading(), false);
                }
            }
            this.cancel();
        }
    }
}
