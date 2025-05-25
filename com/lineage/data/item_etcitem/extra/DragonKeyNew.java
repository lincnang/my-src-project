package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.Iterator;

public class DragonKeyNew extends ItemExecutor {
    private int _npcId;
    private int _time;

    public static ItemExecutor get() {
        return new DragonKeyNew();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        if ((castleId > 0) && (ServerWarExecutor.get().isNowWar(castleId))) {
            pc.sendPackets(new S_ServerMessage(1892));
            return;
        }
        boolean isChecked = false;
        for (Iterator<Integer> i$ = ConfigOtherSet2.DRAGON_KEY_MAP_LIST.iterator(); i$.hasNext(); ) {
            int mapid = ((Integer) i$.next()).intValue();
            if (mapid == pc.getMapId()) {
                isChecked = true;
                break;
            }
        }
        if (!isChecked) {
            pc.sendPackets(new S_ServerMessage(1892));
            return;
        }
        if (this._npcId == 0) {
            pc.sendPackets(new S_SystemMessage("龍門編號設定錯誤，請通知線上GM"));
            return;
        }
        pc.getInventory().deleteItem(item);
        int timeDragon = this._time;
        L1SpawnUtil.spawn(pc, this._npcId, 0, timeDragon);
        World.get().broadcastPacketToAll(new S_ServerMessage(2921));
    }

    public void set_set(String[] set) {
        try {
            this._npcId = Integer.parseInt(set[1]);
            this._time = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
    }
}