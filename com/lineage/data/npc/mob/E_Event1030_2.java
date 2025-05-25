package com.lineage.data.npc.mob;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class E_Event1030_2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(E_Event1030_2.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new E_Event1030_2();
    }

    public int type() {
        return 4;
    }

    public void attack(L1PcInstance pc, L1NpcInstance npc) {
        try {
            int i = _random.nextInt(100);
            if ((i >= 0) && (i <= 1)) {
                if (npc.isremovearmor()) {
                    return;
                }
                if (pc.hasSkillEffect(4012)) {
                    return;
                }
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7782));
                pc.setSkillEffect(4012, 12000);
                npc.set_removearmor(true);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.mob.E_Event1030_2 JD-Core Version: 0.6.2
 */