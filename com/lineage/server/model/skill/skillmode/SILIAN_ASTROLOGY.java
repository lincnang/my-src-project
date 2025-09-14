package com.lineage.server.model.skill.skillmode;

import com.add.Tsai.Astrology.SilianAstrologyData;
import com.add.Tsai.Astrology.SilianAstrologyTable;
import com.add.Tsai.Astrology.SilianAstrologyCmd;
import com.add.Tsai.Astrology.SilianRegen;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 絲莉安專用技能（獨立於蓋亞）。
 * - 冷卻與圖示皆走絲莉安專屬欄位
 * - 不寫入技能冷卻資料表
 */
public class SILIAN_ASTROLOGY extends SkillMode {

    public SILIAN_ASTROLOGY() {}

    @Override
    public int start(final L1PcInstance pc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // 依目前選擇的絲莉安技能節點觸發
        Integer key = SilianAstrologyCmd.get().getSilianSkillActive(pc);
        SilianAstrologyData cfg = (key != null) ? SilianAstrologyTable.get().getData(key) : null;
        if (cfg == null || cfg.getSkillId() <= 0) {
            pc.sendPackets(new S_SystemMessage("尚未選擇絲莉安技能節點或節點不是技能型"));
            return 0;
        }
        long now = System.currentTimeMillis();
        int skillId = Math.max(1, cfg.getSkillId());
        long cdUntil = pc.getSilianCooldownUntilForSkill(skillId);
        if (now < cdUntil) {
            int sec = (int)((cdUntil - now) / 1000) + 1;
            pc.sendPackets(new S_SystemMessage("\\fA絲莉安技能尚在冷卻中，剩餘 " + sec + " 秒。"));
            return 0;
        }
        // 檢查並扣除施放成本（僅在施放當下扣）
        if (cfg.getCastItemId() > 0) {
            int need = Math.max(1, cfg.getCastItemCount());
            if (!pc.getInventory().checkItem(cfg.getCastItemId(), need)) {
                pc.sendPackets(new S_SystemMessage("道具不足，無法施放絲莉安技能！"));
                return 0;
            }
            pc.getInventory().consumeItem(cfg.getCastItemId(), need);
        }
        // 改為瞬間回復邏輯：不再使用 HOT 限制，僅由冷卻時間控制施放頻率
        SilianRegen.cast(pc, cfg);
        return 0;
    }

    @Override
    public int start(final com.lineage.server.model.Instance.L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void start(final L1PcInstance pc, final Object obj) throws Exception {
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
    }
}


