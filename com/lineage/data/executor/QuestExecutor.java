package com.lineage.data.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Quest;

public abstract class QuestExecutor {
    public abstract void execute(L1Quest paramL1Quest);

    public abstract void startQuest(L1PcInstance paramL1PcInstance);

    public abstract void endQuest(L1PcInstance paramL1PcInstance);

    public abstract void showQuest(L1PcInstance paramL1PcInstance);

    public abstract void stopQuest(L1PcInstance paramL1PcInstance);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.executor.QuestExecutor JD-Core Version: 0.6.2
 */