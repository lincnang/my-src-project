package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 娃娃能力 模組:各項附加機率傷害<BR>
 *
 * @author dexc
 */
public class Doll_Weight2 extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Weight2.class);
    private int _int1;// 增加娃娃魔法武器發動機率
    private int _int2;// 增加娃娃魔法武器傷害
    private int _int3;// 增加娃娃屬性卷機率
    /**
     * 設置娃娃能力描述
     *
     */
    private String _note;

    /**
     * 娃娃效果:各項附加機率傷害
     */
    public Doll_Weight2() {
    }

    public static L1DollExecutor get() {
        return new Doll_Weight2();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
            _int3 = int3;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    @Override
    public void setDoll(L1PcInstance pc) {
        try {
            if (_int1 != 0) {
                pc.addWeaponsprobability(_int1);
            }
            if (_int2 != 0) {
                pc.addWeapondmg(_int2);
            }
            if (_int3 != 0) {
                pc.addPropertyprobability(_int3);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
            if (_int1 != 0) {
                pc.addWeaponsprobability(-_int1);
            }
            if (_int2 != 0) {
                pc.addWeapondmg(-_int2);
            }
            if (_int3 != 0) {
                pc.addPropertyprobability(-_int3);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean is_reset() {
        return false;
    }
}
