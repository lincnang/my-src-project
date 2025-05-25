package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;

public class TOI_TeleportScroll extends ItemExecutor {
    public static ItemExecutor get() {
        return new TOI_TeleportScroll();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        int x = 0;
        int y = 0;
        short map = 0;
        String nameId = item.getItem().getNameId();
        if (nameId.equalsIgnoreCase("$2169")) {
            x = 32630;
            y = 32935;
            map = 111;
        } else if (nameId.equalsIgnoreCase("$2168")) {
            x = 32630;
            y = 32935;
            map = 121;
        } else if (nameId.equalsIgnoreCase("$2404")) {
            x = 32630;
            y = 32935;
            map = 131;
        } else if (nameId.equalsIgnoreCase("$2405")) {
            x = 32630;
            y = 32935;
            map = 141;
        } else if (nameId.equalsIgnoreCase("$2673")) {
            x = 32630;
            y = 32935;
            map = 151;
        } else if (nameId.equalsIgnoreCase("$2674")) {
            x = 32630;
            y = 32935;
            map = 161;
        } else if (nameId.equalsIgnoreCase("$2675")) {
            x = 32630;
            y = 32935;
            map = 171;
        } else if (nameId.equalsIgnoreCase("$2676")) {
            x = 32630;
            y = 32935;
            map = 181;
        } else if (nameId.equalsIgnoreCase("$2677")) {
            x = 32630;
            y = 32935;
            map = 191;
        } else if (nameId.equalsIgnoreCase("$2862")) {
            x = 32693;
            y = 32876;
            map = 200;
        }
        if (pc.getMap().isEscapable()) {
            pc.getInventory().removeItem(item, 1L);
            L1Teleport.teleport(pc, x, y, map, 5, true);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(7, false));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.TOI_TeleportScroll JD-Core Version:
 * 0.6.2
 */