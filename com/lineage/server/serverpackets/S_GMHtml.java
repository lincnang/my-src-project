package com.lineage.server.serverpackets;

public class S_GMHtml extends ServerBasePacket {
    public S_GMHtml(int _objid, String html) {
        writeC(S_HYPERTEXT);
        writeD(_objid);
        writeS(html);
    }

    public byte[] getContent() {
        return getBytes();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_GMHtml JD-Core Version: 0.6.2
 */