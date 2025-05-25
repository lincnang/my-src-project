package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;

public class Transmission_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Transmission_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        int locx = 0;
        int locy = 0;
        short mapid = 0;
        String nameId = item.getName();
        switch (nameId) {
            case "$2400":
                locx = 32631;
                locy = 32935;
                mapid = 111;
                break;
            case "$2678":
                locx = 32669;
                locy = 32814;
                mapid = 151;
                break;
            case "$2401":
                locx = 32631;
                locy = 32935;
                mapid = 121;
                break;
            case "$2679":
                locx = 32669;
                locy = 32814;
                mapid = 161;
                break;
            case "$2402":
                locx = 32631;
                locy = 32935;
                mapid = 131;
                break;
            case "$2680":
                locx = 32669;
                locy = 32814;
                mapid = 171;
                break;
            case "$2403":
                locx = 32631;
                locy = 32935;
                mapid = 141;
                break;
            case "$2681":
                locx = 32669;
                locy = 32814;
                mapid = 181;
                break;
            case "$2682":
                locx = 32669;
                locy = 32814;
                mapid = 191;
                break;
        }
        if (pc.getMap().isEscapable()) {
            // pc.getInventory().removeItem(item, 1L);
            L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(7, false));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.Transmission_Reel JD-Core Version:
 * 0.6.2
 */