package com.lineage.server.model;

import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCPack_Item;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.timecontroller.server.ServerDeleteItemTimer;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1GroundInventory extends L1Inventory {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1GroundInventory.class);

    public L1GroundInventory(int objectId, int x, int y, short map) {
        try {
            setId(objectId);
            setX(x);
            setY(y);
            setMap(map);
            World.get().addVisibleObject(this);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void setTimer(L1ItemInstance item) {
        try {
            if (item.getItemId() == 40515) {
                return;
            }
            if (L1HouseLocation.isInHouse(getX(), getY(), getMapId())) {
                return;
            }
            if (!ServerDeleteItemTimer.contains(item)) {
                ServerDeleteItemTimer.add(item);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            for (L1ItemInstance item : getItems()) {
                if (perceivedFrom.get_showId() == item.get_showId()) {
                    if (!perceivedFrom.knownsObject(item)) {
                        perceivedFrom.addKnownObject(item);
                        perceivedFrom.sendPackets(new S_NPCPack_Item(item));
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void insertItem(L1ItemInstance item) {
        if (item.getCount() <= 0L) {
            return;
        }
        try {
            setTimer(item);
            for (L1PcInstance pc : World.get().getRecognizePlayer(item)) {
                if (pc.get_showId() == item.get_showId()) {
                    pc.sendPackets(new S_NPCPack_Item(item));
                    pc.addKnownObject(item);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void updateItem(L1ItemInstance item) {
        try {
            for (L1PcInstance pc : World.get().getRecognizePlayer(item)) {
                if (pc.get_showId() == item.get_showId()) {
                    pc.sendPackets(new S_NPCPack_Item(item));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void deleteItem(L1ItemInstance item) {
        try {
            for (L1PcInstance pc : World.get().getRecognizePlayer(item)) {
                pc.sendPackets(new S_RemoveObject(item));
                pc.removeKnownObject(item);
            }
            int itemid = item.getItemId();
            if (itemid == 40312) {// 旅館鑰匙
                InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
            }
            if (itemid == 82503) {// 訓練所鑰匙
                InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
            }
            if (itemid == 82504) {// 龍門憑證
                InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
            }
            _items.remove(item);
            if (_items.size() == 0) {
                World.get().removeVisibleObject(this);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1GroundInventory JD-Core Version: 0.6.2
 */