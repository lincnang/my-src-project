package com.lineage.server.model.Instance;

import com.lineage.server.serverpackets.S_NPCPack_Signboard;
import com.lineage.server.templates.L1Npc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1SignboardInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1SignboardInstance.class);

    public L1SignboardInstance(L1Npc template) {
        super(template);
    }

    public void onAction(L1PcInstance pc) {
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Signboard(this));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
