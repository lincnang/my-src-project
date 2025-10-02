package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ChooseBoxCache;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.templates.L1ChooseBoxOption;
import com.lineage.server.templates.L1ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 自選寶箱（以 S_ShopSellList 呈現選單）
 * 讀取 etcitem_box寶箱自訂選擇，建立一次性清單讓玩家選擇一項領取
 */
public class ChooseBoxItem extends ItemExecutor {

    public static ItemExecutor get() {
        return new ChooseBoxItem();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int boxItemId = item.getItemId();
        final List<L1ChooseBoxOption> opts = ChooseBoxCache.get().getOptions(boxItemId);
        if (opts == null || opts.isEmpty()) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        final List<L1ShopItem> list = new ArrayList<>(opts.size());
        int index = 0;
        for (L1ChooseBoxOption o : opts) {
            // price=0, packCount=數量, enchant=強化
            L1ShopItem s = new L1ShopItem(o.getItemId(), 0, Math.max(1, o.getCount()), Math.max(0, o.getEnchant()), -1);
            list.add(s);
            index++;
            pc.get_otherList().add_chooseData(index, o);
        }
        // 用玩家自身 objId 當作錨點
        pc.sendPackets(new S_ShopSellList(pc, pc.getId(), list));
        // 設置臨時模式碼，令 C_Result 分流至自選寶箱處理
        pc.setTemporary(99);
        // 暫存本次選單來源物件 OBJID 以便兌換時刪除此寶箱
        pc.set_mode_id(item.getId());
    }
}


