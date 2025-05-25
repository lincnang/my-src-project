package com.lineage.server.command.executor;

import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.datatables.sql.LetterTable;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;
import com.lineage.server.world.WorldNpc;

/**
 * 刪除地面物品
 *
 * @author dexc
 */
public class L1DeleteGroundItem implements L1CommandExecutor {
    private L1DeleteGroundItem() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1DeleteGroundItem();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        for (L1ItemInstance item : WorldItem.get().all()) {
            if (item != null) {
                if (item.getX() == 0 && item.getY() == 0) { // 地面上のアイテムではなく、谁かの所有物
                    continue;
                }
                if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item.getMapId())) { // 盟屋内
                    continue;
                }
                final L1Inventory inv = World.get().getInventory(item.getX(), item.getY(), item.getMapId());
                final int itemId = item.getItem().getItemId();
                if (itemId >= 49016 && itemId <= 49025) { // 便笺
                    final LetterTable lettertable = new LetterTable();
                    lettertable.deleteLetter(item.getId());
                } else if (itemId >= 41383 && itemId <= 41400) { // 家具
                    deleteFurniture(item.getId());
                }
                inv.deleteItem(item);
                World.get().removeVisibleObject(item);
                World.get().removeObject(item);
            }
        }
        World.get().broadcastServerMessage("删除地面物品.");
    }

    private void deleteFurniture(int furnitureId) {
        for (L1NpcInstance npc : WorldNpc.get().all()) {
            if (!(npc instanceof L1FurnitureInstance)) {
                continue;
            }
            L1FurnitureInstance furniture = (L1FurnitureInstance) npc;
            if (furniture.getItemObjId() == furnitureId) { // 既に引き出している家具
                FurnitureSpawnReading.get().deleteFurniture(furniture);
            }
        }
    }
}
