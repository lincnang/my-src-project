package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 金幣賭博系統 by: 2014年8月20日13:28:53
 *
 * @author Administrator
 */
public class S_NpcChatPacket extends ServerBasePacket {
    private static final String S_NPC_CHAT_PACKET = "[S] S_NpcChatPacket";
    private byte[] _byte = null;

    public S_NpcChatPacket(L1NpcInstance npc, String chat, int type) {
        buildPacket(npc, chat, type);
    }

    public S_NpcChatPacket(L1NpcInstance npc, String chat) {
        buildPacket(npc, chat, 0);
    }

    public S_NpcChatPacket(L1PcInstance pc, String string) {
        buildPacket(pc, string, 0);
    }

    public S_NpcChatPacket(L1PcInstance pc, String string, int type) {
        buildPacket(pc, string, type);
    }

    private void buildPacket(L1NpcInstance npc, String chat, int type) {
        writeC(S_SAY_CODE);
        writeC(type); // Color
        switch (type) {
            case 0: // normal chat
                // writeC(S_OPCODE_NPCSHOUT); // Key is 16 , can use
                // desc-?.tbl
                writeD(npc.getId());
                writeS(npc.getName() + ": " + chat);
                break;
            case 2: // shout
                // writeC(type); // Color
                writeD(npc.getId());
                writeS("<" + npc.getName() + "> " + chat);
                break;
            case 3: // world chat
                // writeC(type); // XXX 白色
                writeD(npc.getId());
                writeS("[" + npc.getName() + "] " + chat);
                break;
            default:
                break;
        }
    }

    public void buildPacket(L1PcInstance pc, String chat, int type) {
        writeC(S_SAY_CODE);
        writeC(type); // Color
        switch (type) {
            //		case 0:
            //			writeD(pc.getId());
            //			writeS(chat);
            //			break;
            default:
                writeD(pc.getId());
                writeS(chat);
                break;
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
        return S_NPC_CHAT_PACKET;
    }
}
