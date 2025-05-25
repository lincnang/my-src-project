package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillHaste;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Speed extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Speed.class);
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_Speed();
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
            pc.addHasteItemEquipped(1);
            pc.removeHasteSkillEffect();
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, -1));
            if (pc.getMoveSpeed() != 1) {
                pc.setMoveSpeed(1);
                pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.addHasteItemEquipped(-1);
            if (pc.getHasteItemEquipped() == 0) {
                pc.setMoveSpeed(0);
                pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
            }
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
 * com.lineage.server.model.doll.Doll_Speed JD-Core Version: 0.6.2
 */