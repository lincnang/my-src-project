package com.lineage.server.clientpackets;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.datatables.DungeonRTable;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1PolyRace;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldTrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 要求角色移動 基本封包長度:
 *
 * @author daien
 */
public class C_MoveChar extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_MoveChar.class);
    /*
     * public C_MoveChar() { }
     *
     * public C_MoveChar(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    private static final byte HEADING_TABLE_X[] = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte HEADING_TABLE_Y[] = {-1, -1, 0, 1, 1, 1, 0, -1};

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            /*
             * final long noetime = System.currentTimeMillis(); if (noetime -
             * pc.get_spr_move_time() <=
             * SprTable.get().spr_move_speed(pc.getTempCharGfx())) { if
             * (!pc.isGm()) { pc.getNetConnection().kick(); return; } }
             * pc.set_spr_move_time(noetime);
             */
            if (pc.isDead()) {// 死亡
                return;
            }
            if (pc.isTeleport()) { // 順移處理作業
                return;
            }
            // 黑暗之手 左右移動邊更
            if (pc.hasSkillEffect(HAND_DARKNESS) || pc.hasSkillEffect(HAND_DARKNESS)) {
                return;
            }
            if (pc.hasSkillEffect(Phantom_Blade)) {
                return;
            }
            int locx;// 目前位置
            int locy;
            int heading;
            try {
                locx = this.readH();
                locy = this.readH();
                heading = this.readC();
                if (Config.CLIENT_LANGUAGE == 5) {
                    heading %= 8;
                }
            } catch (final Exception e) {
                // 座標取回失敗
                return;
            }
            // 移動中, 取消交易
            if (pc.getTradeID() != 0) {
                L1Trade trade = new L1Trade();
                trade.tradeCancel(pc);
            }
            pc.killSkillEffectTimer(MEDITATION);// 解除冥想術
            pc.setCallClanId(0); // 人物移動呼喚盟友無效
            if (!pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 絕對屏障狀態
                pc.setRegenState(REGENSTATE_MOVE);
            }
            // 解除舊座標障礙宣告
            pc.getMap().setPassable(pc.getLocation(), true);
            if (Config.CLIENT_LANGUAGE == 3) {
                if (heading > 7) { // Taiwan Only
                    heading ^= 0x49;// 換位
                    locx = pc.getX();
                    locy = pc.getY();
                }
                heading = Math.min(heading, 7);
            }
            // 移動前位置
            final int oleLocx = pc.getX();
            final int oleLocy = pc.getY();
            // 移動後位置
            final int newlocx = locx + HEADING_TABLE_X[heading];
            final int newlocy = locy + HEADING_TABLE_Y[heading];
            try {
                // 不允許穿過該點
                boolean isError = (locx != oleLocx) && (locy != oleLocy); // 異位判斷(封包數據 與 核心數據 不吻合)
                // 商店村模式
                if (pc.isPrivateShop()) {
                    isError = true;
                }
                // 無法攻擊/使用道具/技能/回城的狀態
                if (pc.isParalyzedX()) {
                    isError = true;
                }
                if (!isError) {
                    boolean isPassable = pc.getMap().isPassable(oleLocx, oleLocy, heading, null);
                    if (!isPassable) {
                        if (CheckUtil.checkPassable(pc, newlocx, newlocy, pc.getMapId())) {
                            isError = true;
                        }
                    }
                }
                final int move_tile = pc.getMap().getOriginalTile(locx, locy);
                if (!((move_tile & 1) == 1 || (move_tile & 2) == 2)) {
                    isError = true;
                }
                if (pc.hasSkillEffect(L1SkillId.MOVE_STOP)) {
                    isError = true;
                }
                if (isError) {
                    pc.setTeleportX(oleLocx);
                    pc.setTeleportY(oleLocy);
                    pc.setTeleportMapId(pc.getMapId());
                    pc.sendPackets(new S_Lock());
                    return;
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            final int result = pc.speed_Attack().checkInterval(ACT_TYPE.MOVE);
            final int polyId = pc.getTempCharGfx();
            if (result == AcceleratorChecker.R_DISPOSED) {
                _log.info("角色移動異常: " + pc.getName() + " 變身編號:" + polyId);
            } //src042
            // 檢查地圖使用權
            CheckUtil.isUserMap(pc);
            // 地圖切換
            if (DungeonTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
                return;
            }
            // 地圖切換(多點)
            if (DungeonRTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
                return;
            }
            // 記錄移動前座標
            pc.setOleLocX(oleLocx);
            pc.setOleLocY(oleLocy);
            // 設置新作標點
            pc.getLocation().set(newlocx, newlocy);
            // 設置新面向
            pc.setHeading(heading);
            if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
                // 送出移動封包
                pc.broadcastPacketAll(new S_MoveCharPacket(pc));
                /* 精準目標特效跟隨 */
                pc.setTrueTargetMoveSpeed();
            }
            // 寵物競速圈數檢查
            L1PolyRace.getInstance().checkLapFinish(pc);
            // 設置娃娃移動
            pc.setNpcSpeed();
            // 新增座標障礙宣告
            if (!pc.isGmInvis()) {// 排除GM隱身
                pc.getMap().setPassable(pc.getLocation(), false);
            }
            // 踩到陷阱的處理 排除GM隱身
            if (!pc.isGmInvis()) {
                WorldTrap.get().onPlayerMoved(pc);
            }
            // vip光圈跟隨系統
            pc.setSkinMoveSpeed();
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
            boolean isIn = false;
            if ((pc.getMapId() == 4) || (pc.getMapId() == 15) || (pc.getMapId() == 260) || (pc.getMapId() == 29) || (pc.getMapId() == 52) || (pc.getMapId() == 64) || (pc.getMapId() == 66) || (pc.getMapId() == 300) || (pc.getMapId() == 320) || (pc.getMapId() == 330)) {
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
            }
            // 攻城期間是否可以招喚寵物
            if (ConfigAlt.WAR_summon && !pc.getPetList().isEmpty() && L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
                for (Object petList : pc.getPetList().values().toArray()) {
                    if (petList instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) petList;
                        //final S_NewMaster packet = new S_NewMaster(summon);
                        if (summon.destroyed()) {
                            return;
                        }
                        if (summon.tamed()) {
                            // 召喚獸解放
                            summon.liberate();
                        } else {
                            // 解散
                            summon.Death(null);
                        }
                    }
                }
            }
            // 限制攻城招喚寵物
            if (!ConfigOtherSet2.war_pet_summ && !pc.getPetList().isEmpty() && L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
                Object[] array;
                int length = (array = pc.getPetList().values().toArray()).length;
                int i = 0;
                while (i < length) {
                    Object obj = array[i];
                    L1NpcInstance petObject = (L1NpcInstance) obj;
                    if (petObject != null) {
                        if (petObject instanceof L1PetInstance) {
                            L1PetInstance pet = (L1PetInstance) petObject;
                            pet.collect(true);
                            pc.removePet(pet);
                            pet.deleteMe();
                        }
                        if (petObject instanceof L1SummonInstance) {
                            L1SummonInstance summon = (L1SummonInstance) petObject;
                            new S_NewMaster(summon);
                            if (summon.destroyed()) {
                                return;
                            }
                            summon.Death(null);
                        }
                    }
                    i++;
                }
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    // @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
