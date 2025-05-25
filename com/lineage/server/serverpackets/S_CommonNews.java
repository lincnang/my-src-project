package com.lineage.server.serverpackets;

import com.lineage.list.Announcements;

import java.util.ArrayList;

public class S_CommonNews extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CommonNews() {
        ArrayList<String> info = Announcements.get().list();
        writeC(S_NEWS);
        StringBuilder messagePack = new StringBuilder();
        for (String message : info) {
            messagePack.append(message + "\n");
        }
        writeS(messagePack.toString());
    }

    public S_CommonNews(String s) {
        writeC(S_NEWS);
        writeS(s);
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
 * com.lineage.server.serverpackets.S_CommonNews JD-Core Version: 0.6.2
 */