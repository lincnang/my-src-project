package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_TaxRate extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_TaxRate.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            int i = readD();
            int j = readC();
            L1PcInstance player = client.getActiveChar();
            if (i == player.getId()) {
                L1Clan clan = WorldClan.get().getClan(player.getClanname());
                if (clan != null) {
                    int castle_id = clan.getCastleId();
                    if (castle_id != 0) {
                        L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
                        if ((j >= 10) && (j <= 50)) {
                            _log.info(player.getName() + "調整稅收:" + j);
							/*if(j>=20){
								j=20;
							}*/
                            l1castle.setTaxRate(j);
                            CastleReading.get().updateCastle(l1castle);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getMessage());
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_TaxRate JD-Core Version: 0.6.2
 */