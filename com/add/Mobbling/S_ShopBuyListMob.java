package com.add.Mobbling;

import com.add.L1Config;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class S_ShopBuyListMob extends ServerBasePacket {
    public S_ShopBuyListMob(int objid, ArrayList<L1ItemInstance> list) {
        L1Object object = World.get().findObject(objid);
        if (!(object instanceof L1NpcInstance)) {
            return;
        }
        writeC(S_SELL_LIST);
        writeD(objid);
        writeH(list.size());
        for (L1ItemInstance item : list) {
            writeD(item.getId());
            int MobId = item.getGamNo();
            L1Mobbling MobInfo = MobblingLock.create().getMobbling(MobId);
            if ((MobInfo == null) || (MobInfo.get_npcid() != item.getGamNpcId())) {
                continue;
            }
            writeD((int) (L1Config._2153 * MobInfo.get_rate()));
        }
        writeH(0x0007); // 7 = 金幣為單位 顯示總金額
    }

    public byte[] getContent() {
        return getBytes();
    }

    public String getType() {
        return "[S] S_ShopBuyList";
    }
}