package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 娃娃效果：吸血 (只對玩家角色施加能力，累加模式)
 */
public class Doll_Vampire extends L1DollExecutor {

    private static final Log _log = LogFactory.getLog(Doll_Vampire.class);

    private int _vampirePercent; // 吸血比例%
    private int _vampireChance;  // 吸血機率%
    private String _note; // 備註

    public static L1DollExecutor get() {
        return new Doll_Vampire();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _vampirePercent = int1; // 吸血比例
            _vampireChance = int2;  // 吸血機率
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

    @Override
    public void setDoll(L1PcInstance pc) {
        try {
            if (pc != null) {
                pc.add_dice_hp(_vampireChance, _vampirePercent); // 套用累加吸血機率&比例
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
            if (pc != null) {
                pc.add_dice_hp(-_vampireChance, -_vampirePercent); // 收回時相反數減回
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
