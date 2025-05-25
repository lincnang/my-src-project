package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatParty2 extends ServerBasePacket {
    private byte[] _byte = null;

    // ✅ 原本的建構子
    public S_ChatParty2(L1PcInstance pc, String chat) {
        buildPacket(pc.isInvisble() ? 0 : pc.getId(), pc.getName(), chat);
    }

    // ✅ 新增的建構子：支援排行榜稱號
    public S_ChatParty2(String displayName, String chat) {
        buildPacket(0, displayName, chat);
    }

    // ✅ 封裝共同處理邏輯
    private void buildPacket(int id, String name, String chat) {
        writeC(S_SAY);
        writeC(14);
        writeD(id);
        writeS("(" + name + ") " + chat);
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
