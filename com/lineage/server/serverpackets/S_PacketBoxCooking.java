package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_PacketBoxCooking extends ServerBasePacket {
    public static final int COOK_WINDOW = 52;
    public static final int ICON_COOKING = 53;
    private byte[] _byte = null;

    public S_PacketBoxCooking(int value) {
        writeC(S_EVENT);
        writeC(52);
        writeC(219);
        writeC(49);
        writeC(223);
        writeC(2);
        writeC(1);
        writeC(value);
    }

    public S_PacketBoxCooking(L1PcInstance pc, int type, int time) {
        writeC(S_EVENT);
        writeC(53);
        int food = pc.get_food() * 10 - 250;
        if (food < 0) {
            food = 0;
        }
        switch (type) {
            case 7:
                writeC(pc.getStr());
                writeC(pc.getInt());
                writeC(pc.getWis());
                writeC(pc.getDex());
                writeC(pc.getCon());
                writeC(pc.getCha());
                writeH(food);
                writeC(type);
                writeC(36);
                writeH(time);
                writeC(0);
                break;
            case 54:// 象牙塔妙藥
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeH(0);
                writeC(type);
                writeC(42);
                writeH(time);
                writeC(0);
                break;
            default:
                writeC(pc.getStr());
                writeC(pc.getInt());
                writeC(pc.getWis());
                writeC(pc.getDex());
                writeC(pc.getCon());
                writeC(pc.getCha());
                writeH(food);
                writeC(type);
                writeC(38);
                writeH(time);
                writeC(0);
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
 * com.lineage.server.serverpackets.S_PacketBoxCooking JD-Core Version: 0.6.2
 */