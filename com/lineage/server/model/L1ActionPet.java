package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_SelectTarget;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1ActionPet {
    private static final Log _log = LogFactory.getLog(L1ActionPet.class);
    private final L1PcInstance _pc;

    public L1ActionPet(L1PcInstance pc) {
        _pc = pc;
    }

    public L1PcInstance get_pc() {
        return _pc;
    }

    public void action(L1PetInstance npc, String action) {
        try {
            String status = null;
            if (action.equalsIgnoreCase("attackchr")) {
                int currentPetStatus = npc.getCurrentPetStatus();
                String type = "0";
                switch (currentPetStatus) {
                    case 0:
                    case 4:
                    case 6:
                    case 7:
                        type = "5";
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        type = String.valueOf(currentPetStatus);
                }
                npc.onFinalAction(_pc, type);
                _pc.sendPackets(new S_SelectTarget(npc.getId()));
            } else if (action.equalsIgnoreCase("aggressive")) {
                status = "1";
            } else if (action.equalsIgnoreCase("defensive")) {
                status = "2";
            } else if (action.equalsIgnoreCase("stay")) {
                status = "3";
            } else if (action.equalsIgnoreCase("extend")) {
                status = "4";
            } else if (action.equalsIgnoreCase("alert")) {
                status = "5";
            } else if (action.equalsIgnoreCase("dismiss")) {
                status = "6";
            } else if (action.equalsIgnoreCase("getitem")) {
                npc.collect(false);
            } else if (action.equalsIgnoreCase("changename")) {
                _pc.rename(false);
                _pc.setTempID(npc.getId());
                S_Message_YN pack = new S_Message_YN(325);
                _pc.sendPackets(pack);
            }
            if (status != null) {
                npc.onFinalAction(_pc, status);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1ActionPet JD-Core Version: 0.6.2
 */