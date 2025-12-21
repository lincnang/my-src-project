package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillStop;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.monitor.PerformanceMonitor;
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

    // 節流機制：防止技能被濫用
    private static final long SKILL_COOLDOWN_MS = 500; // 0.5秒冷卻時間
    private static final ConcurrentHashMap<Integer, Long> _lastUsedTime = new ConcurrentHashMap<>();

    public W_SK0017() {}

    public static L1WeaponSkillType get() {
        return new W_SK0017();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon, final double srcdmg) {
        try {
            // === 0. 節流檢查（防止技能濫用） ===
            if (pc != null) {
                int pcId = pc.getId();
                Long lastUsed = _lastUsedTime.get(pcId);
                long now = System.currentTimeMillis();
                if (lastUsed != null && now - lastUsed < SKILL_COOLDOWN_MS) {
                    return srcdmg; // 冷卻中，直接返回原傷害
                }
                _lastUsedTime.put(pcId, now);
            }

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

            // === 3. 處理舊的疾病術狀態 ===
            // 如果已有疾病術，正確地移除舊效果
            if (target.hasSkillEffect(L1SkillId.DISEASE)) {
                // 使用 removeSkillEffect 確保完整流程
                // 這會：
                // 1. 停止計時器
                // 2. 呼叫 stopSkill 恢復所有數值
                // 3. 發送必要的狀態更新封包
                target.removeSkillEffect(L1SkillId.DISEASE);

                _log.debug("[W_SK0017] Removed old DISEASE effect from " +
                          (target instanceof L1PcInstance ? ((L1PcInstance) target).getName() : "NPC"));
            }

            // === 4. 施放疾病術 ===
            try (PerformanceMonitor.PerformanceTracker tracker =
                 PerformanceMonitor.trackSkill("DISEASE_CAST",
                    target instanceof L1PcInstance ? ((L1PcInstance) target).getName() : "NPC")) {

                L1SkillUse skillUse = new L1SkillUse();
                skillUse.handleCommands(pc, L1SkillId.DISEASE, target.getId(), target.getX(), target.getY(), 8000,
                        L1SkillUse.TYPE_GMBUFF);
            }

            // === 5. 處理玩家目標的圖示/訊息 ===
            if (target instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) target;
                tgpc.sendPackets(new S_ServerMessage("\\f3惡魔王效果:疾病術的降低了防禦與攻擊！"));
                tgpc.sendPackets(new S_InventoryIcon(760, true, 4585, 7));
                pc.sendPacketsAll(new S_SkillSound(target.getId(), 26195));

                // 注意：移除自定義定時器
                // L1SkillUse.setSkillEffect 已經創建了 8 秒的 L1SkillTimer
                // L1SkillTimer 會在 8 秒後自動調用 stopSkill 並恢復 AC
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

  }