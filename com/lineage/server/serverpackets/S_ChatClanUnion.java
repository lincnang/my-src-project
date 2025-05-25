package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatClanUnion extends ServerBasePacket {
    private byte[] _byte = null;

    // ✅ 原始建構子（保留）
    public S_ChatClanUnion(L1PcInstance pc, String chat) {
        buildPacket(pc.getName(), chat);
    }

    // ✅ 新增支援排行榜稱號的建構子
    public S_ChatClanUnion(String displayName, String chat) {
        buildPacket(displayName, chat);
    }

    // ✅ 共用邏輯
    private void buildPacket(String name, String chat) {
        writeC(S_MESSAGE);
        writeC(4);
        writeS("{{" + name + "}} " + chat);
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
