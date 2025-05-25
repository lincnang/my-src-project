package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Npc_Herbert extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Herbert.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Herbert();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "herbert2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "herbert1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("request t-shirt")) {
            int[] items = {40456, 40455, 40457, 40308};
            int[] counts = {3, 2, 10, 30000};
            int[] gitems = {20085};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request cloak of magic resistance")) {
            int[] items = {40456, 40455, 40457, 40308};
            int[] counts = {10, 2, 1, 1000};
            int[] gitems = {20056};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request cloak of protection")) {
            int[] items = {40456, 40455, 40457, 40308};
            int[] counts = {5, 5, 10, 20000};
            int[] gitems = {20063};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else {
            cmd.equalsIgnoreCase("request sixth goods of war");
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private boolean getItem(L1PcInstance pc, int[] items, int[] counts, int[] gitems, int[] gcounts) {
        if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
            return true;
        }
        CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
        return true;
    }

    public int workTime() {
        return 15;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;
        private Point[] _point = {new Point(33469, 32777), new Point(33472, 32776)};

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
            _npcMove = new NpcWorkMove(npc);
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            try {
                Point point = null;
                int t = Npc_Herbert._random.nextInt(_point.length);
                if (!_npc.getLocation().isSamePoint(_point[t])) {
                    point = _point[t];
                }
                boolean isWork = true;
                while (isWork) {
                    TimeUnit.MILLISECONDS.sleep(_spr);
                    if (point != null) {
                        isWork = _npcMove.actionStart(point);
                    } else {
                        isWork = false;
                    }
                }
            } catch (Exception e) {
                Npc_Herbert._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Herbert JD-Core Version: 0.6.2
 */