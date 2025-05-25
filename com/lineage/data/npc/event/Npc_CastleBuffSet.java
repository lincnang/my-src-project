package com.lineage.data.npc.event;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Skills;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;

public class Npc_CastleBuffSet extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_CastleBuffSet.class);
    private static final int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,};

    public static NpcExecutor get() {
        return new Npc_CastleBuffSet();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getInventory().consumeItem(40308, 1000)) {
                for (int j : allBuffSkill) {
                    final L1Skills skill = SkillsTable.get().getTemplate(j);
                    final L1SkillUse skillUse = new L1SkillUse();
                    skillUse.handleCommands(pc, j, pc.getId(), pc.getX(), pc.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);// */
                }
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_done"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_adena"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.event.Npc_BuffSet JD-Core Version: 0.6.2
 */