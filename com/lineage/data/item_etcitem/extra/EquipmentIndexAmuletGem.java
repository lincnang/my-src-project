package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 符文擴充水晶 642181
 *
 * @author terry0412
 */
public class EquipmentIndexAmuletGem extends ItemExecutor { // src1003
    /**
     *
     */
    private EquipmentIndexAmuletGem() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new EquipmentIndexAmuletGem();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 檢查可擴充的符文欄位 by terry0412
        int count = 0;
        if ((pc.getEquipmentIndexAmulet() & 1) != 1) {
            count += 1;
        }
        // 沒有符合的欄位
        if (count == 0) {
            pc.sendPackets(new S_SystemMessage("無法再擴充手環欄位。"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        // 異常值保護處理 (最多為1)
        if ((count & 1) == 1) {
            pc.setEquipmentIndexAmulet((byte) Math.min(pc.getEquipmentIndexAmulet() + 1, 1));
            pc.sendPackets(new S_SystemMessage("成功擴充手環欄位。"));
        }
        // 更新符文欄位擴充狀態  src1003
        CharacterTable.updateEquipmentIndexAmulet(pc);
    }
}
