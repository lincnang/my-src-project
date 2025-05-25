package com.lineage.server.model.Instance;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.templates.L1Npc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1CnInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1CnInstance.class);

    public L1CnInstance(L1Npc template) {
        super(template);
    }

    public void onAction(L1PcInstance pc) {
        try {
            L1AttackMode attack = new L1AttackPc(pc, this);
            attack.action();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onNpcAI() {
    }

    public void onTalkAction(L1PcInstance player) {
    }

    public void onFinalAction(L1PcInstance player, String action) {
    }

    public void doFinalAction(L1PcInstance player) {
    }
}
