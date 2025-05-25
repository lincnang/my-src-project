package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv50_1;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class Npc_Dspy extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Dspy.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Dspy();
    }

    public int type() {
        return 7;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (npc.getMaster() != null) {
                if (npc.getMaster().equals(pc)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy3"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
                }
                return;
            }
            npc.onTalkAction(pc);
            if (pc.isCrown()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else if (pc.isKnight()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
                    return;
                }
                if (pc.getLevel() >= ElfLv50_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(ElfLv50_1.QUEST.get_id())) {
                        case 3:
                        case 4:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy2"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
                }
            } else if (pc.isWizard()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else if (pc.isDarkelf()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else if (pc.isDragonKnight()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else if (pc.isIllusionist()) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (npc.getMaster() != null) {
            return;
        }
        if (pc.isElf()) {
            if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("start")) {
                L1Npc l1npc = NpcTable.get().getTemplate(80012);
                new L1FollowerInstance(l1npc, npc, pc);
                pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 4);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy3"));
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public void attack(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (npc.getNpcId() != 80012) {
                return;
            }
            if (pc == null) {
                return;
            }
            if ((pc.getX() >= 32557) && (pc.getX() <= 32576) && (pc.getY() >= 32656) && (pc.getY() <= 32687) && (pc.getMapId() == 400)) {
                pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 5);
                if (!pc.getInventory().checkItem(49163)) {
                    CreateNewItem.getQuestItem(pc, npc, 49163, 1L);
                }
                npc.setParalyzed(true);
                npc.deleteMe();
            } else {
                spawnAssassin(pc, npc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void spawnAssassin(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (_random.nextInt(100) <= 2) {
                L1Location loc = npc.getLocation().randomLocation(4, false);
                pc.sendPacketsX8(new S_EffectLocation(loc, 3992));
                L1MonsterInstance mob = L1SpawnUtil.spawnX(80011, loc, npc.get_showId());
                mob.setLink(npc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Dspy JD-Core Version: 0.6.2
 */