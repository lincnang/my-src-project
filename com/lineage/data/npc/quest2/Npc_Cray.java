package com.lineage.data.npc.quest2;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Cray extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Cray.class);

    public static NpcExecutor get() {
        return new Npc_Cray();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.hasSkillEffect(4010)) {
                pc.removeSkillEffect(4010);
            }
            if (pc.hasSkillEffect(4018)) {
                pc.removeSkillEffect(4018);
            }
            if (pc.hasSkillEffect(4009)) {
                return;
            }
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7681));
            SkillMode mode = L1SkillMode.get().getSkill(4009);
            if (mode != null) {
                try {
                    mode.start(pc, null, null, 2400);
                } catch (Exception e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Npc_Cray JD-Core Version: 0.6.2
 */