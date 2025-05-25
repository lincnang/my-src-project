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

public class Npc_Hector extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Hector.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Hector();
    }

    @Override
    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hector2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hector1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("request arctic of helm")) {
            int[] items = {20006, 80021, 41246};
            int[] counts = {1, 1, 50000};
            int[] gitems = {400047};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("request arctic of armor")) {
            int[] items = {20154, 80023, 41246};
            int[] counts = {1, 1, 50000};
            int[] gitems = {400045};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("request arctic of boots")) {
            int[] items = {20205, 80022, 41246};
            int[] counts = {1, 1, 50000};
            int[] gitems = {400046};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        }
        if (!cmd.equalsIgnoreCase("request fifth goods of war")) {
            if (cmd.equalsIgnoreCase("request iron gloves")) {
                int[] items = {20182, 40408, 40308};
                int[] counts = {1, 150, 25000};
                int[] gitems = {20163};
                int[] gcounts = {1};
                isCloseList = getItem(pc, items, counts, gitems, gcounts);
            } else if (cmd.equalsIgnoreCase("request iron visor")) {
                int[] items = {20006, 40408, 40308};
                int[] counts = {1, 120, 16500};
                int[] gitems = {20003};
                int[] gcounts = {1};
                isCloseList = getItem(pc, items, counts, gitems, gcounts);
            } else if (cmd.equalsIgnoreCase("request iron shield")) {
                int[] items = {20231, 40408, 40308};
                int[] counts = {1, 200, 16000};
                int[] gitems = {20220};
                int[] gcounts = {1};
                isCloseList = getItem(pc, items, counts, gitems, gcounts);
            } else if (cmd.equalsIgnoreCase("request iron boots")) {
                int[] items = {20205, 40408, 40308};
                int[] counts = {1, 160, 8000};
                int[] gitems = {20194};
                int[] gcounts = {1};
                isCloseList = getItem(pc, items, counts, gitems, gcounts);
            } else if (cmd.equalsIgnoreCase("request iron plate mail")) {
                int[] items = {20154, 40408, 40308};
                int[] counts = {1, 450, 30000};
                int[] gitems = {20091};
                int[] gcounts = {1};
                isCloseList = getItem(pc, items, counts, gitems, gcounts);
            } else if (cmd.equalsIgnoreCase("request slim plate")) {
                int[] items = {40408, 40308};
                int[] counts = {10, 500};
                int[] gitems = {40526};
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
            } else if (cmd.equalsIgnoreCase("a1")) {
                int[] items = {40408, 40308};
                int[] counts = {10, 500};
                int[] gitems = {40526};
                int[] gcounts = {1};
                long xcount = CreateNewItem.checkNewItem(pc, items, counts);
                if (xcount >= amount) {
                    CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
                }
                isCloseList = true;
            } else if (cmd.equalsIgnoreCase("request lump of steel")) {
                int[] items = {40899, 40408, 40308};
                int[] counts = {5, 5, 500};
                int[] gitems = {40779};
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
            } else if (cmd.equalsIgnoreCase("a2")) {
                int[] items = {40899, 40408, 40308};
                int[] counts = {5, 5, 500};
                int[] gitems = {40779};
                int[] gcounts = {1};
                long xcount = CreateNewItem.checkNewItem(pc, items, counts);
                if (xcount >= amount) {
                    CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
                }
                isCloseList = true;
            }
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
        private Point[] _point = {new Point(33466, 32777), new Point(33464, 32773), // 翻爐
                new Point(33466, 32775)// 打鐵
        };

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
                int t = Npc_Hector._random.nextInt(_point.length);
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
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 19));
                    } else if (_npc.getLocation().isSamePoint(_point[2])) {
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 17));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 18));
                    }
                }
            } catch (Exception e) {
                Npc_Hector._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Hector JD-Core Version: 0.6.2
 */