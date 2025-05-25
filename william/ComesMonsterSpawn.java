package william;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1ComesMonsterDeleteTimer;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 指定時間召怪召物系統
 */
public class ComesMonsterSpawn implements Runnable {
    private Object[] objects;

    public ComesMonsterSpawn(final Object[] objects) {
        this.objects = objects;
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long time = 0;
        try {
            time = sdf.parse(sdf.format((Date) objects[9])).getTime() - sdf.parse(sdf.format(new Date())).getTime();
            //time = sdf.parse(sdf.format((Time)objects[9])).getTime() - sdf.parse(sdf.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (time <= 0) {
            return;
        }
        GeneralThreadPool.get().schedule(this, time);
    }

    @Override
    public void run() {
        try {
            final int type = (int) this.objects[0];
            final int id = (int) this.objects[1];
            final int count = (int) this.objects[2];
            final int mapid = (int) this.objects[3];
            final int x = (int) this.objects[4];
            final int y = (int) this.objects[5];
            final int xe = (int) this.objects[6];
            final int ye = (int) this.objects[7];
            final String s = (String) this.objects[10];
            final int gfxid = (int) this.objects[11];
            final int dt = (int) this.objects[12] * 60 * 1000;
            final L1Npc npc = (type == 0) ? NpcTable.get().getTemplate(id) : null;
            final L1Item item = (type == 1) ? ItemTable.get().getTemplate(id) : null;
            if (s != null && !s.isEmpty()) {
                if (npc != null || item != null) {
                    World.get().broadcastServerMessage(String.format(s, (type == 0) ? npc.get_name() : item.getName()));
                } else {
                    World.get().broadcastServerMessage(s);
                }
            }
            if (npc != null && item != null) {
                return;
            }
            final Random r = new Random();
            final L1Map map = L1WorldMap.get().getMap((short) mapid);
            for (int i = 0; i < count; i++) {
                int nx = 0;
                int ny = 0;
                do {
                    nx = r.nextInt(xe - x + 1) + x;
                    ny = r.nextInt(ye - y + 1) + y;
                } while (!map.isPassable(nx, ny));
                L1Object ob = null;
                if (type == 0) {
                    final L1NpcInstance obj = NpcTable.get().newNpcInstance(npc);
                    obj.setId(IdFactoryNpc.get().nextId());
                    obj.setX(nx);
                    obj.setY(ny);
                    obj.setHomeX(nx);
                    obj.setHomeY(ny);
                    obj.setMap(map);
                    //obj.setHeading(r.nextInt(8));
                    obj.setHeading(r.nextInt(7));
                    World.get().storeObject(obj);
                    World.get().addVisibleObject(obj);
                    if (gfxid > 0) {
                        obj.broadcastPacketAll(new S_SkillSound(obj.getId(), gfxid));
                    }
                    obj.onNpcAI();
                    ob = obj;
                } else {
                    final L1GroundInventory gInventory = World.get().getInventory(nx, ny, (short) mapid);
                    final L1ItemInstance newItem = ItemTable.get().createItem(id);
                    newItem.setCount(1);
                    gInventory.storeItem(newItem);
                    ob = newItem;
                }
                if (dt > 0) {
                    new L1ComesMonsterDeleteTimer(ob, dt).begin();
                }
            }
        } catch (Exception e) {
        }
    }
}
