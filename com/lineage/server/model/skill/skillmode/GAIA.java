package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillWarrior;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

/**
 *   蓋亞技能 (GAIA)
 * - 技能施放時回復最大HP的40%（持續HOT秒數可設定）
 * - 技能冷卻可設定且支援資料庫持久化
 * - 個人冷卻判斷
 * - BUFF圖示、訊息
 */
public class GAIA extends SkillMode {

    public GAIA() {}

    @Override
    public int start(final L1PcInstance pc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // 1. 冷卻時間改為設定檔（單位：毫秒）
        final int reuseDelay = ConfigSkillWarrior.Gaia_Cooldown * 1000; // 例如1200秒=20分鐘

        // 2. 讀取個人冷卻（記憶體，登入時必須從DB同步過來）
        long now = System.currentTimeMillis();
        long nextCast = pc.getSkillReuse(L1SkillId.GAIA);
        if (now < nextCast) {
            int sec = (int)((nextCast - now) / 1000) + 1;
            pc.sendPackets(new S_SystemMessage("\\fA蓋亞技能尚在冷卻中，剩餘 " + sec + " 秒。"));
            return 0;
        }

        // 3. 查詢技能消耗道具
        L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.GAIA);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!pc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                pc.sendPackets(new S_SystemMessage("施放技能需要的道具不足！"));
                return 0;
            }
            // 扣除道具
            pc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 4. 設定下次可施放時間（記憶體+資料庫）
        long nextTimeMillis = now + reuseDelay;
        pc.setSkillReuse(L1SkillId.GAIA, nextTimeMillis); // 記憶體
        CharSkillTable.get().updateSkillCooldown(pc.getId(), L1SkillId.GAIA, new Timestamp(nextTimeMillis)); // 資料庫

        // 5. 技能主效果：HOT秒數用設定檔
        final int hotTime = ConfigSkillWarrior.Gaia_HotTime; // 例如5秒
        final int maxHp = pc.getMaxHp();
        final int totalHeal = (int)(maxHp * 0.4);
        final int healPerSec = totalHeal / hotTime;
        final int lastHeal = totalHeal - healPerSec * (hotTime - 1);

        pc.setSkillEffect(L1SkillId.GAIA, hotTime * 1000); // 上BUFF效果
        pc.sendPackets(new S_InventoryIcon(10297, true, 2777, 2777, 1200));
        pc.sendPackets(new S_SystemMessage("\\fT蓋亞之力發動，開始回復HP。"));

        Timer timer = new Timer();
        for (int i = 1; i <= hotTime; i++) {
            final int heal = (i == hotTime) ? lastHeal : healPerSec;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pc.hasSkillEffect(L1SkillId.GAIA)) {
                        int nowHp = pc.getCurrentHp();
                        int maxHp = pc.getMaxHp();
                        int newHp = Math.min(nowHp + heal, maxHp);
                        pc.setCurrentHp(newHp);
                    }
                }
            }, i * 1000L);
        }
        return 0;
    }

    @Override
    public int start(final com.lineage.server.model.Instance.L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // NPC不支援此技能
        return 0;
    }

    @Override
    public void start(final L1PcInstance pc, final Object obj) throws Exception {
        // 多型備用，不處理
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            ((L1PcInstance) cha).sendPackets(new S_SystemMessage("\\aE蓋亞效果消失。"));
        }
    }
}
