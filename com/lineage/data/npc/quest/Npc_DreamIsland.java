package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_DreamIsland extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_DreamIsland.class);

    public static NpcExecutor get() {
        return new Npc_DreamIsland();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLevel() < 52) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen5"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("e")) {// 使用鑰匙傳送到中央寺廟
            if (pc.getInventory().checkItem(640352)) {
                pc.getInventory().removeItem(pc.getInventory().findItemId(640352));
                L1Teleport.teleport(pc, 32800, 32798, (short) 1935, 3, true);
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen4"));
            }
        } else {
            boolean check = false;
            L1ItemInstance item = pc.getInventory().findItemId(640368);// 惡靈種子
            if (item != null && item.getCount() >= 5) {
                int need_count = 0;
                L1ItemInstance item1 = pc.getInventory().findItemId(640357);// 成長的珠子碎片
                L1ItemInstance item2 = pc.getInventory().findItemId(640358);// 成長的珠子
                if (item1 != null) {
                    if (item2 == null) {
                        need_count = (int) item1.getCount();
                    } else {
                        need_count = (int) (item1.getCount() + item.getCount());
                    }
                } else if (item2 != null) {
                    need_count = (int) item2.getCount();
                }
                if (item.getCount() / 5 >= need_count) {
                    check = true;
                }
            }
            if (!pc.getInventory().checkItem(640357) && !pc.getInventory().checkItem(640358)) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen3"));
            } else if (check) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen2"));
            } else if (cmd.equalsIgnoreCase("a")) {
                L1Teleport.teleport(pc, 32835, 32771, (short) 1931, 5, true);
                if (pc.getInventory().checkItem(640357)) {
                    pc.getInventory().consumeItem(640357, 1);
                    return;
                }
                if (pc.getInventory().checkItem(640358)) {
                    pc.getInventory().consumeItem(640358, 1);
                    return;
                }
            } else if (cmd.equalsIgnoreCase("b")) {
                L1Teleport.teleport(pc, 32703, 32645, (short) 1931, 5, true);
                if (pc.getInventory().checkItem(640357)) {
                    pc.getInventory().consumeItem(640357, 1);
                    return;
                }
                if (pc.getInventory().checkItem(640358)) {
                    pc.getInventory().consumeItem(640358, 1);
                    return;
                }
            } else if (cmd.equalsIgnoreCase("c")) {
                L1Teleport.teleport(pc, 32759, 32714, (short) 1931, 5, true);
                if (pc.getInventory().checkItem(640357)) {
                    pc.getInventory().consumeItem(640357, 1);
                    return;
                }
                if (pc.getInventory().checkItem(640358)) {
                    pc.getInventory().consumeItem(640358, 1);
                    return;
                }
            } else if (cmd.equalsIgnoreCase("d")) {
                L1Teleport.teleport(pc, 32644, 32833, (short) 1931, 5, true);
                if (pc.getInventory().checkItem(640357)) {
                    pc.getInventory().consumeItem(640357, 1);
                    return;
                }
                if (pc.getInventory().checkItem(640358)) {
                    pc.getInventory().consumeItem(640358, 1);
                    return;
                }
            }
        }
    }
}
