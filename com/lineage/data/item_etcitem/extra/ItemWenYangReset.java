package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.storage.mysql.MySqlCharacterStorage;
import com.lineage.server.world.World;

/**
 * 紋樣重置道具
 * 用法：在 item_set 中第二欄帶參數 (例如 "0"=全重置, "1"~"6"=重置指定類型)
 */
public class ItemWenYangReset extends ItemExecutor {

    /** 0：重置全部，1~6：重置指定 type */
    private int resetType = 0;

    public static ItemExecutor get() {
        return new ItemWenYangReset();
    }

    /** 系統自動呼叫，讀取 item_set 的附加參數 */
    @Override
    public void set_set(String[] set) {
        // set[0] 是類別名，set[1] 之後才是自訂參數
        if (set != null && set.length > 1) {
            try {
                resetType = Integer.parseInt(set[1]);
            } catch (Exception ignored) { /* 保持預設 0 */ }
        }
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (resetType >= 1 && resetType <= 6) {
            boolean ok = WenYangJiLuTable.getInstance().resetSingle(pc, resetType);
            if (ok) {
                // 追加：清掉 characters 主表對應欄位
                new MySqlCharacterStorage().resetWenYangByType(pc, resetType);
                pc.sendPackets(new S_PacketBoxGree(23));
                pc.sendPackets(new S_SystemMessage("\\aB已重置紋樣強化與成功次數。"));
            } else {
                pc.sendPackets(new S_SystemMessage("沒有可重置的紋樣紀錄。"));
            }
        } else {
            int rows = WenYangJiLuTable.getInstance().resetAll(pc);
            if (rows > 0) {
                // 追加：清掉 characters 主表全部欄位
                new MySqlCharacterStorage().resetWenYangAll(pc);

                pc.sendPackets(new S_SystemMessage("已重置全部紋樣的強化與成功次數。"));
            } else {
                pc.sendPackets(new S_SystemMessage("沒有任何紋樣紀錄可重置。"));
            }
        }

        // 成功後消耗道具
        pc.getInventory().removeItem(item, 1);

        // TODO: 這裡可考慮補一個刷新面板封包 (讓玩家立即看到歸零效果)
    }

}