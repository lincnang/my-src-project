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
        // 優先嘗試以啟動道具 itemId 反查對應技能配置；若無則回退到玩家當前選擇的節點
        SilianAstrologyData cfg = SilianAstrologyTable.get().getByGrantItemId(item.getItemId());
        if (cfg == null) {
            Integer key = SilianAstrologyCmd.get().getSilianSkillActive(pc);
            cfg = (key != null) ? SilianAstrologyTable.get().getData(key) : null;
        }
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
        // 檢查並扣除施放成本（僅在施放當下扣）
        if (cfg.getCastItemId() > 0) {
            int need = Math.max(1, cfg.getCastItemCount());
            if (!pc.getInventory().checkItem(cfg.getCastItemId(), need)) {
                pc.sendPackets(new S_SystemMessage("道具不足，無法施放絲莉安技能！"));
                return;
            }
            pc.getInventory().consumeItem(cfg.getCastItemId(), need);
        }
        // 改為瞬間回復邏輯：不再使用 HOT 限制，僅由冷卻時間控制施放頻率
        // 完全改用絲莉安專用實作，與蓋亞無任何關聯
        com.add.Tsai.Astrology.SilianRegen.cast(pc, cfg);
    }
}


