package com.lineage.server.timecontroller.quest;

import com.lineage.data.quest.ADLv80_3;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public final class AD80_3_Timer extends TimerTask {
    private static final Log _log = LogFactory.getLog(AD80_3_Timer.class);
    private static final Random _random = new Random();
    private ScheduledFuture<?> _timer;
    private int _qid = -1;
    private int _counter;
    private L1Location _loc;

    public void start() {
        _qid = ADLv80_3.QUEST.get_id();
        _loc = new L1Location(32848, 32877, 1017);
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 2000L, 2000L);
    }

    public void run() {
        try {
            ArrayList<?> questList = WorldQuest.get().getQuests(_qid);
            if (questList.isEmpty()) {
                return;
            }
            for (Object object : questList.toArray()) {
                L1QuestUser quest = (L1QuestUser) object;
                for (L1NpcInstance npc : quest.npcList()) {
                    if (!npc.isDead()) {
                        L1Location loc = npc.getLocation().randomLocation(12, true);
                        L1NpcInstance check_npc;
                        if ((npc.getNpcId() >= 97204) && (npc.getNpcId() <= 97209)) {
                            if (++_counter >= 45) {
                                _counter = 0;
                                boolean check_spawn = false;
                                for (Iterator<?> localIterator2 = quest.npcList().iterator(); localIterator2.hasNext(); ) {
                                    check_npc = (L1NpcInstance) localIterator2.next();
                                    if (check_npc.getNpcId() == 97210) {// 烏雲大精靈
                                        check_spawn = true;
                                        break;
                                    }
                                }
                                if (!check_spawn) {
                                    loc = _loc.randomLocation(12, true);
                                    L1SpawnUtil.spawn(97210, loc, 0, quest.get_id());
                                }
                            }
                        }
                        if ((npc.getNpcId() >= 97204) && (npc.getNpcId() <= 97206)) {
                            if (_random.nextInt(100) < 5) {
                                for (L1PcInstance tgpc : quest.pcList()) {
                                    switch (_random.nextInt(3)) {
                                        case 0:
                                            npc.getNpcTemplate().set_weakAttr(4);
                                            tgpc.sendPackets(new S_PacketBoxGree(6));
                                            break;
                                        case 1:
                                            npc.getNpcTemplate().set_weakAttr(1);
                                            tgpc.sendPackets(new S_PacketBoxGree(7));
                                            break;
                                        case 2:
                                            npc.getNpcTemplate().set_weakAttr(8);
                                            tgpc.sendPackets(new S_PacketBoxGree(8));
                                    }
                                }
                            }
                            if (npc.getStatus() == 13) {// 飛天狀態
                                if (npc.getSkyTime() >= 8) {
                                    for (L1PcInstance tgpc : quest.pcList()) {
                                        tgpc.sendPackets(new S_Weather(World.get().getWeather()));
                                    }
                                    for (L1PcInstance pc : World.get().getRecognizePlayer(npc)) {
                                        pc.sendPackets(new S_RemoveObject(npc));
                                        pc.removeKnownObject(npc);
                                    }
                                    npc.setLocation(loc);
                                    npc.broadcastPacketAll(new S_NPCPack(npc));
                                    npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 11));
                                    npc.setHiddenStatus(0);
                                    npc.setSkyTime(0);
                                    break;
                                }
                                npc.setSkyTime(npc.getSkyTime() + 1);
                                int range;
                                int effectId;
                                if (_random.nextInt(100) < 70) {
                                    effectId = 10405;
                                    range = 3;
                                } else {
                                    effectId = 10407;
                                    range = 5;
                                }
                                S_EffectLocation packet = new S_EffectLocation(loc.getX(), loc.getY(), effectId);
                                for (L1PcInstance tgpc : quest.pcList()) {
                                    if (tgpc.getLocation().isInScreen(loc)) {
                                        tgpc.sendPackets(packet);
                                    }
                                    if (tgpc.getLocation().getTileLineDistance(loc) < range) {
                                        tgpc.sendPacketsAll(new S_DoActionGFX(tgpc.getId(), 2));
                                        tgpc.receiveDamage(npc, _random.nextInt(301) + 300, true, true);
                                    }
                                }
                            } else if (_random.nextInt(100) < 5) {// 5%機率飛天
                                npc.allTargetClear();
                                npc.setStatus(13);
                                npc.broadcastPacketAll(new S_NPCPack(npc));
                                npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 13));
                                npc.setHiddenStatus(2);
                                for (L1PcInstance tgpc : quest.pcList()) {
                                    tgpc.sendPackets(new S_Weather(17));
                                }
                            }
                        }
                    }
                }
            }
            questList.clear();
        } catch (Exception e) {
            _log.error("風龍副本 傷害計能施放 時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            AD80_3_Timer ad80_3Timer = new AD80_3_Timer();
            ad80_3Timer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.quest.AD80_3_Timer JD-Core Version: 0.6.2
 */