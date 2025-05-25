package com.lineage.server.serverpackets;

public class S_Poison extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Poison(int objId, int type) {
        // 0000: 2f 2c 80 a1 01 00 00 08 /,......
        this.writeC(S_POISON);
        this.writeD(objId);
        switch (type) {
            case 0: // 通常
                this.writeC(0x00);
                this.writeC(0x00);
                break;
            case 1: // 綠色
                this.writeC(0x01);
                this.writeC(0x00);
                break;
            case 2: // 灰色
                this.writeC(0x00);
                this.writeC(0x01);
                break;
        }
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
 * com.lineage.server.serverpackets.S_Poison JD-Core Version: 0.6.2
 */