package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * W_SK008 類別
 * 此類別代表特定的武器技能類型，實作了魔法武器的附加魔法效果。
 */
public class W_SK008 extends L1WeaponSkillType {
    // 日誌記錄器，用於記錄錯誤或其他資訊
    private static final Log _log = LogFactory.getLog(W_SK008.class);

    // 隨機數生成器，用於決定技能是否觸發及計算隨機傷害
    private static final Random _random = new Random();

    /**
     * 獲取 W_SK008 類型的武器技能實例。
     *
     * @return W_SK008 的新實例
     */
    public static L1WeaponSkillType get() {
        return new W_SK008();
    }

    /**
     * 一般魔法武器附加魔法方法。
     * 當武器技能觸發時，計算並施加附加的魔法傷害或效果。
     *
     * @param pc     攻擊者的玩家實例
     * @param target 被攻擊的目標角色
     * @param weapon 使用的武器實例
     * @param srcdmg 原始傷害值
     * @return 最終計算後的傷害值
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            // 生成一個介於 0 到 999 之間的隨機數，用於決定技能是否觸發
            int chance = _random.nextInt(1000);

            // 判斷武器的觸發機率是否達到條件
            if (random(weapon) >= chance) {
                // 檢查目標是否已經有技能效果 ID 為 31 的效果
                if (target.hasSkillEffect(31)) {
                    // 移除技能效果 ID 為 31 的效果
                    target.removeSkillEffect(31);

                    // 獲取技能效果的動畫 ID
                    int castgfx2 = SkillsTable.get().getTemplate(31).getCastGfx2();

                    // 向所有玩家廣播技能效果動畫包
                    target.broadcastPacketAll(new S_SkillSound(target.getId(), castgfx2));

                    // 如果目標是玩家實例，則向該玩家單獨發送技能效果動畫包
                    if ((target instanceof L1PcInstance)) {
                        L1PcInstance tgpc = (L1PcInstance) target;
                        tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
                    }

                    // 當技能效果被移除時，返回 0 傷害
                    return 0.0D;
                }

                // 檢查目標是否有技能效果 ID 為 153（魔法消除）的效果
                if (target.hasSkillEffect(153)) { // 魔法消除
                    // 移除技能效果 ID 為 153 的效果
                    target.removeSkillEffect(153);
                }

                // 計算基礎傷害，可能基於目標的護甲或魔法抗性
                double damage = DmgAcMr.getDamage(pc);

                // 如果計算出的傷害會將目標的生命值降至 0 以下，且目標目前生命值不為 1
                if ((target.getCurrentHp() - damage < 0) && (target.getCurrentHp() != 1)) {
                    // 將傷害調整為使目標剩下 1 點生命值
                    damage = target.getCurrentHp() - 1;
                } else if (target.getCurrentHp() == 1) {
                    // 如果目標生命值已經是 1，則不造成額外傷害
                    damage = 0.0D;
                }

                // 增加額外傷害，_type1 為基礎值，_type2 為隨機範圍
                damage += _type1 + _random.nextInt(_type2);

                // 計算總傷害，包含各類傷害來源
                double outdmg = dmg1() + dmg2(srcdmg) + dmg3(pc) + damage;

                // 如果 _type3 大於 0，則對總傷害進行百分比調整
                if (_type3 > 0) {
                    outdmg *= _type3 / 100.0D;
                }

                // 顯示技能效果（例如動畫或特效）
                show(pc, target);

                // 計算最終傷害並應用到目標
                return calc_dmg(pc, target, outdmg, weapon);
            }

            // 如果技能未觸發，返回 0 傷害
            return 0.0D;
        } catch (Exception e) {
            // 如果發生例外，記錄錯誤資訊
            _log.error(e.getLocalizedMessage(), e);
        }

        // 在發生例外時，返回 0 傷害
        return 0.0D;
    }
}

/*
 * 類別位於：
 * C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar
 *
 * 完整類別名稱：
 * com.lineage.server.model.weaponskill.W_SK008
 *
 * 反編譯工具：
 * JD-Core Version: 0.6.2
 */
