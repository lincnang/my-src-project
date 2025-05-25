package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.shop.L1AssessedItem;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.world.World;

import java.util.List;

public class S_ShopBuyList extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopBuyList(int objid, L1PcInstance pc) {
        L1Object object = World.get().findObject(objid);
        if (!(object instanceof L1NpcInstance)) {
            return;
        }
        L1NpcInstance npc = (L1NpcInstance) object;
        int npcId = npc.getNpcTemplate().get_npcId();
        L1Shop shop = ShopTable.get().get(npcId);
        if (shop == null) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        List<L1AssessedItem> assessedItems = shop.assessItems(pc.getInventory());
        if (assessedItems.isEmpty()) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        if (assessedItems.size() <= 0) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        writeC(S_SELL_LIST);
        writeD(objid);
        writeH(assessedItems.size());
        for (L1AssessedItem item : assessedItems) {
            writeD(item.getTargetId());
            writeD(item.getAssessedPrice());
        }
        writeH(0x0007); // 7 = 金幣為單位 顯示總金額
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_ShopBuyList JD-Core Version: 0.6.2
 */