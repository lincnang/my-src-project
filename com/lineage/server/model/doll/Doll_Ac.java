package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Ac extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Ac.class);
    private int _int1;
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_Ac();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
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
            pc.addAc(_int1);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.addAc(-_int1);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean is_reset() {
        return false;
    }
}
