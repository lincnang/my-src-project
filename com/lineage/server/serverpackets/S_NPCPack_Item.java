package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

public class S_NPCPack_Item extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_Item(L1ItemInstance item) {
        buildPacket(item);
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(S_PUT_OBJECT);
        writeH(item.getX());
        writeH(item.getY());
        writeD(item.getId());
        writeH(item.getItem().getGroundGfxId());
        writeC(0);
        writeC(0);
        writeC(item.isNowLighting() ? item.getItem().getLightRange() : 0);
        writeC(0);
        writeD((int) Math.min(item.getCount(), 2000000000L));
        writeH(0);
        String name = "";
        if (item.getCount() > 1L) {
            name = item.getItem().getNameId() + " (" + item.getCount() + ")";
        } else {
            switch (item.getItemId()) {
                case 20383:
                case 41235:
                case 41236:
                    name = item.getItem().getNameId() + " [" + item.getChargeCount() + "]";
                    break;
                case 40006:
                case 40007:
                case 40008:
                case 40009:
                case 140006:
                case 140008:
                    if (item.isIdentified()) {
                        name = item.getItem().getNameId() + " (" + item.getChargeCount() + ")";
                    }
                    break;
                default:
                    if ((item.getItem().getLightRange() != 0) && (item.isNowLighting())) {
                        name = item.getItem().getNameId() + " ($10)";
                    } else {
                        name = item.getItem().getNameId();
                    }
                    break;
            }
        }
        writeS(name);
        writeS(null);
        writeC(0);
        writeD(0);
        writeS(null);
        writeS(null);
        writeC(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(255);
        writeC(255);
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
 * com.lineage.server.serverpackets.S_NPCPack_Item JD-Core Version: 0.6.2
 */