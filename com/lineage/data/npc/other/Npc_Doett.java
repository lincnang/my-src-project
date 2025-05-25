package com.lineage.data.npc.other;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Npc_Doett extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Doett.class);
    private static Work _work = null;

    public static NpcExecutor get() {
        return new Npc_Doett();
    }

    public int type() {
        return 17;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            npc.setHeading(npc.targetDirection(pc.getX(), pc.getY()));
            npc.broadcastPacketAll(new S_ChangeHeading(npc));
            if (_work != null) {
                _work.stopMove();
            }
            if (pc.isCrown()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM1"));
            } else if (pc.isKnight()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM1"));
            } else if (pc.isElf()) {
                if (pc.getLawful() < 0) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettec1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doette1"));
                }
            } else if (pc.isWizard()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM1"));
            } else if (pc.isDarkelf()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM2"));
            } else if (pc.isDragonKnight()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM3"));
            } else if (pc.isIllusionist()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM4"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "doettM4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int workTime() {
        return 30;
    }

    public void work(L1NpcInstance npc) {
        Work work = new Work(npc);
        work.getStart();
    }

    private class Work implements Runnable {
        private final L1NpcInstance _npc;
        private final int _spr;
        private final NpcWorkMove _npcMove;
        private final Random _random = new Random();
        private final int[][] _loc = {{33066, 32311}, {33065, 32319}, {33056, 32326}, {33044, 32323}, {33051, 32314}};
        boolean _isStop = false;

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
            _npcMove = new NpcWorkMove(npc);
        }

        private void stopMove() {
            _isStop = true;
        }

        public void getStart() {
            Npc_Doett._work = this;
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            try {
                int[] loc = _loc[_random.nextInt(_loc.length)];
                Point tgloc = new Point(loc[0], loc[1]);
                boolean isMove = true;
                while (isMove) {
                    TimeUnit.MILLISECONDS.sleep(_spr);
                    if (_isStop) {
                        break;
                    }
                    if (tgloc != null) {
                        isMove = _npcMove.actionStart(tgloc);
                    }
                    if (_npc.getLocation().isSamePoint(tgloc)) {
                        isMove = false;
                    }
                }
            } catch (Exception e) {
                Npc_Doett._log.error(e.getLocalizedMessage(), e);
            } finally {
                Npc_Doett._work = null;
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Doett JD-Core Version: 0.6.2
 */