package com.lineage.data.item_etcitem.furniture;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldNpc;

public class GreatCowPeople extends ItemExecutor {
    public static ItemExecutor get() {
        return new GreatCowPeople();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemObjectId = item.getId();
        if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
            pc.sendPackets(new S_ServerMessage(563));
            return;
        }
        boolean isAppear = true;
        L1FurnitureInstance furniture = null;
        for (L1NpcInstance l1object : WorldNpc.get().all()) {
            if ((l1object instanceof L1FurnitureInstance)) {
                furniture = (L1FurnitureInstance) l1object;
                if (furniture.getItemObjId() == itemObjectId) {
                    isAppear = false;
                    break;
                }
            }
        }
        if ((pc.getHeading() != 0) && (pc.getHeading() != 2)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (isAppear) {
            L1SpawnUtil.spawn(pc, 80164, itemObjectId);
        } else {
            furniture.deleteMe();
            FurnitureSpawnReading.get().deleteFurniture(furniture);
        }
    }
}