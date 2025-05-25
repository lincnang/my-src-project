package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_HpTR extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_HpTR.class);
    private int _int1;
    private int _int2;
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_HpTR();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
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
            pc.set_doll_hpr(_int1);
            pc.set_doll_hpr_time_src(_int2);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.set_doll_hpr(0);
            pc.set_doll_hpr_time_src(0);
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
 * com.lineage.server.model.doll.Doll_HpTR JD-Core Version: 0.6.2
 */