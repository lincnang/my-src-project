package com.lineage.server.model.poison;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SkillIconPoison;

public class L1SilencePoison extends L1Poison {
    private final L1Character _target;
    private static final int SILENCE_DURATION_MS = 300 * 1000;

    private L1SilencePoison(L1Character cha) {
        _target = cha;
        doInfection();
    }

    public static boolean doInfection(L1Character cha) {
        if (!L1Poison.isValidTarget(cha)) {
            return false;
        }
        cha.setPoison(new L1SilencePoison(cha));
        return true;
    }

    private void doInfection() {
        _target.setPoisonEffect(1);
        sendMessageIfPlayer(_target, 310);
        _target.setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, SILENCE_DURATION_MS);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(1, 300));
        }
    }

    public int getEffectId() {
        return 1;
    }

    public void cure() {
        _target.setPoisonEffect(0);
        sendMessageIfPlayer(_target, 311);
        _target.killSkillEffectTimer(L1SkillId.STATUS_POISON_SILENCE);
        _target.setPoison(null);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(1, 0));
        }
    }
}
