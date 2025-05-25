package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.BroadcastController;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

import static com.lineage.server.model.skill.L1SkillId.BROADCAST_CARD;

/**
 * 廣播卡 使用後 畫面上方會出現廣播訊息 世界頻道喊話(&)出現的字顏色正常白色 延遲發話間隔時間 5秒
 *
 * @author terry0412
 */
public class BroadcastCard extends ItemExecutor {
    /**
     *
     */
    private BroadcastCard() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new BroadcastCard();
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
        // 是否關閉中
        if (BroadcastController.getInstance().isStop()) {
            pc.sendPackets(new S_SystemMessage("廣播系統目前已被管理員關閉。"));
            // GM可以隨時開啟廣播系統
            if (pc.isGm()) {
                pc.sendPackets(new S_SystemMessage("需要重新開啟廣播系統請輸入  \"開啟\"。"));
                pc.setSkillEffect(BROADCAST_CARD, 30000);
            }
            return;
        }
        // 顯示提示訊息
        pc.sendPackets(new S_SystemMessage("請在30秒內輸入想要廣播的訊息 (目前已有 " + BroadcastController.getInstance().getMsgSize() + "筆訊息等候廣播)"));
        pc.setSkillEffect(BROADCAST_CARD, 30000);
    }
}
