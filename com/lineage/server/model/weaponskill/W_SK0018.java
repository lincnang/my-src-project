package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 格蘭肯的憤怒
 * PVP 傷害減免疊加技能 + 昏迷抗性減少效果
 * 每層效果：PVP 傷害減免 -1%、昏迷抗性 -2%
 * 最大疊加：5 層
 * 觸發機率：50%
 */
public class W_SK0018 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0018.class);

    // 執行緒調度器
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 技能常數設定
    private static final int MAX_STACK = 5; // 最大疊加層數
    private static final int PVP_DAMAGE_REDUCTION_PER_STACK = -1; // 每層 PVP 傷害減免
    private static final int STUN_RESISTANCE_REDUCTION_PER_STACK = -2; // 每層昏迷抗性減少
    private static final int SKILL_DURATION_MS = 8000; // 技能持續時間（毫秒）
    private static final double TRIGGER_CHANCE = 1.0; // 0.5 觸發機率（50%）// 10% 機率:  = 0.1 //  100% 機率:  = 1.0

    // 隨機數生成器
    private static final Random _random = new Random();

    public W_SK0018() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0018();
    }

    /**
     * 執行技能邏輯
     *
     * @param pc     使用技能的玩家角色
     * @param target 技能目標角色
     * @param weapon 使用的武器
     * @param srcdmg 原始傷害
     * @return 最終傷害
     */
    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
                                     final L1ItemInstance weapon, final double srcdmg) {
        try {
            // === 1. 機率判斷（直接用SQL欄位 random1, random2） ===
            int activationChance = random(weapon); // L1WeaponSkillType 的 random(weapon)
            int chance = _random.nextInt(1000);
            if (chance >= activationChance) {
                return srcdmg; // 未觸發，直接回傳原傷害
            }

            if (pc == null || target == null || !(target instanceof L1PcInstance)) {
                return 0;
            }

            L1PcInstance targetPc = (L1PcInstance) target;

            // 獲取當前疊加層數
            int currentStacks = targetPc.getTemporaryEffect("PVP_DAMAGE_SKILL");
            if (currentStacks >= MAX_STACK) {
                targetPc.sendPackets(new S_SystemMessage("效果已達最大疊加層數。"));
                return 0;
            }

            // 增加疊加層數
            currentStacks++;
            targetPc.addTemporaryEffect("PVP_DAMAGE_SKILL", currentStacks, SKILL_DURATION_MS);

            // 套用技能效果（PVP 傷害減免 + 昏迷抗性減少）
            applySkillEffects(targetPc, currentStacks);

            // 排程移除效果
            final int stacks = currentStacks;
            boolean isLastStack = stacks == MAX_STACK;
            scheduler.schedule(() -> removeSkillEffects(targetPc, stacks, isLastStack),
                    SKILL_DURATION_MS, TimeUnit.MILLISECONDS);

            // 播放動畫，可用SQL欄位設定
            int animId = get_gfxid2(); // 建議統一和W_SK0017，資料庫填18856
            if (animId > 0) {
                pc.sendPacketsAll(new S_SkillSound(target.getId(), animId));
            }
            targetPc.sendPackets(new S_InventoryIcon(10017, true, 3215, 8));

            return srcdmg;
        } catch (Exception e) {
            _log.error("技能執行時發生錯誤: " + e.getLocalizedMessage(), e);
            return 0;
        }
    }


    /**
     * 套用技能效果（PVP 傷害減免 + 昏迷抗性減少）
     *
     * @param target 目標角色
     * @param stacks 當前疊加層數
     */
    private void applySkillEffects(L1Character target, int stacks) {
        if (target instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) target;

            // 計算總減免值
            int totalPvpReduction = PVP_DAMAGE_REDUCTION_PER_STACK * stacks;
            int totalStunResistanceReduction = STUN_RESISTANCE_REDUCTION_PER_STACK * stacks;

            // 設置效果
            targetPc.setPvpDmg_R(totalPvpReduction);
            targetPc.setHunmi(totalStunResistanceReduction);

            // 顯示提示訊息
            targetPc.sendPackets(new S_SystemMessage(
                    String.format("格蘭肯疊加到 %d 層：PVP減免 %d%%，昏迷抗性 %d%%。",
                            stacks, totalPvpReduction, totalStunResistanceReduction)
            ));
        }
    }

    /**
     * 移除技能效果（PVP 傷害減免 + 昏迷抗性減少）
     *
     * @param target      目標角色
     * @param stacks      要移除的層數
     * @param isLastStack 是否是最後一層
     */
    private void removeSkillEffects(L1Character target, int stacks, boolean isLastStack) {
        if (target instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) target;

            // 恢復效果
            targetPc.setPvpDmg_R(0); // 移除所有 PVP 傷害減免
            targetPc.setHunmi(0); // 移除所有昏迷抗性減少

            // 只在最後一層移除時顯示訊息
            if (isLastStack) {
                targetPc.sendPackets(new S_SystemMessage("格蘭肯憤怒技能已恢復。"));
            }
        }
    }
}
