package com.lineage.server.serverpackets;

public class S_SkillIconPoison extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SkillIconPoison(final int type, final int time) {
        writeC(S_EVENT);
        writeC(S_PacketBox.POISON_ICON);
        writeC(type);
        // 2麻痺
        if (type >= 2) {
            //System.out.println("type"+type);
            writeH(0x0000);
            writeC(time);
            writeC(0x00);
            // 中毒
        } else {
            writeH(time);
            writeC(0x00);
            writeC(0x00);
        }
        // System.out.println("type:"+type+" time:"+time);
    }

    public S_SkillIconPoison(final int type, final int n, final int time) {
        writeC(S_EVENT);
        writeC(S_PacketBox.POISON_ICON);
        writeC(type);
        if (type >= 2) {
            //System.out.println("type"+type);
            writeH(0x0000);
            writeC(time);
            writeC(0x00);
        } else {
            writeH(time);
            writeC(0x00);
            writeC(0x00);
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
 * com.lineage.server.serverpackets.S_SkillSound JD-Core Version: 0.6.2
 */