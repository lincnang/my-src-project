package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 類描述：VIP資格<br>
 * 創建時間：2016年12月25日 上午12:52:27<br>
 * 修改備註：<br>
 *
 * @version<br>
 */
@SuppressWarnings("unused")
public class Vip extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Vip.class);
    // vip等級
    private int _level;
    // vip天數
    private int _daycount;

    /**
     *
     */
    private Vip() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Vip();
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
        try {
            // 例外狀況:物件為空
            if (item == null) {
                return;
            }
            // 例外狀況:人物為空
            if (pc == null) {
                return;
            }
            // 已經擁有會員資格
            //if (pc.getVipStartTime() != null && pc.getVipEndTime() != null) {
            //pc.sendPackets(new S_SystemMessage("您還有剩餘VIP時間."));
            //return;
            //}
            // 刪除物件
            pc.getInventory().removeItem(item);
            pc.sendPackets(new S_SystemMessage("\\aE 您已是VIP.請重新登入."));
            // 給予VIP資格
            pc.addVipStatus(_daycount, _level);
            pc.saveVip();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _level = Integer.parseInt(set[1]);
            _daycount = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
    }
}
