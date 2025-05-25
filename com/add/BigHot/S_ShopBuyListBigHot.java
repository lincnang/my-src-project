package com.add.BigHot;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class S_ShopBuyListBigHot extends ServerBasePacket {
    public S_ShopBuyListBigHot(int objid, ArrayList<L1ItemInstance> list) {
        L1Object object = World.get().findObject(objid);
        if (!(object instanceof L1NpcInstance)) {
            return;
        }
        writeC(S_SELL_LIST);
        writeD(objid);
        writeH(list.size());
        for (L1ItemInstance item : list) {
            writeD(item.getId());
            int BigHotId = item.getGamNo();
            L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(BigHotId);
            if (BigHotInfo != null) {
                String A = BigHotInfo.get_number();
                String B = item.getStarNpcId();
                int AB = BigHotInfo.get_money1();
                int BC = BigHotInfo.get_count();
                if (BC != 0) {
                    AB /= BC;
                }
                int CD = BigHotInfo.get_money2();
                int DE = BigHotInfo.get_count1();
                if (DE != 0) {
                    CD /= DE;
                }
                int EF = BigHotInfo.get_money3();
                int FG = BigHotInfo.get_count2();
                if (FG != 0) {
                    EF /= FG;
                }
                int ch = 0;
                for (int a = 0; a < A.split(",").length; a++) {
                    String[] pk = B.split(",");
                    if (("," + A).contains("," + pk[a] + ",")) {
                        ch++;
                    }
                }
                if (ch >= 3) {
                    switch (ch) {
                        case 3:
                            writeD(50);
                            break;
                        case 4:
                            writeD(EF);
                            break;
                        case 5:
                            writeD(CD);
                            break;
                        case 6:
                            writeD(AB);
                    }
                }
            }
        }
    }

    public byte[] getContent() {
        return getBytes();
    }

    public String getType() {
        return "[S] S_ShopBuyList";
    }
}
