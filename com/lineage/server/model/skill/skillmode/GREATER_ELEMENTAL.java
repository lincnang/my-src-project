package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;

public class GREATER_ELEMENTAL extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        L1PcInstance pc = (L1PcInstance) cha;
        int attr = pc.getElfAttr();
        if (attr != 0) {
            if (!pc.getMap().isRecallPets()) {
                pc.sendPackets(new S_ServerMessage(353));
                return 0;
            }
            int petcost = 0;
            Object[] petlist = pc.getPetList().values().toArray();
            for (Object pet : petlist) {
                petcost += ((L1NpcInstance) pet).getPetcost();
            }
            if (petcost == 0) {
                int summonid = 0;
                switch (attr) {
                    case 1:
                        summonid = 81053;
                        break;
                    case 2:
                        summonid = 81050;
                        break;
                    case 4:
                        summonid = 81051;
                        break;
                    case 8:
                        summonid = 81052;
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                }
                if (summonid != 0) {
                    L1Npc npcTemp = NpcTable.get().getTemplate(summonid);
                    L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                    summon.setPetcost(pc.getCha() + 7);
                }
            }
        }
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.skill.skillmode.GREATER_ELEMENTAL JD-Core Version:
 * 0.6.2
 */