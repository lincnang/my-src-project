package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_SystemMessage;

public class LawfulName extends ItemExecutor {
    /**
     * ID變色符
     */
    private LawfulName() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new LawfulName();
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
        if (!pc.isLawfulName()) {
            pc.setLawfulName(true);
            pc.sendPackets(new S_SystemMessage("\\fW開啟-正義滿對其它玩家顯示黃色名稱！"));
            L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
        } else {
            pc.setLawfulName(false);
            pc.sendPackets(new S_SystemMessage("\\fW關閉-正義滿對其它玩家顯示黃色名稱！"));
            L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
        }
    }
}
