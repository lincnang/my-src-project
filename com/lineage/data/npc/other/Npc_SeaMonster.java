package com.lineage.data.npc.other;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class Npc_SeaMonster extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_SeaMonster.class);
    private static final Point[][] _point = {{new Point(32787, 32807), new Point(32828, 32807)}, {new Point(32791, 32794), new Point(32791, 32828)}, {new Point(32776, 32821)}, {new Point(32772, 32823)}, {new Point(32791, 32807), new Point(32789, 32828)}, {new Point(32801, 32802), new Point(32802, 32801), new Point(32804, 32801), new Point(32805, 32802), new Point(32805, 32804), new Point(32809, 32808), new Point(32828, 32808)}};

    public static NpcExecutor get() {
        return new Npc_SeaMonster();
    }

    public int type() {
        return 16;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private class Work implements Runnable {
        private final int _type;
        private final L1QuestUser quest;
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
            _npcMove = new NpcWorkMove(npc);
            quest = WorldQuest.get().get(npc.get_showId());
            _type = npc.get_quest_id();
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, 300L);
        }

        public void run() {
            try {
                if ((quest == null) || (quest.get_orimR() == null)) {
                    return;
                }
                int counter = 0;
                Point point = Npc_SeaMonster._point[_type][counter];
                boolean isWork = true;
                while (isWork) {
                    TimeUnit.MILLISECONDS.sleep(_spr);
                    _npcMove.actionStart(point);
                    if (_npc.getLocation().isSamePoint(point)) {
                        if (counter + 1 >= Npc_SeaMonster._point[_type].length) {
                            isWork = false;
                            _npc.setreSpawn(false);
                            _npc.deleteMe();
                        } else {
                            point = Npc_SeaMonster._point[_type][(++counter)];
                        }
                    }
                }
            } catch (Exception e) {
                Npc_SeaMonster._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_SeaMonster JD-Core Version: 0.6.2
 */