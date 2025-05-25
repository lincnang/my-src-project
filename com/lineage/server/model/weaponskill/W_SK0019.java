package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Armor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 艾保羅泰因系列武器技能實現
 * 效果：
 * - 降低昏迷抗性 -10
 * - 減少近戰和遠程攻擊力 -5
 */
public class W_SK0019 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0019.class);
    private static final Random _random = new Random();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> effectTasks = new ConcurrentHashMap<>();

    public W_SK0019() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0019();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon, final double srcdmg) {
        try {

            // 計算技能觸發機率
            double activationChance = 10.0;

            // 判斷是否觸發技能
            if (_random.nextInt(100) >= activationChance) {
                return 0.0D; // 未觸發技能
            }

            // 應用效果到目標
            applyEffect(pc, target);

            // 返回額外傷害
            return calculateAdditionalDamage(pc);

        } catch (Exception e) {
            _log.error("W_SK0019 技能啟動失敗: ", e);
            return 0.0D; // 返回 0 傷害
        }
    }


    private void applyEffect(L1PcInstance pc, L1Character target) {
        if (target instanceof L1PcInstance) {
            L1PcInstance tgpc = (L1PcInstance) target;

            boolean isAlreadyAffected = effectTasks.containsKey(tgpc.getId());

            // 降低昏迷抗性
            for (L1ItemInstance item : tgpc.getInventory().getItems()) {
                if (item.isEquipped() && item.getItem() instanceof L1Armor) {
                    L1Armor armor = (L1Armor) item.getItem();
                    armor.set_regist_stun(armor.get_regist_stun() - 10);
                }
            }

            // 降低玩家狀態中的近戰和遠程攻擊力
            tgpc.addDmgup(-5); // 減少近戰攻擊力
            tgpc.addBowDmgup(-5); // 減少遠程攻擊力

            // 只有當效果首次應用時才發送提示
            if (!isAlreadyAffected) {
                pc.sendPacketsAll(new S_SkillSound(target.getId(), 18565));
                tgpc.sendPackets(new S_ServerMessage("\\f3艾保羅泰因效果:昏迷抗性-10，近遠攻擊力-5"));
                tgpc.sendPackets(new S_InventoryIcon(20846, true, 4590, 8));
            }

            // 定时移除效果（重新調度）
            scheduleEffectRemoval(tgpc);
        }
    }

    private void scheduleEffectRemoval(L1PcInstance tgpc) {
        ScheduledFuture<?> previousTask = effectTasks.remove(tgpc.getId());
        if (previousTask != null && !previousTask.isDone()) {
            previousTask.cancel(false);
        }

        ScheduledFuture<?> newTask = scheduler.schedule(() -> removeEffect(tgpc), 8, TimeUnit.SECONDS);
        effectTasks.put(tgpc.getId(), newTask);
    }

    private void removeEffect(L1PcInstance tgpc) {
        for (L1ItemInstance item : tgpc.getInventory().getItems()) {
            if (item.isEquipped() && item.getItem() instanceof L1Armor) {
                L1Armor armor = (L1Armor) item.getItem();
                armor.set_regist_stun(armor.get_regist_stun() + 10); // 恢复昏迷抗性
            }
        }

        // 恢复玩家狀態中的近戰和遠程攻擊力
        tgpc.addDmgup(5); // 恢復近戰攻擊力
        tgpc.addBowDmgup(5); // 恢復遠程攻擊力

        // 提示效果结束
        tgpc.sendPackets(new S_ServerMessage("艾保羅泰因效果消失: 恢復昏迷抗性與攻擊力！"));
        tgpc.sendPackets(new S_InventoryIcon(20846, true, 4590, 8));
        effectTasks.remove(tgpc.getId());
    }

    private double calculateAdditionalDamage(L1PcInstance pc) {
        return ((_random.nextInt(pc.getStr() + pc.getDex()) + 1) * 2) + _random.nextInt(50);
    }
}
