package com.lineage.server.model.Instance;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1QuestInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1QuestInstance.class);

    public L1QuestInstance(L1Npc template) {
        super(template);
    }

    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        int npcId = getNpcTemplate().get_npcId();
        switch (npcId) {
            case 70957:
            case 71075:
            case 80012:
            case 81209:
                break;
            default:
                setActived(false);
                startAI();
        }
    }

    public void onAction(L1PcInstance pc) {
        try {
            L1AttackMode attack = new L1AttackPc(pc, this);
            attack.action();
            attack.commit();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTalkAction(L1PcInstance pc) {
        int npcId = getNpcTemplate().get_npcId();
        setHeading(targetDirection(pc.getX(), pc.getY()));
        broadcastPacketAll(new S_ChangeHeading(this));
        if (npcId == 71062) {
            if (pc.getQuest().get_step(31) == 2) {
                pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1b"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
            }
        } else if (npcId == 71075) {
            if (pc.getQuest().get_step(34) == 1) {
                pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1b"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
            }
        }
        set_stop_time(10);
        setRest(true);
    }

    public void onFinalAction(L1PcInstance pc, String action) {
        if (action.equalsIgnoreCase("start")) {
            int npcId = getNpcTemplate().get_npcId();
            if ((npcId == 71062) && (pc.getQuest().get_step(31) == 2)) {
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            } else if ((npcId == 71075) && (pc.getQuest().get_step(34) == 1)) {
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            }
        }
    }
}
