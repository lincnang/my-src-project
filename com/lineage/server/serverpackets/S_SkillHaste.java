package com.lineage.server.serverpackets;

public class S_SkillHaste extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SkillHaste(int objid, int mode, int time) {
        writeC(S_SPEED);
        writeD(objid);
        writeC(mode);
        writeH(time);
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
 * com.lineage.server.serverpackets.S_SkillHaste JD-Core Version: 0.6.2
 */