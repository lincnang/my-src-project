package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;

public abstract class SkillMode {
    public abstract int start(L1PcInstance paramL1PcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception;

    public abstract int start(L1NpcInstance paramL1NpcInstance, L1Character paramL1Character, L1Magic paramL1Magic, int paramInt) throws Exception;

    public abstract void stop(L1Character paramL1Character) throws Exception;

    public abstract void start(L1PcInstance paramL1PcInstance, Object paramObject) throws Exception;
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.SkillMode JD-Core Version: 0.6.2
 */