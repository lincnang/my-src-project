package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;

public class NPC_Skill extends NpcExecutor {
    public static NpcExecutor get() {
        return new NPC_Skill();
    }

    public int type() {
        return 17;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_skill_01"));
    }

    public int workTime() {
        return 11;
    }

    public void work(L1NpcInstance npc) {
        npc.broadcastPacketX8(new S_SkillSound(npc.getId(), 4396));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.NPC_Skill JD-Core Version: 0.6.2
 */