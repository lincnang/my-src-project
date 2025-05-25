package com.lineage.server.model;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.Teleportation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class L1Teleport {
    public static final int TELEPORT = 0;
    public static final int CHANGE_POSITION = 1;
    public static final int ADVANCED_MASS_TELEPORT = 2;
    public static final int CALL_CLAN = 3;
    public static final int[] EFFECT_SPR = {169, 2235, 2236, 2281};
    public static final int[] EFFECT_TIME = {280, 440, 440, 1120};
    private static final Log _log = LogFactory.getLog(L1Teleport.class);

    public static void teleport(L1PcInstance pc, L1Location loc, int head, boolean effectable) {
        teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, 0);
    }

    public static void teleport(L1PcInstance pc, L1Location loc, int head, boolean effectable, int skillType) {
        teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, skillType);
    }

    public static void teleport(L1PcInstance pc, int x, int y, short mapid, int head, boolean effectable) {
        teleport(pc, x, y, mapid, head, effectable, 0);
    }

    public static void teleport(L1PcInstance pc, int x, int y, short mapId, int head, boolean effectable, int skillType) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        if ((effectable) && (skillType >= 0) && (skillType <= EFFECT_SPR.length)) {
            pc.sendPackets(new S_ChangeName(pc, false));
            S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[skillType]);
            pc.sendPackets(packet);
            pc.broadcastPacketAll(packet);
            try {
                TimeUnit.MILLISECONDS.sleep((int) (EFFECT_TIME[skillType] * 0.7D));
            } catch (Exception localException) {
            }
        }
        pc.setTeleportX(x);
        pc.setTeleportY(y);
        pc.setTeleportMapId(mapId);
        pc.setTeleportHeading(head);
        Teleportation.teleportation(pc);
    }

    /**
     * 召喚傳送術
     *
     * @param cha      被召喚的目標
     * @param target   召喚執行者
     * @param distance 召喚至面前距離幾格位置
     */
    public static void teleportToTargetFront(L1Character cha, L1Character target, int distance) {
        int locX = target.getX();
        int locY = target.getY();
        int heading = target.getHeading();
        L1Map map = target.getMap();
        short mapId = target.getMapId();
        switch (heading) {
            case 1:
                locX += distance;
                locY -= distance;
                break;
            case 2:
                locX += distance;
                break;
            case 3:
                locX += distance;
                locY += distance;
                break;
            case 4:
                locY += distance;
                break;
            case 5:
                locX -= distance;
                locY += distance;
                break;
            case 6:
                locX -= distance;
                break;
            case 7:
                locX -= distance;
                locY -= distance;
                break;
            case 0:
                locY -= distance;
                break;
        }
        if (map.isPassable(locX, locY, null)) {// 座標可通行
            if (cha instanceof L1PcInstance) {// 被召喚的是PC
                // teleport((L1PcInstance) cha, locX, locY, mapId,
                // cha.getHeading(), true, 1);
                L1Teleport.teleport((L1PcInstance) cha, locX, locY, mapId, cha.getHeading(), true);
            } else if (cha instanceof L1NpcInstance) {// 被召喚的是NPC
                ((L1NpcInstance) cha).teleport(locX, locY, cha.getHeading());
            }
        } else {// 座標不可通行
            L1Location newloc = target.getLocation().randomLocation(distance, false);// 周圍隨機座標
            if (cha instanceof L1PcInstance) {// 被召喚的是PC
                teleport((L1PcInstance) cha, newloc, cha.getHeading(), true, 1);
                // L1Teleport.teleport((L1PcInstance) cha, locX, locY, mapId,
                // cha.getHeading(), true);
            } else if (cha instanceof L1NpcInstance) {// 被召喚的是NPC
                ((L1NpcInstance) cha).teleport(newloc.getX(), newloc.getY(), cha.getHeading());
            }
        }
    }

    /**
     * 隨機執行移動
     *
     * @param pc
     * @param effectable
     */
    public static void randomTeleport(final L1PcInstance pc, final boolean effectable) {
        try {
            // 本處理違結構???
            final L1Location newLocation = pc.getLocation().randomLocation(200, true);
            final int newX = newLocation.getX();
            final int newY = newLocation.getY();
            final short mapId = (short) newLocation.getMapId();
            L1Teleport.teleport(pc, newX, newY, mapId, 5, effectable);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Teleport JD-Core Version: 0.6.2
 */