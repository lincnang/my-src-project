package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

public class W_SK0020 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0020.class);
    private static final int DEBUFF_DURATION = 8000; // BUFF 持續時間（毫秒）
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // 增加緒程池大小
    private static final ConcurrentHashMap<Integer, ScheduledFuture<?>> effectTasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Long> lastBrave3Timestamp = new ConcurrentHashMap<>(); // 用来记录 STATUS_BRAVE3 的时间戳

    public W_SK0020() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0020();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target, final L1ItemInstance weapon, final double srcdmg) {
        try {
            if (target instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) target;

                // 检查目标是否在3秒内已经有 STATUS_BRAVE3 状态
                if (isInBrave3Cooldown(tgpc)) {
                    return 0.0D; // 直接返回，不再应用技能
                }

                // 如果目标已经有命中BUFF效果，跳过技能应用
                if (tgpc.hasSkillEffect(L1SkillId.HIT_BUFF)) {
                    return 0.0D; // 直接返回，不再应用技能
                }

                // 否則应用命中BUFF效果
                applyHitBuff(tgpc);

                // 取消 STATUS_BRAVE3 状态
                removeBrave3Status(tgpc);

                scheduleBuffRemoval(tgpc);
            }

            return 0.0D; // 无额外伤害

        } catch (Exception e) {
            return 0.0D;
        }
    }

    private void applyHitBuff(L1PcInstance target) {
        target.setSkillEffect(L1SkillId.HIT_BUFF, DEBUFF_DURATION); // 設定命中BUFF
        target.broadcastPacketAll(new S_SkillBrave(target.getId(), 0, 0)); // 广播命中BUFF效果
        target.sendPackets(new S_ServerMessage("底比斯混沌效果:速度-10%"));
    }

    private void removeBrave3Status(L1PcInstance target) {
        if (target.hasSkillEffect(L1SkillId.STATUS_BRAVE3)) {
            target.removeSkillEffect(L1SkillId.STATUS_BRAVE3); // 取消 STATUS_BRAVE3 状态
            target.sendPackets(new S_ServerMessage("底比斯混沌效果消失"));
        }
    }

    private void scheduleBuffRemoval(L1PcInstance target) {
        // 若目標已死亡或其他情況需要取消定時任務，需適當改進此部分
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            removeHitBuff(target);
        }, DEBUFF_DURATION, TimeUnit.MILLISECONDS);

        effectTasks.put(target.getId(), future);
    }

    private void removeHitBuff(L1PcInstance target) {
        target.sendPackets(new S_ServerMessage("底比斯混沌效果消失"));
        target.broadcastPacketAll(new S_SkillBrave(target.getId(), 0, 0));
        effectTasks.remove(target.getId());
    }

    // 检查目标是否在3秒内已经有 STATUS_BRAVE3 状态
    private boolean isInBrave3Cooldown(L1PcInstance target) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastBrave3Timestamp.getOrDefault(target.getId(), 0L);

        // 如果距离上次施放 STATUS_BRAVE3 已超过 5 秒，返回 false；否则返回 true
        return currentTime - lastTime < 8000;
    }

    // 设置目标的 STATUS_BRAVE3 状态时间
    private void setBrave3Timestamp(L1PcInstance target) {
        lastBrave3Timestamp.put(target.getId(), System.currentTimeMillis());
    }
}
