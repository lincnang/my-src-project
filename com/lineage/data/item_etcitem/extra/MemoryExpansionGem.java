package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 擴充記憶水晶
 */
public class MemoryExpansionGem extends ItemExecutor {
    /**
     *
     */
    private MemoryExpansionGem() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new MemoryExpansionGem();
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
        if (pc.getMark_count() < 100) { // 日版記憶座標
            //if (pc.getMark_count() < ConfigAlt.CHAR_BOOK_MAX_CHARGE) {
            int booksize = pc.getMark_count() + 10;
            pc.setMark_count(booksize);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_BOOKMARK_SPACE, booksize));
            pc.getInventory().removeItem(item, 1);
            try {
                pc.save();
            } catch (Exception e) {
                // TODO 自動生成的 catch 塊
                e.printStackTrace();
            }
        } else {
            pc.sendPackets(new S_ServerMessage(2930));
        }
    }
}
