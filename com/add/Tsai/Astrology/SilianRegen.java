package com.add.Tsai.Astrology;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
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

        // 冷卻時間：取資料表冷卻欄位（若未設則預設 5 秒）
        final int cooldownSec = Math.max(1, data.getHotTime() > 0 ? data.getHotTime() : 5);
        // 瞬間回復總量：嚴格依據資料表 Hpr/Mpr（直接一次性回復該數值）
        final int totalHp = Math.max(0, data.getHpr());
        final int totalMp = Math.max(0, data.getMpr());
        if (totalHp == 0 && totalMp == 0) {
            pc.sendPackets(new S_SystemMessage("星盤:絲莉安未設定回復量，無法啟動。"));
            return;
        }

        // 設置對應技能的冷卻（毫秒）
        long now = System.currentTimeMillis();
        int skillId = Math.max(1, data.getSkillId());
        pc.setSilianCooldownUntilForSkill(skillId, now + cooldownSec * 1000L);
        // 持久化 HOT 狀態（秒）
        // 清除HOT持久化（改為瞬間回復，HOT不再使用）
        // 不再使用 HOT 持久化

        // 顯示ICON與逐秒回復設定（固定5秒，實現 HoT 緩回效果）
        final int healTimeSec = 5;
        long now2 = System.currentTimeMillis();
        long until = now2 + healTimeSec * 1000L;
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
        // ICON 顯示冷卻時間，非治療時間
        pc.sendPackets(new S_InventoryIcon(iconId, true, 2783, cooldownSec));

        // 施放特效（若有設定，否則播放預設 7789）
        int g1 = data.getGfxid1();
        int g2 = data.getGfxid2();
        if (g1 <= 0 && g2 <= 0) {
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7789));
        } else {
            if (g1 > 0) pc.sendPacketsAll(new S_SkillSound(pc.getId(), g1));
            if (g2 > 0) pc.sendPacketsAll(new S_SkillSound(pc.getId(), g2));
        }

        // 5秒內平均分配（最後一跳補齊），第一跳立即套用，後續每秒跳一次
        final int hpPerTick = (healTimeSec > 1) ? (totalHp / healTimeSec) : totalHp;
        final int hpLastTick = totalHp - hpPerTick * (healTimeSec - 1);
        final int mpPerTick = (healTimeSec > 1) ? (totalMp / healTimeSec) : totalMp;
        final int mpLastTick = totalMp - mpPerTick * (healTimeSec - 1);

        int firstHp = (healTimeSec == 1) ? hpLastTick : hpPerTick;
        int firstMp = (healTimeSec == 1) ? mpLastTick : mpPerTick;
        boolean changed = false;
        if (firstHp > 0 && pc.getCurrentHp() < pc.getMaxHp()) {
            pc.setCurrentHp(Math.min(pc.getCurrentHp() + firstHp, pc.getMaxHp()));
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            changed = true;
        }
        if (firstMp > 0 && pc.getCurrentMp() < pc.getMaxMp()) {
            pc.setCurrentMp(Math.min(pc.getCurrentMp() + firstMp, pc.getMaxMp()));
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            changed = true;
        }
        if (changed && pc.isInParty()) {
            pc.getParty().updateMiniHP(pc);
        }

        Timer timer = new Timer();
        for (int i = 1; i < healTimeSec; i++) {
            final int hpHeal = (i == healTimeSec - 1) ? hpLastTick : hpPerTick;
            final int mpHeal = (i == healTimeSec - 1) ? mpLastTick : mpPerTick;
            if (hpHeal == 0 && mpHeal == 0) continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() > pc.getSilianRegenUntil()) return;
                    boolean tickChanged = false;
                    if (hpHeal > 0 && pc.getCurrentHp() < pc.getMaxHp()) {
                        pc.setCurrentHp(Math.min(pc.getCurrentHp() + hpHeal, pc.getMaxHp()));
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        tickChanged = true;
                    }
                    if (mpHeal > 0 && pc.getCurrentMp() < pc.getMaxMp()) {
                        pc.setCurrentMp(Math.min(pc.getCurrentMp() + mpHeal, pc.getMaxMp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                        tickChanged = true;
                    }
                    if (tickChanged && pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }
                }
            }, i * 1000L);
        }

        // 提示：啟動成功（使用者已改為此格式）
        pc.sendPackets(new S_SystemMessage("絲莉安星盤:" + data.getNote(), 1));
    }
}


