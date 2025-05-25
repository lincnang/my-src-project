// ✅ 昇華能力恢復工具類
package com.lineage.server.utils;

import com.lineage.server.datatables.ItemSublimationTable;
import com.lineage.server.datatables.lock.CharItemSublimationReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.templates.L1ItemSublimation;

public class SublimationRestoreUtil {

    /**
     * 在角色登入時呼叫，用於恢復所有裝備的昇華能力值
     * @param pc 玩家角色
     */
    public static void restoreAll(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            // 讀取對應昇華資料
            CharItemSublimation sub = CharItemSublimationReading.get().loadItem(item.getId());
            if (sub != null) {
                item.setSublimation(sub);

                // 根據昇華類型/等級取得能力表
                L1ItemSublimation data = ItemSublimationTable.getItemSublimation(sub);
                if (data != null) {
                    // 套用能力加成
                    if (data.getDamage() > 0) {
                        item.setUpdateDmgModifier((int) data.getDamage());
                    }
                    if (data.getMagicDmg() > 0) {
                        item.setUpdateSp((int) data.getMagicDmg());
                    }
                }
            }
        }
    }
}
