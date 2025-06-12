package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.*;

public class W_SK0020 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0020.class);
    private static final int DEBUFF_DURATION = 8000; // BUFF 持續時間（毫秒）
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> effectTasks = new ConcurrentHashMap<>();
    private static final Random _random = new Random();

    public W_SK0020() {}

    public static L1WeaponSkillType get() {
        return new W_SK0020();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
                                     final L1ItemInstance weapon, final double srcdmg) {
        try {
            if (!(target instanceof L1PcInstance)) return 0.0D;
            L1PcInstance tgpc = (L1PcInstance) target;

            // ===== 機率判斷（採用 random1/random2 機率）=====
            int activationChance = random(weapon); // parent class 的 random() 會用 random1/random2 計算
            int chance = _random.nextInt(1000);
            System.out.printf("[W_SK0020] 發動機率 debug: activationChance=%d, 隨機值=%d, random1=%d, random2=%d, 裝備ID=%d, 玩家=%s\n",
                    activationChance, chance, get_random1(), get_random2(),
                    weapon != null ? weapon.getItemId() : -1, pc != null ? pc.getName() : "null");
            if (chance >= activationChance) return 0.0D;

            // ===== 強制移除勇敢狀態 =====
            if (tgpc.hasSkillEffect(L1SkillId.STATUS_BRAVE3)) {
                tgpc.removeSkillEffect(L1SkillId.STATUS_BRAVE3);
                tgpc.sendPackets(new S_ServerMessage("底比斯混沌效果：龍之珍珠效果被移除。"));
            }

            // ===== 若已經有 HIT_BUFF，僅消勇敢不再套負面 =====
            if (tgpc.hasSkillEffect(L1SkillId.HIT_BUFF)) {
                return 0.0D;
            }

            // ===== 套上負面 HIT_BUFF =====
            tgpc.setSkillEffect(L1SkillId.HIT_BUFF, DEBUFF_DURATION);
            tgpc.broadcastPacketAll(new S_SkillBrave(tgpc.getId(), 0, 0));
            tgpc.sendPackets(new S_ServerMessage("底比斯混沌效果：速度-10%，無法獲得龍之珍珠效果。"));

            // ===== 8秒後移除 HIT_BUFF =====
            scheduleBuffRemoval(tgpc);

            return 0.0D;

        } catch (Exception e) {
            _log.error("W_SK0020 技能啟動失敗: ", e);
            return 0.0D;
        }
    }

    private void scheduleBuffRemoval(L1PcInstance target) {
        ScheduledFuture<?> future = scheduler.schedule(() -> removeHitBuff(target), DEBUFF_DURATION, TimeUnit.MILLISECONDS);
        effectTasks.put(target.getId(), future);
    }

    private void removeHitBuff(L1PcInstance target) {
        target.sendPackets(new S_ServerMessage("底比斯混沌效果消失。"));
        target.broadcastPacketAll(new S_SkillBrave(target.getId(), 0, 0));
        effectTasks.remove(target.getId());
    }
}
