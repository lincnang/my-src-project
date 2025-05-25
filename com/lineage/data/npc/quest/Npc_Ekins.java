package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class Npc_Ekins extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Ekins.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new Npc_Ekins();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if ((pc.getInventory().checkItem(241000)) || (pc.getInventory().checkItem(241101))) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins2"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            if (pc.getLevel() < 52) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins3"));
            } else if (cmd.equalsIgnoreCase("a")) {
                if ((pc.getInventory().checkItem(240987, 5L)) && (pc.getInventory().checkItem(241000, 1L))) {
                    int exprd = 20;
                    exprd += _random.nextInt(8);
                    int exp = (int) (ExpTable.getNeedExpNextLevel(64) * (exprd / 1000.0D));
                    pc.addExp(exp);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10418));
                    pc.broadcastPacketAll(new S_SkillSound(pc.getId(), 10418));
                    pc.getInventory().consumeItem(240987, 5L);
                    pc.getInventory().consumeItem(241000, 1L);
                    CreateNewItem.createNewItem(pc, 240989, 3L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins4"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins5"));
                }
            } else if (cmd.equalsIgnoreCase("b")) {
                if ((pc.getInventory().checkItem(240987, 5L)) && (pc.getInventory().checkItem(241101, 1L))) {
                    int exprd = 60;
                    exprd += _random.nextInt(24);
                    int exp = (int) (ExpTable.getNeedExpNextLevel(64) * (exprd / 1000.0D));
                    pc.addExp(exp);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 10418));
                    pc.broadcastPacketAll(new S_SkillSound(pc.getId(), 10418));
                    pc.getInventory().consumeItem(240987, 5L);
                    pc.getInventory().consumeItem(241101, 1L);
                    CreateNewItem.createNewItem(pc, 240989, 9L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins4"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ekins5"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}