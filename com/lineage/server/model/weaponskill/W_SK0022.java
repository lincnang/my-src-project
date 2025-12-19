package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * 武器毀滅性攻擊 使用這項技能武器將會毀滅並造成設定的傷害 _type1:對方HP殘留值等份量 _type2:對方HP殘留值等份 計算後數字加入傷害質
 * 範例: _type1 = 2 _type2 = 3 取回對方殘留HP的1/3 後 * 2次 也就是殘留HP 2/3的意思
 * 只對不死族生物有效（undeadType = 1 或 3）
 *
 * @author daien
 */
public class W_SK0022 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0022.class);

    private static final Random _random = new Random();

    public W_SK0022() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0022();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
                                     final L1ItemInstance weapon, final double srcdmg) {
        try {
            // mode2 和 mode1 的額外條件判斷（原始代碼中的邏輯保留）
            if (mode2(pc, target)) {
                return 0.0D;
            }

            // 計算技能發動機率
            final int random = random(weapon);
            if ((_random.nextInt(1000) + 1) <= random) {
                if (mode1(pc, target)) {
                    return 0.0D;
                }

                // 只對怪物、召喚物、寵物生效
                if ((target instanceof L1MonsterInstance) ||
                        (target instanceof L1SummonInstance) ||
                        (target instanceof L1PetInstance)) {

                    int undeadType = 0;
                    // 獲取不死族類型
                    if (target instanceof L1MonsterInstance) {
                        undeadType = ((L1MonsterInstance) target).getNpcTemplate().get_undead();
                    } else if (target instanceof L1SummonInstance) {
                        undeadType = ((L1SummonInstance) target).getNpcTemplate().get_undead();
                    } else if (target instanceof L1PetInstance) {
                        // 寵物可能需要特殊處理，暫時設定為0
                        undeadType = 0;
                    }

                    // 只對不死族生物（undeadType = 1 或 3）生效
                    if ((undeadType == 1) || (undeadType == 3)) {
                        final L1NpcInstance npc = (L1NpcInstance) target;

                        // 顯示技能動畫
                        show(pc, npc);

                        // 獲取目標當前HP
                        int currentHp = npc.getCurrentHp();

                        // 計算傷害：
                        // 公式：取目標剩餘HP的 (1/_type2)，然後乘以 _type1 倍
                        // 也就是造成目標剩餘HP的 (_type1/_type2) 比例傷害
                        int type1 = get_type1() > 0 ? get_type1() : 2;
                        int type2 = get_type2() > 0 ? get_type2() : 3;

                        // 計算實際傷害
                        int outdmg = (currentHp * type1) / type2;

                        // 確保至少造成1點傷害，但不要超過當前HP
                        if (outdmg < 1) {
                            outdmg = 1;
                        }
                        if (outdmg > currentHp) {
                            outdmg = currentHp - 1; // 留1點血避免立即死亡
                        }

                        return calc_dmg(pc, target, outdmg, weapon);
                    }
                }
            }
            return 0;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * mode1 額外條件判斷（可根據需要實現特定邏輯）
     * 預設返回false表示不阻擋技能
     */
    private boolean mode1(L1PcInstance pc, L1Character target) {
        // 可以在這裡添加特定的條件判斷
        // 例如：檢查PC的狀態、目標的狀態等
        return false;
    }

    /**
     * mode2 額外條件判斷（可根據需要實現特定邏輯）
     * 預設返回false表示不阻擋技能
     */
    private boolean mode2(L1PcInstance pc, L1Character target) {
        // 可以在這裡添加特定的條件判斷
        // 例如：檢查安全區域、特定地圖等
        return false;
    }
}