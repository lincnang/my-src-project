package com.lineage.server.model.Instance;

import com.lineage.server.serverpackets.S_Fishing;
import com.lineage.server.templates.L1NewMap;
import com.lineage.server.utils.NewMapUtil;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class L1FishingPc extends L1PcInstance {
    private static final Random R = new Random();
    private static final long serialVersionUID = 4798707454231555948L;
    private int fx;
    private int fy;

    public final void join() {
        setMap((short) 5490);
        ArrayList<L1NewMap> maps = NewMapUtil.getBlock(getMapId());
        int size = maps.size();
        int x = 0;
        int y = 0;
        if (size > 0) {
            L1NewMap map = (L1NewMap) maps.get(R.nextInt(size));
            int f = 1;
            int h = 0;
            do {
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fx = map.getX();
                    fy = map.getY();
                    x = R.nextInt(64);
                    y = R.nextInt(64);
                    f = R.nextInt(3) + 2;
                    h = R.nextInt(8);
                } while (((map.getTileType(x, y) & 0x1) == 1) || (map.getX() + x > 32805));
                setX(map.getX() + x);
                setY(map.getY() + y);
                if ((map.getTileType(x + f, y, 64)) && (h == 2)) {
                    fx += x + f;
                    fy += y;
                    setHeading(2);
                    break;
                }
                if ((map.getTileType(x - f, y, 64)) && (h == 6)) {
                    fx += x - f;
                    fy += y;
                    setHeading(6);
                    break;
                }
                if ((map.getTileType(x, y - f, 64)) && (h == 4)) {
                    fx += x;
                    fy += y + f;
                    setHeading(4);
                    break;
                }
                if ((map.getTileType(x, y - f, 64)) && (h == 0)) {
                    fx += x;
                    fy += y - f;
                    setHeading(0);
                    break;
                }
                if ((map.getTileType(x + f, y + f, 64)) && (h == 3)) {
                    fx += x + f;
                    fy += y + f;
                    setHeading(3);
                    break;
                }
                if ((map.getTileType(x - f, y - f, 64)) && (h == 7)) {
                    fx += x - f;
                    fy += y - f;
                    setHeading(7);
                    break;
                }
                if ((map.getTileType(x + f, y - f, 64)) && (h == 1)) {
                    fx += x + f;
                    fy += y - f;
                    setHeading(1);
                    break;
                }
            } while ((!map.getTileType(x - f, y + f, 64)) || (h != 5));
            fx += x - f;
            fy += y + f;
            setHeading(5);
            setTempCharGfx(getClassId());
            setX(x + map.getX());
            setY(y + map.getY());
            World.get().storeObject(this);
            World.get().addVisibleObject(this);
        }
    }

    public final void onPerceive(L1PcInstance perceivedFrom) {
        super.onPerceive(perceivedFrom);
        broadcastPacketAll(new S_Fishing(getId(), 71, fx, fy));
    }
}
