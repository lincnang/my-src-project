package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DoorInstance;

public class S_Door extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Door(L1DoorInstance door) {
        buildPacket(door.getEntranceX(), door.getEntranceY(), door.getDirection(), door.getPassable());
    }

    public S_Door(int x, int y, int direction, int passable) {
        buildPacket(x, y, direction, passable);
    }

    private void buildPacket(int x, int y, int direction, int passable) {
        writeC(S_CHANGE_ATTR);
        writeH(x);
        writeH(y);
        writeC(direction);
        writeC(passable);
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
 * com.lineage.server.serverpackets.S_Door JD-Core Version: 0.6.2
 */