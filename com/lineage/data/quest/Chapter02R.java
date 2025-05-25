package com.lineage.data.quest;

import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.templates.L1Rank;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldMob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Chapter02R extends TimerTask {
    public static final int DELAY_SPEED = 1;
    private static final Log _log = LogFactory.getLog(Chapter02R.class);
    private static final Random _random = new Random();
    private static final L1Location[] CABIN_LOC_LIST = new L1Location[4];

    static {
        CABIN_LOC_LIST[0] = new L1Location(32671, 32802, 9101);// 哈汀船艙
        CABIN_LOC_LIST[1] = new L1Location(32735, 32802, 9101);// 敵艦船艙
        CABIN_LOC_LIST[2] = new L1Location(32735, 32862, 9101);// 幽靈船艙
        CABIN_LOC_LIST[3] = new L1Location(32799, 32863, 9101);// 飛龍船艙
    }

    public final L1Party party;
    private final L1QuestUser quest;
    private final L1Location loc;
    private final L1NpcInstance door;
    private final L1NpcInstance npc1;
    private final L1NpcInstance npc2;
    public L1NpcInstance portal;
    private ScheduledFuture<?> _timer;
    private L1NpcInstance ship;
    private volatile int ship_step = -1;
    private int counter1;
    private int counter2;
    private int counter3;
    private boolean fire_order;
    private boolean defense_okay;
    private int critical_hit;
    private boolean hit_okay;
    private int counter_alt;
    private int counter_alt2;
    private L1PcInstance quest_pc;
    private int mimic_quest_order;
    private int mimic_quest_count;
    private boolean squid_version;
    private int round;
    private int old_score;
    private int check_power;
    private int mob_power;
    private int count_down;
    private int fire_point;
    private L1Location next_fire_loc;
    private boolean send_gift;

    public Chapter02R(L1QuestUser quest, L1Party party, L1NpcInstance door, L1NpcInstance npc1, L1NpcInstance npc2) {
        this.quest = quest;
        this.party = party;
        loc = new L1Location(32798, 32803, quest.get_mapid());
        this.door = door;
        this.npc1 = npc1;
        this.npc2 = npc2;
        next_fire_loc = loc.randomLocation(8, true);
    }

    public void startR() {
        int timeMillis = 10000;
        _timer = GeneralThreadPool.get().schedule(this, timeMillis);
    }

    public void run() {
        cancel();
        try {
            if (round <= 0) {
                sendPacketsToAll(new S_NpcChat(party.getLeader().getId(), "$9529"));
                sendPacketsToAll(new S_PacketBoxGree(2, "$9529"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9530"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                if (party.getLeader().get_actionId() != 68) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9531"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9532"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9533"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9534"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9535"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9536"));
                    L1NpcInstance effect1 = L1SpawnUtil.spawnT(97109, 32797, 32808, (short) quest.get_mapid(), 0, 100);
                    effect1.set_showId(quest.get_id());
                    quest.addNpc(effect1);
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9537"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9538"));
                    TimeUnit.MILLISECONDS.sleep(4000L);
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9539"));
                    L1NpcInstance effect2 = L1SpawnUtil.spawnT(97110, 32801, 32808, (short) quest.get_mapid(), 0, 100);
                    effect2.set_showId(quest.get_id());
                    quest.addNpc(effect2);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    effect1.set_spawnTime(1);
                    effect2.set_spawnTime(1);
                }
                sendPacketsToAll(new S_PacketBoxGree(2, "$9540"));
                TimeUnit.MILLISECONDS.sleep(5000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9603"));
                sendPacketsToAll(new S_PacketBoxGree(1));
                quest.spawnQuestMob(0, 5);
                TimeUnit.MILLISECONDS.sleep(10000L);
                round = 1;
            }
            for (int n = 12; round <= n; round += 1) {
                TimeUnit.MILLISECONDS.sleep(3000L);
                if (send_gift) {
                    break;
                }
                if (quest.size() < 3) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9547"));
                    break;
                }
                if (fire_point >= 10) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9562"));
                    break;
                }
                hit_okay = false;
                counter_alt = 0;
                counter_alt2 = 0;
                mimic_quest_order = -1;
                if (round % 4 == 0) {
                    critical_hit = 0;
                }
                if (fire_point >= 7) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$12097"));
                } else {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9548"));
                }
                TimeUnit.MILLISECONDS.sleep(4000L);
                int score_before = quest.get_score();
                int type = randomHintSeaMonster();
                if (type == 1) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9541"));
                    sendPacketsToAll(new S_EffectLocation(32801, 32789, 8142));
                    sendPacketsToAll(new S_EffectLocation(32798, 32820, 8142));
                } else if (type == 2) {
                    if (++counter1 < 3) {
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9542"));
                        spawnSeaMonster(97097);
                        TimeUnit.MILLISECONDS.sleep(10000L);
                    }
                } else if (type == 3) {
                    if (++counter2 < 3) {
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9543"));
                        spawnSeaMonster(97098);
                        TimeUnit.MILLISECONDS.sleep(10000L);
                    }
                } else if ((type == 4) && (++counter3 < 3)) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9544"));
                    sendPacketsToAll(new S_EffectLocation(32800, 32794, 8241));
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    sendPacketsToAll(new S_EffectLocation(32800, 32794, 8241));
                }
                TimeUnit.MILLISECONDS.sleep(10000L);
                if (round >= 12) {
                    sendPacketsToAll(new S_PacketBoxGree(2, "$9556"));
                    TimeUnit.MILLISECONDS.sleep(3000L);
                    int locx = fire_order ? 32796 : 32802;
                    int locy = 32824;
                    ship = L1SpawnUtil.spawn(97099, new L1Location(locx, locy, quest.get_mapid()), 0, quest.get_id());
                    ship.WORK.work(ship);
                } else {
                    if ((counter1 >= 3) || (counter2 >= 3) || (counter3 >= 3)) {
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9549"));
                        TimeUnit.MILLISECONDS.sleep(4000L);
                        sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$" + (9608 + round)));
                        sendPacketsToAll(new S_PacketBoxGree(1));
                        TimeUnit.MILLISECONDS.sleep(3000L);
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9558"));
                        if (counter1 >= 3) {
                            int i = 0;
                            for (int size = 3; i < size; i++) {
                                L1Location new_loc = loc.randomLocation(5, true);
                                L1SpawnUtil.spawn(97095, new_loc, 0, quest.get_id()).set_quest_id(56254);
                            }
                            counter1 = 0;
                            TimeUnit.MILLISECONDS.sleep(20000L);
                        } else if (counter2 >= 3) {
                            int i = 0;
                            for (int size = 3; i < size; i++) {
                                L1Location new_loc = loc.randomLocation(5, true);
                                L1SpawnUtil.spawn(97096, new_loc, 0, quest.get_id()).set_quest_id(56255);
                            }
                            counter2 = 0;
                            TimeUnit.MILLISECONDS.sleep(20000L);
                        } else if (counter3 >= 3) {
                            L1SpawnUtil.spawn(97103, new L1Location(32795, 32795, loc.getMapId()), 0, quest.get_id());
                            L1SpawnUtil.spawn(97104, new L1Location(32804, 32796, loc.getMapId()), 0, quest.get_id());
                            TimeUnit.MILLISECONDS.sleep(4000L);
                            L1NpcInstance squid = L1SpawnUtil.spawn(97105, new L1Location(32800, 32794, loc.getMapId()), 0, quest.get_id());
                            counter3 = 0;
                            TimeUnit.MILLISECONDS.sleep(5000L);
                            sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$10720"));
                            TimeUnit.MILLISECONDS.sleep(55000L);
                            boolean checkNotDead = false;
                            for (L1NpcInstance npc : quest.npcList()) {
                                if ((npc.getNpcId() == 97105) && (!npc.isDead())) {
                                    checkNotDead = true;
                                    break;
                                }
                            }
                            if (!checkNotDead) {
                                L1SpawnUtil.spawn(97118, new L1Location(32796, 32803, quest.get_mapid()), 0, quest.get_id()).set_quest_id(49311);
                            } else {
                                squid.set_spawnTime(1);
                            }
                            TimeUnit.MILLISECONDS.sleep(10000L);
                        }
                    } else {
                        if ((check_power >= 120) || (_random.nextInt(5) == 0)) {
                            sendPacketsToAll(new S_PacketBoxGree(2, "$9588"));
                            mob_power = Math.min(mob_power + 1, 2);
                        } else if ((check_power < 60) || (_random.nextInt(5) == 0)) {
                            sendPacketsToAll(new S_PacketBoxGree(2, "$9589"));
                            mob_power = Math.max(mob_power - 1, 0);
                        } else {
                            sendPacketsToAll(new S_PacketBoxGree(2, "$9550"));
                        }
                        check_power = 0;
                        randomMagicCircle();
                        TimeUnit.MILLISECONDS.sleep(10000L);
                        defenseFailed();
                        TimeUnit.MILLISECONDS.sleep(2000L);
                        randomMagicCircle();
                        TimeUnit.MILLISECONDS.sleep(6000L);
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9551"));
                        spawnEnemyShip();
                        TimeUnit.MILLISECONDS.sleep(4000L);
                        defenseFailed();
                        TimeUnit.MILLISECONDS.sleep(2000L);
                        randomMagicCircle();
                        TimeUnit.MILLISECONDS.sleep(10000L);
                        defenseFailed();
                        counter_alt = 0;
                        counter_alt2 = 0;
                    }
                    for (int i = 0; i < 300; i++) {
                        TimeUnit.MILLISECONDS.sleep(500L);
                        if (ship_step == 0) {
                            ship_step = -1;
                            count_down = 60;
                            sendPacketsToAll(new S_PacketBoxGree(2));
                            if (door.getCurrentHp() > 377) {
                                door.setCurrentHp(door.getCurrentHp() - 377);
                                int nowStatus = 36 - door.getCurrentHp() / 1000;
                                if (door.getStatus() != nowStatus) {
                                    door.setStatus(Math.min(nowStatus, 36));
                                    door.broadcastPacketAll(new S_DoActionGFX(door.getId(), door.getStatus()));
                                }
                            }
                            if (round < 12) {
                                if (ship.getGfxId() == 8263) {
                                    sendPacketsToAll(new S_PacketBoxGree(2, "$9552"));
                                } else if (ship.getGfxId() == 8260) {
                                    sendPacketsToAll(new S_PacketBoxGree(2, "$9553"));
                                } else if (ship.getGfxId() == 8166) {
                                    sendPacketsToAll(new S_PacketBoxGree(2, "$9554"));
                                } else {
                                    sendPacketsToAll(new S_PacketBoxGree(2, "$9555"));
                                }
                            }
                        } else {
                            int j;
                            if (ship_step == 1) {
                                ship_step = -1;
                                TimeUnit.MILLISECONDS.sleep(2000L);
                                sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$" + (9608 + round)));
                                sendPacketsToAll(new S_PacketBoxGree(1));
                                TimeUnit.MILLISECONDS.sleep(4000L);
                                if (critical_hit >= 3) {
                                    sendPacketsToAll(new S_PacketBoxGree(2, "$9559"));
                                    TimeUnit.MILLISECONDS.sleep(2000L);
                                    portal = L1SpawnUtil.spawn(97111, new L1Location(32799, 32809, quest.get_mapid()), 0, quest.get_id());
                                }
                                sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$9563"));
                                quest.add_score(_random.nextInt(30));
                                if (round >= 12) {
                                    if (quest.get_score() < 1000) {
                                        quest.spawnQuestMob(252);// 巴風特
                                    } else if (quest.get_score() < 1200) {
                                        quest.spawnQuestMob(253);// 奧爾丁
                                    } else if (quest.get_score() < 2000) {
                                        quest.spawnQuestMob(254);// 四色
                                    } else {
                                        quest.spawnQuestMob(255);// 四色(脫帽西瑪)
                                    }
                                }
                                if (portal == null) {
                                    quest.spawnQuestMob(1 + mob_power + (round - 1) * 10, 4);
                                } else {
                                    int ship_type = getCabinLocation();
                                    if (ship_type == 1) {
                                        quest.spawnQuestMob(191 + mob_power, 4); // 敵艦船艙
                                        quest.spawnQuestMob(201 + mob_power, 4); // 歐林甲板
                                    } else if (ship_type == 2) {
                                        quest.spawnQuestMob(211 + mob_power, 4); // 幽林船艙
                                        quest.spawnQuestMob(221 + mob_power, 4); // 歐林甲板
                                    } else if (ship_type == 3) {
                                        quest.spawnQuestMob(231 + mob_power, 4); // 飛龍船艙
                                        quest.spawnQuestMob(241 + mob_power, 4); // 歐林甲板
                                    }
                                }
                                TimeUnit.MILLISECONDS.sleep(15000L);
                                sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$9564"));
                                if (portal == null) {
                                    quest.spawnQuestMob(4 + mob_power + (round - 1) * 10, 4);
                                } else {
                                    int ship_type = getCabinLocation();
                                    if (ship_type == 1) {
                                        quest.spawnQuestMob(194 + mob_power, 4);// 敵艦船艙
                                        quest.spawnQuestMob(204 + mob_power, 4);// 歐林甲板
                                    } else if (ship_type == 2) {
                                        quest.spawnQuestMob(214 + mob_power, 4);// 幽林船艙
                                        quest.spawnQuestMob(224 + mob_power, 4);// 歐林甲板
                                    } else if (ship_type == 3) {
                                        quest.spawnQuestMob(234 + mob_power, 4);// 飛龍船艙
                                        quest.spawnQuestMob(244 + mob_power, 4);// 歐林甲板
                                    }
                                }
                                TimeUnit.MILLISECONDS.sleep(15000L);
                                sendPacketsToAll(new S_PacketBoxGree(2, "\\f=$9565"));
                                if (portal == null) {
                                    quest.spawnQuestMob(7 + mob_power + (round - 1) * 10, 4);
                                } else {
                                    int ship_type = getCabinLocation();
                                    if (ship_type == 1) {
                                        quest.spawnQuestMob(197 + mob_power, 4);// 敵艦船艙
                                        quest.spawnQuestMob(207 + mob_power, 4);// 歐林甲板
                                    } else if (ship_type == 2) {
                                        quest.spawnQuestMob(217 + mob_power, 4);// 幽林船艙
                                        quest.spawnQuestMob(227 + mob_power, 4);// 歐林甲板
                                    } else if (ship_type == 3) {
                                        quest.spawnQuestMob(237 + mob_power, 4);// 飛龍船艙
                                        quest.spawnQuestMob(247 + mob_power, 4);// 歐林甲板
                                    }
                                }
                                TimeUnit.MILLISECONDS.sleep(15000L);
                                int this_round = (round - 1) * 10;
                                j = 0;
                                for (int k = 60; j < k; j++) {
                                    TimeUnit.MILLISECONDS.sleep(1000L);
                                    boolean isRemainMob = false;
                                    Iterator<L1MonsterInstance> localIterator2 = WorldMob.get().getVisibleMob(party.getLeader()).iterator();
                                    while (localIterator2.hasNext()) {
                                        L1MonsterInstance mob = (L1MonsterInstance) localIterator2.next();
                                        if (!mob.isDead()) {
                                            isRemainMob = true;
                                            break;
                                        }
                                    }
                                    if (!isRemainMob) {
                                        sendPacketsToAll(new S_PacketBoxGree(2, "\\f3$9560"));
                                        quest.add_score(Math.max(_random.nextInt(k) - j, 5));
                                        boolean checkOkayInFirstTime = false;
                                        int mobId3;
                                        int mobId1;
                                        int mobId2;
                                        if (squid_version) {
                                            mobId1 = quest.spawnQuestMob(this_round, 0, 0);
                                            mobId2 = mobId1;
                                            mobId3 = mobId1;
                                        } else {
                                            if (_random.nextInt(100) < 30) {
                                                if (mimic_quest_order <= -1) {
                                                    mimic_quest_order = (2 << _random.nextInt(3));
                                                }
                                                if (mimic_quest_order == 2) {
                                                    mobId1 = quest.spawnQuestMob(this_round, 0, 0);
                                                    mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
                                                    mobId3 = mobId2;
                                                } else {
                                                    if (mimic_quest_order == 4) {
                                                        mobId2 = quest.spawnQuestMob(this_round, 0, 0);
                                                        mobId1 = quest.spawnQuestMob(this_round, mobId2, 0);
                                                        mobId3 = mobId1;
                                                    } else {
                                                        if (mimic_quest_order == 8) {
                                                            mobId3 = quest.spawnQuestMob(this_round, 0, 0);
                                                            mobId1 = quest.spawnQuestMob(this_round, mobId3, 0);
                                                            mobId2 = mobId1;
                                                        } else {
                                                            mobId1 = quest.spawnQuestMob(this_round, 0, 0);
                                                            mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
                                                            mobId3 = quest.spawnQuestMob(this_round, mobId1, mobId2);
                                                        }
                                                    }
                                                }
                                            } else {
                                                mobId1 = quest.spawnQuestMob(this_round, 0, 0);
                                                mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
                                                mobId3 = quest.spawnQuestMob(this_round, mobId1, mobId2);
                                            }
                                        }
                                        L1Location new_loc_1 = new L1Location(32793, 32800, quest.get_mapid()).randomLocation(4, true);
                                        L1Location new_loc_2 = new L1Location(32802, 32800, quest.get_mapid()).randomLocation(4, true);
                                        L1Location new_loc_3 = new L1Location(32799, 32805, quest.get_mapid()).randomLocation(4, true);
                                        sendPacketsToAll(new S_EffectLocation(new_loc_1.getX(), new_loc_1.getY(), 7930));
                                        sendPacketsToAll(new S_EffectLocation(new_loc_2.getX(), new_loc_2.getY(), 7930));
                                        sendPacketsToAll(new S_EffectLocation(new_loc_3.getX(), new_loc_3.getY(), 7930));
                                        L1NpcInstance mob1 = L1SpawnUtil.spawn(mobId1, new_loc_1, 0, quest.get_id());
                                        L1NpcInstance mob2 = L1SpawnUtil.spawn(mobId2, new_loc_2, 0, quest.get_id());
                                        L1NpcInstance mob3 = L1SpawnUtil.spawn(mobId3, new_loc_3, 0, quest.get_id());
                                        int m = 0;
                                        for (int p = 12; m < p; m++) {
                                            TimeUnit.MILLISECONDS.sleep(500L);
                                            if (ship_step == 2) {
                                                break;
                                            }
                                            if ((!checkOkayInFirstTime) && ((mob1.isDead()) || (mob2.isDead()) || (mob3.isDead()))) {
                                                checkOkayInFirstTime = true;
                                                if (((mimic_quest_order == 2) && (mob1.isDead())) || ((mimic_quest_order == 4) && (mob2.isDead())) || ((mimic_quest_order == 8) && (mob3.isDead()) && (++mimic_quest_count >= 4))) {
                                                    mimic_quest_count = 0;
                                                    squid_version = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (ship_step == 2) {
                                ship_step = -1;
                                if (portal == null) {
                                    break;
                                }
                                portal.deleteMe();
                                portal = null;
                                for (L1PcInstance pc : quest.pcList()) {
                                    if (!pc.getLocation().isInScreen(loc)) {
                                        teleport(pc, -1);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                Object hardin;
                if (round <= 10) {
                    quest.add_score(_random.nextInt(5) + 11);
                    sendPacketsToAll(new S_PacketBoxGree(4));
                    sendPacketsToAll(new S_PacketBoxGree(2, "\\f3$9608"));
                    TimeUnit.MILLISECONDS.sleep(3000L);
                    L1PcInstance partyMember = party.partyUser();
                    if (partyMember != null) {
                        quest_pc = partyMember;
                        L1Location new_loc = CABIN_LOC_LIST[0];
                        L1Location new_loc2 = new L1Location(new_loc.getX() + 6, new_loc.getY() - 6, new_loc.getMapId());
                        hardin = L1SpawnUtil.spawn(97119, new_loc2, 4, quest.get_id());
                        ((L1NpcInstance) hardin).set_spawnTime(40);
                        L1SpawnUtil.spawn(97120, new_loc2, 4, quest.get_id()).set_spawnTime(60);
                        L1NpcInstance mimic_1 = L1SpawnUtil.spawn(97121, new L1Location(new_loc.getX() - 3, new_loc.getY() - 4, new_loc.getMapId()), 0, quest.get_id());
                        mimic_1.set_quest_id(2);
                        mimic_1.set_spawnTime(40);
                        L1NpcInstance mimic_2 = L1SpawnUtil.spawn(97121, new L1Location(new_loc.getX() + 6, new_loc.getY() - 2, new_loc.getMapId()), 0, quest.get_id());
                        mimic_2.set_quest_id(4);
                        mimic_2.set_spawnTime(40);
                        L1NpcInstance mimic_3 = L1SpawnUtil.spawn(97121, new L1Location(new_loc.getX() - 1, new_loc.getY() + 5, new_loc.getMapId()), 0, quest.get_id());
                        mimic_3.set_quest_id(8);
                        mimic_3.set_spawnTime(40);
                        teleport(partyMember, 0);
                        TimeUnit.MILLISECONDS.sleep(2000L);
                        ((L1NpcInstance) hardin).broadcastPacketX10(new S_NpcChat(((L1NpcInstance) hardin).getId(), "$12122"));
                        TimeUnit.MILLISECONDS.sleep(3000L);
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9606"));
                        ((L1NpcInstance) hardin).broadcastPacketX10(new S_NpcChat(((L1NpcInstance) hardin).getId(), "$12123"));
                        TimeUnit.MILLISECONDS.sleep(3000L);
                        ((L1NpcInstance) hardin).broadcastPacketX10(new S_NpcChat(((L1NpcInstance) hardin).getId(), "$12124"));
                    }
                    if (quest_pc != null) {
                        for (int i = 0; i < 15; i++) {
                            TimeUnit.MILLISECONDS.sleep(1000L);
                            if (quest_pc.getLocation().isInScreen(loc)) {
                                quest.add_score(Math.max(_random.nextInt(35) - i * 2, 0));
                                break;
                            }
                            if (i == 7) {
                                sendPacketsToAll(new S_PacketBoxGree(2, "$9545"));
                            } else if (i == 14) {
                                sendPacketsToAll(new S_PacketBoxGree(2, "$9546"));
                                TimeUnit.MILLISECONDS.sleep(4000L);
                                if (!quest_pc.getLocation().isInScreen(loc)) {
                                    teleport(quest_pc, -1);
                                }
                                TimeUnit.MILLISECONDS.sleep(4000L);
                                break;
                            }
                        }
                    }
                    if (check_power <= 0) {
                        check_power = (quest.get_score() - score_before);
                    }
                } else if (round >= 12) {
                    for (int i = 0; i < 100; i++) {
                        TimeUnit.MILLISECONDS.sleep(6000L);
                        if (fire_point >= 10) {
                            sendPacketsToAll(new S_PacketBoxGree(2, "$9587"));
                            TimeUnit.MILLISECONDS.sleep(4000L);
                            sendPacketsToAll(new S_PacketBoxGree(2, "\\f3$9586"));
                            break;
                        }
                        int mob_count = 0;
                        hardin = WorldMob.get().getVisibleMob(party.getLeader()).iterator();
                        while (((Iterator<?>) hardin).hasNext()) {
                            L1MonsterInstance mob = (L1MonsterInstance) ((Iterator<?>) hardin).next();
                            if (!mob.isDead()) {
                                mob_count++;
                            }
                        }
                        if (mob_count <= 0) {
                            int bonus_box_id = Math.min(quest.get_score() / 100, 16);
                            L1NpcInstance mimic = L1SpawnUtil.spawn(97122, new L1Location(32796, 32803, quest.get_mapid()), 0, quest.get_id());
                            if (bonus_box_id <= 0) {
                                break;
                            }
                            mimic.set_quest_id(56235 + bonus_box_id);
                            send_gift = true;
                            break;
                        }
                        if (mob_count <= 4) {
                            sendPacketsToAll(new S_PacketBoxGree(2, "$9585"));
                        }
                    }
                }
            }
            if ((round >= 12) && (fire_point < 10)) {
                sendPacketsToAll(new S_PacketBoxGree(2, "$9579"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9580"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9581"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9582"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9583"));
                TimeUnit.MILLISECONDS.sleep(4000L);
                sendPacketsToAll(new S_PacketBoxGree(2, "$9584"));
                TimeUnit.MILLISECONDS.sleep(10000L);
                int score = quest.get_score();
                int rankId = BoardOrimReading.get().writeTopic(score, party.getLeader().getName(), party.getPartyMembers());
                List<L1Rank> totalList = BoardOrimReading.get().getTotalList();
                int totalSize = 0;
                int i = 0;
                int r = 5;
                for (int n = totalList.size(); (i < r) && (i < n); i++) {
                    L1Rank rank = (L1Rank) totalList.get(i);
                    if (rank != null) {
                        totalSize += rank.getMemberSize();
                    }
                }
                sendPacketsToAll(new S_PacketBoxGree(totalList, totalSize, rankId, score));
            }
            TimeUnit.MILLISECONDS.sleep(15000L);
            clearAllObject();
        } catch (Exception e) {
            _log.error("歐林海戰副本發生錯誤", e);
            GeneralThreadPool.get().cancel(_timer, false);
            clearAllObject();
            startR();
        }
    }

    public final void calcScore() {
        if (old_score < quest.get_score()) {
            old_score = quest.get_score();
            for (L1PcInstance pc : quest.pcList()) {
                pc.sendPackets(new S_PacketBoxGree(4, String.valueOf(old_score)));
            }
        }
        if (count_down > 0) {
            count_down -= 1;
            if (count_down == 0) {
                boolean isRemainMob = false;
                Iterator<L1MonsterInstance> localIterator2 = WorldMob.get().getVisibleMob(party.getLeader()).iterator();
                while (localIterator2.hasNext()) {
                    L1MonsterInstance mob = (L1MonsterInstance) localIterator2.next();
                    if (!mob.isDead()) {
                        isRemainMob = true;
                        break;
                    }
                }
                if (isRemainMob) {
                    sendPacketsToAll(new S_PacketBoxGree(1));
                    L1Location new_loc = loc.randomLocation(8, true);
                    L1SpawnUtil.spawn(_random.nextInt(5) + 97112, new_loc, 0, quest.get_id());
                    if (++fire_point < 10) {
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9561"));
                        count_down = 10;
                    } else {
                        sendPacketsToAll(new S_PacketBoxGree(2, "$9562"));
                    }
                }
            }
        }
    }

    private final int randomHintSeaMonster() {
        if (squid_version) {
            squid_version = false;
            return 4;
        }
        int chance = _random.nextInt(100);
        if (chance < 35) {
            return 1;
        }
        if (chance < 65) {
            return 2;
        }
        if (chance < 95) {
            return 3;
        }
        return 4;
    }

    private final void spawnSeaMonster(int npcId) {
        L1NpcInstance monster_0 = L1SpawnUtil.spawn(npcId, new L1Location(32778, 32800, quest.get_mapid()), 0, quest.get_id());
        monster_0.set_quest_id(0);
        monster_0.WORK.work(monster_0);
        L1NpcInstance monster_1 = L1SpawnUtil.spawn(npcId, new L1Location(32801, 32784, quest.get_mapid()).randomLocation(1, true), 0, quest.get_id());
        monster_1.set_quest_id(1);
        monster_1.WORK.work(monster_1);
        L1NpcInstance monster_2 = L1SpawnUtil.spawn(npcId, new L1Location(32810, 32787, quest.get_mapid()).randomLocation(1, true), 0, quest.get_id());
        monster_2.set_quest_id(2);
        monster_2.WORK.work(monster_2);
        int i = 0;
        for (int n = 3; i < n; i++) {
            L1NpcInstance spawn_monster = L1SpawnUtil.spawn(npcId, new L1Location(32793, 32803, quest.get_mapid()).randomLocation(2, true), 0, quest.get_id());
            spawn_monster.set_quest_id(3 + i);
            spawn_monster.WORK.work(spawn_monster);
        }
    }

    private final void randomMagicCircle() {
        defense_okay = false;
        L1Location new_loc = loc.randomLocation(7, true);
        L1NpcInstance npc = L1SpawnUtil.spawnT(97109, new_loc.getX(), new_loc.getY(), (short) new_loc.getMapId(), 0, 12);
        npc.set_showId(quest.get_id());
        quest.addNpc(npc);
        int i = 0;
        for (int n = quest.size() - 2; i < n; i++) {
            new_loc = loc.randomLocation(7, true);
            L1NpcInstance npc2 = L1SpawnUtil.spawnT(97110, new_loc.getX(), new_loc.getY(), (short) new_loc.getMapId(), 0, 12);
            npc2.set_showId(quest.get_id());
            quest.addNpc(npc2);
        }
    }

    public final void attack() {
        L1NpcInstance checkNpc = null;
        for (L1NpcInstance npc : quest.npcList()) {
            if (npc.getGfxId() == 8322) {
                for (L1PcInstance pc : quest.pcList()) {
                    if (pc.getLocation().isSamePoint(npc.getLocation())) {
                        checkNpc = npc;
                        break;
                    }
                }
                break;
            }
        }
        if (checkNpc != null) {
            if (fire_order) {
                npc1.broadcastPacketAll(new S_DoActionGFX(npc1.getId(), 2));
                if (_random.nextInt(100) < 75) {
                    sendPacketsToAll(new S_EffectLocation(32790, _random.nextInt(5) + 32816, 8233));
                }
            } else {
                npc2.broadcastPacketAll(new S_DoActionGFX(npc2.getId(), 2));
                if (_random.nextInt(100) < 75) {
                    sendPacketsToAll(new S_EffectLocation(32801, _random.nextInt(5) + 32816, 8233));
                }
            }
            fire_order = (!fire_order);
            if (round <= 0) {
                return;
            }
            counter_alt += 1;
        } else {
            if (round <= 0) {
                return;
            }
            counter_alt2 += 1;
        }
        if (counter_alt > counter_alt2 + 5) {
            if (!hit_okay) {
                critical_hit += 1;
            }
            hit_okay = true;
            counter_alt = 0;
            sendPacketsToAll(new S_PacketBoxGree(2, "\\f3Critical HIT!"));
            quest.add_score(3);
        } else if (counter_alt > counter_alt2 + 4) {
            sendPacketsToAll(new S_PacketBoxGree(2, "Double HIT!"));
            quest.add_score(1);
        } else if (counter_alt > counter_alt2 + 2) {
            sendPacketsToAll(new S_PacketBoxGree(2, "\\f=HIT!"));
        }
    }

    public final void defense() {
        if (round <= 0) {
            return;
        }
        List<L1NpcInstance> checkList = new ArrayList<L1NpcInstance>();
        for (Iterator<L1NpcInstance> iterator = quest.npcList().iterator(); iterator.hasNext(); ) {
            L1NpcInstance npc = (L1NpcInstance) iterator.next();
            if (npc.getGfxId() == 8323) {
                checkList.add(npc);
            }
        }
        if (!checkList.isEmpty()) {
            int checkCount = 0;
            for (Iterator<L1NpcInstance> iterator1 = checkList.iterator(); iterator1.hasNext(); ) {
                L1NpcInstance npc = (L1NpcInstance) iterator1.next();
                for (Iterator<L1PcInstance> iterator4 = quest.pcList().iterator(); iterator4.hasNext(); ) {
                    L1PcInstance pc = (L1PcInstance) iterator4.next();
                    if (pc.getLocation().isSamePoint(npc.getLocation())) {
                        checkCount++;
                    }
                }
            }
            if (checkCount >= quest.size() - 2) {
                L1NpcInstance npc;
                for (Iterator<L1NpcInstance> iterator2 = checkList.iterator(); iterator2.hasNext(); npc.deleteMe()) {
                    npc = (L1NpcInstance) iterator2.next();
                }
                if (defense_okay) {
                    return;
                }
                defense_okay = true;
                L1PcInstance pc;
                for (Iterator<L1PcInstance> iterator3 = quest.pcList().iterator(); iterator3.hasNext(); pc.sendPackets(new S_SkillSound(pc.getId(), 10165))) {
                    pc = (L1PcInstance) iterator3.next();
                }
                return;
            }
        }
        counter_alt2++;
    }

    private final void defenseFailed() {
        if (!defense_okay) {
            defense_okay = true;
            sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
            try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
            sendPacketsToAll(new S_PacketBoxGree(2));
            try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
            try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            L1SpawnUtil.spawn(_random.nextInt(5) + 97112, next_fire_loc, 0, quest.get_id());
            next_fire_loc = loc.randomLocation(8, true);
            if (++fire_point < 10) {
                sendPacketsToAll(new S_PacketBoxGree(2, "$9604"));
            } else {
                sendPacketsToAll(new S_PacketBoxGree(2, "$9562"));
            }
        }
    }

    public final int getCabinLocation() {
        if (ship == null) {
            return -1;
        }
        int type = 0;
        switch (ship.getGfxId()) {
            case 8164:
            case 8165:
            case 8166:
                type = 1;
                break;
            case 8260:
                type = 2;
                break;
            case 8263:
                type = 3;
        }
        return type;
    }

    public final void teleport(L1PcInstance pc, int type) {
        if ((pc.getId() == party.getLeader().getId()) || (pc.getMapId() != loc.getMapId()) || (!quest.pcList().contains(pc))) {
            return;
        }
        if (type <= -1) {
            L1Location new_loc = loc.randomLocation(5, true);
            L1Teleport.teleport(pc, new_loc, pc.getHeading(), true);
        } else if (type < CABIN_LOC_LIST.length) {
            if (type != 0) {
                if (portal != null) {
                    if (pc.getLocation().getTileLineDistance(portal.getLocation()) > 1)
                        ;
                }
            } else {
                L1Location new_loc = CABIN_LOC_LIST[type].randomLocation(5, true);
                L1Teleport.teleport(pc, new_loc, pc.getHeading(), true);
            }
        }
    }

    private final void spawnEnemyShip() {
        L1NpcInstance spawn_ship = null;
        int locx = fire_order ? 32796 : 32802;
        int locy = 32824;
        if (critical_hit >= 4) {
            if (round == 4) {
                spawn_ship = L1SpawnUtil.spawn(97107, new L1Location(locx, locy, quest.get_mapid()), 0, quest.get_id());
                quest.spawnQuestMob(257);// 德雷克、迪哥、豪勢
            } else if (round == 8) {
                spawn_ship = L1SpawnUtil.spawn(97106, new L1Location(locx, locy, quest.get_mapid()), 0, quest.get_id());
                quest.spawnQuestMob(258);// 艾爾摩大將軍殭屍、殭屍王、墮落的靈魂
            }
        }
        if (spawn_ship == null) {
            spawn_ship = L1SpawnUtil.spawn(_random.nextInt(3) + 97099, new L1Location(locx, locy, quest.get_mapid()), 0, quest.get_id());
            quest.spawnQuestMob(256);// 傑尼斯女王、巫妖、墮落
        }
        ship = spawn_ship;
        ship.WORK.work(ship);
    }

    public final void checkQuestOrder(L1PcInstance pc, int quest_id) {
        mimic_quest_order -= quest_id;
        if (mimic_quest_order <= 0) {
            if ((mimic_quest_order == 0) && (++mimic_quest_count >= 4)) {
                mimic_quest_count = 0;
                squid_version = true;
            }
            quest.get_orimR().teleport(pc, -1);
        }
    }

    public final void shipReturnStep(int step) {
        ship_step = step;
    }

    private final void sendPacketsToAll(ServerBasePacket packet) {
        if (quest.size() > 0) {
            for (L1PcInstance pc : quest.pcList()) {
                pc.sendPackets(packet);
            }
        }
    }

    private final void clearAllObject() {
        Iterator<L1Object> localIterator = World.get().getVisibleObjects(quest.get_mapid()).values().iterator();
        while (localIterator.hasNext()) {
            L1Object obj = (L1Object) localIterator.next();
            if (obj.get_showId() == quest.get_id()) {
                if ((obj instanceof L1EffectInstance)) {
                    L1EffectInstance mob = (L1EffectInstance) obj;
                    mob.deleteMe();
                } else if ((obj instanceof L1MerchantInstance)) {
                    L1MerchantInstance mob = (L1MerchantInstance) obj;
                    mob.setreSpawn(false);
                    mob.deleteMe();
                } else if ((obj instanceof L1MonsterInstance)) {
                    L1MonsterInstance mob = (L1MonsterInstance) obj;
                    if (!mob.isDead()) {
                        mob.setDead(true);
                        mob.setStatus(8);
                        mob.setCurrentHpDirect(0);
                        mob.deleteMe();
                    }
                } else if ((obj instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) obj;
                    L1Location loc = new L1Location(32580, 32931, 0).randomLocation(5, true);
                    L1Teleport.teleport(pc, loc, pc.getHeading(), true);
                } else if ((obj instanceof L1Inventory)) {
                    L1Inventory inventory = (L1Inventory) obj;
                    inventory.clearItems();
                }
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.quest.Chapter02R JD-Core Version: 0.6.2
 */