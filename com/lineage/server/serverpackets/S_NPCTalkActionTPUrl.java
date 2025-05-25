package com.lineage.server.serverpackets;

import com.lineage.server.model.L1NpcTalkData;

public class S_NPCTalkActionTPUrl extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCTalkActionTPUrl(L1NpcTalkData cha, Object[] prices, int objid) {
        buildPacket(cha, prices, objid);
    }

    private void buildPacket(L1NpcTalkData npc, Object[] prices, int objid) {
        String htmlid = "";
        htmlid = npc.getTeleportURL();
        writeC(S_HYPERTEXT);
        writeD(objid);
        writeS(htmlid);
        writeH(1);
        writeH(prices.length);
        for (Object price : prices) {
            writeS(String.valueOf(((Integer) price).intValue()));
        }
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
 * com.lineage.server.serverpackets.S_NPCTalkActionTPUrl JD-Core Version: 0.6.2
 */