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

public class W_SK0019 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0019.class);
    private static final Random _random = new Random();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> effectTasks = new ConcurrentHashMap<>();

    public W_SK0019() {}

    public static L1WeaponSkillType get() {
        return new W_SK0019();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon, final double srcdmg) {
        try {
            // 0. 只對玩家觸發
            if (!(target instanceof L1PcInstance)) return 0.0D;
            L1PcInstance tgpc = (L1PcInstance) target;

            // 1. 有效果還在時，完全不處理
            if (effectTasks.containsKey(tgpc.getId())) {
                // System.out.println("[W_SK0019] 玩家已有負面狀態，不再觸發。");
                return 0.0D;
            }

            // 2. 判斷發動機率（資料庫 random1/random2）
            int activationChance = random(weapon);
            int chance = _random.nextInt(1000);
            System.out.printf("[W_SK0019] 發動機率 debug: activationChance=%d, 隨機值=%d, random1=%d, random2=%d, 裝備ID=%d, 玩家=%s\n",
                    activationChance, chance, get_random1(), get_random2(), weapon != null ? weapon.getItemId() : -1, pc.getName());

            if (chance >= activationChance) {
                return 0.0D;
            }

            // 3. 首次套用效果
            applyEffect(pc, tgpc);

            // 4. 傷害計算（如有需求）
            return calculateAdditionalDamage(pc);

        } catch (Exception e) {
            _log.error("W_SK0019 技能啟動失敗: ", e);
            return 0.0D;
        }
    }

    /** 負面效果套用&動畫，只能進來一次 */
    private void applyEffect(L1PcInstance pc, L1PcInstance tgpc) {
        // 降低裝備昏迷抗性
        for (L1ItemInstance item : tgpc.getInventory().getItems()) {
            if (item.isEquipped() && item.getItem() instanceof L1Armor) {
                L1Armor armor = (L1Armor) item.getItem();
                armor.set_regist_stun(armor.get_regist_stun() - 10);
            }
        }
        // 降低近戰&遠距攻擊力
        tgpc.addDmgup(-5);
        tgpc.addBowDmgup(-5);

        // 動畫&提示
        int effectGfx = get_gfxid2(); // 建議資料庫設 18565
        if (effectGfx > 0) {
            pc.sendPacketsAll(new S_SkillSound(tgpc.getId(), effectGfx));
        }
        tgpc.sendPackets(new S_ServerMessage("\\f3艾保羅泰因效果:昏迷抗性-10，近遠攻擊力-5"));
        tgpc.sendPackets(new S_InventoryIcon(20846, true, 4590, 8));

        // 安排自動移除
        scheduleEffectRemoval(tgpc);
    }

    private void scheduleEffectRemoval(L1PcInstance tgpc) {
        ScheduledFuture<?> previousTask = effectTasks.remove(tgpc.getId());
        if (previousTask != null && !previousTask.isDone()) {
            previousTask.cancel(false);
        }
        ScheduledFuture<?> newTask = scheduler.schedule(() -> removeEffect(tgpc), 8, TimeUnit.SECONDS);
        effectTasks.put(tgpc.getId(), newTask);
    }

    /** 自動移除負面效果，狀態恢復 */
    private void removeEffect(L1PcInstance tgpc) {
        for (L1ItemInstance item : tgpc.getInventory().getItems()) {
            if (item.isEquipped() && item.getItem() instanceof L1Armor) {
                L1Armor armor = (L1Armor) item.getItem();
                armor.set_regist_stun(armor.get_regist_stun() + 10); // 恢复昏迷抗性
            }
        }
        tgpc.addDmgup(5);
        tgpc.addBowDmgup(5);

        tgpc.sendPackets(new S_ServerMessage("艾保羅泰因效果消失: 恢復昏迷抗性與攻擊力！"));
        tgpc.sendPackets(new S_InventoryIcon(20846, true, 4590, 8));
        effectTasks.remove(tgpc.getId());
    }

    /** 額外傷害可根據你的需求設定，也可以回傳 0 */
    private double calculateAdditionalDamage(L1PcInstance pc) {
        return ((_random.nextInt(pc.getStr() + pc.getDex()) + 1) * 2) + _random.nextInt(50);
    }
}
