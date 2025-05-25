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

public class Npc_Ship extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Ship.class);

    public static NpcExecutor get() {
        return new Npc_Ship();
    }

    public int type() {
        return 16;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private class Work implements Runnable {
        private final L1QuestUser quest;
        private final Point[] _point;
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
            _npcMove = new NpcWorkMove(npc);
            quest = WorldQuest.get().get(npc.get_showId());
            _point = new Point[]{new Point(_npc.getX(), 32811), new Point(_npc.getX(), 32813), new Point(_npc.getX(), 32825)};
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            try {
                if ((quest == null) || (quest.get_orimR() == null)) {
                    return;
                }
                int counter = 0;
                Point point = _point[counter];
                boolean isWork = true;
                while (isWork) {
                    TimeUnit.MILLISECONDS.sleep(_spr);
                    _npcMove.actionStart(point);
                    if (_npc.getLocation().isSamePoint(point)) {
                        quest.get_orimR().shipReturnStep(counter);
                        if (counter == 0) {
                            TimeUnit.MILLISECONDS.sleep(2000L);
                            point = _point[java.lang.Math.min(++counter, _point.length)];
                        } else if (counter == 1) {
                            TimeUnit.MILLISECONDS.sleep(120000L);
                            quest.get_orimR().shipReturnStep(counter);
                            point = _point[java.lang.Math.min(++counter, _point.length)];
                        } else if (counter == 2) {
                            isWork = false;
                            _npc.setreSpawn(false);
                            _npc.deleteMe();
                        }
                    }
                }
            } catch (Exception e) {
                Npc_Ship._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Ship JD-Core Version: 0.6.2
 */