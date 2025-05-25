package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.Controller.FishingTimeController;
import com.lineage.server.datatables.FishingTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Fishing;
import com.lineage.server.serverpackets.S_NewCreateItem;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Fishing;

/**
 * 高彈力釣竿<BR>
 *
 * @author dexc
 */
public class FishingPole extends ItemExecutor {
    /**
     *
     */
    private FishingPole() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new FishingPole();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int fishX = data[0];
        final int fishY = data[1];
        this.startFishing(pc, item, fishX, fishY);
    }

    private void startFishing(final L1PcInstance pc, final L1ItemInstance item, final int fishX, final int fishY) {
        if (pc.getMapId() != 5490) {
            // 無法在這個地區使用釣竿。
            pc.sendPackets(new S_ServerMessage(1138));
            return;
        }
        int rodLength = 0;
        if (item.getItemId() == 83001) { // 高彈力釣竿
            rodLength = 4;
        } else {
            rodLength = 5;
        }
        if (pc.getMap().isFishingZone(fishX, fishY)) {
            if (pc.getMap().isFishingZone(fishX + 1, fishY) && pc.getMap().isFishingZone(fishX - 1, fishY) && pc.getMap().isFishingZone(fishX, fishY + 1) && pc.getMap().isFishingZone(fishX, fishY - 1)) {
                if ((fishX > pc.getX() + rodLength) || (fishX < pc.getX() - rodLength)) {
                    // 無法在這個地區使用釣竿。
                    pc.sendPackets(new S_ServerMessage(1138));
                } else if ((fishY > pc.getY() + rodLength) || (fishY < pc.getY() - rodLength)) {
                    // 無法在這個地區使用釣竿。
                    pc.sendPackets(new S_ServerMessage(1138));
                } else if (pc.getInventory().checkItem(83002, 1)) {// 營養釣餌
                    pc.sendPacketsAll(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
                    // 日版钓鱼
                    pc.setFishing(true);
                    pc.setFishingItem(item);
                    pc.fishX = fishX;
                    pc.fishY = fishY;
                    final L1Fishing temp = FishingTable.get().get_item(pc.getFishingItem().getItemId());
                    int itemFishTime = 240;
                    if (temp != null) {
                        itemFishTime = temp.getFishTime();
                    }
                    final long fishtime = System.currentTimeMillis() + (itemFishTime * 1000);
                    boolean ck = false;
                    if (item.getItemId() != 83001) { // 不是高弹力钓竿
                        ck = true;
                    }
                    pc.setFishingTime(fishtime);
                    FishingTimeController.getInstance().addMember(pc);
                    pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.FISH_WINDOW, 1, ck, (fishtime - System.currentTimeMillis()) / 1000));
                } else {
                    // 釣魚就必須要有餌。
                    pc.sendPackets(new S_ServerMessage(1137));
                }
            } else {
                // 無法在這個地區使用釣竿。
                pc.sendPackets(new S_ServerMessage(1138));
            }
        } else {
            // 無法在這個地區使用釣竿。
            pc.sendPackets(new S_ServerMessage(1138));
        }
    }
}
