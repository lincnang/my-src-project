package com.lineage.data.item_etcitem.extra;

import com.add.Tsai.Astrology.SilianAstrologyData;
import com.add.Tsai.Astrology.SilianAstrologyTable;
import com.add.Tsai.Astrology.SilianAstrologyCmd;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 絲莉安啟動道具：使用後觸發絲莉安 HOT（依據技能節點資料 Hpr/Mpr/冷卻時間）
 * 道具不在此刪除，切換/重啟由 Table 的 cleanup 機制回收
 */
public class SilianSkillStarter extends ItemExecutor {

    private SilianSkillStarter() {}

    public static ItemExecutor get() {
        return new SilianSkillStarter();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null || item == null) return;
        // 以「當前選擇的絲莉安技能節點」觸發 HOT（從指令端紀錄取得）
        Integer key = SilianAstrologyCmd.get().getSilianSkillActive(pc);
        SilianAstrologyData cfg = (key != null) ? SilianAstrologyTable.get().getData(key) : null;
        if (cfg == null || cfg.getSkillId() <= 0) {
            pc.sendPackets(new S_SystemMessage("尚未選擇絲莉安技能節點或節點不是技能型"));
            return;
        }
        long now = System.currentTimeMillis();
        int skillId = Math.max(1, cfg.getSkillId());
        long cdUntil = pc.getSilianCooldownUntilForSkill(skillId);
        if (now < cdUntil) {
            int sec = (int) ((cdUntil - now) / 1000) + 1;
            pc.sendPackets(new S_SystemMessage("\\fA星盤:絲莉安尚在冷卻中，剩餘 " + sec + " 秒。"));
            return;
        }
        // 若 HOT 仍在進行，也禁止重複施放（避免登入初期競態）
        long hotUntil = pc.getSilianRegenUntil();
        if (now < hotUntil) {
            int sec = (int) ((hotUntil - now) / 1000) + 1;
            pc.sendPackets(new S_SystemMessage("\\fA星盤:絲莉安效果進行中，剩餘 " + sec + " 秒。"));
            return;
        }
        // 完全改用絲莉安專用實作，與蓋亞無任何關聯
        com.add.Tsai.Astrology.SilianRegen.cast(pc, cfg);
    }
}


