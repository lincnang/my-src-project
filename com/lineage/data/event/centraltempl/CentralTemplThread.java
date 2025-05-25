package com.lineage.data.event.centraltempl;

import com.lineage.server.IdFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 中央寺廟副本
 *
 * @author TeX
 */
public class CentralTemplThread extends Thread {
    private static final Log _log = LogFactory.getLog(CentralTemplThread.class);
    short mapId;
    L1PcInstance pc;
    L1NpcInstance npc;
    int type = -1;

    public CentralTemplThread(int mapId, L1PcInstance pc) {
        this.mapId = ((short) mapId);
        this.pc = pc;
    }

    /**
     * 中央寺廟線程開始
     * 共三回合
     *
     * @author TeX
     */
    public void run() {
        _log.info("中央寺廟副本開始：玩家:" + this.pc.getName() + ",mapid:" + this.mapId);
        try {
            L1Teleport.teleport(this.pc, 32795, 32867, this.mapId, 1, true);
            this.npc = ((L1NpcInstance) spawn(new L1Location(32801, 32862, this.mapId), 190114, 1, 4).get(0));
            L1FieldObjectInstance object = spwanField(7572, 32801, 32862, this.mapId);
            ArrayList<L1NpcInstance> npclist = new ArrayList<>();
            npclist.addAll(spawn(new L1Location(32798, 32862, this.mapId), 190110, 1, 2));
            npclist.addAll(spawn(new L1Location(32801, 32865, this.mapId), 190110, 1, 0));
            npclist.addAll(spawn(new L1Location(32804, 32859, this.mapId), 190110, 1, 5));
            npclist.addAll(spawn(new L1Location(32798, 32861, this.mapId), 190111, 1, 2));
            npclist.addAll(spawn(new L1Location(32802, 32866, this.mapId), 190111, 1, 0));
            if (isKillNpc(npclist) == -1) {
                return;
            }
            TimeUnit.MILLISECONDS.sleep(1000L);
            this.pc.getInventory().storeItem(640354, 1L);
            object.deleteMe();
            this.pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND_NUMBER, 1, 3));//第一回合
            sendMsg("$17947");
            npclist = new ArrayList<>(spawn());
            TimeUnit.MILLISECONDS.sleep(5000L);
            sendMsg("$17701");
            isKillNpc(npclist);
            TimeUnit.MILLISECONDS.sleep(1000L);
            this.pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND_NUMBER, 2, 3));//第二回合
            npclist = new ArrayList<>();
            sendMsg("$17969");
            this.type = 0;
            int small_boss = 190106 + Random.nextInt(4);
            switch (small_boss) {
                case 190106:
                    sendMsg("$17941");
                    npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), small_boss, 1, 4));
                    break;
                case 190107:
                    sendMsg("$17944");
                    npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), small_boss, 1, 7));
                    break;
                case 190108:
                    sendMsg("$17942");
                    npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), small_boss, 1, 0));
                    break;
                case 190109:
                    sendMsg("$17943");
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), small_boss, 1, 2));
                    break;
                default:
                    sendMsg("$17943");
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), small_boss, 1, 2));
            }
            npclist.addAll(spawn());
            TimeUnit.MILLISECONDS.sleep(5000L);
            sendMsg("$17703");
            isKillNpc(npclist);
            TimeUnit.MILLISECONDS.sleep(1000L);
            this.pc.sendPackets(new S_PacketBox(S_PacketBox.ROUND_NUMBER, 3, 3));//第三回合
            this.type = 0;
            npclist = new ArrayList<>();
            small_boss = 190106 + Random.nextInt(4);
            switch (small_boss) {
                case 190106:
                    sendMsg("$17941");
                    npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), small_boss, 1, 4));
                    break;
                case 190107:
                    sendMsg("$17944");
                    npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), small_boss, 1, 7));
                    break;
                case 190108:
                    sendMsg("$17942");
                    npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), small_boss, 1, 0));
                    break;
                case 190109:
                    sendMsg("$17943");
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), small_boss, 1, 2));
                    break;
                default:
                    sendMsg("$17943");
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), small_boss, 1, 2));
            }
            sendMsg("$17969");
            npclist.addAll(spawn());
            TimeUnit.MILLISECONDS.sleep(5000L);
            sendMsg("$17703");
            isKillNpc(npclist);
            TimeUnit.MILLISECONDS.sleep(1000L);
            npclist = new ArrayList<>();
            small_boss = Random.nextInt(4);
            switch (small_boss) {
                case 0:
                    npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190112, 1, 4));
                    break;
                case 1:
                    npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190112, 1, 7));
                    break;
                case 2:
                    npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190112, 1, 0));
                    break;
                case 3:
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190112, 1, 2));
                    break;
                default:
                    npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190112, 1, 2));
            }
            sendMsg("$17995:$17713");
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190098, 10, 4));
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190099, 10, 4));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190101, 10, 7));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190100, 10, 7));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190102, 10, 0));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190103, 10, 0));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190104, 10, 2));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190105, 10, 2));
            TimeUnit.MILLISECONDS.sleep(5000L);
            isKillNpc(npclist);
            sendMsg("$17707");
            this.npc.transform(190115);
            TimeUnit.MILLISECONDS.sleep(3000L);
            sendMsg("$17708");
            TimeUnit.MILLISECONDS.sleep(3000L);
            sendMsg("$17709");
            TimeUnit.MILLISECONDS.sleep(3000L);
            sendMsg("$17710");
            TimeUnit.MILLISECONDS.sleep(3000L);
            sendMsg("$17712");
            TimeUnit.MILLISECONDS.sleep(3000L);
            this.npc.broadcastPacketAll(new S_SkillSound(this.npc.getId(), 169));
            drop();
            this.npc.deleteMe();
            sendMsg("$17962");
            TimeUnit.MILLISECONDS.sleep(10000L);
            quitCentralTempl();
        } catch (InterruptedException localInterruptedException) {
        }
    }

    private int isKillNpc(ArrayList<L1NpcInstance> list) {
        int count = 0;
        while (count++ < 300) {
            if (!isCentralTempl()) {
                quitCentralTempl();
                return -1;
            }
            boolean isAllDeath = true;
            for (L1NpcInstance npc : list) {
                if ((this.type == 0) && (npc.getNpcId() >= 190106) && (npc.getNpcId() <= 190109)) {
                    sendMsg("$17968");
                    this.pc.getInventory().storeItem(640355, 1L);
                    this.type = 1;
                }
                if (!npc.isDead()) {
                    isAllDeath = false;
                    break;
                }
            }
            if (isAllDeath) {
                return count;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000L);
            } catch (InterruptedException localInterruptedException) {
            }
        }
        sendMsg("$17714");
        try {
            TimeUnit.MILLISECONDS.sleep(3000L);
        } catch (InterruptedException localInterruptedException1) {
        }
        sendMsg("$17715");
        this.npc.deleteMe();
        isCentralTempl();
        quitCentralTempl();
        return -1;
    }

    private void drop() {
        int[] drop1 = {40087, 40074, 140087, 140074, 240087, 240074};
        int[] drop2 = {264, 262, 260, 263, 261, 326, 337, 336, 328, 329, 21152, 21154, 21153, 21155};
        if (Random.nextInt(100) > 10) {
            L1ItemInstance item = ItemTable.get().createItem(drop1[Random.nextInt(drop1.length)]);
            World.get().getInventory(this.npc.getLocation()).storeItem(item);
        } else {
            L1ItemInstance item = ItemTable.get().createItem(drop2[Random.nextInt(drop2.length)]);
            World.get().getInventory(this.npc.getLocation()).storeItem(item);
        }
        L1ItemInstance item = ItemTable.get().createItem(640353);
        World.get().getInventory(this.npc.getLocation()).storeItem(item);
    }

    private boolean isCentralTempl() {
        if (this.pc == null) {
            return false;
        }
        if (this.pc.getOnlineStatus() == 0) {
            return false;
        }
        if (this.pc.getMapId() != this.mapId) {
            return false;
        }
        if (this.pc.isDead()) {
            this.pc.sendPackets(new S_NpcChatPacket(this.pc, "$18636", 21));
            try {
                TimeUnit.MILLISECONDS.sleep(2000L);
            } catch (InterruptedException localInterruptedException) {
            }
            this.pc.sendPackets(new S_NpcChatPacket(this.pc, "$18637", 21));
            quitCentralTempl();
            return false;
        }
        return true;
    }

    private void quitCentralTempl() {
        if (this.pc != null) {
            L1ItemInstance[] itemlist = this.pc.getInventory().findItemsId(640354);
            for (L1ItemInstance itemInstance : itemlist) {
                this.pc.getInventory().removeItem(itemInstance);
            }
            itemlist = this.pc.getInventory().findItemsId(640355);
            for (L1ItemInstance l1ItemInstance : itemlist) {
                this.pc.getInventory().removeItem(l1ItemInstance);
            }
            if (this.pc.getMapId() == this.mapId) {
                try {
                    this.pc.sendPackets(new S_ServerMessage(1476));
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    this.pc.sendPackets(new S_ServerMessage(1477));
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    this.pc.sendPackets(new S_ServerMessage(1478));
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    this.pc.sendPackets(new S_ServerMessage(1480));
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    this.pc.sendPackets(new S_ServerMessage(1481));
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    this.pc.sendPackets(new S_ServerMessage(1482));
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    this.pc.sendPackets(new S_ServerMessage(1483));
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    this.pc.sendPackets(new S_ServerMessage(1484));
                } catch (InterruptedException localInterruptedException) {
                }
                L1Teleport.teleport(this.pc, 33703, 32502, (short) 4, 5, true);
            }
        }
        World.get().closeMap(this.mapId);
        L1CentralTemple.get().mapStat[(this.mapId - 1936)] = false;
        interrupt();
    }

    private ArrayList<L1NpcInstance> spawn(L1Location loc, int npcid, int count, int heading) throws InterruptedException {
        ArrayList<L1NpcInstance> list = new ArrayList<>();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                list.add(spawnNpc(loc, npcid, 0, heading));
                TimeUnit.MILLISECONDS.sleep(10L);
            }
        } else {
            list.add(spawnNpc(loc, npcid, 0, heading));
        }
        return list;
    }

    private L1NpcInstance spawnNpc(L1Location loc, int npcid, int randomRange, int heading) {
        L1Npc l1npc = NpcTable.get().getTemplate(npcid);
        L1NpcInstance field = null;
        if (l1npc == null) {
            _log.error("召喚的NPCID:" + npcid + "不存在");
            return null;
        }
        field = NpcTable.get().newNpcInstance(npcid);
        field.setId(IdFactory.get().nextId());
        field.setMap((short) loc.getMapId());
        field.getLocation().set(loc);
        field.setHomeX(field.getX());
        field.setHomeY(field.getY());
        field.setHeading(heading);
        field.setLightSize(l1npc.getLightSize());
        field.setLightSize(0);
        World.get().storeObject(field);
        World.get().addVisibleObject(field);
        return field;
    }

    private L1FieldObjectInstance spwanField(int gfxid, int x, int y, int mapid) {
        L1FieldObjectInstance field = (L1FieldObjectInstance) NpcTable.get().newNpcInstance(190000);
        if (field != null) {
            field.setId(IdFactoryNpc.get().nextId());
            field.setGfxId(gfxid);
            field.setTempCharGfx(gfxid);
            field.setMap((short) mapid);
            field.setX(x);
            field.setY(y);
            field.setHomeX(x);
            field.setHomeY(y);
            field.setHeading(5);
            World.get().storeObject(field);
            World.get().addVisibleObject(field);
        }
        return field;
    }

    private void sendMsg(String msg) {
        this.pc.sendPackets(new S_PacketBox(84, 2, msg));
    }

    private ArrayList<L1NpcInstance> spawn() {
        ArrayList<L1NpcInstance> npclist = new ArrayList<>();
        try {
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190098, 5, 4));
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190099, 5, 4));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190101, 5, 7));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190100, 5, 7));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190102, 5, 0));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190103, 5, 0));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190104, 5, 2));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190105, 5, 2));
            TimeUnit.MILLISECONDS.sleep(5000L);
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190098, 5, 4));
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190099, 5, 4));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190101, 5, 7));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190100, 5, 7));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190102, 5, 0));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190103, 5, 0));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190104, 5, 2));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190105, 5, 2));
            TimeUnit.MILLISECONDS.sleep(5000L);
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190098, 5, 4));
            npclist.addAll(spawn(new L1Location(32800, 32845, this.mapId), 190099, 5, 4));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190101, 5, 7));
            npclist.addAll(spawn(new L1Location(32817, 32862, this.mapId), 190100, 5, 7));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190102, 5, 0));
            npclist.addAll(spawn(new L1Location(32801, 32878, this.mapId), 190103, 5, 0));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190104, 5, 2));
            npclist.addAll(spawn(new L1Location(32785, 32861, this.mapId), 190105, 5, 2));
        } catch (InterruptedException localInterruptedException) {
        }
        return npclist;
    }
}