package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ChatParty;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class C_ChatParty extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while (pc.isTeleport()) {
                return;
            }
            int type = readC();
            switch (type) {
                case 0:
                    String name = readS();
                    if (!pc.isInChatParty()) {
                        pc.sendPackets(new S_ServerMessage(425));
                        return;
                    }
                    if (!pc.getChatParty().isLeader(pc)) {
                        pc.sendPackets(new S_ServerMessage(427));
                        return;
                    }
                    L1PcInstance targetPc = World.get().getPlayer(name);
                    L1DeInstance de = null;
                    if (targetPc == null) {
                        de = C_ChatWhisper.getDe(name);
                    }
                    if ((targetPc == null) && (de == null)) {
                        pc.sendPackets(new S_ServerMessage(109, name));
                        return;
                    }
                    if (pc.getId() == targetPc.getId()) {
                        return;
                    }
                    for (L1PcInstance member : pc.getChatParty().getMembers()) {
                        if (member.getName().toLowerCase().equals(name.toLowerCase())) {
                            pc.getChatParty().kickMember(member);
                            return;
                        }
                    }
                    pc.sendPackets(new S_ServerMessage(426, name));
                    break;
                case 1:
                    if (pc.isInChatParty()) {
                        pc.getChatParty().leaveMember(pc);
                    }
                    break;
                case 2:
                    L1ChatParty chatParty = pc.getChatParty();
                    if (pc.isInChatParty()) {
                        pc.sendPackets(new S_Party("party", pc.getId(), chatParty.getLeader().getName(), chatParty.getMembersNameList()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(425));
                    }
                    break;
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_ChatParty JD-Core Version: 0.6.2
 */