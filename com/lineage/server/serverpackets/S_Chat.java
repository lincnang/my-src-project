package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;

public class S_Chat extends ServerBasePacket {
    private byte[] _byte = null;

    // ✅ 玩家使用一般聊天（含 objectId，可顯示頭上泡泡）
    public S_Chat(L1PcInstance pc, String chat) {
        buildPacket(pc, chat);
    }

    // ✅ NPC 說話（任務、事件用）
    public S_Chat(L1NpcInstance npc, String chat) {
        writeC(S_SAY);
        writeC(0); // chatType = 0
        writeD(npc.isInvisble() ? 0 : npc.getId());
        writeS(npc.getNameId() + ": " + chat);
    }

    // ✅ 泛用物件說話（可用於 clone、變身等）
    public S_Chat(L1Object object, String chat) {
        writeC(S_SAY);
        writeC(0); // chatType = 0
        writeD(object.getId());
        writeS(chat);
    }

    // ⚠️ 純字串說話（無角色 ID，不會在頭上顯示氣泡）
    public S_Chat(String viewName, String chat) {
        writeC(S_SAY);
        writeC(0);
        writeD(0); // 若要支援點選名稱可改傳入角色 ID
        writeS(viewName + ": " + chat);
    }

    // ✅ 封包核心邏輯：使用玩家物件構建封包
    private void buildPacket(L1PcInstance pc, String chat) {
        writeC(S_SAY);
        writeC(0); // chatType = 0：一般說話
        writeD(pc.isInvisble() ? 0 : pc.getId());

        // ✅ 使用 getViewName() 支援排行榜前綴顯示
        writeS(pc.getViewName() + ": " + chat);
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
