package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 戒指擴充水晶
 *
 * @author terry0412
 */
public class EarringsExpansionGem extends ItemExecutor {  //src014
    /**
     *
     */
    private EarringsExpansionGem() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new EarringsExpansionGem();
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
        // 檢查可擴充的戒指欄位 by terry0412
        int count = 0;
        if ((pc.getEarringsExpansion() & 1) != 1) { // 左邊戒指
            count += 1;
        }
        // 沒有符合的欄位
        if (count == 0) {
            pc.sendPackets(new S_SystemMessage("無法再擴充耳環欄位。"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        // 異常值保護處理 (最多為1)
        if ((count & 1) == 1) {
            pc.setEarringsExpansion((byte) Math.min(pc.getRingsExpansion() + 1, 1));
            pc.sendPackets(new S_SystemMessage("成功擴充右邊耳環欄位。"));
        }
        // 更新戒指欄位擴充狀態  //src014
        CharacterTable.updateEarringsExpansion(pc);
    }
}
