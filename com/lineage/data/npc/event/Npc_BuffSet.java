package com.lineage.data.npc.event;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 魔法輔助商人
 *
 * @author terry0412
 */
public class Npc_BuffSet extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_BuffSet.class);

    public static NpcExecutor get() {
        return new Npc_BuffSet();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_01"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
			/*if (cmd.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(MagicianSet.ITEM_ID, MagicianSet.ITEM_COUNT)) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, 43, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 79, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 216, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 211, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 148, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 168, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 160, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
				}
			} else if (cmd.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(MagicianSet.ITEM_ID, MagicianSet.ITEM_COUNT)) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, 43, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 79, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 216, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 211, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 149, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 168, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 160, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
				}
			} else if (cmd.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(MagicianSet.ITEM_ID, MagicianSet.ITEM_COUNT)) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, 43, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 79, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 216, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 211, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 206, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 168, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					skillUse.handleCommands(pc, 160, pc.getId(), pc.getX(), pc.getY(), 0, 4);
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
				}
			} else*/
            if (cmd.equalsIgnoreCase("0")) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_01"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
