package com.add.Tsai.Astrology;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 絲莉安專用：啟動 HOT 與冷卻（完全獨立於蓋亞）。
 */
public final class SilianRegen {

    private SilianRegen() {}

    /**
     * 依節點資料觸發絲莉安的 HOT 與共用冷卻。
     */
    public static void cast(final L1PcInstance pc, final SilianAstrologyData data) {
        if (pc == null || data == null) return;

        final int hotTime = Math.max(1, data.getHotTime() > 0 ? data.getHotTime() : 5);
        final int totalHp = Math.max(0, data.getHpr());
        final int totalMp = Math.max(0, data.getMpr());
        if (totalHp == 0 && totalMp == 0) {
            pc.sendPackets(new S_SystemMessage("星盤:絲莉安未設定回復量，無法啟動。"));
            return;
        }

        // 設置對應技能的冷卻（毫秒）
        long now = System.currentTimeMillis();
        int skillId = Math.max(1, data.getSkillId());
        pc.setSilianCooldownUntilForSkill(skillId, now + hotTime * 1000L);
        // 持久化 HOT 狀態（秒）
        try {
            if (pc.get_other() != null) {
                int untilS = (int) ((now + hotTime * 1000L) / 1000L);
                pc.get_other().set_silian_hot_until_s(untilS);
                pc.get_other().set_silian_hot_skill_id(skillId);
                new com.lineage.server.datatables.sql.CharOtherTable().storeOther(pc.getId(), pc.get_other());
            }
        } catch (Throwable ignore) {}

        // 均勻分配每秒回復
        final int hpPerTick = (hotTime > 1) ? (totalHp / hotTime) : totalHp;
        final int hpLastTick = totalHp - hpPerTick * (hotTime - 1);
        final int mpPerTick = (hotTime > 1) ? (totalMp / hotTime) : totalMp;
        final int mpLastTick = totalMp - mpPerTick * (hotTime - 1);

        // 設置絲莉安專用再生時間戳與圖示
        long until = now + hotTime * 1000L;
        pc.setSilianRegenUntil(until);
        int iconId;
        switch (skillId) {
        case 2:
            iconId = 9708;
            break;
        case 3:
            iconId = 9718;
            break;
        case 1:
        default:
            iconId = 9700;
            break;
        }
        pc.sendPackets(new S_InventoryIcon(iconId, true, 2783, hotTime));
        // 施放特效（若有設定，否則播放預設 7789）
        int g1 = data.getGfxid1();
        int g2 = data.getGfxid2();
        if (g1 <= 0 && g2 <= 0) {
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7789));
        } else {
            if (g1 > 0) pc.sendPacketsAll(new S_SkillSound(pc.getId(), g1));
            if (g2 > 0) pc.sendPacketsAll(new S_SkillSound(pc.getId(), g2));
        }
        // 提示：啟動成功（使用者已改為此格式）
        pc.sendPackets(new S_SystemMessage("絲莉安星盤:" + data.getNote(), 1));

        Timer timer = new Timer();
        for (int i = 1; i <= hotTime; i++) {
            final int hpHeal = (i == hotTime) ? hpLastTick : hpPerTick;
            final int mpHeal = (i == hotTime) ? mpLastTick : mpPerTick;
            if (hpHeal == 0 && mpHeal == 0) continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() > pc.getSilianRegenUntil()) return;
                    if (hpHeal > 0 && pc.getCurrentHp() < pc.getMaxHp()) {
                        pc.setCurrentHp(Math.min(pc.getCurrentHp() + hpHeal, pc.getMaxHp()));
                    }
                    if (mpHeal > 0 && pc.getCurrentMp() < pc.getMaxMp()) {
                        pc.setCurrentMp(Math.min(pc.getCurrentMp() + mpHeal, pc.getMaxMp()));
                    }
                }
            }, i * 1000L);
        }
    }
}


