package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1SignboardInstance;

public class S_NPCPack_Signboard extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_Signboard(L1SignboardInstance signboard) {
        writeC(S_PUT_OBJECT);
        writeH(signboard.getX());
        writeH(signboard.getY());
        writeD(signboard.getId());
        writeH(signboard.getGfxId());
        writeC(0);
        writeC(getDirection(signboard.getHeading()));
        writeC(0);
        writeC(0);
        writeD(0);
        writeH(0);
        writeS(null);
        writeS(signboard.getName());
        writeC(0);
        writeD(0);
        writeS(null);
        writeS(null);
        writeC(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(255);
        writeC(255);
    }

    private int getDirection(int heading) {
        int dir = 0;
        switch (heading) {
            case 2:
                dir = 1;
                break;
            case 3:
                dir = 2;
                break;
            case 4:
                dir = 3;
                break;
            case 6:
                dir = 4;
                break;
            case 7:
                dir = 5;
            case 5:
        }
        return dir;
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
 * com.lineage.server.serverpackets.S_NPCPack_Signboard JD-Core Version: 0.6.2
 */