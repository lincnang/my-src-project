package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.world.WorldClan;

public class S_RetrievePledgeList extends ServerBasePacket {
    public boolean NonValue = false;
    private byte[] _byte = null;
    private byte[] status = null;

    public S_RetrievePledgeList(int objid, L1PcInstance pc) {
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan == null) {
            return;
        }
        if ((clan.getWarehouseUsingChar() != 0) && (clan.getWarehouseUsingChar() != pc.getId())) {
            pc.sendPackets(new S_ServerMessage(209));
            return;
        }
        if (pc.getInventory().getSize() < 180) {
            int size = clan.getDwarfForClanInventory().getSize();
            if (size > 0) {
                clan.setWarehouseUsingChar(pc.getId());
                writeC(S_RETRIEVE_LIST);
                writeD(objid);
                writeH(size);
                writeC(5);
                for (final Object itemObject : clan.getDwarfForClanInventory().getItems()) {
                    final L1ItemInstance item = (L1ItemInstance) itemObject;
                    this.writeD(item.getId());
                    int i = item.getItem().getUseType();
                    if (i < 0) {
                        i = 0;
                    }
                    this.writeC(i);
                    this.writeH(item.get_gfxid());
                    this.writeC(item.getBless());
                    this.writeD((int) Math.min(item.getCount(), 2000000000));
                    this.writeC(item.isIdentified() ? 0x01 : 0x00);
                    this.writeS(item.getViewName());
                    if (!item.isIdentified()) {
                        this.writeC(0);
                    } else {
                        status = item.getStatusBytes();
                        this.writeC(status.length);
                        for (byte b : status) {
                            this.writeC(b);
                        }
                    }
                }
                this.writeH(0x001e);
                this.writeD(0x00);
                this.writeH(0x00);
                this.writeH(0x08);
            } else {
                NonValue = true;
            }
        } else {
            pc.sendPackets(new S_ServerMessage(263));
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
 * com.lineage.server.serverpackets.S_RetrievePledgeList JD-Core Version: 0.6.2
 */