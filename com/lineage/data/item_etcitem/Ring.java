package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

public class Ring extends ItemExecutor {
    public static ItemExecutor get() {
        return new Ring();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        L1PcInstance partner = null;
        boolean partner_stat = false;
        if (QuestMapTable.get().isQuestMap(pc.getMapId())) {// 無法傳送至任務地圖
            return;
        }
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if (pc.getPartnerId() != 0) {
            partner = (L1PcInstance) World.get().findObject(pc.getPartnerId());
            if ((partner != null) && (partner.getPartnerId() != 0) && (pc.getPartnerId() == partner.getId()) && (partner.getPartnerId() == pc.getId())) {
                partner_stat = true;
            }
        } else {
            pc.sendPackets(new S_ServerMessage(662));
            return;
        }
        if (item.getId() != pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) {// 道具objid不等於任務進度
            pc.sendPackets(new S_ServerMessage(79)); // \f1沒有任何事情發生。
            return;
        } else if (item.getChargeCount() <= 0) { // 次數為0
            pc.sendPackets(new S_ServerMessage(791)); // 需要補充結婚戒指的能量
            return;
        }
        if (partner_stat) {
            boolean castle_area = L1CastleLocation.checkInAllWarArea(partner.getX(), partner.getY(), partner.getMapId());
            if (((partner.getMapId() == 0) || (partner.getMapId() == 4) || (partner.getMapId() == 304)) && (!castle_area)) {
                L1Teleport.teleport(pc, partner.getX(), partner.getY(), partner.getMapId(), 5, true);
                if (item.getItemId() >= 40903 && item.getItemId() <= 40908) {
                    item.setChargeCount(item.getChargeCount() - 1);
                    pc.getInventory().updateItem(item, 128);
                }
            } else {
                pc.sendPackets(new S_ServerMessage(547));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(546));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Ring JD-Core Version: 0.6.2
 */