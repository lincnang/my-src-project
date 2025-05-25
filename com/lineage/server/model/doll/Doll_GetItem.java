package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 娃娃能力 模組:給予物品<BR>
 * 給予物品 參數：TYPE1(時間:秒) TYPE2(給予物品編號) TYPE3(給予的數量)
 *
 * @author dexc
 */
public class Doll_GetItem extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_GetItem.class);
    private int _int1;// 值1
    private int _int2;// 值2
    private int _int3;// 值3
    private String _note;

    /**
     * 娃娃效果:給予物品
     */
    public Doll_GetItem() {
    }

    public static L1DollExecutor get() {
        return new Doll_GetItem();
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    @Override
    public void set_power(final int int1, final int int2, final int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
            _int3 = int3;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void setDoll(final L1PcInstance pc) {
        try {
            pc.set_doll_get(_int2, _int3);
            pc.set_doll_get_time_src(_int1);
            pc.set_doll_get_time(_int1); // 修正BUG
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void removeDoll(final L1PcInstance pc) {
        try {
            pc.set_doll_get(0, 0);
            pc.set_doll_get_time_src(0);
            pc.set_doll_get_time(0); // 修正BUG
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean is_reset() {
        return false;
    }
}
