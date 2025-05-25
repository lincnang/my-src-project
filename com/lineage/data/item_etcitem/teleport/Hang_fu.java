package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 掛機開關符
 */
public class Hang_fu extends ItemExecutor {
    private int _polyid = -1;

    /**
     *
     */
    private Hang_fu() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Hang_fu();
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
        // 自動掛機啟動
        if (!pc.getMap().isAutoBot()) {
            pc.sendPackets(new S_ServerMessage("\\aD 此地圖無法掛機.."));
            return;
        }
        if (!pc.isActivated()) {
            pc.startPcAI();
            pc.sendPackets(new S_ServerMessage("\\aD 自動狩獵已開啟，停止請再次雙擊"));
            if (_polyid != 0) {
                L1PolyMorph.doPoly(pc, _polyid, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC); // 變身
            }
            item.startEquipmentTimer(pc); // 開始物件計時
        } else {
            pc.stopPcAI();
            pc.sendPackets(new S_ServerMessage("\\aD 自動狩獵已停止。"));
            if (_polyid != 0) {
                L1PolyMorph.undoPoly(pc); // 停止變身
            }
            L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _polyid = Integer.parseInt(set[1]);
        } catch (Exception ignored) {
        }
    }
}
