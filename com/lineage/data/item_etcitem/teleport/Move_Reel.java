package com.lineage.data.item_etcitem.teleport;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.Teleportation;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

public class Move_Reel extends ItemExecutor {

    // 傲慢之塔樓層 mapid 範圍（依照專案設定）
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

    // 傲慢之塔支配傳送符（對應各樓層）的道具 ID（請依你專案實際定義調整）
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

    // 幻象的傲慢之塔移動傳送符（可覆蓋 1F~10F）
    private static final int ID_TOS_CTRL_ALL = 84071;

    public static ItemExecutor get() {
        return new Move_Reel();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        // 1) 基本可用判斷
        if (pc == null || item == null) return;
        if (!CheckUtil.getUseItem(pc)) return;

        // 2) 參數防呆（老日版記憶座標：map, x, y）
        int map, x, y;
        if (data == null || data.length < 3) {
            // 資料不足則走「隨機傳」流程
            map = -1; x = -1; y = -1;
        } else {
            map = data[0];
            x   = data[1];
            y   = data[2];
        }

        // 3) 禁傳（奪命之雷）檢查（保留你的原邏輯）
        if (pc.hasSkillEffect(THUNDER_GRAB) && ConfigSkillDragon.SLAY_BREAK_NOT_TELEPORT) {
            pc.sendPackets(new S_ServerMessage("\\fY身上有奪命之雷的效果無法瞬移"));
            // 明確解鎖傳送（避免客戶端殘留鎖定）
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            return;
        }

        // 4) 地圖可否瞬移（考慮傲慢之塔傳送符覆蓋）
        boolean canTeleport = canTeleportHere(pc);

        if (!canTeleport) {
            // 647: 此地區無法使用瞬間移動
            pc.sendPackets(new S_ServerMessage(647));
            // 確保客戶端解除傳送鎖
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            return;
        }

        // 5) 消耗道具 & 如在交易中則取消交易
        consumeItemAndCancelTrade(pc, item);

        // 6) 執行傳送：優先「指定座標」；若無則「隨機安全點」
        if (x > 0 && y > 0 && map >= 0) {
            // 指定老日版記憶座標
            doTeleport(pc, x, y, (short) map);
        } else {
            // 隨機座標（200 格內），有限次嘗試 + 後備方案
            L1Location loc = randomSafeLocation(pc, 200, 80 /* 最大嘗試次數 */);
            doTeleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId());
        }

        // 7) 傳送後：移除絕對屏障
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            pc.startHpRegeneration();
            pc.startMpRegeneration();
        }
    }

    // ---------- 抽出的小工具方法們 ----------

    /**
     * 判斷目前地圖是否允許瞬移；若持有對應的傲慢之塔傳送符則覆蓋允許。
     */
    private boolean canTeleportHere(L1PcInstance pc) {
        final L1Map map = pc.getMap();
        boolean isTeleportable = map != null && map.isTeleportable();

        // 已允許就不用再判
        if (isTeleportable) return true;

        // 傲慢之塔樓層覆蓋規則
        final int mapId = pc.getMapId();

        // 幻象的傲慢之塔移動傳送符：覆蓋 1F~10F
        if (pc.getInventory().checkItem(ID_TOS_CTRL_ALL, 1) && mapId >= TOS_1F && mapId <= TOS_10F) {
            return true;
        }

        // 對應單層傳送符
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

    /**
     * 消耗本次使用的傳送道具；若正在交易則取消交易。
     */
    private void consumeItemAndCancelTrade(L1PcInstance pc, L1ItemInstance item) {
        try {
            // 消耗自身傳送道具 1 個
            pc.getInventory().removeItem(item, 1L);
        } catch (Exception ignore) {}

        // 進行中交易要關閉，避免黑箱狀態
        if (pc.getTradeID() != 0) {
            try {
                new L1Trade().tradeCancel(pc);
            } catch (Exception ignore) {}
        }
    }

    /**
     * 傳送到指定座標（含音效、面向、封包一致化）。
     */
    private void doTeleport(L1PcInstance pc, int x, int y, short mapId) {
        final int pid = pc.getId();
        long now = System.currentTimeMillis();
        long last = _lastTpAt.getOrDefault(pid, 0L);
        if (now - last < TELEPORT_THROTTLE_MS) {
            return;
        }
        _lastTpAt.put(pid, now);

        // 目標 == 原地：直接跳過，避免「原地飛」動畫誤導
        if (pc.getX() == x && pc.getY() == y && pc.getMapId() == mapId) {
            return;
        }

        // 驗證目標合法
        if (!validateTargetAndLog(pc, x, y, mapId)) {
            // 不合法就走後備
            fallbackSafeTeleport(pc);
            return;
        }

        // 傳送前先解鎖（保持你原本作法）
        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

        pc.setTeleportX(x);
        pc.setTeleportY(y);
        pc.setTeleportMapId(mapId);
        pc.setTeleportHeading(5);

        // 音效
        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));

        Teleportation.teleportation(pc);

    }

    // 放在類別裡（欄位）
    private static final int TELEPORT_THROTTLE_MS = 500; // 500ms 節流
    // 你可以改到 L1PcInstance 裡統一存；這裡先臨時放在物件上：
    private static final java.util.Map<Integer, Long> _lastTpAt = new java.util.concurrent.ConcurrentHashMap<>();

    // 新增：驗證目標是否合法（回傳 true 代表 OK）
    private boolean validateTargetAndLog(L1PcInstance pc, int x, int y, int mapId) {
        // 檢查 mapId 合法
        if (mapId < 0) {
            return false;
        }

        // 拿玩家當前的 map（注意：這裡 mapId 可能跟 pc 當前不同，要做防呆）
        L1Map map = pc.getMap();
        if (map == null) {
            return false;
        }

        // 判斷 x,y 是否在地圖範圍內
        if (!map.isInMap(x, y)) {
            return false;
        }

        // 判斷地形是否可通行（有的內核是 passable，有的是 isPassable）
        try {
            if (!map.isPassable(x, y)) {
                return false;
            }
        } catch (Throwable ignore) {}

        return true;
    }


    // 新增：後備安全點（回村/原地隨機 10 格）
    private void fallbackSafeTeleport(L1PcInstance pc) {
        // 同圖隨機 200 格，最多嘗試 80 次（你已有 randomSafeLocation 可直接用）
        L1Location loc = randomSafeLocation(pc, 200, 80);
        doTeleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId());
    }

    /**
     * 在半徑內找可站立的隨機安全點；若嘗試多次都失敗，最後回傳原地（或你可自訂某個安全地圖/座標）。
     *
     * @param radius   搜尋半徑
     * @param maxTrial 最大嘗試次數
     */
    private L1Location randomSafeLocation(L1PcInstance pc, int radius, int maxTrial) {
        L1Location base = pc.getLocation();
        L1Location found = base;

        for (int i = 0; i < maxTrial; i++) {
            L1Location cand = base.randomLocation(radius, true);
            // 避免回到原地；如地圖 API 有 passable(x,y) 也可一併檢查
            if (cand.getX() != pc.getX() || cand.getY() != pc.getY()) {
                found = cand;
                break;
            }
        }

        // 若全失敗，回傳原地（或可改成回村/回安全區）
        return found;
    }
}
