package com.lineage.server.serverpackets;

import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.gambling.Gambling;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.timecontroller.event.GamblingTime;

import java.util.Map;

public class S_ShopSellListGam extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopSellListGam(L1PcInstance pc, L1NpcInstance npc) {
        writeC(S_BUY_LIST);
        writeD(npc.getId());
        Gambling gambling = GamblingTime.get_gambling();
        Map<Integer, GamblingNpc> list = gambling.get_allNpc();
        if (list.size() <= 0) {
            writeH(0);
            return;
        }
        writeH(list.size());
        L1Item item = ItemTable.get().getTemplate(40309);
        int i = 0;
        for (GamblingNpc gamblingNpc : list.values()) {
            i++;
            pc.get_otherList().add_gamList(gamblingNpc, i);
            writeD(i);
            writeH(item.getGfxId());
            writeD(GamblingSet.GAMADENA);
            int no = GamblingTime.get_gamblingNo();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(gamblingNpc.get_npc().getNameId());
            stringBuilder.append(" [" + no + "-" + gamblingNpc.get_npc().getNpcId() + "]");
            writeS(stringBuilder.toString());
            writeC(0);
        }
        writeH(0x17d4); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
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
