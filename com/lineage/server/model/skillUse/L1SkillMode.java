package com.lineage.server.model.skillUse;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

public abstract class L1SkillMode {
    public abstract int start(L1PcInstance paramL1PcInstance, L1Character paramL1Character, int paramInt1, int paramInt2, int paramInt3, String paramString) throws Exception;

    public abstract int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, int paramInt1, int paramInt2, int paramInt3, String paramString) throws Exception;

    public void useMode(L1Character user, int skillid) {
    }

    public void useLoc(L1Character user, L1Character targ, int skillid) {
    }

    public abstract void stop(L1Character paramL1Character) throws Exception;
}
