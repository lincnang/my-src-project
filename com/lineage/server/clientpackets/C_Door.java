package com.lineage.server.clientpackets;

import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1House;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class C_Door extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport())) {
                return;
            }
            int objectId = readD();
            L1Object obj = World.get().findObject(objectId);
            if ((obj instanceof L1DoorInstance)) {
                L1DoorInstance door = (L1DoorInstance) World.get().findObject(objectId);
                if (door == null) {
                    return;
                }
                if (door.getDoorId() == 2101) {
                    return;
                }
                if ((door.getDoorId() >= 5001) && (door.getDoorId() <= 5010)) {
                    return;
                }
                switch (door.getDoorId()) {
                    case 2600:
                        if (isExistKeeper(pc, door.getKeeperId())) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            return;
                        } else if (door.getOpenStatus() == 29) {
                            int count = Random.nextInt(20) + 5;
                            L1ItemInstance item = ItemTable.get().createItem(40010);
                            item.setCount(count);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, door.getName(), item.getLogName()));
                            door.open();
                        }
                        break;
                    case 6006:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (pc.getInventory().consumeItem(40163, 1L)) {
                            door.open();
                            final CloseTimer closetimer = new CloseTimer(door);
                            closetimer.begin();
                        }
                        break;
                    case 6007:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (pc.getInventory().consumeItem(40313, 1L)) {
                            door.open();
                            final CloseTimer closetimer = new CloseTimer(door);
                            closetimer.begin();
                        }
                        break;
                    case 10000:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (!pc.getInventory().consumeItem(40581, 1L)) {
                            break;
                        }
                        door.open();
                        break;
                    case 10001:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (!pc.getInventory().consumeItem(40594, 1L)) {
                            break;
                        }
                        door.open();
                        break;
                    case 10002:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (!pc.getInventory().consumeItem(40604, 1L)) {
                            break;
                        }
                        door.open();
                        break;
                    case 10003:
                        break;
                    case 10004:
                        if (door.getOpenStatus() == 28) {
                            return;
                        }
                        if (!pc.getInventory().consumeItem(40543, 1L)) {
                            break;
                        }
                        door.open();
                        break;
                    case 3501: // 寵物競速
                    case 3502:
                        break;
                    case 6033: // 死亡競賽
                    case 6034:
                        break;
                    case 10005:
                    case 10006:
                    case 10007:
                        break;
                    case 10008:
                    case 10009:
                    case 10010:
                        break;
                    case 10011:
                    case 10012:
                    case 10013:
                        break;
                    case 10019:
                    case 10036:
                        break;
                    case 10037:
                    case 10038:
                    case 10039:
                        break;
                    case 10015:
                        if (!pc.get_hardinR().DOOR_1) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            door.close();
                        } else if (door.getOpenStatus() == 29) {
                            door.open();
                        }
                        break;
                    case 10016:
                        if (!pc.get_hardinR().DOOR_2) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            door.close();
                        } else if (door.getOpenStatus() == 29) {
                            door.open();
                        }
                        break;
                    case 10017:
                        if (!pc.get_hardinR().DOOR_2) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            door.close();
                        } else if (door.getOpenStatus() == 29) {
                            door.open();
                        }
                        break;
                    case 10020:
                        if (!pc.get_hardinR().DOOR_4) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            door.close();
                        } else if (door.getOpenStatus() == 29) {
                            door.open();
                            pc.get_hardinR().DOOR_4OPEN = true;
                        }
                        break;
                    default:
                        if (isExistKeeper(pc, door.getKeeperId())) {
                            break;
                        }
                        if (door.getOpenStatus() == 28) {
                            door.close();
                        } else if (door.getOpenStatus() == 29) {
                            door.open();
                        }
                        break;
                }
            } else if (obj instanceof L1BoxInstance) { // 寶箱NPC
                final L1BoxInstance npc = (L1BoxInstance) obj;
                // 啟動對話控制項
                npc.onTalkAction(pc);
                return;
            } else if ((obj instanceof L1NpcInstance)) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                switch (npc.getNpcId()) {
                    case 70918:
                        openDeLv50(pc, npc);
                }
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    private void openDeLv50(L1PcInstance pc, L1NpcInstance npc) {
        L1ItemInstance item = pc.getInventory().checkItemX(40600, 1L);
        if (item != null) {
            pc.getInventory().removeItem(item, 1L);
            final HashMap<Integer, L1Object> mapList = new HashMap<Integer, L1Object>();
            mapList.putAll(World.get().getVisibleObjects(DarkElfLv50_1.MAPID));
            npc.setStatus(28);
            npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 28));
            for (L1Object tgobj : mapList.values()) {
                if ((tgobj instanceof L1NpcInstance)) {
                    L1NpcInstance tgnpc = (L1NpcInstance) tgobj;
                    if ((tgnpc.getNpcId() == 70905) && (tgnpc.get_showId() == npc.get_showId())) {
                        tgnpc.deleteMe();
                    }
                }
            }
            mapList.clear();
        }
    }

    private boolean isExistKeeper(L1PcInstance pc, int keeperId) {
        if (keeperId == 0) {
            return false;
        }
        if (pc.isGm()) {
            return false;
        }
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseReading.get().getHouseTable(houseId);
                if (keeperId == house.getKeeperId()) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public class CloseTimer extends TimerTask {
        private L1DoorInstance _door;

        public CloseTimer(L1DoorInstance door) {
            _door = door;
        }

        public void run() {
            if (_door.getOpenStatus() == 28) {
                _door.close();
            }
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 5000L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_Door JD-Core Version: 0.6.2
 */