package com.lineage.server.timecontroller.server;

import com.eric.StartCheckWarTime;
import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.datatables.CastleWarGiftTable;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.S_PacketBoxWar;
import com.lineage.server.serverpackets.S_TestPacket;
import com.lineage.server.serverpackets.S_WarIcon;
import com.lineage.server.serverpackets.S_WarNameAndTime;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.lineage.server.model.skill.L1SkillId.Red_Knight;

public class ServerWarExecutor {
    private static final Log _log = LogFactory.getLog(ServerWarExecutor.class);
    private static ServerWarExecutor _instance;
    private final boolean[] _is_replaced = new boolean[8];
    private L1Castle[] _l1castle = new L1Castle[8];
    private Calendar[] _war_start_time = new Calendar[8];
    private Calendar[] _war_end_time = new Calendar[8];
    private boolean[] _is_now_war = new boolean[8];
    private String[] _castleName = {"肯特", "妖魔", "風木", "奇巖", "海音", "侏儒", "亞丁", "狄亞得要塞"};

    private ServerWarExecutor() {
        for (int i = 0; i < _l1castle.length; i++) {
            _l1castle[i] = CastleReading.get().getCastleTable(i + 1);
            /** [原碼] 修正攻城逾時不更新 */
            Calendar nowTime = getRealTime();
            Calendar oldTime = getRealTime();
            oldTime.setTime(_l1castle[i].getWarTime().getTime());
            _war_start_time[i] = _l1castle[i].getWarTime();
            oldTime.add(ConfigAlt.ALT_WAR_TIME_UNIT, ConfigAlt.ALT_WAR_TIME);
            if (StartCheckWarTime.getInstance().isActive(_l1castle[i].getId()) && nowTime.after(oldTime)) {
                if (i == 1) {// 妖魔城堡
                    nowTime.add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL - 3);
                } else {
                    nowTime.add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL);
                }
                _l1castle[i].setWarTime(nowTime);
                CastleReading.get().updateCastle(_l1castle[i]);
            }
            /** End */
            _war_end_time[i] = ((Calendar) _l1castle[i].getWarTime().clone());
            _war_end_time[i].add(ConfigAlt.ALT_WAR_TIME_UNIT, ConfigAlt.ALT_WAR_TIME);
        }
    }

    public static ServerWarExecutor get() {
        if (_instance == null) {
            _instance = new ServerWarExecutor();
        }
        return _instance;
    }

    public Calendar getRealTime() {
        TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        Calendar cal = Calendar.getInstance(_tz);
        return cal;
    }

    public boolean isNowWar() {
        return this._is_now_war[0] || this._is_now_war[1] || this._is_now_war[2] || this._is_now_war[3] || this._is_now_war[4] || this._is_now_war[5] || this._is_now_war[6] || this._is_now_war[7];
    }

    public boolean isNowWar(int castle_id) {
        return _is_now_war[(castle_id - 1)];
    }

    public void setReplaced(final int castle_id) {
        _is_replaced[castle_id - 1] = true;
    }

    public void setWarTime(int castle_id, Calendar calendar) {
        _war_start_time[(castle_id - 1)] = ((Calendar) calendar.clone());
    }

    public void setEndWarTime(int castle_id, Calendar calendar) {
        _war_end_time[(castle_id - 1)] = ((Calendar) calendar.clone());
        _war_end_time[(castle_id - 1)].add(ConfigAlt.ALT_WAR_TIME_UNIT, ConfigAlt.ALT_WAR_TIME);
    }

    /**
     * 發佈訊息給予上線玩家
     *
     */
    public void checkCastleWar(final L1PcInstance player) {
        for (int i = 0; i < 8; i++) {
            if (this._is_now_war[i]) {
                // (641) %s的攻城戰正在進行中。
                player.sendPackets(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_GOING, i + 1));
                player.sendPackets(new S_TestPacket(S_TestPacket.a, 6298, 1512, "00 ff ff"));// 屏幕顯示 攻城戰進行中
                //player.sendPackets(new S_TestPacket(S_TestPacket.a, 6300, 3892, "00 ff ff"));// 屏幕顯示 特羅斯-城堡順利攻下，這次攻城的MVP為%s 將給予獎勵金幣%s！
                //player.sendPackets(new S_TestPacket(S_TestPacket.a, 6300, 3656, "00 ff ff"));// 屏幕顯示 攻城-紅騎士團在攻城戰中勝利了
            }
        }
    }

    /**
     * 戰爭中城堡數量
     *
     */
    public int checkCastleWar() {
        int x = 0;
        for (int i = 0; i < 8; i++) {
            if (this._is_now_war[i]) {
                x++;
            }
        }
        return x;
    }

    protected void checkWarTime() {
        try {
            for (int i = 0; i < 8; i++) {// i + 1 = 城堡編號
                final Calendar now = getRealTime();
                if (this._war_start_time[i].before(now) // 戰爭開始
                        && this._war_end_time[i].after(now)) {
                    if (this._is_now_war[i] == false) {
                        this._is_now_war[i] = true;
                        World.get().broadcastPacketToAll(new S_TestPacket(S_TestPacket.a, 6298, 1510, "00 ff ff"));// 屏幕顯示 攻城-攻城戰已開啟
                        // 歸0殺人次數
                        CharOtherReading.get().tam();
                        // 召喚戰爭範圍旗幟
                        final L1WarSpawn warspawn = new L1WarSpawn();
                        if (ConfigOtherSet2.Red_Knight) { // 是否開啟召喚騎士團
                            L1Clan KnightClan = null;
                            for (L1Clan checkClan : WorldClan.get().getAllClans()) {
                                if (checkClan.getCastleId() == i + 1) {
                                    KnightClan = checkClan;
                                    warspawn.BlackKnight(i + 1, KnightClan); // 召喚守城:黑騎士團
                                    warspawn.RedKnight(i + 1, KnightClan); // 召喚攻城:紅騎士團
                                    World.get().broadcastPacketToAll(new S_TestPacket(S_TestPacket.a, 6300, 3751, "00 ff ff"));// 屏幕顯示 現在開始募集紅騎士團成員
                                }
                            }
                        }
                        warspawn.SpawnFlag(i + 1);
                        // 關閉城門修理
                        for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
                            if (L1CastleLocation.checkInWarArea(i + 1, door)) {
                                door.repairGate();
                            }
                        }
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        final String time = sdf.format(this._war_start_time[i].getTime());
                        _log.info(_castleName[i] + " 的攻城戰開始。時間: " + time);
                        // (639) %s的攻城戰開始。
                        World.get().broadcastPacketToAll(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_BEGIN, i + 1));
                        int[] loc = new int[3];
						/*for (final L1PcInstance pc : World.get().getAllPlayers()) {
							final int castleId = i + 1;
							if (L1CastleLocation.checkInWarArea(castleId, pc) && !pc.isGm()) { // 戰爭範圍旗幟內
								final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
								if (clan != null) {
									if (clan.getCastleId() == castleId) { // 城盟成員
										continue;
									}
								}
								loc = L1CastleLocation.getGetBackLoc(castleId);
								L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
							}
						}
					}*/
                        for (final L1PcInstance pc : World.get().getAllPlayers()) {
                            final int castleId = i + 1;
                            if (L1CastleLocation.checkInWarArea(castleId, pc)) { // 戰爭範圍旗幟內
                                if (!pc.isGm()) {
                                    final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                                    if (clan != null) {
                                        if (clan.getCastleId() == castleId) { // 城盟成員
                                            continue;
                                        }
                                    }
                                    loc = L1CastleLocation.getGetBackLoc(castleId);
                                    L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                                }
                                // 給予時間
                                sendIcon(castleId, pc);
                            }
                        }
                    }
                } else if (this._war_end_time[i].before(now)) { // 攻城戰結束
                    if (this._is_now_war[i] == true) {
                        this._is_now_war[i] = false;
                        // (640) %s的攻城戰結束。
                        World.get().broadcastPacketToAll(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_END, i + 1));
                        World.get().broadcastPacketToAll(new S_TestPacket(S_TestPacket.a, 6298, 1511, "00 ff ff")); // 屏幕顯示 攻城戰已經結束
                        if (i == 1) {// 妖魔城堡
                            this._war_start_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL - 3);
                            this._war_end_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL - 3);
                        } else {
                            this._war_start_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL);
                            this._war_end_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL);
                        }
                        this._l1castle[i].setTaxRate(10); // 稅率10%
                        this._l1castle[i].setPublicMoney(0); // 公金
                        CastleReading.get().updateCastle(this._l1castle[i]);
                        final int castle_id = i + 1;
                        final List<L1War> list = WorldWar.get().getWarList();
                        for (L1War war : list) {
                            if (war.get_castleId() == castle_id) {
                                war.ceaseCastleWar();// 城堡戰爭時間終止,防禦方獲勝
                            }
                        }
                        for (final L1Object l1object : World.get().getObject()) {
                            // 攻城戰旗幟消除
                            if (l1object instanceof L1FieldObjectInstance) {
                                final L1FieldObjectInstance flag = (L1FieldObjectInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, flag)) {
                                    flag.deleteMe();
                                }
                            }
                            // 地面王冠物件刪除
                            if (l1object instanceof L1CrownInstance) {
                                final L1CrownInstance crown = (L1CrownInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, crown)) {
                                    crown.deleteMe();
                                }
                            }
                            // 守護者之塔消除
                            if (l1object instanceof L1TowerInstance) {
                                final L1TowerInstance tower = (L1TowerInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
                                    tower.deleteMe();
                                }
                            }
                            // 消除時間
                            if (l1object instanceof L1PcInstance) {
                                final L1PcInstance tgpc = (L1PcInstance) l1object;
                                tgpc.sendPackets(new S_WarNameAndTime());
                                if (tgpc.hasSkillEffect(Red_Knight)) {
                                    tgpc.killSkillEffectTimer(Red_Knight); // 刪除紅騎士團徽章狀態
                                    tgpc.sendPackets(new S_WarIcon(tgpc.getId()));
                                    tgpc.broadcastPacketAll(new S_WarIcon(tgpc.getId()));
                                }
                            }
                            // 投石器刪除
                            if (l1object instanceof L1CatapultInstance) {
                                L1CatapultInstance catapult = (L1CatapultInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, catapult)) {
                                    catapult.deleteMe();
                                }
                            }
                            // 刪除紅騎士團
                            if (l1object instanceof L1RedKnightInstance) {
                                L1RedKnightInstance RedKnight = (L1RedKnightInstance) l1object;
                                if (RedKnight != null) {
                                    RedKnight.deleteMe();
                                }
                            }
                            // 刪除黑騎士團
                            if (l1object instanceof L1BlackKnightInstance) {
                                L1BlackKnightInstance BlackKnight = (L1BlackKnightInstance) l1object;
                                if (BlackKnight != null) {
                                    BlackKnight.deleteMe();
                                }
                            }
                        }
                        // 重新召換守護者之塔和投石器
                        final L1WarSpawn warspawn = new L1WarSpawn();
                        warspawn.spawnTower(castle_id);
                        warspawn.SpawnCatapult(castle_id);
                        // 恢復城門
                        for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
                            if (L1CastleLocation.checkInWarArea(castle_id, door)) {
                                door.repairGate();
                            }
                        }
                        // 戰爭結束訊息
                        World.get().broadcastPacketToAll(new S_PacketBoxWar());
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        final String time = sdf.format(now.getTime());
                        _log.info(_castleName[i] + " 的攻城戰結束。時間: " + time);
                        // 攻城獎勵
                        CastleWarGiftTable.get().get_gift(castle_id);
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void sendIcon(final int castle_id, final L1PcInstance pc) {
        pc.sendPackets(new S_WarNameAndTime(_is_replaced[castle_id - 1] ? true : false, (int) ((_war_end_time[castle_id - 1].getTimeInMillis() - getRealTime().getTimeInMillis()) / 1000), L1CastleLocation.castleClan(castle_id) != null ? L1CastleLocation.castleClan(castle_id).getClanName() : _l1castle[castle_id - 1].getName()));
    }
}
