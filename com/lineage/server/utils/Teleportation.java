package com.lineage.server.utils;

import com.lineage.config.ConfigFreeKill;
import com.lineage.server.CheckFightTimeController;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.timecontroller.pc.MapTimerThread;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.lineage.server.model.skill.L1SkillId.Red_Knight;

public class Teleportation {
    private static final Log _log = LogFactory.getLog(Teleportation.class);

    public static void teleportation(L1PcInstance pc) {
        try {
            if (pc == null) {
                return;
            }
            if (pc.getOnlineStatus() == 0) {
                return;
            }
            if (pc.getNetConnection() == null) {
                return;
            }
            if (pc.isDead()) {
                return;
            }
            if (pc.isTeleport()) {
                return;
            }
            if (!CheckUtil.getUseItem(pc)) {
                return;
            }
            // 解除舊座標障礙宣告
            pc.getMap().setPassable(pc.getLocation(), true);
            // 安全區域右下顯示死亡懲罰狀態圖示
            if (pc.getZoneType() == 0) {
                if (pc.getSafetyZone()) {
                    pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, false));
                    pc.setSafetyZone(false);
                }
            } else {
                if (!pc.getSafetyZone()) {
                    pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, true));
                    pc.setSafetyZone(true);
                }
            }
			/*boolean isIn = false;
			if ((pc.getMapId() == 4) || (pc.getMapId() == 15) || (pc.getMapId() == 260) || (pc.getMapId() == 29)
					|| (pc.getMapId() == 52) || (pc.getMapId() == 64) || (pc.getMapId() == 66)
					|| (pc.getMapId() == 300) || (pc.getMapId() == 320) || (pc.getMapId() == 330)) {
				final int castle_id = L1CastleLocation.getCastleIdByArea(pc);
				if (castle_id > 0) {
					if (ServerWarExecutor.get().isNowWar(castle_id)) {
						// 战争资讯
						if (!pc.hasSkillEffect(L1SkillId.WAR_INFORMATION)) {
							pc.setSkillEffect(L1SkillId.WAR_INFORMATION, 0);
							ServerWarExecutor.get().sendIcon(castle_id, pc);
						}
						isIn = true;
					}
				}
			}
			if (!isIn) {
				if (pc.hasSkillEffect(L1SkillId.WAR_INFORMATION)) {
					pc.sendPackets(new S_WarNameAndTime());
					pc.killSkillEffectTimer(L1SkillId.WAR_INFORMATION);
				}
			}*/
            short mapId = pc.getTeleportMapId();
            if ((pc.isDead()) && (mapId != 9101)) {
                return;
            }
            int x = pc.getTeleportX();
            int y = pc.getTeleportY();
            int head = pc.getTeleportHeading();
            // 玩家傳送前所在地圖 by terry0412
            final short pc_mapId = pc.getMapId();
            L1Map map = L1WorldMap.get().getMap(mapId);
            if ((!map.isInMap(x, y)) && (!pc.isGm())) {
                x = pc.getX();
                y = pc.getY();
                mapId = pc.getMapId();
            }
            if (pc.getMapId() != mapId) { // 若傳送地圖出現變化 停止自動狩獵
                pc.stopPcAI();
            }
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if ((clan != null) && (clan.getWarehouseUsingChar() == pc.getId())) {
                clan.setWarehouseUsingChar(0);
            }
            World.get().moveVisibleObject(pc, mapId);
            pc.setLocation(x, y, mapId);
            pc.setHeading(head);
            pc.setOleLocX(x);
            pc.setOleLocY(y);
            boolean isUnderwater = pc.getMap().isUnderwater();
            pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));
            if ((!pc.isGhost()) && (!pc.isGmInvis()) && (!pc.isInvisble())) {
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
            }
            if (pc.isReserveGhost()) {
                pc.endGhost();
            }
            pc.sendPackets(new S_OwnCharPack(pc));
            pc.removeAllKnownObjects();
            pc.updateObject();
            pc.sendVisualEffectAtTeleport();
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.setCallClanId(0);
            final HashSet<L1PcInstance> subjects = new HashSet<>();
            subjects.add(pc);
            if (!pc.isGhost()) {
                // 可以攜帶寵物
                if (pc.getMap().isTakePets()) {
                    // 寵物的跟隨移動
                    for (final L1NpcInstance petNpc : pc.getPetList().values()) {
                        // 主人身邊隨機座標取回
                        final L1Location loc = pc.getLocation().randomLocation(3, false);
                        int nx = loc.getX();
                        int ny = loc.getY();
                        if ((pc.getMapId() == 5125) || (pc.getMapId() == 5131) || (pc.getMapId() == 5132) || (pc.getMapId() == 5133) || (pc.getMapId() == 5134)) { // 寵物戰戰場
                            nx = 32799 + ThreadLocalRandom.current().nextInt(5) - 3;
                            ny = 32864 + ThreadLocalRandom.current().nextInt(5) - 3;
                        }
                        // 設置副本編號
                        petNpc.set_showId(pc.get_showId());
                        teleport(petNpc, nx, ny, mapId, head);
                        if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
                            final L1SummonInstance summon = (L1SummonInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Summon(summon, pc));
                        } else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
                            final L1PetInstance pet = (L1PetInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Pet(pet, pc));
                        }
                        for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
                            // 畫面內可見人物 認識更新
                            visiblePc.removeKnownObject(petNpc);
                            if (visiblePc.get_showId() == petNpc.get_showId()) {
                                subjects.add(visiblePc);
                            }
                        }
                    }
                }
                // 娃娃的跟隨移動
                if (!pc.getDolls().isEmpty()) {
                    // 主人身邊隨機座標取回
                    final L1Location loc = pc.getLocation().randomLocation(3, false);
                    final int nx = loc.getX();
                    final int ny = loc.getY();
                    final Object[] dolls = pc.getDolls().values().toArray();
                    for (final Object obj : dolls) {
                        final L1DollInstance doll = (L1DollInstance) obj;
                        teleport(doll, nx, ny, mapId, head);
                        pc.sendPackets(new S_NPCPack_Doll(doll, pc));
                        // 設置副本編號
                        doll.set_showId(pc.get_showId());
                        for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
                            // 畫面內可見人物 認識更新
                            visiblePc.removeKnownObject(doll);
                            if (visiblePc.get_showId() == doll.get_showId()) {
                                subjects.add(visiblePc);
                                subjects.add(visiblePc);
                            }
                        }
                    }
                }
                // 娃娃的跟隨移動
                if (!pc.getDolls2().isEmpty()) {
                    // 主人身邊隨機座標取回
                    final L1Location loc = pc.getLocation().randomLocation(3, false);
                    final int nx = loc.getX();
                    final int ny = loc.getY();
                    final Object[] dolls = pc.getDolls2().values().toArray();
                    for (final Object obj : dolls) {
                        final L1DollInstance2 doll = (L1DollInstance2) obj;
                        teleport(doll, nx, ny, mapId, head);
                        pc.sendPackets(new S_NPCPack_Doll(doll, pc));
                        // 設置副本編號
                        doll.set_showId(pc.get_showId());
                        for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
                            // 畫面內可見人物 認識更新
                            visiblePc.removeKnownObject(doll);
                            if (visiblePc.get_showId() == doll.get_showId()) {
                                subjects.add(visiblePc);
                                subjects.add(visiblePc);
                            }
                        }
                    }
                }
                // 取回娃娃
                if (pc.get_power_doll() != null) {
                    // 主人身邊隨機座標取回
                    final L1Location loc = pc.getLocation().randomLocation(3, false);
                    final int nx = loc.getX();
                    final int ny = loc.getY();
                    teleport(pc.get_power_doll(), nx, ny, mapId, head);
                    pc.sendPackets(new S_NPCPack_Doll(pc.get_power_doll(), pc));
                    // 設置副本編號
                    pc.get_power_doll().set_showId(pc.get_showId());
                    for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(pc.get_power_doll())) {
                        // 畫面內可見人物 認識更新
                        visiblePc.removeKnownObject(pc.get_power_doll());
                        if (visiblePc.get_showId() == pc.get_power_doll().get_showId()) {
                            subjects.add(visiblePc);
                        }
                    }
                }
            }
            // 精準目標特效的跟隨移動
            final ArrayList<L1EffectInstance> effectlist = pc.get_TrueTargetEffectList();
            for (L1EffectInstance effect : effectlist) {
                final L1Location loc = pc.getLocation();
                final int nx = loc.getX();
                final int ny = loc.getY();
                teleport(effect, nx, ny, mapId, head);
                pc.sendPackets(new S_NPCPack_Eff(effect));
                // 設置副本編號
                effect.set_showId(pc.get_showId());
                for (final L1PcInstance knownPc : effect.getKnownPlayers()) {
                    // 已認識玩家 加入更新清單
                    knownPc.removeKnownObject(effect);
                    if (knownPc.get_showId() == effect.get_showId()) {
                        knownPc.addKnownObject(effect);
                        knownPc.sendPackets(new S_NPCPack_Eff(effect));
                    }
                }
            }
            // VIP光圈跟隨
            if (pc.getSkins() != null) {
                Map<Integer, L1SkinInstance> skinList = pc.getSkins();
                for (Integer gfxid : skinList.keySet()) {
                    L1SkinInstance skin = (L1SkinInstance) skinList.get(gfxid);
                    teleport(skin, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading());
                    pc.sendPackets(new S_NPCPack_Skin(skin));
                    // 設置副本編號
                    skin.set_showId(pc.get_showId());
                    for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(skin)) {
                        // 畫面內可見人物 認識更新
                        visiblePc.removeKnownObject(skin);
                        if (visiblePc.get_showId() == skin.get_showId()) {
                            subjects.add(visiblePc);
                        }
                    }
                }
            }
            /*龍之祝福null查修問題跳過 by老爹 5/26
            if (pc == null || pc.getInventory() == null) {
                // 记录错误日志或者进行其他处理
                System.err.println("Error: L1PcInstance or its inventory is null.");
                return;
            }
            */
            // 武器劍靈系統 - 分身
            if (pc != null && pc.get_otherList() != null && pc.get_otherList().get_illusoryList() != null) {
                if (!pc.get_otherList().get_illusoryList().isEmpty()) {
                    final Object[] illList = pc.get_otherList().get_illusoryList().values().toArray();
                    for (final Object obj : illList) {
                        final L1IllusoryInstance ill = (L1IllusoryInstance) obj;
                        if (ill != null) {
                            // 主人身邊隨機座標取回
                            if (pc.getLocation() != null) {
                                final L1Location loc = pc.getLocation().randomLocation(3, false);
                                if (loc != null) {
                                    final int nx = loc.getX();
                                    final int ny = loc.getY();
                                    teleport(ill, nx, ny, mapId, head);
                                    pc.sendPackets(new S_NPCPack_Ill(ill));
                                    // 設置副本編號
                                    ill.set_showId(pc.get_showId());
                                    for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(ill)) {
                                        visiblePc.removeKnownObject(ill);
                                        if (visiblePc.get_showId() == ill.get_showId()) {
                                            subjects.add(visiblePc);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /* 可見物更新處理 */
            for (L1PcInstance updatePc : subjects) {
                updatePc.updateObject();
            }
            Integer time = ServerUseMapTimer.MAP.get(pc);
            if (time != null) {
                ServerUseMapTimer.put(pc, time);
            }
            // 地圖編號前後有變動 by terry0412
            if (pc_mapId != mapId) {
                // 地圖群組設置資料 (入場時間限制)
                final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(mapId);
                if (mapsLimitTime != null) {
                    final int order_id = mapsLimitTime.getOrderId();
                    final int used_time = pc.getMapsTime(order_id);
                    final int effective_limit = mapsLimitTime.getLimitTime() + pc.getMapsBonusTime(order_id);
                    if (used_time < effective_limit) {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, effective_limit - used_time));
                    }
                }
                if (ConfigFreeKill.FREE_FIGHT_SWITCH) // src015
                {
                    if (CheckFightTimeController.getInstance().isFightMap(mapId)) {
                        pc.sendPackets(new S_BlueMessage(166, "\\f2你進入了掃街區，請小心迴避"));
                        pc.setSkillEffect(6820, 1200000);
                    }
                }
            }
            boolean isTimingmap = MapsTable.get().isTimingMap(mapId);// 傳送地圖是否為限時地圖
            /* 加入限時地圖清單 */
            if (isTimingmap) { // 是限時地圖
                // 地圖限制時間(秒數)
                final int maxMapUsetime = MapsTable.get().getMapTime(mapId) * 60;
                // 已使用秒數
                int usedtime = pc.getMapUseTime(mapId);
                // 剩餘時間(秒)
                int leftTime = (maxMapUsetime - usedtime);
                MapTimerThread.put(pc, leftTime);
                // System.out.println("加入限時地圖清單");
            } else if (MapTimerThread.TIMINGMAP.get(pc) != null) {// 在清單中
                MapTimerThread.TIMINGMAP.remove(pc);// 移出清單
                // System.out.println("移出清單");
            }
            pc.setTeleport(false);
            if (pc.hasSkillEffect(167)) {
                pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), pc.getSkillEffectTimeSec(167)));
            }
            for (final L1PcInstance IconPc : World.get().getAllPlayers()) {
                if (IconPc.hasSkillEffect(Red_Knight)) { // 紅騎士團徽章狀態
                    IconPc.sendPackets(new S_WarIcon(IconPc.getId(), 1));
                    IconPc.broadcastPacketAll(new S_WarIcon(IconPc.getId(), 1));
                }
                if (IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_1) || IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_2) || IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_3) || IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_4) || IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_5) || IconPc.hasSkillEffect(L1SkillId.MiniGameIcon_6)) { // 遊戲各類徽章狀態
                    IconPc.sendPackets(new S_MiniGameIcon(IconPc));
                    IconPc.broadcastPacketAll(new S_MiniGameIcon(IconPc));
                }
            }
            // 新增座標障礙宣告
            if (!pc.isInvisble() && !pc.isGmInvis()
                    && !pc.isGhost() && !pc.isActivated()) {
                pc.getMap().setPassable(pc.getLocation(), false);
            }
            pc.getPetModel();
            if (pc.get_vipLevel() > 0) {
                final S_VipShow vipShow = new S_VipShow(pc.getId(), pc.get_vipLevel());
                pc.sendPacketsAll(vipShow);
            }
            if (pc.hasSkillEffect(L1SkillId.AIFOREND)) { // 特效驗證系統
                int rndxx = pc.getX() + ThreadLocalRandom.current().nextInt(20) - 10;
                int rndyy = pc.getY() + ThreadLocalRandom.current().nextInt(20) - 10;
                while (CheckUtil.checkPassable(pc, rndxx, rndyy, pc.getMapId()) || !pc.getMap().isInMap(rndxx, rndyy) || pc.getMap().getOriginalTile(rndxx, rndyy) == 0 || pc.getX() == rndxx && pc.getY() == rndyy || !pc.glanceCheck(rndxx, rndyy) || !pc.getMap().isPassable(rndxx, rndyy, 0, null) || !pc.getMap().isPassable(rndxx, rndyy, 1, null) || !pc.getMap().isPassable(rndxx, rndyy, 2, null) || !pc.getMap().isPassable(rndxx, rndyy, 3, null) || !pc.getMap().isPassable(rndxx, rndyy, 4, null) || !pc.getMap().isPassable(rndxx, rndyy, 5, null) || !pc.getMap().isPassable(rndxx, rndyy, 6, null) || !pc.getMap().isPassable(rndxx, rndyy, 7, null)) {
                    rndxx = pc.getX() + ThreadLocalRandom.current().nextInt(20) - 10;
                    rndyy = pc.getY() + ThreadLocalRandom.current().nextInt(20) - 10;
                }
                int[] xyz = {rndxx, rndyy};
                pc.set_aixyz(xyz);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 寵物的傳送
     *
     */
    private static void teleport(L1NpcInstance npc, int x, int y, short map, int head) {
        try {
            World.get().moveVisibleObject(npc, map);
            L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true, 2);
            npc.setX(x);
            npc.setY(y);
            npc.setMap(map);
            npc.setHeading(head);
            //            L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false, 2);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
