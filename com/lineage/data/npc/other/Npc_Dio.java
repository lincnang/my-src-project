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

public class Npc_Dio extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Dio.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Dio();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dio2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dio1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("sell")) {
            pc.sendPackets(new S_ShopBuyList(npc.getId(), pc));
        }
        if (cmd.equalsIgnoreCase("request arctic stone1")) {
            int[] items = {400044};
            int[] counts = {1};
            int[] gitems = {80021};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("request arctic stone2")) {
            int[] items = {20211};
            int[] counts = {1};
            int[] gitems = {80022};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public int workTime() {
        return 19;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private static class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;
        private Point[] _point = {new Point(33457, 32775), new Point(33455, 32773), new Point(33453, 32774), new Point(33454, 32777)};

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
                int t = Npc_Dio._random.nextInt(_point.length);
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
                    if (_npc.getLocation().isSamePoint(_point[0])) {
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));
                    } else if (_npc.getLocation().isSamePoint(_point[1])) {
                        _npc.setHeading(6);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));
                    } else if (_npc.getLocation().isSamePoint(_point[2])) {
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));
                    } else if (_npc.getLocation().isSamePoint(_point[3])) {
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                    }
                }
            } catch (Exception e) {
                Npc_Dio._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Dio JD-Core Version: 0.6.2
 */