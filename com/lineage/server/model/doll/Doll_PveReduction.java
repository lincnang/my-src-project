package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 娃娃效果：純PVE減傷（被怪物攻擊時減免傷害）
 */
public class Doll_PveReduction extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_PveReduction.class);

    private int _reductionValue; // PVE減傷值
    private String _note; // 備註

    /**
     * 返回自身實例
     */
    public static L1DollExecutor get() {
        return new Doll_PveReduction();
    }

    /**
     * 設定能力值
     */
    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _reductionValue = int1; // 只使用type1來設定減傷數值
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String get_note() {
        return _note;
    }

    @Override
    public void set_note(String note) {
        _note = note;
    }

    /**
     * 娃娃裝備時賦予減傷效果
     */
    @Override
    public void setDoll(L1PcInstance pc) {
        try {
            if (_reductionValue > 0) {
                pc.addDamageReductionPVE(_reductionValue);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 娃娃卸下時移除減傷效果
     */
    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
            if (_reductionValue > 0) {
                pc.addDamageReductionPVE(-_reductionValue);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 是否需要重設
     */
    @Override
    public boolean is_reset() {
        return false;
    }
}
