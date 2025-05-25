package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1ActionSummon {
    private static final Log _log = LogFactory.getLog(L1ActionSummon.class);
    private final L1PcInstance _pc;

    public L1ActionSummon(L1PcInstance pc) {
        _pc = pc;
    }

    public L1PcInstance get_pc() {
        return _pc;
    }

    public void action(L1SummonInstance npc, String action) {
        try {
            String status = null;
            if (action.equalsIgnoreCase("aggressive")) {
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
 * com.lineage.server.model.L1ActionSummon JD-Core Version: 0.6.2
 */