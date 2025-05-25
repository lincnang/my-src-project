package com.lineage.data.npc.quest2;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Npc_DragonB1 extends NpcExecutor {
    public static final Map<Integer, checkDragonTimer1> _timer = new HashMap<Integer, checkDragonTimer1>();
    private static final Log _log = LogFactory.getLog(Npc_DragonB1.class);

    public static NpcExecutor get() {
        return new Npc_DragonB1();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            L1QuestUser quest = WorldQuest.get().get(pc.get_showId());
            if (quest != null) {
                if (_timer.get(Integer.valueOf(pc.get_showId())) == null) {
                    boolean isFound = false;
                    if (!quest.npcList().isEmpty()) {
                        for (L1NpcInstance find_npc : quest.npcList()) {
                            if ((find_npc.getNpcId() == 71014) || (find_npc.getNpcId() == 71015) || (find_npc.getNpcId() == 71016)) {
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (!isFound) {
                        checkDragonTimer1 timer = new checkDragonTimer1(npc.getMapId(), quest);
                        timer.begin();
                        _timer.put(Integer.valueOf(pc.get_showId()), timer);
                    }
                }
                L1Location loc = new L1Location(32795, 32662, npc.getMapId()).randomLocation(5, false);
                L1Teleport.teleport(pc, loc.getX(), loc.getY(), npc.getMapId(), 4, true);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
    }

    private static class checkDragonTimer1 extends TimerTask {
        private final int mapId;
        private final L1QuestUser quest;

        public checkDragonTimer1(int mapId, L1QuestUser quest) {
            this.mapId = mapId;
            this.quest = quest;
        }

        public void run() {
            cancel();
            try {
                sendServerMessage(1570);
                TimeUnit.MILLISECONDS.sleep(2000L);
                sendServerMessage(1571);
                TimeUnit.MILLISECONDS.sleep(2000L);
                sendServerMessage(1572);
                TimeUnit.MILLISECONDS.sleep(2000L);
                L1Location loc = new L1Location(32786, 32689, mapId).randomLocation(5, true);
                L1SpawnUtil.spawn(71014, loc, new Random().nextInt(8), quest.get_id());
            } catch (Exception localException) {
            } finally {
                Npc_DragonB1._timer.remove(Integer.valueOf(quest.get_id()));
            }
        }

        private final void sendServerMessage(int msgid) {
            if (!quest.pcList().isEmpty()) {
                for (L1PcInstance pc : quest.pcList()) {
                    pc.sendPackets(new S_ServerMessage(msgid));
                }
            }
        }

        public final void begin() {
            GeneralThreadPool.get().schedule(this, 30000L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Npc_DragonB1 JD-Core Version: 0.6.2
 */