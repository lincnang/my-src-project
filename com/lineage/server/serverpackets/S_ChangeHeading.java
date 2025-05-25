package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

public class S_ChangeHeading extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ChangeHeading(L1Character cha) {
        buildPacket(cha);
    }

    private void buildPacket(L1Character cha) {
        writeC(S_CHANGE_DIRECTION);
        writeD(cha.getId());
        writeC(cha.getHeading());
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
 * com.lineage.server.serverpackets.S_ChangeHeading JD-Core Version: 0.6.2
 */