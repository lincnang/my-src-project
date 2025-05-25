package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
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

/**
 * 惡魔王系列武器傷害公式
 * 【力量+敏捷】*2 +【浮動0-50】
 * 發動機率：
 * 基礎機率3%
 * 隨武器加成提高：
 * +8=5%
 * +9=6%
 * +10=8%
 * +11=10%
 * 之後每多1+1%
 * 隨機施展「疾病術」機率1% +9 2%
 */
public class W_SK0017 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0017.class);
    private static final Random _random = new Random();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> diseaseTasks = new ConcurrentHashMap<>();

    public W_SK0017() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0017();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon,
                                     final double srcdmg) {
        try {
            // 獲取武器的強化等級
            int enchantLevel = weapon.getEnchantLevel();

            // 計算技能觸發機率
            double activationChance = 3.0;
            if (enchantLevel >= 8) {
                switch (enchantLevel) {
                    case 8:
                        activationChance = 5.0;
                        break;
                    case 9:
                        activationChance = 6.0;
                        break;
                    case 10:
                        activationChance = 8.0;
                        break;
                    case 11:
                        activationChance = 10.0;
                        break;
                    default:
                        if (enchantLevel > 11) {
                            activationChance = 10.0 + (enchantLevel - 11);
                        }
                        break;
                }
            }

            // 判斷是否觸發技能
            if (_random.nextInt(1000) >= activationChance * 10) {
                return 0.0D; // 未觸發技能
            }

            // 確保疾病術的狀態正確移除
            synchronized (target) {
                if (target.hasSkillEffect(L1SkillId.DISEASE)) {
                    L1SkillStop.stopSkill(target, L1SkillId.DISEASE);
                    target.removeSkillEffect(L1SkillId.DISEASE);
                }
            }

            // 施放疾病術
            L1SkillUse skillUse = new L1SkillUse();
            skillUse.handleCommands(pc, L1SkillId.DISEASE, target.getId(), target.getX(), target.getY(), 8000,
                    L1SkillUse.TYPE_GMBUFF);

            // 處理目標是玩家的情況
            if (target instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) target;

                // 顯示技能效果圖示
                tgpc.sendPackets(new S_ServerMessage("\\f3惡魔王效果:疾病術的降低了防禦與攻擊！"));
                tgpc.sendPackets(new S_InventoryIcon(760, true, 4585, 7));
                //                target.broadcastPacketAll(new S_SkillSound(target.getId(), 26195));
                //                tgpc.sendPackets(new S_SkillSound(tgpc.getId(), 26195));
                pc.sendPacketsAll(new S_SkillSound(target.getId(), 26195));
                // 取消舊的定時任務
                ScheduledFuture<?> previousTask = diseaseTasks.remove(tgpc.getId());
                if (previousTask != null && !previousTask.isDone()) {
                    previousTask.cancel(false);
                }

                // 安排新的定時任務
                ScheduledFuture<?> newTask = scheduler.schedule(() -> {
                    try {
                        removeDiseaseEffect(tgpc); // 統一移除邏輯
                    } catch (Exception e) {
                        _log.error("移除疾病術效果時發生例外: ", e);
                    }
                }, 8, TimeUnit.SECONDS);

                // 保存新的任務
                if (newTask != null) {
                    diseaseTasks.put(tgpc.getId(), newTask);
                }
            }

            // 計算額外傷害
            // 【力量+敏捷】*2 +【浮動0-50】
            double damage = ((_random.nextInt(pc.getStr() + pc.getDex()) + 1) * _type1) + +dmg1();

            if (_type3 > 0) {
                damage *= (_type3 / 100D);
            }

            int outdmg = (int) (dmg2(srcdmg) + dmg3(pc) + damage);
            if ((target.getCurrentHp() - outdmg) < 0) {
                outdmg = 1;
            }
            return calc_dmg(pc, target, outdmg, weapon);


        } catch (Exception e) {
            _log.error("W_SK0017 技能啟動失敗: ", e);
            return 0.0D; // 返回 0 傷害
        }
    }

    /**
     * 移除疾病術效果並清理相關狀態
     */
    private void removeDiseaseEffect(L1PcInstance tgpc) {
        synchronized (tgpc) {
            // 確保效果只移除一次
            if (!tgpc.hasSkillEffect(L1SkillId.DISEASE)) {
                _log.debug("疾病術效果已經不存在，無需再次移除。");
                return;
            }

            // 移除效果
            L1SkillStop.stopSkill(tgpc, L1SkillId.DISEASE);
            tgpc.removeSkillEffect(L1SkillId.DISEASE);
            tgpc.sendPackets(new S_ServerMessage("惡魔王效果:疾病術的效果消失了！"));
            tgpc.sendPackets(new S_InventoryIcon(760, true, 4585, 7));
            diseaseTasks.remove(tgpc.getId());
            _log.debug("疾病術效果已被移除，圖示也已關閉。");
        }
    }
}
