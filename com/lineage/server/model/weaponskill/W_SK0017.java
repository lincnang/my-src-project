package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillStop;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.*;

public class W_SK0017 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0017.class);
    private static final Random _random = new Random();
    // private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> diseaseTasks = new ConcurrentHashMap<>();

    public W_SK0017() {}

    public static L1WeaponSkillType get() {
        return new W_SK0017();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon, final double srcdmg) {
        try {
            // === 1. 判斷技能發動機率（使用 random1/random2） ===
            int activationChance = random(weapon); // parent class 的 random() 會用 random1/random2 算出千分比
            int chance = _random.nextInt(1000);
            if (chance >= activationChance) {
                return 0.0D; // 未觸發，直接返回
            }

            // === 2. 播放動畫（SQL欄位 gfxid2）===
            int animId = get_gfxid2();
            if (animId > 0) {
                if (target instanceof L1NpcInstance) {
                    ((L1NpcInstance) target).broadcastPacketAll(new S_SkillSound(target.getId(), animId));
                } else if (target instanceof L1PcInstance) {
                    ((L1PcInstance) target).sendPacketsAll(new S_SkillSound(target.getId(), animId));
                }
                pc.sendPackets(new S_SkillSound(target.getId(), animId)); // 攻擊者也能看到
            }

            // === 3. 移除舊的疾病術狀態 ===
            synchronized (target) {
                if (target.hasSkillEffect(L1SkillId.DISEASE)) {
                    L1SkillStop.stopSkill(target, L1SkillId.DISEASE);
                    target.removeSkillEffect(L1SkillId.DISEASE);
                }
            }

            // === 4. 施放疾病術 ===
            L1SkillUse skillUse = new L1SkillUse();
            skillUse.handleCommands(pc, L1SkillId.DISEASE, target.getId(), target.getX(), target.getY(), 8000,
                    L1SkillUse.TYPE_GMBUFF);

            // === 5. 處理玩家目標的圖示/訊息與定時器 ===
            if (target instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) target;
                tgpc.sendPackets(new S_ServerMessage("\\f3惡魔王效果:疾病術的降低了防禦與攻擊！"));
                tgpc.sendPackets(new S_InventoryIcon(760, true, 4585, 7));
                pc.sendPacketsAll(new S_SkillSound(target.getId(), 26195));

                // 定時8秒後自動移除疾病術
                ScheduledFuture<?> previousTask = diseaseTasks.remove(tgpc.getId());
                if (previousTask != null && !previousTask.isDone()) {
                    previousTask.cancel(false);
                }
                ScheduledFuture<?> newTask = com.lineage.server.thread.GeneralThreadPool.get().schedule((Runnable) () -> {
                    try {
                        removeDiseaseEffect(tgpc);
                    } catch (Exception e) {
                        _log.error("移除疾病術效果時發生例外: ", e);
                    }
                }, 8000L);
                if (newTask != null) {
                    diseaseTasks.put(tgpc.getId(), newTask);
                }
            }

            // === 6. 傷害計算 ===
            double damage = ((_random.nextInt(pc.getStr() + pc.getDex()) + 1) * get_type1()) + dmg1();
            if (get_type3() > 0) {
                damage *= ((double)get_type3() / 100.0);
            }
            int outdmg = (int) (dmg2(srcdmg) + dmg3(pc) + damage);
            if ((target.getCurrentHp() - outdmg) < 0) {
                outdmg = 1;
            }

            // === 7. 返回最終傷害 ===
            return calc_dmg(pc, target, outdmg, weapon);

        } catch (Exception e) {
            _log.error("W_SK0017 技能啟動失敗: ", e);
            return 0.0D;
        }
    }

    /** 疾病術自動移除邏輯 */
    private void removeDiseaseEffect(L1PcInstance tgpc) {
        synchronized (tgpc) {
            // 移除自訂圖示 (修正為 false)
            tgpc.sendPackets(new S_InventoryIcon(760, false, 4585, 7));
            
            if (!tgpc.hasSkillEffect(L1SkillId.DISEASE)) {
                _log.debug("疾病術效果已經不存在，無需再次移除。");
                diseaseTasks.remove(tgpc.getId());
                return;
            }
            L1SkillStop.stopSkill(tgpc, L1SkillId.DISEASE);
            tgpc.removeSkillEffect(L1SkillId.DISEASE);
            tgpc.sendPackets(new S_ServerMessage("惡魔王效果:疾病術的效果消失了！"));
            diseaseTasks.remove(tgpc.getId());
            _log.debug("疾病術效果已被移除，圖示也已關閉。");
        }
    }
}
