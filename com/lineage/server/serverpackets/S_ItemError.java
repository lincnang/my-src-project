package com.lineage.server.serverpackets;

public class S_ItemError extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ItemError(int skillid) {
        buildPacket(skillid);
    }

    private void buildPacket(int skillid) {
        // writeC(S_OPCODE_ITEMERROR);
        this.writeC(S_NOT_ENOUGH_FOR_SPELL); // XXX S_NOT_ENOUGH_FOR_SPELL 修改为 S_OPCODE_MATERIAL
        writeD(skillid);
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
 * com.lineage.server.serverpackets.S_ItemError JD-Core Version: 0.6.2
 */