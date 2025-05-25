package com.lineage.data.item_etcitem.teleport;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.Teleportation;

import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

public class Move_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Move_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if (pc.hasSkillEffect(THUNDER_GRAB) && ConfigSkillDragon.SLAY_BREAK_NOT_TELEPORT == true) {
            pc.sendPackets(new S_ServerMessage("\\fY身上有奪命之雷的效果無法瞬移"));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        } else {
            int mapid = pc.getMapId();
            int map = data[0]; // 日版記憶座標
            int x = data[1]; // 日版記憶座標
            int y = data[2]; // 日版記憶座標
            boolean isTeleport = pc.getMap().isTeleportable();// 地圖設定是否可順移
            if (pc.getInventory().checkItem(84041, 1) && mapid == 3301) {// 傲慢之塔支配傳送符(1樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84042, 1) && mapid == 3302) {// 傲慢之塔支配傳送符(2樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84043, 1) && mapid == 3303) {// 傲慢之塔支配傳送符(3樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84044, 1) && mapid == 3304) {// 傲慢之塔支配傳送符(4樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84045, 1) && mapid == 3305) {// 傲慢之塔支配傳送符(5樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84046, 1) && mapid == 3306) {// 傲慢之塔支配傳送符(6樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84047, 1) && mapid == 3307) {// 傲慢之塔支配傳送符(7樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84048, 1) && mapid == 3308) {// 傲慢之塔支配傳送符(8樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84049, 1) && mapid == 3309) {// 傲慢之塔支配傳送符(9樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84050, 1) && mapid == 3310) {// 傲慢之塔支配傳送符(10樓)
                isTeleport = true;
            } else if (pc.getInventory().checkItem(84071, 1) && mapid >= 3301 && mapid <= 3310) {// 幻象的傲慢之塔移動傳送符
                isTeleport = true;
            }
            if (!isTeleport) {
                pc.sendPackets(new S_ServerMessage(647));
                pc.sendPackets(new S_Paralysis(7, false));
            } else {
                if ((x > 0) && (y > 0)) { // 日版記憶座標
                    pc.getInventory().removeItem(item, 1L);
                    if (pc.getTradeID() != 0) {
                        L1Trade trade = new L1Trade();
                        trade.tradeCancel(pc);
                    }
                    pc.setTeleportX(x);
                    pc.setTeleportY(y);
                    pc.setTeleportMapId((short) map);
                    pc.setTeleportHeading(5);
                    pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
                    Teleportation.teleportation(pc);
                } else {
                    pc.getInventory().removeItem(item, 1L);
                    L1Location newLocation;
                    int newX = pc.getX();
                    int newY = pc.getY();
                    short mapId = pc.getMapId();
                    boolean right = false;
                    while (!right) {
                        newLocation = pc.getLocation().randomLocation(200, true);
                        newX = newLocation.getX();
                        newY = newLocation.getY();
                        mapId = (short) newLocation.getMapId();
                        if (newX == pc.getX() && newY == pc.getY()) {
                            right = false;
                        } else {
                            right = true;
                        }
                    }
                    if (pc.getTradeID() != 0) {
                        L1Trade trade = new L1Trade();
                        trade.tradeCancel(pc);
                    }
                    pc.setTeleportX(newX);
                    pc.setTeleportY(newY);
                    pc.setTeleportMapId(mapId);
                    pc.setTeleportHeading(5);
                    pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
                    Teleportation.teleportation(pc);
                }
                if (pc.hasSkillEffect(78)) {// 絕對屏障
                    pc.killSkillEffectTimer(78);
                    pc.startHpRegeneration();
                    pc.startMpRegeneration();
                }
            }
        }
    }
}