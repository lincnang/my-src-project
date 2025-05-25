package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import william.Honor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HonorItem extends ItemExecutor {

    private static final Log _log = LogFactory.getLog(HonorItem.class);
    private int _r = 0;
    private String _s = "";

    /**
     *
     */
    private HonorItem() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new HonorItem();
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
        pc.setHonor(pc.getHonor() + _r);
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_ServerMessage("你的" + _s + "上升了" + _r + "點。"));
        try {
            Honor.getInstance().checkHonor(pc, false,false);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        // ★ 加在這裡 → 確保加完聲望、處理完稱號後才存檔
        try {
            pc.save();
        } catch (final Exception e) {
            _log.error("角色存檔失敗: " + e.getLocalizedMessage(), e);
        }
        pc.getInventory().removeItem(item, 1); // 最後才扣道具
    }
    @Override
    public void set_set(String[] set) {
        try {
            _r = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _s = String.valueOf(set[2]);

        } catch (Exception e) {
        }
    }
}
