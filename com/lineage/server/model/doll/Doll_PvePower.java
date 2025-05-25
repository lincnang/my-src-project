package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 娃娃效果：PVE 傷害增加（專用）
 */
public class Doll_PvePower extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_PvePower.class);

    private int _value; // PVE攻擊加成數值
    private String _note; // 備註

    /**
     * 返回自身實例
     */
    public static L1DollExecutor get() {
        return new Doll_PvePower();
    }

    /**
     * 設定能力值
     */
    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _value = int1; // 只使用type1，代表PVE攻擊增加
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
     * 娃娃裝備時賦予能力
     */
    @Override
    public void setDoll(L1PcInstance pc) {
        try {
            if (_value > 0) {
                pc.addDamageIncreasePVE(_value); // 增加PVE攻擊加成
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 娃娃卸下時移除能力
     */
    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
            if (_value > 0) {
                pc.addDamageIncreasePVE(-_value);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean is_reset() {
        return false;
    }
}
