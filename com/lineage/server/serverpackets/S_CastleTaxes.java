package com.lineage.server.serverpackets;

import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.utils.BinaryOutputStream;
import com.lineage.server.world.WorldClan;

import java.io.IOException;

/**
 * 地圖城堡顯示稅收
 */
public class S_CastleTaxes extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 地圖城堡顯示稅收
     */
    public S_CastleTaxes() {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(318);
        for (int i = 0; i < 8; i++) {
            int castleId = i + 1;
            L1Castle castle = CastleReading.get().getCastleTable(castleId);
            BinaryOutputStream bos = new BinaryOutputStream();
            try {
                if (castle != null) {
                    String clanName = WorldClan.get().getCastleClanName(castleId);
                    bos.writeC(1, castleId);
                    bos.writeS(2, clanName);
                    L1Clan clan = WorldClan.get().getClan(clanName);
                    if (clan != null) {
                        bos.writeS(3, clan.getLeaderName());
                    } else {
                        bos.writeS(3, "");
                    }
                    bos.writeC(4, 0L);
                    bos.writeC(5, 0L);
                    bos.writeC(6, (long) castle.getPublicMoney() * 2L);
                    bos.writeC(7, (long) castle.getPublicMoney() * 2L);
                }
                writeByte(1, bos.getBytes());
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }
            }
        }
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = _bao.toByteArray();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
