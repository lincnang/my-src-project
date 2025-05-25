package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class Scroll_Mass_Teleport extends ItemExecutor {
    public static ItemExecutor get() {
        return new Scroll_Mass_Teleport();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        int map = data[0]; // 日版記憶座標
        int x = data[1]; // 日版記憶座標
        int y = data[2]; // 日版記憶座標
        boolean isTeleport = L1WorldMap.get().getMap((short) map).isEscapable();
        if (!isTeleport) {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(7, false));
        } else {
            if ((x > 0) && (y > 0)) { // 日版記憶座標
                pc.getInventory().removeItem(item, 1L);
                ArrayList<L1Object> objList = World.get().getVisiblePoint(pc.getLocation(), 3, pc.get_showId());
                for (L1Object tgObj : objList) {
                    if ((tgObj instanceof L1PcInstance)) {
                        L1PcInstance tgPc = (L1PcInstance) tgObj;
                        if (!tgPc.isDead()) {
                            if (tgPc.getClanid() != 0) {
                                if (tgPc.getClanid() == pc.getClanid()) {
                                    if (!tgPc.isPrivateShop()) {
                                        L1BuffUtil.cancelAbsoluteBarrier(tgPc);
                                        tgPc.setTeleportX(x);
                                        tgPc.setTeleportY(y);
                                        tgPc.setTeleportMapId((short) map);
                                        tgPc.sendPackets(new S_Message_YN(748));
                                    }
                                }
                            }
                        }
                    }
                }
                L1Teleport.teleport(pc, x, y, (short) map, 5, true);
            } else {
                pc.getInventory().removeItem(item, 1L);
                L1Location newLocation = pc.getLocation().randomLocation(200, true);
                int newX = newLocation.getX();
                int newY = newLocation.getY();
                short newMapId = (short) newLocation.getMapId();
                ArrayList<L1Object> objList = World.get().getVisiblePoint(pc.getLocation(), 3, pc.get_showId());
                for (L1Object tgObj : objList) {
                    if ((tgObj instanceof L1PcInstance)) {
                        L1PcInstance tgPc = (L1PcInstance) tgObj;
                        if (!tgPc.isDead()) {
                            if (tgPc.getClanid() != 0) {
                                if (tgPc.getClanid() == pc.getClanid()) {
                                    if (!tgPc.isPrivateShop()) {
                                        L1BuffUtil.cancelAbsoluteBarrier(tgPc);
                                        tgPc.setTeleportX(newX);
                                        tgPc.setTeleportY(newY);
                                        tgPc.setTeleportMapId(newMapId);
                                        pc.sendPackets(new S_Message_YN(748));
                                    }
                                }
                            }
                        }
                    }
                }
                L1Teleport.teleport(pc, newX, newY, newMapId, 5, true);
            }
            L1BuffUtil.cancelAbsoluteBarrier(pc);
        }
    }
}
