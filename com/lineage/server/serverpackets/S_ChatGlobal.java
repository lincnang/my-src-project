package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 廣播頻道
 *
 * @author dexc
 */
public class S_ChatGlobal extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 廣播頻道
     *
     */
    public S_ChatGlobal(final L1PcInstance pc, final String chat) {
        buildPacket(pc, chat);
    }

    /**
     * NPC對話輸出
     *
     */
    public S_ChatGlobal(final L1NpcInstance npc, final String chat) {
        writeC(S_MESSAGE);
        writeC(0x03);
        writeS("[" + npc.getNameId() + "] " + chat);
    }
    /**
     * 排行榜稱號版本：使用指定名稱顯示
     */
    public S_ChatGlobal(final String displayName, final String chat) {
        writeC(S_MESSAGE);
        writeC(0x03);
        writeS("[" + displayName + "] " + chat);
    }
    /**
     * 共用廣播頻道
     *
     */
    public S_ChatGlobal(final String chat) {
        writeC(S_MESSAGE);
        writeC(0x03);
        writeS(chat);
    }

    private void buildPacket(final L1PcInstance pc, final String chat) {
        writeC(S_MESSAGE);
        writeC(0x03);
        if (pc.isGm()) {
            writeS("\\f3[********] " + chat);
            //} else if (pc.isProtector()) { //2017/04/23
            //	writeS("[**守護者**] " + chat);
        } else {
            writeS("[" + pc.getName() + "] " + chat);
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}