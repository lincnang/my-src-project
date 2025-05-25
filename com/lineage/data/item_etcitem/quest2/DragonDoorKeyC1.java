package com.lineage.data.item_etcitem.quest2;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class DragonDoorKeyC1 extends ItemExecutor {
    public static ItemExecutor get() {
        return new DragonDoorKeyC1();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getMapId() == 1017) {
            pc.getInventory().removeItem(item, 1L);
            for (L1Object obj : World.get().getVisibleObjects(1017).values()) {
                if ((obj instanceof L1DoorInstance)) {
                    L1DoorInstance door = (L1DoorInstance) obj;
                    if (door.get_showId() == pc.get_showId()) {
                        if (door.getDoorId() == 10037) {
                            door.open();
                            door.deleteMe();
                        }
                    }
                }
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest2.DragonDoorKeyC1 JD-Core Version: 0.6.2
 */