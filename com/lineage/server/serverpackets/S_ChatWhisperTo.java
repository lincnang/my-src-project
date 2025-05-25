package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatWhisperTo extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ChatWhisperTo(L1PcInstance pc, String chat) {
        buildPacket(pc, chat);
    }

    public S_ChatWhisperTo(L1DeInstance de, String chat) {
        writeC(S_MESSAGE);
        writeC(9);
        writeS("-> (" + de.getNameId() + ") " + chat);
    }

    /**
     * 管理器調用密語
     *
     * @param from
     * @param chat
     */
    public S_ChatWhisperTo(String from, String chat) {
        writeC(S_OPCODE_WHISPERCHAT);
        writeS(from);
        writeS(chat);
    }

    private void buildPacket(L1PcInstance pc, String chat) {
        writeC(S_MESSAGE);
        writeC(9);
        writeS("-> (" + pc.getName() + ") " + chat);
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
 * com.lineage.server.serverpackets.S_ChatWhisperTo JD-Core Version: 0.6.2
 */