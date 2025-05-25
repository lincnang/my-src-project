package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillIconBlessOfEva;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Water extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Water.class);
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_Water();
    }

    public void set_power(int int1, int int2, int int3) {
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    public void setDoll(L1PcInstance pc) {
        try {
            if (pc.hasSkillEffect(1003)) {
                pc.killSkillEffectTimer(1003);
            }
            pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), -1));
            pc.setSkillEffect(1003, 0);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.killSkillEffectTimer(1003);
            pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean is_reset() {
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.doll.Doll_Water JD-Core Version: 0.6.2
 */