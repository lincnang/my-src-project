package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.*;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldClan;

public class Clan_Transmission_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Clan_Transmission_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if ((pc.getMap().isEscapable()) || (pc.isGm())) {
            int castle_id = 0;
            int house_id = 0;
            if (pc.getClanid() != 0) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    castle_id = clan.getCastleId();
                    house_id = clan.getHouseId();
                }
            }
            if (castle_id != 0) {
                if ((pc.getMap().isEscapable()) || (pc.isGm())) {
                    int[] loc = new int[3];
                    loc = L1CastleLocation.getCastleLoc(castle_id);
                    int locx = loc[0];
                    int locy = loc[1];
                    short mapid = (short) loc[2];
                    L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
                    pc.getInventory().removeItem(item, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                    pc.sendPackets(new S_Paralysis(7, false));
                }
            } else if (house_id != 0) {
                if ((pc.getMap().isEscapable()) || (pc.isGm())) {
                    int[] loc = new int[3];
                    loc = L1HouseLocation.getHouseLoc(house_id);
                    int locx = loc[0];
                    int locy = loc[1];
                    short mapid = (short) loc[2];
                    L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
                    pc.getInventory().removeItem(item, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                    pc.sendPackets(new S_Paralysis(7, false));
                }
            } else if (pc.getHomeTownId() > 0) {
                int[] loc = L1TownLocation.getGetBackLoc(pc.getHomeTownId());
                int locx = loc[0];
                int locy = loc[1];
                short mapid = (short) loc[2];
                L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
                pc.getInventory().removeItem(item, 1L);
            } else {
                int[] loc = GetbackTable.GetBack_Location(pc, true);
                L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                pc.getInventory().removeItem(item, 1L);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(7, false));
        }
        L1BuffUtil.cancelAbsoluteBarrier(pc);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.Clan_Transmission_Reel JD-Core
 * Version: 0.6.2
 */