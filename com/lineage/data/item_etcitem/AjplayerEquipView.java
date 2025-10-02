package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.world.World;

/**
 * 類似 Ajplayer 的「目標選取」道具：
 * 顯示被選取玩家當前配戴裝備（以商店清單樣式，含圖示與加乘）。
 * 使用後消耗 1 個道具。
 */
public class AjplayerEquipView extends ItemExecutor {

    public static ItemExecutor get() {
        return new AjplayerEquipView();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (data == null || data.length == 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        final int targetObjId = data[0];
        final L1Object target = World.get().findObject(targetObjId);
        if (!(target instanceof L1PcInstance)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        final L1PcInstance targetPc = (L1PcInstance) target;

        // 以 S_ShopSellList 商店清單樣式顯示目標玩家目前配戴裝備
        // anchor 物件使用 viewer 本人 objId，非 NPC 情境也能正常顯示
        pc.sendPackets(new S_ShopSellList(pc, pc.getId(), targetPc));

        // 消耗道具
        pc.getInventory().removeItem(item, 1);
    }
}


