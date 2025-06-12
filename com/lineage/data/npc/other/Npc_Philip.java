package com.lineage.data.npc.other;

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

public class Npc_Philip extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Philip.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Philip();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLawful() < 0) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "philip2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "philip1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("buy")) {
            pc.sendPackets(new S_ShopSellList(pc,npc.getId()));
        } else if (cmd.equalsIgnoreCase("sell")) {
            pc.sendPackets(new S_ShopBuyList(npc.getId(), pc));
        }
    }

    public int workTime() {
        return 14;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private static class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;
        private Point[] _point = {new Point(33452, 32755), new Point(33451, 32757), new Point(33448, 32758)};

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
                int t = Npc_Philip._random.nextInt(_point.length);
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
                    if (_npc.getLocation().isSamePoint(_point[2])) {
                        _npc.setHeading(4);
                        _npc.broadcastPacketX8(new S_ChangeHeading(_npc));
                        TimeUnit.MILLISECONDS.sleep(_spr);
                        for (int i = 0; i < 3; i++) {
                            _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));
                            TimeUnit.MILLISECONDS.sleep(_spr);
                        }
                    }
                }
            } catch (Exception e) {
                Npc_Philip._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Philip JD-Core Version: 0.6.2
 */