package com.lineage.server.serverpackets;

public class S_SkillIconShield extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SkillIconShield(int type, int time) {
        writeC(S_MAGE_SHIELD);//S_OPCODE_SKILLICONSHIELD
        writeH(time);
        writeC(type);
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
 * com.lineage.server.serverpackets.S_SkillIconShield JD-Core Version: 0.6.2
 */