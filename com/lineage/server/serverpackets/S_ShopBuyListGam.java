package com.lineage.server.serverpackets;

import com.lineage.data.event.GamblingSet;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Gambling;

import java.util.Map;

public class S_ShopBuyListGam extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopBuyListGam(L1PcInstance pc, L1NpcInstance npc, Map<Integer, L1Gambling> sellList) {
        writeC(S_SELL_LIST);
        writeD(npc.getId());
        if (sellList.isEmpty()) {
            writeH(0);
            return;
        }
        if (sellList.size() <= 0) {
            writeH(0);
            return;
        }
        writeH(sellList.size());
        for (Integer itemobjid : sellList.keySet()) {
            writeD(itemobjid);
            L1Gambling gam = (L1Gambling) sellList.get(itemobjid);
            int adena = (int) (GamblingSet.GAMADENA * gam.get_rate());
            writeD(adena);
        }
        writeH(0x0007); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
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
 * com.lineage.server.serverpackets.S_ShopBuyListGam JD-Core Version: 0.6.2
 */