package com.lineage.server.serverpackets;

public class S_SkillIconBlessOfEva extends ServerBasePacket {
    public S_SkillIconBlessOfEva(int objectId, int time) {
        writeC(S_BREATH);
        writeD(objectId);
        writeH(time);
    }

    public byte[] getContent() {
        return getBytes();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_SkillIconBlessOfEva JD-Core Version: 0.6.2
 */