package com.add.Mobbling;

import com.add.L1Config;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;

import java.io.IOException;
import java.util.ArrayList;

public class S_ShopSellListMob extends ServerBasePacket {
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();

    public S_ShopSellListMob(int objId, ArrayList<int[]> Mobs) {
        writeC(S_BUY_LIST);
        writeD(objId);
        writeH(Mobs.size());
        L1ItemInstance dummy = new L1ItemInstance();
        if (_Mob.get_isStart()) {
            return;
        }
        for (int i = 0; i < Mobs.size(); ++i) {
            int[] info = (int[]) Mobs.get(i);
            L1Item item = ItemTable.get().getTemplate(L1Config._2155);
            int price = (int) L1Config._2153;
            L1Npc npc = NpcTable.get().getTemplate(info[0]);
            writeD(i);
            writeH(item.getGfxId());
            writeD(price);
            writeS(npc.get_nameid() + "(" + info[1] + "-" + info[0] + ")");
            dummy.setItem(item);
            L1ItemStatus itemInfo = new L1ItemStatus(item);
            byte[] status = itemInfo.getStatusBytes(true).getBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
        writeH(7);
    }

    public byte[] getContent() throws IOException {
        return this._bao.toByteArray();
    }
}