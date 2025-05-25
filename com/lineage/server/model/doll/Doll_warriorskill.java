package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_warriorskill extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_warriorskill.class);
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_warriorskill();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    public void setDoll(L1PcInstance pc) {
        try {
            pc.setSkillEffect(5222, 0);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.killSkillEffectTimer(5222);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean is_reset() {
        return false;
    }
}