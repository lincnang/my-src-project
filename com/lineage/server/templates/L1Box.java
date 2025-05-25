package com.lineage.server.templates;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1Box {
    private static final Log _log = LogFactory.getLog(L1Box.class);
    private static final int _int8 = 128;
    private static final int _int7 = 64;
    private static final int _int6 = 32;
    private static final int _int5 = 16;
    private static final int _int4 = 8;
    private static final int _int3 = 4;
    private static final int _int2 = 2;
    private static final int _int1 = 1;
    private int _box_item_id;
    private int _get_item_id;
    private int _randomint;
    private int _random;
    private int _min_count;
    private int _max_count;
    private int _use_type = 127;
    private boolean _out = false;
    private boolean _isCrown;
    private boolean _isKnight;
    private boolean _isElf;
    private boolean _isWizard;
    private boolean _isDarkelf;
    private boolean _isDragonKnight;
    private boolean _isIllusionist;
    private boolean _isWarrior;
    private int _broad;
    private int _enchant;

    public int get_broad() {
        return this._broad;
    }

    public void set_broad(int broad) {
        this._broad = broad;
    }

    public int get_enchant() {
        return this._enchant;
    }

    public void set_enchant(int enchant) {
        this._enchant = enchant;
    }

    public int get_box_item_id() {
        return _box_item_id;
    }

    public void set_box_item_id(int box_item_id) {
        _box_item_id = box_item_id;
    }

    public int get_item_id() {
        return _get_item_id;
    }

    public void set_get_item_id(int get_item_id) {
        _get_item_id = get_item_id;
    }

    public int get_randomint() {
        return _randomint;
    }

    public void set_randomint(int randomint) {
        _randomint = randomint;
    }

    public int get_random() {
        return _random;
    }

    public void set_random(int random) {
        _random = random;
    }

    public int get_min_count() {
        return _min_count;
    }

    public void set_min_count(int min_count) {
        _min_count = min_count;
    }

    public int get_max_count() {
        return _max_count;
    }

    public void set_max_count(int max_count) {
        _max_count = max_count;
    }

    public boolean is_out() {
        return _out;
    }

    public void set_out(boolean out) {
        _out = out;
    }

    public int get_use_type() {
        return _use_type;
    }

    public void set_use_type(int use_type) {
        this._use_type = use_type;
        if (use_type >= _int8) {
            use_type -= _int8;//戰士
            _isWarrior = true;
        }
        if (use_type >= _int7) {
            use_type -= _int7;
            _isIllusionist = true;
        }
        if (use_type >= _int6) {
            use_type -= _int6;
            _isDragonKnight = true;
        }
        if (use_type >= _int5) {
            use_type -= _int5;
            _isDarkelf = true;
        }
        if (use_type >= _int4) {
            use_type -= _int4;
            _isWizard = true;
        }
        if (use_type >= _int3) {
            use_type -= _int3;
            _isElf = true;
        }
        if (use_type >= _int2) {
            use_type -= _int2;
            _isKnight = true;
        }
        if (use_type >= _int1) {
            use_type -= _int1;
            _isCrown = true;
        }
        if (use_type > 0) {
            _log.error("寶箱可用職業設定錯誤:餘數大於0 編號:" + _box_item_id + "/" + _get_item_id);
        }
    }

    public boolean is_use(L1PcInstance pc) {
        try {
            if ((pc.isCrown()) && (_isCrown)) {
                return true;
            }
            if ((pc.isKnight()) && (_isKnight)) {
                return true;
            }
            if ((pc.isElf()) && (_isElf)) {
                return true;
            }
            if ((pc.isWizard()) && (_isWizard)) {
                return true;
            }
            if ((pc.isDarkelf()) && (_isDarkelf)) {
                return true;
            }
            if ((pc.isDragonKnight()) && (_isDragonKnight)) {
                return true;
            }
            if ((pc.isIllusionist()) && (_isIllusionist)) {
                return true;
            }
            if ((pc.isWarrior()) && (_isWarrior)) {
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\7.0\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Box JD-Core Version: 0.6.2
 */