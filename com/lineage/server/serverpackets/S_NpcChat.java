package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class S_NpcChat extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NpcChat(L1NpcInstance npc, String chat) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(npc.getId());
        writeS(npc.getNameId() + ": " + chat);
    }

    public S_NpcChat(int objid, String chat) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(objid);
        writeS(chat);
    }

    public S_NpcChat(L1NpcInstance npc, String chat, boolean name) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(npc.getId());
        writeS((name ? npc.getNameId() + ": " : "") + chat);
    }

    /**
     * 安息攻擊使用
     *
     * @param pc
     * @param chat
     */
    public S_NpcChat(L1NpcInstance npc, L1PcInstance pc, String chat) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(npc.getId());
        writeS(npc.getNameId() + ": " + chat + "! " + pc.getName());
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
 * com.lineage.server.serverpackets.S_NpcChat JD-Core Version: 0.6.2
 */