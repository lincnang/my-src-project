package com.lineage.data.item_weapon.proficiency;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 玩家武器熟練度
 * @author 聖子默默
 */
public class WeaponProficiency {
    private static final Log _log = LogFactory.getLog(WeaponProficiency.class);
    /**
     * true = 所有等級的屬性都變動<br>
     * false = 只變動當前最高等級的屬性
     */
    private static final boolean _all = false;
    /**
     * 以不同熟練度等級計算屬性加成（實際計算中不會用到此屬性）
     */
    private final int _proficiencyLevel;
    private final int _exp;
    private final short _hp;
    private final short _mp;

    /**
     * 武器熟練度設定
     * 其他屬性請自行新增
     *
     * @param level 熟練度等級
     * @param exp 所需經驗
     * @param hp 增加的HP屬性
     * @param mp 增加的MP屬性
     */
    public WeaponProficiency(int level, int exp, short hp, short mp) {
        _proficiencyLevel = level;
        _exp = exp;
        _hp = hp;
        _mp = mp;
    }

    /**
     * 熟練度變更玩家屬性<br>
     * 裝備/卸除武器時執行一次<br>
     * 因為並未嚴格檢測玩家武器,有可能會在極端情況下出現問題,但得測試過才知道
     *
     * @param pc  執行對象
     * @param type 武器類型
     * @param proficiencyLevel 熟練度等級 每個等級的屬性都變更
     * @param add 變更的屬性倍率 負數為減少
     */
    public static void addWeaponProficiencyStatus(final L1PcInstance pc, final int type, final int proficiencyLevel, final int add) {
        try {
            if (proficiencyLevel <= 0) {
                return;
            }
            final int maxProficienciesLevel = WeaponProficiencyTable.getMaxProficienciesLevel(type);// 最大等级
            final int level = Math.min(proficiencyLevel, maxProficienciesLevel);// 避免數據錯誤（超出最大等級設定）
            if (_all) {// 所有等級屬性加成全部啓用 默認關閉
                for (int i = 0; i < level; ++i) {// 從1級開始計算 依次增加
                    effect(pc, type, add, i);
                }
                return;
            }
            effect(pc, type, add, level);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 屬性加成
     *
     * @param pc 玩家
     * @param type 武器類型
     * @param add 增加屬性的倍數
     * @param level 武器熟練度等級
     */
    private static void effect(L1PcInstance pc, int type, int add, int level) {
        WeaponProficiency weaponProficiency = WeaponProficiencyTable.getProficiency(type, level);
        if (weaponProficiency == null) {// 防粗心設定
            return;
        }
        if (weaponProficiency.getHP() != 0) {
            pc.addMaxHp(weaponProficiency.getHP() * add);
        }
        if (weaponProficiency.getMp() != 0) {
            pc.addMaxMp(weaponProficiency.getMp() * add);
        }
        // 其他屬性請自行新增
    }

    public int getProficiencyLevel() {
        return _proficiencyLevel;
    }

    public int getExp() {
        return _exp;
    }

    public int getHP() {
        return _hp;
    }

    public int getMp() {
        return _mp;
    }
}
