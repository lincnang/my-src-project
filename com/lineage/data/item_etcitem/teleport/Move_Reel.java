package com.lineage.data.item_etcitem.teleport;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Broadcaster;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.Teleportation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

public class Move_Reel extends ItemExecutor {

    // 動作與特效
    private static final int CAST_ACTION    = ActionCodes.ACTION_SkillBuff; // 播施法動作
    private static final int EFFECT_ID      = 169;   // 特效ID（若沒反應可換專案內已知存在的ID）
    private static final int TP_DELAY_MS    = 500;   // 延遲0.5秒再瞬移，避免特效被刷新吃掉

    // 防止連點節流
    private static final int TELEPORT_THROTTLE_MS = 500;
    private static final Map<Integer, Long> _lastTpAt = new ConcurrentHashMap<>();

    // 傲慢之塔樓層 mapid 範圍
    private static final int TOS_1F  = 3301;
    private static final int TOS_2F  = 3302;
    private static final int TOS_3F  = 3303;
    private static final int TOS_4F  = 3304;
    private static final int TOS_5F  = 3305;
    private static final int TOS_6F  = 3306;
    private static final int TOS_7F  = 3307;
    private static final int TOS_8F  = 3308;
    private static final int TOS_9F  = 3309;
    private static final int TOS_10F = 3310;

    // 傲慢之塔支配傳送符 ID
    private static final int ID_TOS_CTRL_1F  = 84041;
    private static final int ID_TOS_CTRL_2F  = 84042;
    private static final int ID_TOS_CTRL_3F  = 84043;
    private static final int ID_TOS_CTRL_4F  = 84044;
    private static final int ID_TOS_CTRL_5F  = 84045;
    private static final int ID_TOS_CTRL_6F  = 84046;
    private static final int ID_TOS_CTRL_7F  = 84047;
    private static final int ID_TOS_CTRL_8F  = 84048;
    private static final int ID_TOS_CTRL_9F  = 84049;
    private static final int ID_TOS_CTRL_10F = 84050;

    // 幻象的傲慢之塔移動傳送符（覆蓋1F~10F）
    private static final int ID_TOS_CTRL_ALL = 84071;

    public static ItemExecutor get() {
        return new Move_Reel();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null || item == null) return;
        if (!CheckUtil.getUseItem(pc)) return;

        int map, x, y;
        if (data == null || data.length < 3) {
            map = -1; x = -1; y = -1;
        } else {
            map = data[0];
            x   = data[1];
            y   = data[2];
        }

        // 禁傳檢查
        if (pc.hasSkillEffect(THUNDER_GRAB) && ConfigSkillDragon.SLAY_BREAK_NOT_TELEPORT) {
            pc.sendPackets(new S_ServerMessage("\\fY身上有奪命之雷的效果無法瞬移"));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            return;
        }

        // 地圖可否瞬移
        if (!canTeleportHere(pc)) {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            return;
        }

        // 消耗道具 & 取消交易
        consumeItemAndCancelTrade(pc, item);

        if (x > 0 && y > 0 && map >= 0) {
            doTeleport(pc, x, y, (short) map);
        } else {
            L1Location loc = randomSafeLocation(pc, 200, 80);
            doTeleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId());
        }
    }

    // 判斷是否允許瞬移
    private boolean canTeleportHere(L1PcInstance pc) {
        final L1Map map = pc.getMap();
        boolean isTeleportable = map != null && map.isTeleportable();
        if (isTeleportable) return true;

        final int mapId = pc.getMapId();
        if (pc.getInventory().checkItem(ID_TOS_CTRL_ALL, 1) && mapId >= TOS_1F && mapId <= TOS_10F) {
            return true;
        }

        if      (mapId == TOS_1F  && pc.getInventory().checkItem(ID_TOS_CTRL_1F, 1))  return true;
        else if (mapId == TOS_2F  && pc.getInventory().checkItem(ID_TOS_CTRL_2F, 1))  return true;
        else if (mapId == TOS_3F  && pc.getInventory().checkItem(ID_TOS_CTRL_3F, 1))  return true;
        else if (mapId == TOS_4F  && pc.getInventory().checkItem(ID_TOS_CTRL_4F, 1))  return true;
        else if (mapId == TOS_5F  && pc.getInventory().checkItem(ID_TOS_CTRL_5F, 1))  return true;
        else if (mapId == TOS_6F  && pc.getInventory().checkItem(ID_TOS_CTRL_6F, 1))  return true;
        else if (mapId == TOS_7F  && pc.getInventory().checkItem(ID_TOS_CTRL_7F, 1))  return true;
        else if (mapId == TOS_8F  && pc.getInventory().checkItem(ID_TOS_CTRL_8F, 1))  return true;
        else if (mapId == TOS_9F  && pc.getInventory().checkItem(ID_TOS_CTRL_9F, 1))  return true;
        else if (mapId == TOS_10F && pc.getInventory().checkItem(ID_TOS_CTRL_10F, 1)) return true;

        return false;
    }

    // 消耗道具 & 取消交易
    private void consumeItemAndCancelTrade(L1PcInstance pc, L1ItemInstance item) {
        try {
            pc.getInventory().removeItem(item, 1L);
        } catch (Exception ignore) {}
        if (pc.getTradeID() != 0) {
            try {
                new L1Trade().tradeCancel(pc);
            } catch (Exception ignore) {}
        }
    }

    // 傳送到指定座標（先播特效/音效，延遲後再瞬移）
    private void doTeleport(L1PcInstance pc, int x, int y, short mapId) {
        final int pid = pc.getId();
        long now = System.currentTimeMillis();
        long last = _lastTpAt.getOrDefault(pid, 0L);
        if (now - last < TELEPORT_THROTTLE_MS) return;
        _lastTpAt.put(pid, now);

        if (pc.getX() == x && pc.getY() == y && pc.getMapId() == mapId) return;

        if (!validateTargetAndLog(pc, x, y, mapId)) {
            fallbackSafeTeleport(pc);
            return;
        }

        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

        pc.setTeleportX(x);
        pc.setTeleportY(y);
        pc.setTeleportMapId(mapId);
        pc.setTeleportHeading(5);

        // 播施法動作
        S_DoActionGFX act = new S_DoActionGFX(pid, CAST_ACTION);
        pc.sendPackets(act);
        Broadcaster.broadcastPacket(pc, act);

        // 播音效
        S_SkillSound sfx = new S_SkillSound(pid, EFFECT_ID);
        pc.sendPackets(sfx);
        Broadcaster.broadcastPacket(pc, sfx);

        // 延遲後再瞬移
        GeneralThreadPool.get().schedule(() -> {
            try {
                Teleportation.teleportation(pc);

                // 傳送後：移除絕對屏障
                if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                    pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
                    pc.startHpRegeneration();
                    pc.startMpRegeneration();
                }
            } catch (Throwable t) {}
        }, TP_DELAY_MS);
    }

    /**
     * 驗證目標是否合法：
     * - 同地圖：用 pc.getMap() 檢查範圍與可通行
     * - 跨地圖：交給 Teleportation 處理（這裡直接放行，以免依賴 WorldMap/L1WorldMap）
     */
    private boolean validateTargetAndLog(L1PcInstance pc, int x, int y, int mapId) {
        if (mapId < 0) return false;

        // 同地圖才做嚴格檢查
        if (pc.getMapId() == mapId) {
            L1Map cur = pc.getMap();
            if (cur == null) return false;
            if (!cur.isInMap(x, y)) return false;
            try {
                if (!cur.isPassable(x, y)) return false;
            } catch (Throwable ignore) {}
            return true;
        }

        // 跨地圖：這裡不做 isInMap/isPassable（避免依賴 WorldMap 類別）
        // 由 Teleportation.teleportation(pc) 內部處理邊界/落點安全
        return true;
    }


    // 後備安全點
    private void fallbackSafeTeleport(L1PcInstance pc) {
        L1Location loc = randomSafeLocation(pc, 200, 80);
        doTeleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId());
    }

    // 半徑內隨機安全點
    private L1Location randomSafeLocation(L1PcInstance pc, int radius, int maxTrial) {
        L1Location base = pc.getLocation();
        L1Location found = base;
        for (int i = 0; i < maxTrial; i++) {
            L1Location cand = base.randomLocation(radius, true);
            if (cand.getX() != pc.getX() || cand.getY() != pc.getY()) {
                found = cand;
                break;
            }
        }
        return found;
    }
}
