package com.lineage.server.templates;

import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.model.Instance.L1PcInstance;

public class L1User_Power {
    private int _object_id;
    private int _c1_type;
    private String _note;
    private L1Name_Power _power;

    public int get_object_id() {
        return _object_id;
    }

    public void set_object_id(int _object_id) {
        this._object_id = _object_id;
    }

    public int get_c1_type() {
        return _c1_type;
    }

    public void set_c1_type(int _c1_type) {
        this._c1_type = _c1_type;
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String _note) {
        this._note = _note;
    }

    public void set_power(L1PcInstance pc, boolean login) {
        int score = pc.get_other().get_score();
        int lv = C1_Name_Type_Table.get().getLv(_c1_type, score);
        L1Name_Power power = C1_Name_Type_Table.get().get(_c1_type, lv);
        if (_power != null) {
            if (power == null) {
                _power.get_c1_classname().remove_c1(pc);
                _power = null;
                return;
            }
            if (!_power.equals(power)) {
                _power.get_c1_classname().remove_c1(pc);
                power.get_c1_classname().set_c1(pc);
                _power = power;
            } else if (login) {
                _power.get_c1_classname().set_c1(pc);
            }
        } else {
            _power = power;
            if (_power != null) {
                _power.get_c1_classname().set_c1(pc);
            }
        }
    }

    public L1Name_Power get_power() {
        return _power;
    }

    public int get_power_lv() {
        return _power.get_c1_id();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1User_Power JD-Core Version: 0.6.2
 */