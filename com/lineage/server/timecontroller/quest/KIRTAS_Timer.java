package com.lineage.server.timecontroller.quest;

import com.lineage.server.model.Instance.L1NpcInstance;

import java.util.TimerTask;

public class KIRTAS_Timer extends TimerTask {
    private final L1NpcInstance _npc;

    public KIRTAS_Timer(L1NpcInstance npc) {
        _npc = npc;
    }

    public void run() {
        if (_npc.getNpcId() == 81163) {
            if (_npc.hasSkillEffect(11057) || // 吉爾塔斯-全部反射
                    _npc.hasSkillEffect(11058) || // 吉爾塔斯-絕對屏障
                    _npc.hasSkillEffect(11059) || // 吉爾塔斯-鏡反射
                    _npc.hasSkillEffect(11060)) { // 吉爾塔斯-反擊屏障
                _npc.setbarrierTime(_npc.getbarrierTime() + 1);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.quest.KIRTAS_Timer JD-Core Version: 0.6.2
 */