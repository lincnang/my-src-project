package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

public class C_Drawal extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport())) {
                return;
            }
            long count = readD();
            if (count > 2147483647L) {
                count = 2147483647L;
            }
            count = Math.max(0L, count);
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) {
                    L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
                    long money = l1castle.getPublicMoney();
                    money -= count;
                    L1ItemInstance item = ItemTable.get().createItem(40308);
                    if (item != null) {
                        l1castle.setPublicMoney(money);
                        CastleReading.get().updateCastle(l1castle);
                        if (pc.getInventory().checkAddItem(item, count) == 0) {
                            pc.getInventory().storeItem(40308, count);
                        } else {
                            World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(40308, count);
                        }
                        pc.sendPackets(new S_ServerMessage(143, "$457", "$4 (" + count + ")"));
                    }
                }
            }
        } catch (Exception localException) {
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
 * com.lineage.server.clientpackets.C_Drawal JD-Core Version: 0.6.2
 */