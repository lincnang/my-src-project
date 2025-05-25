package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 紋樣積分
 * by 老爹
 */
public class Wenyang_jifen extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Wenyang_jifen.class);
    private int _count;

    /**
     *
     */
    private Wenyang_jifen() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Wenyang_jifen();
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
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (pc.isPrivateShop()) {
                pc.sendPackets(new S_SystemMessage("擺攤狀態下 無法使用"));
                return;
            }
            if (pc.isFishing()) {
                pc.sendPackets(new S_SystemMessage("釣魚狀態下 無法使用"));
                return;
            }
            pc.setWenyangJiFen(pc.getWenyangJiFen() + _count);//獲得積分數量
            pc.sendPackets(new S_SystemMessage("獲得紋樣積分【" + _count + "】"));
            pc.save();//更新角色數據
            pc.getInventory().removeItem(item, 1L);//刪除道具
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _count = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
