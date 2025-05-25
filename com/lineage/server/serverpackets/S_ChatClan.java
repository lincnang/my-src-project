package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatClan extends ServerBasePacket {
    private byte[] _byte = null;

    // ✅ 原始建構子（不動）
    public S_ChatClan(L1PcInstance pc, String chat) {
        buildPacket(pc.getName(), chat);
    }

    // ✅ 新增建構子：支援傳入自定義名稱（排行榜稱號）
    public S_ChatClan(String displayName, String chat) {
        buildPacket(displayName, chat);
    }

    // ✅ 重構共用封包寫入邏輯
    private void buildPacket(String name, String chat) {
        writeC(S_MESSAGE);
        writeC(4);
        writeS("{" + name + "} " + chat);
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
