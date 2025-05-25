package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Regist_Fear extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Regist_Fear.class);
    private int _int1;
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_Regist_Fear();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            this._int1 = int1;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String get_note() {
        return this._note;
    }

    public void set_note(String note) {
        this._note = note;
    }

    public void setDoll(L1PcInstance pc) {
        try {
            pc.addRegistFear(this._int1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            pc.addRegistFear(-this._int1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean is_reset() {
        return false;
    }
}