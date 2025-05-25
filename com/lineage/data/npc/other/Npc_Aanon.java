package com.lineage.data.npc.other;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class Npc_Aanon extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Aanon.class);

    public static NpcExecutor get() {
        return new Npc_Aanon();
    }

    public int type() {
        return 19;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isKnight()) {
                if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
                } else if (pc.getLevel() >= KnightLv15_1.QUEST.get_questlevel()) {
                    if (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id()) == 2) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon4"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon8"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if ((pc.isKnight()) && (cmd.equalsIgnoreCase("request hood of red knight"))) {
            if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                return;
            }
            if (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id()) == 2) {
                if (CreateNewItem.checkNewItem(pc, new int[]{40540, 40601, 20005}, new int[]{1, 1, 1}) < 1L) {
                    isCloseList = true;
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{40540, 40601, 20005}, new int[]{1, 1, 1}, new int[]{20027}, 1L, new int[]{1});
                    QuestClass.get().endQuest(pc, KnightLv15_1.QUEST.get_id());
                    isCloseList = true;
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aanon1"));
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public int workTime() {
        return 8;
    }

    public void work(L1NpcInstance npc) {
        if (npc.getStatus() != 4) {
            npc.setStatus(4);
            npc.broadcastPacketAll(new S_NPCPack(npc));
        }
        Work work = new Work(npc);
        work.getStart();
    }

    private static class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;

        private Work(L1NpcInstance npc) {
            _npc = npc;
            _spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 30));
                    TimeUnit.MILLISECONDS.sleep(_spr);
                }
            } catch (Exception e) {
                Npc_Aanon._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Aanon JD-Core Version: 0.6.2
 */