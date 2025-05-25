package com.lineage.server.serverpackets;

public class S_PacketBoxIcon2 extends ServerBasePacket {
    private static final int ICONS2 = 21;// 黑妖魔法圖示
    private byte[] _byte = null;

    public S_PacketBoxIcon2(int type, int time, int value, int test) {
        writeC(S_EVENT);
        writeC(ICONS2);
        writeC(type);// 黑妖:雙重破壞
        writeC(time);// 黑妖:附加劇毒
        writeC(test);// 黑妖:燃燒鬥志
        writeC(value);// 閃避圖示 (幻術:鏡像、黑妖:闇影閃避)
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
 * com.lineage.server.serverpackets.S_PacketBoxIcon2 JD-Core Version: 0.6.2
 */