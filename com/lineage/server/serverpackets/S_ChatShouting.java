package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatShouting extends ServerBasePacket {
    private byte[] _byte = null;

    // ✅ 建構子：玩家使用 shout（含角色 ID，會顯示在頭上）
    public S_ChatShouting(L1PcInstance pc, String chat) {
        buildPacket(pc, chat);
    }

    // ✅ 建構子：NPC 使用 shout（用於喊話型 NPC）
    public S_ChatShouting(L1NpcInstance npc, String chat) {
        writeC(S_SAY);
        writeC(2);
        writeD(npc.isInvisble() ? 0 : npc.getId());
        writeS("<" + npc.getNameId() + "> " + chat);
        writeH(npc.getX());
        writeH(npc.getY());
    }

    // ✅ 建構子：純名稱 shout（無 ID，不會顯示泡泡，保留用）
    public S_ChatShouting(String displayName, String chat) {
        writeC(S_SAY);
        writeC(2);
        writeD(0); // 無角色 ID，僅聊天窗顯示
        writeS("<" + displayName + "> " + chat);
        writeH(0);
        writeH(0);
    }

    // ✅ 核心封包產生邏輯（角色 shout）
    private void buildPacket(L1PcInstance pc, String chat) {
        writeC(S_SAY);
        writeC(2); // chatType = 2：大喊
        writeD(pc.isInvisble() ? 0 : pc.getId());

        if (pc.isProtector()) {
            writeS("<**守護者**> " + chat);
        } else {
            // ✅ 使用 getViewName()，顯示排行榜前綴名稱
            writeS("<" + pc.getViewName() + "> " + chat);
        }

        writeH(pc.getX());
        writeH(pc.getY());
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
