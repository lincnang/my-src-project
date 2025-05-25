package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Npc_Jason extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Jason.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Jason();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jason2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jason1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("request timber")) {
            int[] items = {42502};
            int[] counts = {4};
            int[] gitems = {42503};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount == 1L) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            } else if (xcount > 1L) {
                pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));
            } else if (xcount < 1L) {
                isCloseList = true;
            }
        } else if (cmd.equals("a1")) {
            int[] items = {42502};
            int[] counts = {4};
            int[] gitems = {42503};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount >= amount) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
            }
            isCloseList = true;
        } else if (cmd.equalsIgnoreCase("request blank box")) {
            int[] items = {42502, 40308};
            int[] counts = {5, 500};
            int[] gitems = {42504};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount == 1L) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            } else if (xcount > 1L) {
                pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a2"));
            } else if (xcount < 1L) {
                isCloseList = true;
            }
        } else if (cmd.equals("a2")) {
            int[] items = {42502, 40308};
            int[] counts = {5, 500};
            int[] gitems = {42504};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount >= amount) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
            }
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public int workTime() {
        return 25;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private static class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;
        private Point[] _point = {new Point(33449, 32763), new Point(33449, 32762), new Point(33450, 32764), new Point(33452, 32765)};

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
                int t = Npc_Jason._random.nextInt(_point.length);
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
                    if (_npc.getLocation().isSamePoint(_point[1])) {
                        _npc.setHeading(6);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 18));
                    } else if (_npc.getLocation().isSamePoint(_point[2])) {
                        _npc.setHeading(6);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 17));
                    }
                }
            } catch (Exception e) {
                Npc_Jason._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Jason JD-Core Version: 0.6.2
 */