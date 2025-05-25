package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Aras extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Aras.class);

    public static NpcExecutor get() {
        return new Npc_Aras();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLawful() < -500) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras12"));
            } else if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras9"));
                } else if (pc.getLevel() >= ElfLv15_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras7"));
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras1"));
                            break;
                        case 2:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras3"));
                            break;
                        case 3:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));
                            break;
                        case 4:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras13"));
                            break;
                        case 5:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras8"));
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras11"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isElf()) {
            switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
                case 0:
                    break;
                case 1:
                    if (cmd.equalsIgnoreCase("A")) {
                        L1ItemInstance item = pc.getInventory().checkItemX(40637, 1L);
                        if (item != null) {
                            pc.getInventory().removeItem(item, 1L);
                        }
                        CreateNewItem.createNewItem(pc, 40664, 1L);
                        pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 2);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras2"));
                    }
                    break;
                case 2:
                    try {
                        if (cmd.matches("[0-9]+")) {
                            status2(pc, npc, Integer.parseInt(cmd));
                        }
                    } catch (Exception e) {
                        _log.error(e.getLocalizedMessage(), e);
                    }
                case 3:
                    if (cmd.equalsIgnoreCase("B")) {
                        L1ItemInstance item = pc.getInventory().checkItemX(40664, 1L);
                        if (item != null) {
                            pc.getInventory().removeItem(item, 1L);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras13"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras14"));
                        }
                        CreateNewItem.createNewItem(pc, 40665, 1L);
                        pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 4);
                    }
                    break;
                case 4:
                    break;
                case 5:
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private void status2(L1PcInstance pc, L1NpcInstance npc, int intValue) {
        switch (intValue) {
            case 1:
                getItem(pc, npc, 40684, 40699);
                break;
            case 2:
                getItem(pc, npc, 40683, 40698);
                break;
            case 3:
                getItem(pc, npc, 40679, 40693);
                break;
            case 4:
                getItem(pc, npc, 40682, 40697);
                break;
            case 5:
                getItem(pc, npc, 40681, 40695);
                break;
            case 6:
                getItem(pc, npc, 40680, 40694);
                break;
            case 7:
                if (CreateNewItem.checkNewItem(pc, new int[]{40684, 40683, 40679, 40682, 40681, 40680}, new int[]{1, 1, 1, 1, 1, 1}) < 1L) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras5"));
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{40684, 40683, 40679, 40682, 40681, 40680}, new int[]{1, 1, 1, 1, 1, 1}, new int[]{40699, 40698, 40693, 40697, 40695, 40694}, 1L, new int[]{1, 1, 1, 1, 1, 1});
                    pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 3);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));
                }
                break;
        }
    }

    private void getItem(L1PcInstance pc, L1NpcInstance npc, int srcid, int getid) {
        if (CreateNewItem.checkNewItem(pc, srcid, 1) < 1L) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras5"));
        } else {
            CreateNewItem.createNewItem(pc, srcid, 1, getid, 1);
            if (checkItem(pc)) {
                pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 3);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras10"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aras4"));
            }
        }
    }

    private boolean checkItem(L1PcInstance pc) {
        int i = 0;
        int[] itemids = {40699, 40698, 40693, 40697, 40695, 40694};
        for (int itemid : itemids) {
            L1ItemInstance item = pc.getInventory().checkItemX(itemid, 1L);
            if (item != null) {
                i++;
            }
        }
        if (i >= 6) {
            return true;
        }
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Aras JD-Core Version: 0.6.2
 */