package com.lineage.server.model.Instance;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NPCPack_Ill;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.templates.L1Npc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1IllusoryInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1IllusoryInstance.class);

    public L1IllusoryInstance(L1Npc template) {
        super(template);
    }

    /**
     * 接觸資訊
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            if (getCurrentHp() > 0) {
                perceivedFrom.sendPackets(new S_NPCPack_Ill(this));
                onNpcAI();
                //if (getBraveSpeed() > 0) {
                //perceivedFrom.sendPackets(new S_SkillBrave(getId(), getBraveSpeed(), 600000));
                //}
                perceivedFrom.sendPackets(new S_SkillHaste(getId(), 1, 3600 * 1000));
                perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600 * 1000));
            } else {
                perceivedFrom.sendPackets(new S_NPCPack_Ill(this));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setLink(L1Character cha) {
        if (get_showId() != cha.get_showId()) {
            return;
        }
        if (cha instanceof L1PcInstance) {
            return;
        }
        if (_hateList.isEmpty()) {
            _hateList.add(cha, 0);
            checkTarget();
        }
    }

    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActived(false);
        startAI();
    }

    public void onTalkAction(L1PcInstance talkFrom) {
        super.onTalkAction(talkFrom);
    }

    public void onAction(L1PcInstance pc) {
        try {
            L1AttackMode attack = new L1AttackPc(pc, this);
            attack.action();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void ReceiveManaDamage(final L1Character attacker, final int damageMp) {
        super.ReceiveManaDamage(attacker, damageMp);
    }

    public void receiveDamage(final L1Character attacker, final int damage) {
        super.receiveDamage(attacker, damage);
    }

    public void setCurrentHp(int i) {
    }

    public void setCurrentMp(int i) {
    }
}
