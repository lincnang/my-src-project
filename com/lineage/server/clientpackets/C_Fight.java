package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.FaceToFace;

public class C_Fight extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost()) {
                return;
            }
            L1PcInstance target = FaceToFace.faceToFace(pc);
            if ((target != null) && (!target.isParalyzed())) {
                if (pc.getFightId() != 0) {
                    pc.sendPackets(new S_ServerMessage(633));
                    return;
                }
                if (target.getFightId() != 0) {
                    target.sendPackets(new S_ServerMessage(634));
                    return;
                }
                pc.setFightId(target.getId());
                target.setFightId(pc.getId());
                target.sendPackets(new S_Message_YN(630, pc.getName()));
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
 * com.lineage.server.clientpackets.C_Fight JD-Core Version: 0.6.2
 */